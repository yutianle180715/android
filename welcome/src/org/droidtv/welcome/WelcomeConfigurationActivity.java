package org.droidtv.welcome;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.droidtv.welcome.configsettings.ui.OptionsManager.MenuInvisibleCallBack;

public class WelcomeConfigurationActivity extends Activity implements MenuInvisibleCallBack {
	private static final String TAG = "WelcomeConfigurationActivity";
	private Context mContext;
	private EditWelcomeMenu mEditWelcomeMenu;
	
/*	public WelcomeConfigurationActivity(Context mContext) {
		super();
		
	}*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.mContext = this;
		mEditWelcomeMenu = new EditWelcomeMenu(mContext, this);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mEditWelcomeMenu.show();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onInvisible() {
		// TODO Auto-generated method stub
		WelcomeConfigurationActivity.this.finish();
	}

}
