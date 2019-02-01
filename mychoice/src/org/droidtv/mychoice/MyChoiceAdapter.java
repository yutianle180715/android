package org.droidtv.mychoice;

import java.util.List;

import org.droidtv.mychoice.common.BaseRecyclerAdapter;
import org.droidtv.mychoice.common.BaseViewHolder;
import org.droidtv.mychoice.common.BaseViewHolder.onItemCommonClickListener;
import org.droidtv.mychoice.common.BaseViewHolder.onItemCommonFocuseChangedListener;
import org.droidtv.htv.provider.HtvContract;
import org.droidtv.htv.provider.HtvContract.HtvChannelSetting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MyChoiceAdapter extends BaseRecyclerAdapter<ChannelData> {

	private BaseViewHolder.onItemCommonClickListener commonClickListener;
	private static updateRunnable updatedata = null;
	private Context mContext;
	private onItemScrollListner commonScrollListner;

	public interface onItemScrollListner {
		void onScroll(int position);
	}

	public void setCommonScrollListner(onItemScrollListner commonScrollListner) {
		this.commonScrollListner = commonScrollListner;
	}

	public MyChoiceAdapter(Context context, List<ChannelData> dataList) {
		super(context, dataList, R.layout.mychoice_item_layout);
		// TODO Auto-generated constructor stub
		mContext = context;
		updatedata = new updateRunnable();
	}

	public MyChoiceAdapter(Context context, List<ChannelData> dataList, BaseViewHolder.onItemCommonClickListener commonClickListener) {
		super(context, dataList, R.layout.mychoice_item_layout);
		// TODO Auto-generated constructor stub
		this.commonClickListener = commonClickListener;
		mContext = context;
		updatedata = new updateRunnable();
	}

	 class updateRunnable implements Runnable {
		 private int disNum;
		 private int[] mychoiceArray;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Cursor cursor = null;
			String[] mProjection = { HtvContract.HtvChannelSetting._ID,
					HtvContract.HtvChannelSetting.COLUMN_DISPLAY_NUMBER };
			try {
				cursor = mContext.getContentResolver().query(HtvContract.HtvChannelSetting.CONTENT_URI,
						mProjection, null, null, null, null);
				int rows_num = cursor.getCount();

				ContentValues tVal = new ContentValues();

				tVal.put(HtvContract.HtvChannelSetting.COLUMN_FREEPKG, mychoiceArray[0]);
				tVal.put(HtvContract.HtvChannelSetting.COLUMN_PAYPKG1, mychoiceArray[1]);
				tVal.put(HtvContract.HtvChannelSetting.COLUMN_PAYPKG2, mychoiceArray[2]);


				mContext.getContentResolver().update(HtvContract.HtvChannelSetting.CONTENT_URI, tVal,
						new StringBuilder(HtvContract.HtvChannelSetting.COLUMN_DISPLAY_NUMBER).append("=")
								.append(disNum).toString(), null);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
				cursor = null;
			}
		}

		public void setDisNum(int disNum) {
			this.disNum = disNum;
		}

		public void setMychoiceArray(int[] mychoiceArray) {
			this.mychoiceArray = mychoiceArray;
		}

	 }

	private synchronized boolean updateChannelData(final int dispNum, final int [] mychoiceArray) {
		updatedata.setDisNum(dispNum);
		updatedata.setMychoiceArray(mychoiceArray);
		updatedata.run();
		return true;
	}

	@Override
	public void bindData(final BaseViewHolder holder, ChannelData data, int position) {
		// TODO Auto-generated method stub
		holder.setText(R.id.clnumber, Integer.toString(data.getChannelNum()))
				.setText(R.id.cnname, data.getChannelName())
				.setImageResource(R.id.cllogo, data.getChannelLogo())
				.setChecked(R.id.freebt, data.getMychoicefree())
				.setChecked(R.id.pkg1bt, data.getMychoicepkg1())
				.setChecked(R.id.pkg2bt, data.getMychoicepkg2())
				.setLayoutView(R.id.headerlayout);

		if (position < 1) {
			holder.setFocusView(R.id.headerlayout);
		}

		final int checked = 1;
		final int nchecked = 0;
		//from the layout colors channel_list_parent_text_color.xml
		final String textcolorfocued = "#76D7D6";

		holder.setCommonFocuseChangedListener(new onItemCommonFocuseChangedListener() {

			@Override
			public void onItemFocuseChanged(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				commonScrollListner.onScroll(position);
				switch (v.getId()) {
				case R.id.freebt:
				case R.id.pkg1bt:
				case R.id.pkg2bt:
					if (hasFocus) {
						holder.setTextColor(R.id.clnumber, textcolorfocued);
						holder.setTextColor(R.id.cnname, textcolorfocued);
						holder.setImageBackgroud(R.id.cllogo, mContext.getResources().getDrawable(R.drawable.logo_button_hi));
					} else {
						holder.setTextColor(R.id.clnumber, mContext.getResources().getColor(R.color.channel_list_parent_text_color));
						holder.setTextColor(R.id.cnname, mContext.getResources().getColor(R.color.channel_list_parent_text_color));
						holder.setImageBackgroud(R.id.cllogo, mContext.getResources().getDrawable(R.drawable.logo_button_no));
					}
					break;

				case R.id.headerlayout:
					if (hasFocus) {
						holder.setTextColor(R.id.clnumber, textcolorfocued);
						holder.setTextColor(R.id.cnname, textcolorfocued);
						holder.setImageBackgroud(R.id.cllogo, mContext.getResources().getDrawable(R.drawable.logo_button_hi));
					} else {
						holder.setTextColor(R.id.clnumber, mContext.getResources().getColor(R.color.channel_list_parent_text_color));
						holder.setTextColor(R.id.cnname, mContext.getResources().getColor(R.color.channel_list_parent_text_color));
						holder.setImageBackgroud(R.id.cllogo, mContext.getResources().getDrawable(R.drawable.logo_button_no));
					}
					break;

				default:
					break;
				}
			}
		});

		holder.setCommonClickListener(new onItemCommonClickListener() {
			@Override
			public void onItemClickListener(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.freebt:
					if (!holder.getChecked(R.id.freebt)) {
						holder.setChecked(R.id.freebt, true);
						data.setMychoicefree(checked);
					} else {
						holder.setChecked(R.id.freebt, true);
						data.setMychoicefree(checked);
						if (holder.getChecked(R.id.pkg1bt)) {
							holder.setChecked(R.id.pkg1bt, false);
							data.setMychoicepkg1(nchecked);
						}
						if (holder.getChecked(R.id.pkg2bt)) {
							holder.setChecked(R.id.pkg2bt, false);
							data.setMychoicepkg2(nchecked);
						}
					}
					int[] temp0 = new int[]{data.getMychoicefree(), data.getMychoicepkg1(), data.getMychoicepkg2()};
					updateChannelData(data.getChannelNum(), temp0);
					break;
				case R.id.pkg1bt:
					if (!holder.getChecked(R.id.pkg1bt)) {
						holder.setChecked(R.id.pkg1bt, false);
						data.setMychoicepkg1(nchecked);
						if (!holder.getChecked(R.id.pkg2bt)) {
							holder.setChecked(R.id.freebt, true);
							data.setMychoicefree(checked);
						}
					} else {
						holder.setChecked(R.id.pkg1bt, true);
						data.setMychoicepkg1(checked);
						if (holder.getChecked(R.id.freebt)) {
							holder.setChecked(R.id.freebt, false);
							data.setMychoicefree(nchecked);
						}
					}
					int[] temp1 = new int[]{data.getMychoicefree(), data.getMychoicepkg1(), data.getMychoicepkg2()};
					updateChannelData(data.getChannelNum(), temp1);
					break;
				case R.id.pkg2bt:
					if (!holder.getChecked(R.id.pkg2bt)) {
						holder.setChecked(R.id.pkg2bt, false);
						data.setMychoicepkg2(nchecked);
						if (!holder.getChecked(R.id.pkg1bt)) {
							holder.setChecked(R.id.freebt, true);
							data.setMychoicefree(checked);
						}
					} else {
						holder.setChecked(R.id.pkg2bt, true);
						data.setMychoicepkg2(checked);
						if (holder.getChecked(R.id.freebt)) {
							holder.setChecked(R.id.freebt, false);
							data.setMychoicefree(nchecked);
						}
					}
					int[] temp2 = new int[]{data.getMychoicefree(), data.getMychoicepkg1(), data.getMychoicepkg2()};
					updateChannelData(data.getChannelNum(), temp2);
					break;

				default:
					break;
				}
			}
		});
	}

}
