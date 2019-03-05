package org.droidtv.weather;

import org.droidtv.welcome.R;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import org.droidtv.welcome.data.manager.WelcomeDataManager;
import org.droidtv.welcome.data.manager.WelcomeDataManager.WeatherInfoDataListener;
import org.droidtv.welcome.data.manager.WelcomeDataManager.WeatherSettingsListener;


public class WeatherRefreshHelper implements WeatherInfoDataListener, WeatherSettingsListener {

    private Context mContext;
    private ImageView img_weatherIcon;
    private TextView tv_temperature;
    private UiThreadHandler mUiThreadHandler;
    private WelcomeDataManager mWelcomeDataManager;
    private String TAG = "WeatherRefreshHelper";

    public WeatherRefreshHelper(Context context,ImageView icon,TextView tv) {
        mContext = context;
        img_weatherIcon = icon;
        tv_temperature = tv;
    	init(context);
	}

    private void init(Context context) {
        initView();
        mWelcomeDataManager = getWelcomeDataManagerInstance();
        mWelcomeDataManager.registerWeatherInfoDataListener(this);
        mWelcomeDataManager.registerWeatherSettingsListener(this);
        mUiThreadHandler = new UiThreadHandler(this);
    }

    private void initView() {
    	/*img_weatherIcon = (ImageView) (mContext.getResources().findViewById(R.id.weather_icon));
		tv_temperature = (TextView) (mContext.getResources().findViewById(R.id.temperature));*/
      /*  if (mWelcomeDataManager.isWeatherEnabled()) {
        	img_weatherIcon.setVisibility(View.VISIBLE);
        	tv_temperature.setVisibility(View.VISIBLE);
        } else {
        	img_weatherIcon.setVisibility(View.INVISIBLE);
        	tv_temperature.setVisibility(View.INVISIBLE);
        }
        setTemperatureText();*/
	}

    private WelcomeDataManager getWelcomeDataManagerInstance(){
    	if(mWelcomeDataManager != null){
    		return mWelcomeDataManager;
    	}
    	mWelcomeDataManager = WelcomeDataManager.getInstance();
    	return mWelcomeDataManager;
    }

    @Override
    public void onWeatherInfoDataReceived() {
        Message message = Message.obtain(mUiThreadHandler, UiThreadHandler.MSG_WHAT_WEATHER_INFO_DATA_RECEIVED);
        message.sendToTarget();
        Log.d(TAG,"WeatherRefreshHelper:onWeatherInfoDataReceived()");
    }

    @Override
    public void onWeatherInfoDataReceived(int value) {
        Message message = Message.obtain(mUiThreadHandler, UiThreadHandler.MSG_WHAT_WEATHER_INFO_DATA_RECEIVED);
        message.arg1 = value;
        message.sendToTarget();
        Log.d(TAG,"WeatherRefreshHelper:onWeatherInfoDataReceived(int value)");
    }

    @Override
    public void onWeatherSettingsChanged() {
    	  mUiThreadHandler.sendEmptyMessage(UiThreadHandler.MSG_WHAT_WEATHER_SETTING_CHANGED);
    }

    @Override
    public void onWeatherSettingsChanged(int value) {
        Message message = Message.obtain(mUiThreadHandler, UiThreadHandler.MSG_WHAT_WEATHER_SETTING_CHANGED);
        message.arg1 = value;
        message.sendToTarget();

    }

    private void setTemperatureText() {
    	img_weatherIcon.setImageResource(mWelcomeDataManager.getWeatherIcon());
		tv_temperature.setText(mWelcomeDataManager.getCurrentTemperature());
    }

    private void onWeatherVisibilitySettingChanged() {
        if (mWelcomeDataManager.isWeatherEnabled()) {
        	img_weatherIcon.setVisibility(View.VISIBLE);
        	tv_temperature.setVisibility(View.VISIBLE);
        } else {
        	img_weatherIcon.setVisibility(View.INVISIBLE);
        	tv_temperature.setVisibility(View.INVISIBLE);
        }
    }

    private static class UiThreadHandler extends Handler {
        WeakReference<WeatherRefreshHelper> mWeatherMenuItemRef;
        private static final int MSG_WHAT_WEATHER_INFO_DATA_RECEIVED = 101;
        private static final int MSG_WHAT_WEATHER_SETTING_CHANGED = 102;
        private String TAG = "WeatherRefreshHelper";
        private UiThreadHandler(WeatherRefreshHelper weatherMenuItem) {
            super();
            mWeatherMenuItemRef = new WeakReference<>(weatherMenuItem);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_WHAT_WEATHER_INFO_DATA_RECEIVED) {
            	WeatherRefreshHelper weatherMenuItem = mWeatherMenuItemRef.get();
                if (weatherMenuItem != null) {
                    weatherMenuItem.setTemperatureText();
            		Log.d(TAG,"WeatherRefreshHelper-->UiThreadHandler:setTemperatureText()");
                }
                return;
            }

            if (msg.what == MSG_WHAT_WEATHER_SETTING_CHANGED) {
            	WeatherRefreshHelper weatherMenuItem = mWeatherMenuItemRef.get();
                if (weatherMenuItem != null) {
                    weatherMenuItem.onWeatherVisibilitySettingChanged();
                	Log.d(TAG,"WeatherRefreshHelper-->UiThreadHandler:onWeatherVisibilitySettingChanged()");
                }
                return;
            }
        }
    }
}
