package org.droidtv.welcome;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;

public class WelcomeDreamSettings extends Activity {
	private Preference mPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_settings);

	}


}
