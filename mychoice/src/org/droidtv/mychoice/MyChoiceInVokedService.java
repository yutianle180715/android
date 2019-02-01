package org.droidtv.mychoice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.droidtv.htv.htvmychoice.IMychoiceControl;

public class MyChoiceInVokedService extends Service implements IMychoiceControl {
	public static final String TAG = "MyChoiceInVokedService";
	private IMyChoiceResultListener mListener = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return IMychoiceControl.Instance.asBinder(this);
	}

	@Override
	public void setResult(int result, int keycode) {
		Log.v(TAG, "Result: " + result + ", keycode: " + keycode);
		if (mListener != null) {
			mListener.onResult(result, keycode);
			Log.v(TAG, "mListener.onResult start;");
		}
	}

	@Override
	public void registerListenerCallback(IMyChoiceResultListener listener) {
		Log.d(TAG, "registerListenerCallback() called");
		this.mListener = listener;
	}

	@Override
	public void unregisterListenerCallback(IMyChoiceResultListener listener) {
		Log.d(TAG, "unregisterListenerCallback() called");
		if (mListener == listener) {
			this.mListener = null;
		}
	}

}
