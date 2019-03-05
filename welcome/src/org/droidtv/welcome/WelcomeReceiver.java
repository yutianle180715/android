package org.droidtv.welcome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author daemon.yu
 * start service when clone start
 *
 */
public class WelcomeReceiver extends BroadcastReceiver {
	private final static String TAG = "WelcomeReceiver";
	private final static String CLONESERVICE_CLONE_STATE_STARTED = "org.droidtv.intent.action.clone.state.started";
	private final static String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (CLONESERVICE_CLONE_STATE_STARTED.equals(action)) {
			Log.v(TAG, "action: "+ action);
			context.startService(new Intent(context, WelcomeCloneService.class));
		} 
	}

}
