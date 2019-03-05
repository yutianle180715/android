package org.droidtv.welcome.data.manager;

import org.droidtv.welcome.message.MessageHelper;
import org.droidtv.welcome.util.*;
import org.droidtv.welcome.configsettings.ui.*;
import android.content.Context;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import org.droidtv.welcome.util.*;
import org.droidtv.welcome.R;
import org.droidtv.welcome.util.Constants;
import org.droidtv.tv.persistentstorage.ITvSettingsManager;
import org.droidtv.tv.persistentstorage.ITvSettingsManager.ITvSettingsManagerNotify;
import org.droidtv.tv.persistentstorage.TvSettingsConstants;
import org.droidtv.tv.persistentstorage.TvSettingsDefinitions;
import org.droidtv.weather.WeatherInfo;
import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class WelcomeDataManager extends ContextualObject  {

	private String TAG = this.getClass().getSimpleName();
	private static WelcomeDataManager sWelcomeDataManager;
	private WeatherDataManager mWeatherDataManager;
	private final ITvSettingsManager mTvSettingsManager;
	private ConfigureHelper mConfigureHelper;
	private int mSelectedCountryConstant;
	private int mSelectedLanguageConstant;
//	private CloneDataManager mCloneDataManager;
	private Context mContext;
	private ArrayList<WeakReference<BackgroundImageChangeObserver>> mBackgroundImageChangeObserverRefs;
	private ArrayList<WeakReference<SidePanelTextColorChangeListener>> mSidePanelTextColorChangeListenerRefs;
	private ArrayList<WeakReference<WelcomeConfigurationResetListener>> mWelcomeConfigurationResetListenerRefs;
	private ArrayList<WeakReference<SidePanelListener>> mSidePanelListenerRefs;
	private SharedPreferences mDefaultSharedPreferences;
	private boolean mMediaScannerInProgress;
	private WeakReference<AccountIconListener> mAccountIconListenerRef;
	private boolean mCommonSettingDefaultValue;
	private int mAppsCountry;
	private boolean mIsAppCountryFilterEnabled;
	private boolean mIsProfessionalModeEnabled;
	private int mDashboardMode;
    private int mSourceDefaultValue; //= 101;
    private MessageHelper mMsgHelper;
    private final int PBSMGR_PROPERTY_FORCE_CUSTOM_NAME_ON = 1;
	private int[] property = {
			  TvSettingsConstants.PBSMGR_PROPERTY_PROFESSIONAL_MODE,
			  TvSettingsConstants.PBSMGR_PROPERTY_FEATURE_WEATHER_APP,
			  TvSettingsConstants.PBSMGR_PROPERTY_FORCE_CUSTOM_NAME
		  };
	
	protected WelcomeDataManager(Context context) {
		super(context);
        mContext = context;
        mCommonSettingDefaultValue = mContext.getResources().getBoolean(R.bool.common_default_setting);
        mSourceDefaultValue = mContext.getResources().getInteger(R.integer.source_default_setting);
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mMediaScannerInProgress = false;
        mBackgroundImageChangeObserverRefs = new ArrayList<>();
        mSidePanelTextColorChangeListenerRefs = new ArrayList<>();
        mWelcomeConfigurationResetListenerRefs = new ArrayList<>();
        mSidePanelListenerRefs = new ArrayList<>();
        mTvSettingsManager = ITvSettingsManager.Instance.getInterface();
        mMsgHelper = new MessageHelper(mContext);
	}

    public static WelcomeDataManager getInstance() {
        if (sWelcomeDataManager == null) {
            throw new IllegalStateException("WelcomeDataManager has not been set up yet!.Make sure to call WelcomeDataManager.init(context) before accessing an instance");
        }
        return sWelcomeDataManager;
    }

    public ConfigureHelper getConfigureHelper(){
    	if(mConfigureHelper == null){
    	 mConfigureHelper = new ConfigureHelper();
    	}
    	return mConfigureHelper;
    }

    public static void init(Context appContext) {
        if (sWelcomeDataManager != null) {
            return;
        }
        sWelcomeDataManager = new WelcomeDataManager(appContext);
        sWelcomeDataManager.initInternal();
//        sWelcomeDataManager.initCloneDataManager();
        sWelcomeDataManager.initWeatherDataManager();
    }

    private void initInternal() {
    	updateProfessionalModeEnabledState();
    //  mUiThreadHandler = new UiThreadHandler(this);
        registerFactoryResetReceiver();
        registerSettingsCallbacks();
    }

    /*private void initCloneDataManager() {
        mCloneDataManager = new CloneDataManager(getContext());
    }*/

    private void initWeatherDataManager() {
        mWeatherDataManager = new WeatherDataManager(getContext());
    }

    private ITvSettingsManager.ITvSettingsManagerNotifyWithValue mSettingsWithValueCallbacks = new ITvSettingsManager.ITvSettingsManagerNotifyWithValue() {
        @Override
        public void OnUpdateWithIntValue(int iProperty, int value) {

            if (iProperty == TvSettingsConstants.PBSMGR_PROPERTY_PROFESSIONAL_MODE) {
                updateProfessionalModeEnabledState();
                if (mWeatherDataManager != null) {
                    mWeatherDataManager.professionalModeChanged();
                }
                return;
            }

            if (iProperty == TvSettingsConstants.PBSMGR_PROPERTY_FEATURE_WEATHER_APP) {
                if (mWeatherDataManager != null) {
                    mWeatherDataManager.onWeatherSettingsChanged(value);
                }
                return;
            }

            if (iProperty == TvSettingsConstants.PBSMGR_PROPERTY_FORCE_CUSTOM_NAME) {
                if (mWeatherDataManager != null) {
                    mWeatherDataManager.onWeatherInfoReceivedNotify(value);
                }
                return;
            }

        }
    };

    public void onConfigurationChanged() {
        /*mSourceDataManager.onConfigurationChanged();
        mChannelDataManager.onConfigurationChanged();
        mSmartInfoDataManager.onConfigurationChanged();*/
    }

    private void registerFactoryResetReceiver() {
       /* IntentFilter intentFilter = new IntentFilter(Constants.ACTION_FACTORY_RESET);
        getContext().registerReceiver(mEventsBroadcastReceiver, intentFilter);*/
    }

    public int getMainBackgroundColorFilter() {
        return mDefaultSharedPreferences.
                getInt(Constants.PREF_KEY_MAIN_BACKGROUND_COLOR_FILTER, getContext().getColor(R.color.main_background_default_color_filter));
    }

    public SharedPreferences getDefaultSharedPreferences() {
        return mDefaultSharedPreferences;
    }

    public interface BackgroundImageChangeObserver {
        void changeBackgroundImage(Bitmap bitmap);

        void changeBackgroundImage(int drawableResourceId);

        void changeBackgroundColorFilter(int color);

        void clearBackground();
    }

    public String getPremisesName() {
    	String premisesName = null;
    	String jediPremisesName = null;
    	String weatherPremisesName = null;
    	String defTVPremisesName = null;
    	defTVPremisesName = mTvSettingsManager.getString(TvSettingsConstants.PBSMGR_PROPERTY_PREMISES_NAME,0,"");

        if (mTvSettingsManager.getInt(TvSettingsConstants.PBSMGR_PROPERTY_FORCE_CUSTOM_NAME, 0, 0) == PBSMGR_PROPERTY_FORCE_CUSTOM_NAME_ON) {
        	jediPremisesName = mMsgHelper.getPremisesName();//form JEDI
        	Log.d(TAG,"JEDI provide jediPremisesName = " + jediPremisesName);
        	if(jediPremisesName == null){
        		jediPremisesName = defTVPremisesName;
        		Log.d(TAG,"jediPremisesName = null,TV NVM provide defTVPremisesName = " + defTVPremisesName);
        	}
        	return jediPremisesName ;
        }else{
        	 weatherPremisesName = mWeatherDataManager.getPremisesName();//form Weather Service when PBSMGR_PROPERTY_FORCE_CUSTOM_NAME is OFF
        	 Log.d(TAG,"Weather Service premisesName = " + weatherPremisesName);
        	 if(weatherPremisesName == null){
        		weatherPremisesName = defTVPremisesName;
             	Log.d(TAG,"weatherPremisesName = null,TV NVM provide defTVPremisesName = " + defTVPremisesName);
             }
        	return weatherPremisesName;
        }
    }

    public String getCurrentTemperature() {
        return mWeatherDataManager.getCurrentTemperature();
    }
    
    public int getWeatherIcon() {
        return mWeatherDataManager.getWeatherIcon();
    }

    public boolean getPbsWeatherFeature(){
    	return mWeatherDataManager.isWeatherEnabled();
    }

    public interface WeatherInfoDataListener {
        void onWeatherInfoDataReceived();

        void onWeatherInfoDataReceived(int value);
    }

    public interface WeatherSettingsListener {
        void onWeatherSettingsChanged();

        void onWeatherSettingsChanged(int value);
    }

    public interface WelcomeConfigurationResetListener {
        void revertSessionChanges();

        void resetSettings();
    }


    public void registerWeatherInfoDataListener(WeatherInfoDataListener listener) {
        mWeatherDataManager.registerWeatherInfoDataListener(listener);
    }

    public void unregisterWeatherInfoDataListener(WeatherInfoDataListener listener) {
        mWeatherDataManager.unregisterWeatherInfoDataListener(listener);
    }

    public void registerWeatherSettingsListener(WeatherSettingsListener listener) {
        mWeatherDataManager.registerWeatherSettingsListener(listener);
    }

    public void unregisterWeatherSettingsListener(WeatherSettingsListener listener) {
        mWeatherDataManager.unregisterWeatherSettingsListener(listener);
    }

    public boolean isWeatherEnabled() {
        return mWeatherDataManager.isWeatherEnabled();
    }

    public boolean isWeatherEnabled(int value) {
        return mWeatherDataManager.isWeatherEnabled(value);
    }

    public boolean isProfessionalModeEnabled() {
        return mIsProfessionalModeEnabled;
    }

    public interface ThumbnailDataListener {
        void onAvailableImagesFetched(Cursor cursor);
    }

    public interface ThumbnailBitmapFetchListener {
        void onThumbnailBitmapFetchComplete(long id, Bitmap bitmap);
    }

    public interface ImageFetchListener {
        void onImageFetchComplete(Bitmap bitmap);
    }

    public interface SidePanelTextColorChangeListener {
        void changeSidePanelHighlightedTextColor(int color);

        void changeSidePanelNonHighlightedTextColor(int color);
    }

    public interface AccountIconListener {
    	void showAccountIcon(boolean show);
    }

    public interface SidePanelListener {
        void changeSidePanelBackgroundColor(int color);

        void showSidePanel();

        void hideSidePanel();
    }

    interface CloneDataListener {
        void cloneDataApplied(boolean success);

        void cloneDataDeleted(boolean success);
    }

    interface CloneFileUriListener {
        void cloneInFileUriCreated(Pair<Uri, Uri> uri);

        void cloneOutFileUriCreated(Pair<Uri, Uri> uri);
    }

    public void applyCloneDataPrefs() {
        System.exit(0);
    }

    public int getSidePanelBackgroundColor() {
        return mDefaultSharedPreferences.getInt(Constants.PREF_KEY_SIDEPANEL_BACKGROUND_COLOR, getContext().getColor(R.color.side_panel_background_default));
    }

   public class ConfigureHelper{
	   public boolean getConfigurePremisesName(){
	    	boolean premisesName = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_PREMISES_NAME_SETTING, mCommonSettingDefaultValue);
	    	Log.d(TAG, "premisesName config status: " + premisesName);
	    	return premisesName;
	    }

	    public boolean getConfigureGuestName(){
	    	boolean guestName = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_GUEST_NAME_SETTING, mCommonSettingDefaultValue);
	    	Log.d(TAG, "guestName config status: " + guestName);
	    	return guestName;
	    }

	    public boolean getConfigureWelcome(){
	    	boolean welcome = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_WELCOME_SETTING, mCommonSettingDefaultValue);
	    	Log.d(TAG, "welcome config status: " + welcome);
	    	return welcome;
	    }

	    public boolean getConfigureDate(){
	    	boolean date = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_DATE_SETTING, mCommonSettingDefaultValue);
	    	Log.d(TAG, "date config status: " + date);
	    	return date;
	    }

	    public boolean getConfigureTime(){
	    	boolean time = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_TIME_SETTING, mCommonSettingDefaultValue);
	    	Log.d(TAG, "time config status: " + time);
	    	return time;
	    }

	    public boolean getConfigureWeather(){
	    	boolean weather = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_WEATHER_SETTING, mCommonSettingDefaultValue);
	    	Log.d(TAG, "weather config status: " + weather);
	    	return weather;
	    }

	    public int getConfigurePhilipsDefault(){
	    	int philipsDefault = mDefaultSharedPreferences.getInt(Constants.PREF_KEY_DEFAULT_SOURCE_SETTING, mSourceDefaultValue);
	    	Log.d(TAG, "PhilipsDefault config status: " + philipsDefault);
	    	return philipsDefault;
	    }

   }

    public ITvSettingsManager getTvSettingsManager() {
        return mTvSettingsManager;
    }

    public void hideSidePanel() {
        for (int i = 0; mSidePanelListenerRefs != null && i < mSidePanelListenerRefs.size(); i++) {
            WeakReference<SidePanelListener> listenerRef = mSidePanelListenerRefs.get(i);
            if (listenerRef == null) {
                continue;
            }

            SidePanelListener listener = listenerRef.get();
            if (listener != null) {
                listener.hideSidePanel();
            }
        }
    }

/*    public interface ImageFileFetchListener {
        void onImageFileFetched(ImageFile imageFile);
    }*/

  /*  public void setBackground(String filePath) {
        mImageDataManager.fetchImage(filePath, Constants.MAIN_BACKGROUND_WIDTH, Constants.MAIN_BACKGROUND_HEIGHT, new ImageFetchListener() {
        	@Override
        	public void onImageFetchComplete(Bitmap bitmap) {
                for (int i = 0; mBackgroundImageChangeObserverRefs != null && i < mBackgroundImageChangeObserverRefs.size(); i++) {
                    WeakReference<BackgroundImageChangeObserver> listenerRef = mBackgroundImageChangeObserverRefs.get(i);
                    if (listenerRef == null) {
                        continue;
                    }

                    BackgroundImageChangeObserver listener = listenerRef.get();
                    if (listener != null) {
                        listener.changeBackgroundImage(bitmap);
                    }
                }
            }
        });
    }

    public void setBackground(int drawableResourceId) {
        mImageDataManager.fetchImageFromResourceId(drawableResourceId, Constants.MAIN_BACKGROUND_WIDTH, Constants.MAIN_BACKGROUND_HEIGHT, new ImageFetchListener() {
            @Override
            public void onImageFetchComplete(Bitmap bitmap) {
                for (int i = 0; mBackgroundImageChangeObserverRefs != null && i < mBackgroundImageChangeObserverRefs.size(); i++) {
                    WeakReference<BackgroundImageChangeObserver> listenerRef = mBackgroundImageChangeObserverRefs.get(i);
                    if (listenerRef == null) {
                        continue;
                    }

                    BackgroundImageChangeObserver listener = listenerRef.get();
                    if (listener != null) {
                        listener.changeBackgroundImage(bitmap);
                    }
                }
            }
        });
    }*/

    public void setBackgroundColorFilter(int color) {
        for (int i = 0; mBackgroundImageChangeObserverRefs != null && i < mBackgroundImageChangeObserverRefs.size(); i++) {
            WeakReference<BackgroundImageChangeObserver> listenerRef = mBackgroundImageChangeObserverRefs.get(i);
            if (listenerRef == null) {
                continue;
            }

            BackgroundImageChangeObserver listener = listenerRef.get();
            if (listener != null) {
                listener.changeBackgroundColorFilter(color);
            }
        }
    }

    public void clearBackground() {
        for (int i = 0; mBackgroundImageChangeObserverRefs != null && i < mBackgroundImageChangeObserverRefs.size(); i++) {
            WeakReference<BackgroundImageChangeObserver> listenerRef = mBackgroundImageChangeObserverRefs.get(i);
            if (listenerRef == null) {
                continue;
            }

            BackgroundImageChangeObserver listener = listenerRef.get();
            if (listener != null) {
                listener.clearBackground();
            }
        }
    }

    public void saveMainBackgroundColorFilter(int color) {
        mDefaultSharedPreferences.edit().putInt(Constants.PREF_KEY_MAIN_BACKGROUND_COLOR_FILTER, color).apply();
    }

    public void saveSidePanelNonHighlightedTextColor(int color) {
        mDefaultSharedPreferences.edit().putInt(Constants.PREF_KEY_SIDEPANEL_NON_HIGHLIGHTED_TEXT_COLOR, color).apply();
     //   mTvSettingsManager.putInt(TvSettingsConstants.PBSMGR_PROPERTY_DDB_NON_HIGHLIGHTED_TEXT_COLOR, 0, color);
    }

    public void changeSidePanelHighlightedTextColor(int color) {
        for (int i = 0; mSidePanelTextColorChangeListenerRefs != null && i < mSidePanelTextColorChangeListenerRefs.size(); i++) {
            WeakReference<SidePanelTextColorChangeListener> listenerRef = mSidePanelTextColorChangeListenerRefs.get(i);
            if (listenerRef == null) {
                continue;
            }

            SidePanelTextColorChangeListener listener = listenerRef.get();
            if (listener != null) {
                listener.changeSidePanelHighlightedTextColor(color);
            }
        }
    }

    public void changeSidePanelNonHighlightedTextColor(int color) {
        for (int i = 0; mSidePanelTextColorChangeListenerRefs != null && i < mSidePanelTextColorChangeListenerRefs.size(); i++) {
            WeakReference<SidePanelTextColorChangeListener> listenerRef = mSidePanelTextColorChangeListenerRefs.get(i);
            if (listenerRef == null) {
                continue;
            }

            SidePanelTextColorChangeListener listener = listenerRef.get();
            if (listener != null) {
                listener.changeSidePanelNonHighlightedTextColor(color);
            }
        }
    }

    public void addConfigurationResetListener(WelcomeConfigurationResetListener dashboardConfigurationResetListener) {
        mWelcomeConfigurationResetListenerRefs.add(new WeakReference<WelcomeConfigurationResetListener>(dashboardConfigurationResetListener));
    }

    public void applyMainBackground() {/*
        fetchSavedImageFile(getContext().getFilesDir() + Constants.PATH_MAIN_BACKGROUND, new ImageFileFetchListener() {
            @Override
            public void onImageFileFetched(ImageFile imageFile) {
                if (imageFile == null || imageFile.getFile() == null || !imageFile.getFile().exists()) {
                    setBackground(R.drawable.default_main_background);
                } else {
                    setBackground(imageFile.getFile().getPath());
                }
            }
        });
    */}

    public void showAccountIcon(boolean show) {
        AccountIconListener listener = null;
        if (mAccountIconListenerRef == null) {
            return;
        }
        listener = mAccountIconListenerRef.get();
        if (listener != null) {
            listener.showAccountIcon(show);
        }
    }

    public void removeConfigurationResetListener(WelcomeConfigurationResetListener welcomeConfigurationResetListener) {
        if (mWelcomeConfigurationResetListenerRefs == null) {
            return;
        }

        for (int i = 0; i < mWelcomeConfigurationResetListenerRefs.size(); i++) {
            WeakReference<WelcomeConfigurationResetListener> listenerRef = mWelcomeConfigurationResetListenerRefs.get(i);
            if (listenerRef == null) {
                continue;
            }
            WelcomeConfigurationResetListener listener = listenerRef.get();
            if (listener != null && listener.equals(welcomeConfigurationResetListener)) {
                mWelcomeConfigurationResetListenerRefs.remove(listenerRef);
                return;
            }
        }
    }

    public void showSidePanel() {
        for (int i = 0; mSidePanelListenerRefs != null && i < mSidePanelListenerRefs.size(); i++) {
            WeakReference<SidePanelListener> listenerRef = mSidePanelListenerRefs.get(i);
            if (listenerRef == null) {
                continue;
            }

            SidePanelListener listener = listenerRef.get();
            if (listener != null) {
                listener.showSidePanel();
            }
        }
    }

    public int getSidePanelNonHighlightedTextColor() {
        return mDefaultSharedPreferences.getInt(Constants.PREF_KEY_SIDEPANEL_NON_HIGHLIGHTED_TEXT_COLOR, getContext().getColor(R.color.side_panel_non_highlighted_text_color_default));
    }

    public int getSidePanelHighlightedTextColor() {
        return mDefaultSharedPreferences.getInt(Constants.PREF_KEY_SIDEPANEL_HIGHLIGHTED_TEXT_COLOR, getContext().getColor(R.color.side_panel_highlighted_text_color_default));
    }

    public boolean isMainBackgroundEnabled() {
        return mDefaultSharedPreferences.
        getBoolean(Constants.PREF_KEY_MAIN_BACKGROUND_ENABLED, getContext().getResources().getBoolean(R.bool.enable_main_background));
    }

    public boolean hasSavedConfiguration() {
        return hasSavedConfigurationPreferences() || hasSavedImages();
    }

    private boolean hasSavedConfigurationPreferences() {
        int sidePanelBackgroundDefaultColor = getContext().getColor(R.color.side_panel_background_default);
        int sidePanelHighlightedDefaultColor = getContext().getColor(R.color.side_panel_highlighted_text_color_default);
        int sidePanelNonHighlightedDefaultColor = getContext().getColor(R.color.side_panel_non_highlighted_text_color_default);
        int mainBackgroundDefaultColorFilter = getContext().getColor(R.color.main_background_default_color_filter);
        boolean mainBackgroundEnabledDefaultValue = getContext().getResources().getBoolean(R.bool.enable_main_background);

        if (getSidePanelBackgroundColor() != sidePanelBackgroundDefaultColor) {
            return true;
        }
        if (getSidePanelHighlightedTextColor() != sidePanelHighlightedDefaultColor) {
            return true;
        }
        if (getSidePanelNonHighlightedTextColor() != sidePanelNonHighlightedDefaultColor) {
            return true;
        }
        if (getMainBackgroundColorFilter() != mainBackgroundDefaultColorFilter) {
            return true;
        }
        if (isMainBackgroundEnabled() != mainBackgroundEnabledDefaultValue) {
            return true;
        }

        return false;
    }

    private boolean hasSavedImages() {
        File imagesDirectory = new File(getContext().getFilesDir() + Constants.PATH_IMAGES);
        if (!imagesDirectory.exists()) {
            return false;
        }
        File[] childDirectories = imagesDirectory.listFiles();
        if (childDirectories == null || childDirectories.length == 0) {
            return false;
        }

        for (int i = 0; i < childDirectories.length; i++) {
            File[] images = childDirectories[i].listFiles();
            if (images != null && images.length > 0) {
                return true;
            }
        }
        return false;
    }

    private void updateProfessionalModeEnabledState() {
        mIsProfessionalModeEnabled = mTvSettingsManager.getInt(TvSettingsConstants.PBSMGR_PROPERTY_PROFESSIONAL_MODE, 0, 0) == 1;
    }

    private void registerSettingsCallbacks() {

        mTvSettingsManager.SetCallBackWithValues(mSettingsWithValueCallbacks,property);
    }
    
    public boolean isClockFormatAMPM() {
		return mTvSettingsManager.getInt(TvSettingsConstants.PBSMGR_PROPERTY_DNT_CLOCK_FORMAT, 0, 0) == TvSettingsDefinitions.PbsClockFormatConstants.PBSMGR_CLOCK_FORMAT_AMPM;
	} 
}

