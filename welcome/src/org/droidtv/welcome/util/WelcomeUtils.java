package org.droidtv.welcome.util;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import org.droidtv.welcome.R;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Calendar;

import org.droidtv.welcome.data.manager.WelcomeDataManager;

public class WelcomeUtils {

	private WelcomeDataManager mWelcomeDataManager;
  	private static final String CLOCK_FORMAT_AM_PM = "hh:mm";
  	private static final String CLOCK_FORMAT_24 = "HH:mm";
  	private String TAG = this.getClass().getSimpleName();
	public WelcomeUtils() {
		mWelcomeDataManager = WelcomeDataManager.getInstance();
	}
	
	@SuppressWarnings("null")
	public static String[] getAllVedios(String path) {
		String[] temp = null;
		File container = new File(path);
		FilenameFilter mff = new FilenameFilter() {
			@SuppressLint("DefaultLocale")
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				if (name != null && name.endsWith("mp4")) {
					return true;
				} else {
					return false;
				}
			}
		};
		if (container.exists()&&container.isDirectory()) {
			Log.v("EAGLE", container.getPath());
			String[] names = container.list(mff);
			int length = names.length;
			for (int i = 0; i < length; i++) {
				temp[i] = path + "/" + names[i];
			}
		}
		return temp;
	}
	
	public void setCurrentTime(TextClock tv_currentTime,TextView tv_currentTimeLable,boolean timeConfigure) {
		 long date = System.currentTimeMillis();
		 if (timeConfigure && mWelcomeDataManager != null && mWelcomeDataManager.isClockFormatAMPM()) {
			  tv_currentTime.setFormat12Hour(CLOCK_FORMAT_AM_PM);
              Calendar cal = Calendar.getInstance();
              cal.setTimeInMillis(date);
              if (cal.get(Calendar.AM_PM) == Calendar.AM) {
            	  tv_currentTimeLable.setText(org.droidtv.ui.strings.R.string.HTV_MAIN_AM);
              } else {
            	  tv_currentTimeLable.setText(org.droidtv.ui.strings.R.string.HTV_MAIN_PM);
              }
              tv_currentTimeLable.setVisibility(View.VISIBLE);
          } else {
        	  tv_currentTime.setFormat24Hour(CLOCK_FORMAT_24);
              tv_currentTimeLable.setVisibility(View.GONE);
          }
		} 
	
	public void setCurrentDate(TextView tv_currentDate){
		Calendar mCalendar = Calendar.getInstance();
		long timeInMillis = System.currentTimeMillis();
		Log.d(TAG,"timeInMillis = " + timeInMillis);
		mCalendar.setTimeInMillis(timeInMillis);
		CharSequence currentDate = DateFormat.format("EEEE, MMMM d", mCalendar);
		tv_currentDate.setText(currentDate);
	}
	
}
