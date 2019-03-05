package org.droidtv.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.droidtv.welcome.util.Constants;

public class WeatherReceiver extends BroadcastReceiver {
	private String TAG = this.getClass().getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		if (Constants.INTENT_UPDATE_WEATHER_WIDGET.equals(intent.getAction()))
		{
			if(intent.getStringExtra("s_today_temperature") != null){
			   WeatherInfo.current_temperature = intent.getStringExtra("s_today_temperature");
			   Log.d(TAG, "WeatherReceiver: WeatherInfo.current_temperature = " + WeatherInfo.current_temperature);
			}else{
			   Log.d(TAG, "WeatherReceiver: WeatherInfo.current_temperature is null");
			}

			if(intent.getStringExtra("s_today_icon_small") != null){
				WeatherInfo.current_weather_icon = intent.getIntExtra("s_today_icon_small", 0);
				Log.d(TAG, "WeatherReceiver: WeatherInfo.current_weather_icon = " + WeatherInfo.current_weather_icon);
			}else{
				Log.d(TAG, "WeatherReceiver: WeatherInfo.current_weather_icon is null");
			}
		}

	}

}
