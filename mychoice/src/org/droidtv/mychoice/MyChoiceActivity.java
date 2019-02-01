package org.droidtv.mychoice;

import java.util.ArrayList;
import java.util.List;

import org.droidtv.mychoice.util.MyChoiceAPIWrapper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.SystemProperties;
import org.droidtv.mychoice.common.BaseViewHolder;

import org.droidtv.tv.blur.BlurSurface;
import org.droidtv.ui.tvwidget2k15.blur.BlurSurfaceView;
import org.droidtv.ui.tvwidget2k15.blur.BlurSurfaceView.BlurHost;
import org.droidtv.ui.tvwidget2k15.blur.IBlurCallback;

public class MyChoiceActivity extends Activity implements BaseViewHolder.onItemCommonClickListener, MyChoiceAdapter.onItemScrollListner{

	private static final String TAG = "MyChoiceNewActivity";
	private RecyclerView mRecyclerView = null;
	private ImageView upArrowIV = null, downArrowIV = null;
	private List<ChannelData> datas = null;
	private MyChoiceAPIWrapper mThemeApi = null;
	private MyChoiceAdapter mMyChoiceAdapter = null;
	private BlurSurfaceView mBlurSurface = null;
	private Context mContext = null;
	private ProgressDialog pd = null;
	private boolean dataReady = false;

	public View  mainview=null;
	public Handler mMainHandler;
	public final int MYCHOICE_UI_SHOW = 0x01;
	public final int MYCHOICE_UI_DISMISS = 0x02;
	public final int MYCHOICE_UI_UPDATE = 0x03;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SystemProperties.set("sys.htv.mychoice_page", "IN_PROGRESS");
		mContext = this;
		mThemeApi = MyChoiceAPIWrapper.getInstance();
		mThemeApi.setApplicationContext(mContext);

		mMainHandler = new Handler() {
			@Override
			public synchronized void handleMessage(Message msg) {
				switch (msg.what) {
				case MYCHOICE_UI_UPDATE:
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							initData();
						}
					}).start();
					break;
				case MYCHOICE_UI_SHOW:
					pd.dismiss();
					updateUI();

					break;
				case MYCHOICE_UI_DISMISS:
					if (!dataReady) {
						pd.dismiss();
						finish();
					}
					break;
				default:
					super.handleMessage(msg);
					break;
				}

			};
		};

		initUI();

	}

	void initUI() {
		Log.v("TAG", "initui");
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mainview = inflater.inflate(R.layout.activity_main_3,null);
		setContentView(mainview);
		mRecyclerView = (RecyclerView) mainview.findViewById(R.id.rcv);
		upArrowIV = (ImageView) mainview.findViewById(R.id.up_arrow);
		downArrowIV = (ImageView) mainview.findViewById(R.id.down_arrow);
		//mBlurSurface = (BlurSurfaceView) mainview.findViewById(R.id.bsv);
		//mBlurSurface.setBlurCallback(new MyChoiceBlurCallback(this, new BlurSurface()));
		//mBlurSurface.setName(TAG);
		//mBlurSurface.setMode(mThemeApi.BlurMode);
		//mBlurSurface.setRegion(new Rect(0, 0, mThemeApi.DM.widthPixels, mThemeApi.DM.heightPixels));
		//mBlurSurface.setVisibility(View.VISIBLE);
		//loading ui
		pd = new ProgressDialog(mContext, R.style.CustomDialog);
		pd.setContentView(R.layout.application_loader);
		pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				Message m = mMainHandler.obtainMessage();
				m.what = MYCHOICE_UI_DISMISS;
				mMainHandler.sendMessage(m);

			}
		});
		WindowManager.LayoutParams params = pd.getWindow().getAttributes();
	    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
	    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
	    pd.getWindow().setAttributes(params);
		pd.show();

		Message m = mMainHandler.obtainMessage();
		m.what= MYCHOICE_UI_UPDATE;
		mMainHandler.sendMessage(m);
	}

	void initData() {
		datas = new ArrayList<ChannelData>();
		datas = mThemeApi.getChannelList(datas);
		mMyChoiceAdapter = new MyChoiceAdapter(mContext, datas, this);
		mMyChoiceAdapter.setHasStableIds(true);
		mMyChoiceAdapter.setCommonScrollListner(this);
		dataReady = true;
		Message m = mMainHandler.obtainMessage();
		m.what= MYCHOICE_UI_SHOW;
		mMainHandler.sendMessage(m);
	}

	void updateUI() {
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
		mRecyclerView.setAdapter(mMyChoiceAdapter);
		mRecyclerView.setItemAnimator(null);
//		LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_animation_fall_down);
//		mRecyclerView.setLayoutAnimation(animation);
		if (mRecyclerView.getVisibility() != View.VISIBLE) {
			mRecyclerView.setVisibility(View.VISIBLE);
		}
		if (mMyChoiceAdapter.getItemCount() > 8) {
			upArrowIV.setVisibility(View.GONE);
			downArrowIV.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mBlurSurface != null) {
			mBlurSurface.onResume();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		SystemProperties.set("sys.htv.mychoice_page", "OFF");
		//align broadcast for channels app service to sync data -- by Daemon.yu
		Intent it = new Intent("org.droidtv.mychoice.MYCHOICE_DATA_CHANGED");
		sendBroadcast(it);
		super.onPause();
		if (mBlurSurface != null) {
			Log.v("EAGLE", "mBlurSurface onpause.");
			mBlurSurface.onPause();
		}
		if (pd != null) pd.dismiss();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		SystemProperties.set("sys.htv.mychoice_page", "OFF");
		super.onDestroy();
		if (pd != null) pd.dismiss();
		if (datas != null) {
			datas.clear();
			datas = null;
		}
		if (mMainHandler != null) {
			mMainHandler.removeCallbacksAndMessages(null);
			mMainHandler = null;
		}
		if (mBlurSurface != null) {
			mBlurSurface.setVisibility(View.GONE);
                        mBlurSurface = null;
			Log.v("EAGLE", "mBlurSurface onDestroy.");
                }

	}

	@Override
	public void onItemClickListener(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(int position) {
		// TODO Auto-generated method stub
		if (mMyChoiceAdapter.getItemCount() < 8) {
			// do nothing
		} else {
			if (position < 1) {
				Log.v("EAGLE", "the first item.");
				upArrowIV.setVisibility(View.GONE);
			} else if ((position + 2) > mMyChoiceAdapter.getItemCount()) {
				Log.v("EAGLE", "the last item.");
				downArrowIV.setVisibility(View.GONE);
			} else {
				upArrowIV.setVisibility(View.VISIBLE);
				downArrowIV.setVisibility(View.VISIBLE);
			}
		}
	}
}
class MyChoiceBlurCallback extends IBlurCallback {
	private BlurSurface mBS;
	private Context mCtx;

	public MyChoiceBlurCallback(Context ctx, BlurSurface bs) {
		mCtx = ctx;
		mBS = bs;
	}

	@Override
	public BlurHost getHost() {
		return new BlurHost().setActivity((Activity) mCtx);
	}

	@Override
	public void onDrawFrame(int left, int top, int right, int bottom, int scalefactor, int blurradius, float darkness,
			String blursurfaceid, int blurmode) {
		mBS.drawBlurSurface(left, top, right, bottom, scalefactor, blurradius, darkness, blursurfaceid, blurmode);
	}

	@Override
	public int setupBlur(int width, int height, int scalefactor) {
		return mBS.setupBlur(width, height, scalefactor);
	}
}
