package org.droidtv.welcome;

import java.io.File;
import java.util.Calendar;

import org.droidtv.weather.WeatherInfo;
import org.droidtv.weather.WeatherRefreshHelper;
import org.droidtv.welcome.data.manager.WelcomeDataManager;
import org.droidtv.welcome.data.manager.WelcomeDataManager.ConfigureHelper;
import org.droidtv.welcome.data.manager.WelcomeDataManager.WeatherInfoDataListener;
import org.droidtv.welcome.data.manager.WelcomeDataManager.WeatherSettingsListener;
import org.droidtv.welcome.database.WelcomeContract;
import org.droidtv.welcome.message.MessageHelper;
import org.droidtv.welcome.util.WelcomeUtils;

import android.graphics.PorterDuff;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.service.dreams.DreamService;
import android.text.format.DateFormat;
import android.text.style.LineHeightSpan.WithDensity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import com.philips.professionaldisplaysolutions.jedi.IPMS;
/*import com.philips.professionaldisplaysolutions.jedi.PMS;*/
import com.philips.professionaldisplaysolutions.jedi.IPMS.GuestDetails;
import com.philips.professionaldisplaysolutions.jedi.IPMS.GuestMessage;
import com.philips.professionaldisplaysolutions.jedi.jedifactory.JEDIFactory;

public class WelcomeDreamService extends DreamService{
    private static final boolean DEBUG = true;
    //implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener {
    private String videoPath = Environment.getExternalStorageDirectory().getPath() + "/welcome";
    private String video00 = Environment.getExternalStorageDirectory().getPath() + "/welcome/ht.mp4";
    private TextView tv_HotelName;
    private TextView tv_GuestName;
    private TextView tv_Welcome;
    private TextureView textureView;
    private TextView tv_currentDate;
    private TextClock tv_currentTime;
    private TextView tv_currentTimeLable;
	private TextView tv_messageTitle;
    private ImageView img_weatherIcon;
    private TextView tv_temperature;
    private ImageView container;
    private MessageHelper mMsgHelper;
    private WelcomeMediaPlayer mVideoPlayer = null;
    private WelcomeImagePlayer mImagePlayer = null;
    private Bitmap curBitmap = null;
    private WeatherInfo mWeatherinfo;
    private Context mContext;
    private String TAG = this.getClass().getSimpleName();
    private IPMS mPms;
    private boolean playMode = false;//playMode = false,play local default resource;playMode = true,play custom resource from clone;
    private final static int INTERNAL_TIME = 10000;
    private final static int PLAY_NEXT = 1;
    private final static int NEXT_IMAGE = 2;
    private int curPlayImage = 0;
    private WelcomeDataManager mWelcomeDataManager;
    private ConfigureHelper mConfigureHelper;
    private Handler mImgHandler = null;
	private boolean mPremisesNameConfigure;
	private boolean mGuestNameConfigure;
	private boolean mWelcomeConfigure;
	private boolean mDateConfigure;
	private boolean mTimeConfigure;
	private boolean mWeatherConfigure;
	private int mPhilipsDefConfigure;
	private final int PLAY_LOCAL_PICTURES = 101;
	private final int PLAY_LOCAL_VIDEO1 = 102;
	private final int PLAY_LOCAL_VIDEO2 = 103;
    private final static int[] defaultimages = {
    	R.drawable.welcome_image1,
		R.drawable.welcome_image2,
		R.drawable.welcome_image3,
		R.drawable.welcome_image4,
		R.drawable.welcome_image5,
		R.drawable.welcome_image6 };
    private int curimageid = 0;
  	private static final String CLOCK_FORMAT_AM_PM = "hh:mm";
  	private static final String CLOCK_FORMAT_24 = "HH:mm";
  	private WeatherRefreshHelper mWeatherRefreshHelper;
  	private LinearLayout mArrowsLyt;
	private ImageView mAnimationArrow;
	private Animation mAnimationToArrows;

    @Override
    public void onCreate() {
    	super.onCreate();
		mContext = getApplicationContext();
		mPms = JEDIFactory.getInstance(IPMS.class);
        mWelcomeDataManager = WelcomeDataManager.getInstance();
        if(mWelcomeDataManager != null) mConfigureHelper = mWelcomeDataManager.getConfigureHelper();
		mImgHandler = new ImagePlayerHandler();
	//	mMsgHelper.registerCallback(mContext,mPms,this);
    	Log.d(TAG, "WelcomeDreamService:onCreate()");
    	{
    		ContentValues values = new ContentValues();
    		values.put(WelcomeContract.WelcomeEntry.COLUMN_NAME, "test");
    		Uri muri = getContentResolver().insert(WelcomeContract.CONTENT_URI, values);
    		Log.v(TAG, "muri: " + muri);
    	}
    }

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		setInteractive(false);// We want the screen saver to exit upon user interaction.
		setFullscreen(true);// Hide system UI
		setContentView(R.layout.mainlayout);
    	initView();
    	startArrowAnimation();
    	initSettingsConfig();
    	showWelcomeContent();
    	playMediaResource(playMode);
    	Log.d(TAG, "WelcomeDreamService:onAttachedToWindow()");
	}

	private void initView(){
		mWeatherinfo = new WeatherInfo();
		mMsgHelper = new MessageHelper(mContext);
		container = (ImageView) findViewById(R.id.container);
		tv_HotelName = (TextView) findViewById(R.id.premisesName);
		tv_GuestName = (TextView) findViewById(R.id.guestName);
		textureView = (TextureView) findViewById(R.id.textureView);
		tv_Welcome = (TextView)findViewById(R.id.welcome);
		tv_currentTime = (TextClock) findViewById(R.id.time);
		tv_currentTimeLable = (TextView) findViewById(R.id.timeLable);
		tv_currentDate = (TextView) findViewById(R.id.date);
		img_weatherIcon = (ImageView) findViewById(R.id.weather_icon);
		tv_temperature = (TextView) findViewById(R.id.temperature);
		tv_messageTitle = (TextView) findViewById(R.id.message_title);
		mAnimationArrow = (ImageView) findViewById(R.id.animation_arrow);
		mArrowsLyt = (LinearLayout) findViewById(R.id.arrow_icons);

		mWeatherRefreshHelper = new WeatherRefreshHelper(mContext,img_weatherIcon,tv_temperature);
		tv_Welcome.setText(mContext.getString(org.droidtv.ui.strings.R.string.HTV_WI_DASHBOARD_GREETING_WELCOME));
	  //img_weatherIcon.setImageResource(R.drawable.ic_unknown_day);
	}

	private void initSettingsConfig(){
		if(mConfigureHelper != null){
			mPremisesNameConfigure = mConfigureHelper.getConfigurePremisesName();
			mGuestNameConfigure = mConfigureHelper.getConfigureGuestName();
			mWelcomeConfigure = mConfigureHelper.getConfigureWelcome();
			mDateConfigure = mConfigureHelper.getConfigureDate();
			mTimeConfigure = mConfigureHelper.getConfigureTime();
			mWeatherConfigure = mConfigureHelper.getConfigureWeather();
			mPhilipsDefConfigure = mConfigureHelper.getConfigurePhilipsDefault();
		}
	}

	private void setMessageContent() {
		tv_HotelName.setText(mWelcomeDataManager.getPremisesName());
		if(tv_Welcome.isShown()){
			tv_GuestName.setText(mMsgHelper.getGuestName(mPms));
		}else{
			tv_GuestName.setText(mMsgHelper.getGuestName(mPms).trim());
		}

	}

	/* start -- use imageview to play the animation of fading */
	private void imagesPlayWithFading(ImageView iv, Drawable[] images) {
		Log.v(TAG, "imagesPlay Transition");
		TransitionDrawable td = new TransitionDrawable(images);
		iv.setImageDrawable(td);
		td.startTransition(1000);//1 second
	}

	private void playImages(boolean isInit) {
		Log.v(TAG, "id: " + curimageid);
		if (isInit) {
			container.setImageResource(defaultimages[0]);
		} else {
			int size = defaultimages.length;
			if (curimageid > size - 1 && size > 2) {
				curimageid = curimageid % size;
			}
			int nextimageid = curimageid + 1;
			if (nextimageid > size - 1) {
				nextimageid = nextimageid % size;
			}
			Drawable[] images = { getResources().getDrawable(defaultimages[curimageid]),
					getResources().getDrawable(defaultimages[nextimageid]) };
			imagesPlayWithFading(container, images);
			curimageid++;
		}

		{
			Message nextimage = mImgHandler.obtainMessage();
			nextimage.what = NEXT_IMAGE;
			nextimage.arg1 = curimageid;
			mImgHandler.sendMessageDelayed(nextimage, INTERNAL_TIME);
		}
	}
	/* end-- use imageview to play the animation of fading */

    private void playLocalMediaResource() {
    	int playRes = mConfigureHelper.getConfigurePhilipsDefault();
		/*
		 * config the view to play
		 * video: textureView
		 * image: imageview
		 */
		if (playRes > PLAY_LOCAL_PICTURES) {
			textureView.setVisibility(View.VISIBLE);
			container.setVisibility(View.GONE);
		} else {
			container.setVisibility(View.VISIBLE);
			textureView.setVisibility(View.GONE);
		}
    	switch (playRes) {
			case PLAY_LOCAL_PICTURES:
//				playLocalPictures();

				playImages(true);
				Log.d(TAG, "playLocalMediaResource():PLAY_LOCAL_PICTURES");
				break;

			case PLAY_LOCAL_VIDEO1:
				mVideoPlayer = new WelcomeMediaPlayer(mContext,textureView, R.raw.welcome_video1);
				Log.d(TAG, "playLocalMediaResource():PLAY_LOCAL_VIDEO1");
				break;

			case PLAY_LOCAL_VIDEO2:
				mVideoPlayer = new WelcomeMediaPlayer(mContext,textureView, R.raw.welcome_video2);
				Log.d(TAG, "playLocalMediaResource():PLAY_LOCAL_VIDEO2");
				break;

			default:
//				playLocalPictures();
				playImages(true);
				break;
		}
	}

	private class ImagePlayerHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int what  = msg.what;
			switch (what) {
			case PLAY_NEXT:
				int imageid = msg.arg1;
				Log.v(TAG, "imageid: " + imageid);
				mImagePlayer.playBitmap(getBitmap(defaultimages[imageid]));
				playNextImage();
				break;

			case NEXT_IMAGE:
				int id = msg.arg1;
				Log.v(TAG, "NEXT_IMAGE id: " + id);
				playImages(false);
				break;

			default:
				break;
			}
		}
    }

    private void playMediaResource(boolean playMode){
    	if (playMode) {
    		//play custom pictures and videos from clone resource
		} else {
			//play default pictures and videos from application local
			playLocalMediaResource();
		}
    }

	private void playNextImage() {
		curPlayImage++;
		curPlayImage = curPlayImage%6;
		Message next = mImgHandler.obtainMessage();
		next.what = PLAY_NEXT;
		next.arg1 = curPlayImage;
		mImgHandler.sendMessageDelayed(next, INTERNAL_TIME);
	}

	private void playDefaultImage(WelcomeImagePlayer wip, int[] object) {
		// TODO Auto-generated method stub
		int length = object.length;
		for (int i = 0; i < length; i++) {
		}
	}

	private Bitmap getBitmap(int resid) {
		//from drawable
		Bitmap result = null;
		//this will cause RuntimeException: Canvas: trying to use a recycled bitmap
		/*BitmapDrawable temp = (BitmapDrawable) getResources().getDrawable(R.drawable.welcome_image1, null);
		result = temp.getBitmap();*/
		result = BitmapFactory.decodeResource(getResources(), resid);
		return result;
	}

	private void displaySettingsConfigure(){
		if (mPremisesNameConfigure) {
			tv_HotelName.setVisibility(View.VISIBLE);
		} else {
			tv_HotelName.setVisibility(View.INVISIBLE);
		}

		if (mGuestNameConfigure) {
			tv_GuestName.setVisibility(View.VISIBLE);
		} else {
			tv_GuestName.setVisibility(View.INVISIBLE);
		}

		if (mWelcomeConfigure) {
			tv_Welcome.setVisibility(View.VISIBLE);
		} else {
			tv_Welcome.setVisibility(View.GONE);
		}

		if (mDateConfigure) {
			tv_currentDate.setVisibility(View.VISIBLE);
		} else {
			tv_currentDate.setVisibility(View.INVISIBLE);
		}

		if (mTimeConfigure) {
			tv_currentTime.setVisibility(View.VISIBLE);
			tv_currentTimeLable.setVisibility(View.VISIBLE);
		} else {
			tv_currentTime.setVisibility(View.INVISIBLE);
			tv_currentTimeLable.setVisibility(View.INVISIBLE);
		}

/*		if (mWeatherConfigure) { the method setWeatherData() to do
			img_weatherIcon.setVisibility(View.VISIBLE);
			tv_temperature.setVisibility(View.VISIBLE);
		} else {
			img_weatherIcon.setVisibility(View.INVISIBLE);
			tv_temperature.setVisibility(View.INVISIBLE);
		}*/

		if(DEBUG){
			Log.d(TAG, "mPremisesNameConfigure: " + mPremisesNameConfigure);
			Log.d(TAG, "mGuestNameConfigure: " + mGuestNameConfigure);
			Log.d(TAG, "mWelcomeConfigure: " + mWelcomeConfigure);
			Log.d(TAG, "mDateConfigure: " + mDateConfigure);
			Log.d(TAG, "mTimeConfigure: " + mTimeConfigure);
			Log.d(TAG, "mWeatherConfigure: " + mWeatherConfigure);
		}
	}

	private void showWelcomeContent(){
		displaySettingsConfigure();
    	setMessageContent();
    	setWelcomeDateTime();
    	setWeatherData();
	}

	private void setWeatherData() {
		boolean status =  mWelcomeDataManager.getPbsWeatherFeature();
		if(status && mWeatherConfigure){
			img_weatherIcon.setImageResource(mWelcomeDataManager.getWeatherIcon());
		//	img_weatherIcon.setBackgroundResource(mWelcomeDataManager.getWeatherIcon());
			tv_temperature.setText(mWelcomeDataManager.getCurrentTemperature());
			img_weatherIcon.setVisibility(View.VISIBLE);
			tv_temperature.setVisibility(View.VISIBLE);
		}else{
			img_weatherIcon.setVisibility(View.INVISIBLE);
			tv_temperature.setVisibility(View.INVISIBLE);
		}
	}
	/*private void setWelcomeBgImage() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		mDstRect = new Rect();
		mSrcRect = new Rect();
		Bitmap bgbitmap = getBitmap();
		textureView.setSurfaceTextureListener(new SurfaceTextureListener() {

			@Override
			public void onSurfaceTextureUpdated(SurfaceTexture surface) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
				// TODO Auto-generated method stub
				if (bgbitmap != null && !bgbitmap.isRecycled()) {
					bgbitmap.recycle();
				}
				return true;
			}

			@Override
			public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
				// TODO Auto-generated method stub
				drawBitmap(textureView, bgbitmap);
			}
		});

	}
	private boolean drawBitmap(TextureView ttv, Bitmap bm) {
		// TODO Auto-generated method stub
		boolean temp = false;
		if (ttv == null) {
			Log.v(TAG, "drawBitmap:TextureView is null");
			return temp;
		}
		if (bm == null) {
			Log.v(TAG, "drawBitmap:Bitmap is null");
			return temp;
		}
		Canvas canvas = ttv.lockCanvas(new Rect(ttv.getLeft(), ttv.getTop(), ttv.getWidth(), ttv.getHeight()));
		if (canvas == null) {
			Log.v(TAG, "drawBitmap:Canvas is null");
			return temp;
		}
		canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		mSrcRect.set(0, 0, bm.getWidth(), bm.getHeight());
		mDstRect.set(0, 0, ttv.getWidth(), bm.getHeight()*ttv.getWidth()/bm.getWidth());
		canvas.drawBitmap(bm, mSrcRect, mDstRect, mPaint);
		ttv.unlockCanvasAndPost(canvas);

		return temp;
	}*/

	private void playLocalPictures(){
		curBitmap = getBitmap(R.drawable.welcome_image1);
		if (curBitmap != null) {
			mImagePlayer = new WelcomeImagePlayer(textureView, curBitmap);
			curPlayImage = 0;
		}
		if (mImagePlayer!= null) {
//			playDefaultImage(mImagePlayer, int[] images);
			playNextImage();
		}
	}

	private void setWelcomeVideos(){
		String[] temp = {video00};
		if (temp != null) {
		//	mMyMediaPlayer = new WelcomeMediaPlayer(textureView, temp);
			mVideoPlayer = new WelcomeMediaPlayer(mContext,textureView, R.raw.welcome_video1);
		}
	}

	private void setWelcomeDateTime()
	{
		WelcomeUtils utils = new WelcomeUtils();
		utils.setCurrentTime(tv_currentTime,tv_currentTimeLable,mTimeConfigure);
		utils.setCurrentDate(tv_currentDate);
	}

	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
//		textureView.setRotation(80.f);
		if (mVideoPlayer != null ) mVideoPlayer.play();
		Log.d(TAG, "---onDreamingStarted()---");
	}

	@Override
	public void onDreamingStopped() {
		super.onDreamingStopped();
		Log.d(TAG, "---onDreamingStopped()---");
		/*if (mVideoPlayer != null ) mVideoPlayer.stop();
		if (mVideoPlayer != null ) mVideoPlayer.release();
		if (mImagePlayer != null) mImagePlayer.stop();*/
		if (mImgHandler != null) {
			mImgHandler.removeCallbacksAndMessages(null);
			mImgHandler = null;
		}
//		System.exit(0);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
	Log.d(TAG, "WelcomeDreamService:onConfigurationChanged()");
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mVideoPlayer = null;
		mImagePlayer = null;
		mImgHandler = null;
		Log.d(TAG, "---onDetachedFromWindow()---");
	//	mMsgHelper.unRegisterCallback(mContext,mPms,this);
	}

	  @Override//PR:TF518PHIEUMTK07-6481,Flicker seen while Welcome screen (Video 1/Video 2) is removed with RC_Back key.
	  public boolean dispatchKeyEvent(KeyEvent event) {
		  Log.d(TAG, "dispatchKeyEvent(KeyEvent event):textureRelease()");
		  if (mVideoPlayer != null ) mVideoPlayer.stop();
		  if (mVideoPlayer != null ) mVideoPlayer.release(); //PR:TF518PHIEUMTK07-6619,TV can not play the the Welcome screen (Video 1/Video 2) on invoking second time on jTIF source.
		  if (mImagePlayer != null) mImagePlayer.stop();
		  if (mVideoPlayer != null) mVideoPlayer.textureRelease();
		  System.exit(0);
	  	return super.dispatchKeyEvent(event);
	  }

	 private void startArrowAnimation() {
		 if (isRighToleft()) {
	         float rotation = 180;
	         mAnimationArrow.setRotation(rotation);
	     }

        mAnimationToArrows = AnimationUtils.loadAnimation(mContext, R.anim.cast_indicator_anim);
        mAnimationArrow.startAnimation(mAnimationToArrows);
	 }

   private boolean isRighToleft() {
        Configuration config = getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

//
//	@Override
//	public void onPrepared(MediaPlayer mp) {
//		// TODO Auto-generated method stub
//		try {
//            if (mp != null) {
//                mp.start();
//            }
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        }
//
//	}
//
//	@Override
//	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//		// TODO Auto-generated method stub
//		this.mTexture = surface;//textureView.getSurfaceTexture();
//		playVideo();
//	}
//
//	@Override
//	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//		// TODO Auto-generated method stub
////		this.mTexture = surface;
//	}
//
//	@Override
//	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//		// TODO Auto-generated method stub
//		Log.v("EAGLE","onSurfaceTextureDestroyed");
//
//		return false;
//	}
//
//	@Override
//	public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//		// TODO Auto-generated method stub
//
//	}
//
//	private void playVideo() {
//        if (mediaPlayer == null) {
//            surface = new Surface(mTexture);
//            initMediaPlayer();
//        }
//    }
//
//    private void initMediaPlayer() {
//        mediaPlayer = new MediaPlayer();
//        try {
//            mediaPlayer.setDataSource(videoPath);
//            mediaPlayer.setSurface(surface);
//            mediaPlayer.prepareAsync();
//            mediaPlayer.setOnPreparedListener(this);
//            mediaPlayer.setLooping(true);
//        } catch (IllegalArgumentException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        } catch (SecurityException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        } catch (IllegalStateException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//    }

 
}
