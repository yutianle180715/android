package org.droidtv.welcome;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import org.droidtv.welcome.util.Constants;
import org.droidtv.welcome.data.manager.WelcomeCloneDataManager;
import org.droidtv.welcome.data.manager.WelcomeDataManager;
/**
 * @author daemon.yu
 * this service is for welcome data clone
 * the lifecycle of the service: 
 * from	org.droidtv.intent.action.clone.state.started
 * to	org.droidtv.intent.action.clone.state.stopped
 */
public class WelcomeCloneService extends Service {
	private final static String TAG = "WelcomeCloneService";
	private final static String CLONESERVICE_CLONE_STATE_STOPPED = "org.droidtv.intent.action.clone.state.stopped";
	private WelcomeCloneDataManager mCloneDataManager = null;
	private SharedPreferences mDefaultSharedPreferences;
    private SharedPreferences.Editor mDefaultSharedPreferencesEditor;
    //boolean items
	private String[] spes0 = { Constants.PREF_KEY_PREMISES_NAME_SETTING, Constants.PREF_KEY_GUEST_NAME_SETTING,
			Constants.PREF_KEY_WELCOME_SETTING, Constants.PREF_KEY_DATE_SETTING, Constants.PREF_KEY_TIME_SETTING,
			Constants.PREF_KEY_WEATHER_SETTING };
	//interger items
	private String[] spes1 = { Constants.PREF_KEY_DEFAULT_SOURCE_SETTING };
	private int mSourceDefaultValue = 0;
	private boolean mCommonSettingDefaultValue = false;
	private BroadcastReceiver mBroadcastReceiver = null;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//init the sharepreference
		mDefaultSharedPreferences = WelcomeDataManager.getInstance().getDefaultSharedPreferences();
        mDefaultSharedPreferencesEditor = mDefaultSharedPreferences.edit();
        mCommonSettingDefaultValue = this.getResources().getBoolean(R.bool.common_default_setting);
        mSourceDefaultValue = this.getResources().getInteger(R.integer.source_default_setting);
        initSharePreferences();
        //prepare stop bc
		prepareBC();
		//prepare clone data manager
		mCloneDataManager = new WelcomeCloneDataManager(this);
		Log.v(TAG, "onCreate");
	}

	private void initSharePreferences() {
		// TODO Auto-generated method stub
		int length = spes0.length;
		for (int i = 0; i < length; i++) {
			if (mDefaultSharedPreferences.contains(spes0[i])) {
				continue;
			} else {
				mDefaultSharedPreferencesEditor.putBoolean(spes0[i], mCommonSettingDefaultValue);
				mDefaultSharedPreferencesEditor.apply();
			}
		}
		length = spes1.length;
		for (int j = 0; j < length; j++) {
			if (mDefaultSharedPreferences.contains(spes1[j])) {
				continue;
			} else {
				mDefaultSharedPreferencesEditor.putInt(spes1[j], mSourceDefaultValue);
				mDefaultSharedPreferencesEditor.apply();
			}
		}
	}

	private void prepareBC() {
		// TODO Auto-generated method stub
		mBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if (CLONESERVICE_CLONE_STATE_STOPPED.equals(action)) {
					Log.v(TAG, CLONESERVICE_CLONE_STATE_STOPPED);
					if (mCloneDataManager != null)
						mCloneDataManager.unregisterForCloneUpdates();
					WelcomeCloneService.this.stopSelf();
				}
			}
		};
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(CLONESERVICE_CLONE_STATE_STOPPED);
		WelcomeCloneService.this.registerReceiver(mBroadcastReceiver, mIntentFilter);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		cleanBC();
		//need to restart process.
		WelcomeDataManager.getInstance().applyCloneDataPrefs();
		Log.v(TAG, "onDestroy");
		super.onDestroy();
	}

	private void cleanBC() {
		// TODO Auto-generated method stub
		if (mBroadcastReceiver != null) {
			WelcomeCloneService.this.unregisterReceiver(mBroadcastReceiver);
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
