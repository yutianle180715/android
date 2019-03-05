package org.droidtv.welcome;

import android.app.Application;
import android.content.res.Configuration;

import org.droidtv.welcome.data.manager.WelcomeDataManager;

public class WelcomeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        WelcomeDataManager.init(getApplicationContext());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        WelcomeDataManager.getInstance().onConfigurationChanged();
    }
}
