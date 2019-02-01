package org.droidtv.mychoice.osds;

//import org.droidtv.mychoice.MyChoiceUtils;
import org.droidtv.tv.logger.ILogger;
import org.droidtv.tv.logger.ILogger.MyChoicePINValidation;
//import org.droidtv.tv.logger.ILogger.MyChoiceTV;

import org.droidtv.tv.media.TvURI;
import org.droidtv.tv.media.TvURI.Medium;
import org.droidtv.tv.media.TvURI.UriData;
import org.droidtv.tv.persistentstorage.TvSettingsDefinitions;
import org.droidtv.tv.persistentstorage.TvSettingsDefinitions.ChannelFilterConstants;
import org.droidtv.tv.provider.IChannelContract;
import org.droidtv.tv.provider.IFavoriteContract.IBaseFavoriteColumns;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.media.tv.TvContract.Channels;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.provider.SyncStateContract.Columns;
import android.util.Log;


public class MyChoiceLogger {

	private static final String LOGGER_TAG = "MyChoiceLogger";//MyChoiceLogger.class.getSimpleName();
	private static MyChoiceLogger _instance = null;
	private static Context mContext = null;
	protected static ILogger mBinder = null;

	private static ILogger.MyChoicePINValidation myChoicePIN = null;
	private static MyChoiceLogger mMyChoiceLogger = null;



	private MyChoiceLogger(Context context){
		Log.d(LOGGER_TAG, "MyChoiceLogger cunstructor called ");
		_instance = this;
		mContext = context;
	}

	public static MyChoiceLogger getInstance(Context mContext){
		if(mMyChoiceLogger == null){
			mMyChoiceLogger = new MyChoiceLogger(mContext);
		}
		Log.d(LOGGER_TAG, "getInstance called succesfully ..");
		return mMyChoiceLogger;
	}

	public static MyChoiceLogger getInstance(){
		return _instance;
	}

	public void bindToLoggingService() {

		Intent i = new Intent();
		i.setAction("org.droidtv.tv.intent.action.START_LOGGER");
		try{
			PackageManager mPackageManager = mContext.getPackageManager();
			ResolveInfo mResolveInfo = mPackageManager.resolveService(i, 0);
			if(mResolveInfo != null) {
				i.setClassName(mResolveInfo.serviceInfo.packageName, mResolveInfo.serviceInfo.name);
				boolean serviceBound =mContext.getApplicationContext().bindService(i, conn, Context.BIND_AUTO_CREATE);
				Log.d(LOGGER_TAG," in bindToNetvLoggerService  after binding call, servicebound="+serviceBound);
			}else {
				Log.d(LOGGER_TAG, "org.droidtv.tv.intent.action.START_LOGGER: not able to resolve ");
			}
		}catch(SecurityException e){
			Log.d(LOGGER_TAG, "Caught Security Exception : " + ""+e.getLocalizedMessage());
		}catch(Exception e){
			Log.d(LOGGER_TAG , "Caught exception :: Could bind to service : " +""+e.getLocalizedMessage());
		}
	}

	public ServiceConnection conn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(LOGGER_TAG," onServiceConnected callback received");
			mBinder = ILogger.Instance.asInterface(service);

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(LOGGER_TAG,"received onServicedisconnected for LoggingHelper, shut this service");
			mBinder = null;
		}
	};
    public static void logMyChoicePin(ILogger.MyChoicePINValidation pkg){
        if(mBinder != null){
            mBinder.Log(pkg);
        }else{
            Log.d(LOGGER_TAG,"Logging binder is NULL ");
        }
    }

}
