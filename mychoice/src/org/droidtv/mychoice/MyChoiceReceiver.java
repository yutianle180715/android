package org.droidtv.mychoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author daemon.yu
 * @Description
 */
public class MyChoiceReceiver extends BroadcastReceiver {
	//private final String INTENT_BOOT_COMPLETED	= "android.intent.action.BOOT_COMPLETED";
	private final String INTENT_BOOT_COMPLETED	= "android.intent.action.LOCKED_BOOT_COMPLETED";
	private final String INTENT_START_MYCHOCIE_SERVICE = "org.droidtv.mychoice.MyChoiceService";

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (INTENT_BOOT_COMPLETED.equals(action)) {
			Log.d("EAGLE","INTENT_BOOT_COMPLETED");
			context.startService(new Intent(context, MyChoiceService.class));
		}

	}

}
