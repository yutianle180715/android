package org.droidtv.welcome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.util.Log;

import org.droidtv.welcome.R;
import org.droidtv.welcome.data.manager.WelcomeDataManager;
import org.droidtv.welcome.data.manager.WelcomeDataManager.WelcomeConfigurationResetListener;
import org.droidtv.welcome.R.id;
import org.droidtv.welcome.R.layout;
import org.droidtv.welcome.configsettings.ui.OptionsMenu.IOptionsMenuCallBack;
import org.droidtv.welcome.configsettings.ui.BackgroundImageColorFilterNode;
import org.droidtv.welcome.configsettings.ui.ListOptionsNode;
import org.droidtv.welcome.configsettings.ui.ListOptionsNode.ListOptionsNodeListener;
/*import org.droidtv.welcome.configsettings.ui.MainBackgroundThumbnailBrowserNode;*/
import org.droidtv.welcome.configsettings.ui.OptionsManager;
import org.droidtv.welcome.configsettings.ui.OptionsManager.MenuInvisibleCallBack;
import org.droidtv.welcome.configsettings.ui.SidePanelBackgroundColorNode;
import org.droidtv.welcome.configsettings.ui.OptionsManager.IOptionsNodeStateCallBack;
import org.droidtv.welcome.configsettings.ui.OptionsManager.OptionsListener2;
import org.droidtv.welcome.configsettings.ui.OptionsNode;
import org.droidtv.welcome.configsettings.ui.SidePanelBackgroundColorNode;
import org.droidtv.welcome.configsettings.ui.SidePanelHighlightedTextColorNode;
/*import org.droidtv.welcome.configsettings.ui.ThumbnailBrowserNode;*/
import org.droidtv.welcome.configsettings.ui.SidePanelNonHighlightedTextColorNode;
import org.droidtv.welcome.util.Constants;
import org.droidtv.ui.tvwidget2k15.dialog.ModalDialog;
import org.droidtv.ui.tvwidget2k15.dialog.ModalDialogFooterButtonProp;
import org.droidtv.ui.tvwidget2k15.dialog.ModalDialogInterface;


public class EditWelcomeMenu implements WelcomeDataManager.WelcomeConfigurationResetListener {
    private OptionsManager mOptionsManager;
    private OptionsListener mOptionsListener;
    private ListOptionsNodeCallback mListOptionsNodeCallback;
    private WelcomeDataManager mWelcomeDataManager;
    private Context mContext;
/*    private MainBackgroundThumbnailBrowserNode mMainBackgroundThumbnailBrowserNode;*/
/*    private HotelLogoThumbnailBrowserNode mHotelLogoThumbnailBrowserNode;*/
    private SidePanelBackgroundColorNode mSidePanelBackgroundColorNode;
    private SidePanelHighlightedTextColorNode mSidePanelHighlightedTextColorNode;
    private SidePanelNonHighlightedTextColorNode mSidePanelNonHighlightedTextColorNode;
/*    private BackgroundImageColorFilterNode mBackgroundImageColorFilterNode;*/
    private SharedPreferences mDefaultSharedPreferences;
    private SharedPreferences.Editor mDefaultSharedPreferencesEditor;
    private boolean mMainBackgroundEnabledDefaultValue;
    private boolean mShowAccountIconDefaultValue;
    private boolean mCommonSettingDefaultValue;
    private int mSourceDefaultValue; //= 101;
    private final boolean mIsMainBackgroundEnabledAtSessionStart;
    private String TAG = this.getClass().getSimpleName();

    public EditWelcomeMenu(Context context, MenuInvisibleCallBack micb) {
        mContext = context;
        mOptionsManager = new OptionsManager(mContext);
        mOptionsManager.setMenuInvisibleCallBack(micb);
        mWelcomeDataManager = WelcomeDataManager.getInstance();
        mDefaultSharedPreferences = mWelcomeDataManager.getDefaultSharedPreferences();
        mDefaultSharedPreferencesEditor = mDefaultSharedPreferences.edit();
        mMainBackgroundEnabledDefaultValue = mContext.getResources().getBoolean(R.bool.enable_main_background);
        mIsMainBackgroundEnabledAtSessionStart = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_MAIN_BACKGROUND_ENABLED,
                mMainBackgroundEnabledDefaultValue);
        mShowAccountIconDefaultValue = mContext.getResources().getBoolean(R.bool.enable_show_icon);
        mCommonSettingDefaultValue = mContext.getResources().getBoolean(R.bool.common_default_setting);//common_default_setting:true
        mSourceDefaultValue = mContext.getResources().getInteger(R.integer.source_default_setting);
        setupOptionsManager();
    }

    public OptionsManager getOptionsManager() {
        return mOptionsManager;
    }

    public void show() {
        mOptionsManager.show();
    }

    private void setupOptionsManager() {
        mOptionsManager.enableDiming(false);

        mOptionsListener = new OptionsListener();
        mOptionsManager.setOptionsListener(mOptionsListener);
        mOptionsManager.setOptionsMenuListner(mOptionsListener);
        mOptionsManager.setOptionsNodeListener(mOptionsListener);
        mOptionsManager.setOptionsLayout(org.droidtv.welcome.R.layout.edit_welcome_layout);

        mWelcomeDataManager.addConfigurationResetListener(this);
        mListOptionsNodeCallback = new ListOptionsNodeCallback();

//        ListOptionsNode showAccountIconSettingListNode = (ListOptionsNode) mOptionsManager.findOptionsNodeById(R.id.show_account_icon_setting);
//        showAccountIconSettingListNode.setListListener(mListOptionsNodeCallback);
        ListOptionsNode premisesNameSettingListNode = (ListOptionsNode) mOptionsManager.findOptionsNodeById(R.id.premises_name_setting);
        premisesNameSettingListNode.setListListener(mListOptionsNodeCallback);
        ListOptionsNode guestNameSettingListNode = (ListOptionsNode) mOptionsManager.findOptionsNodeById(R.id.guest_name_setting);
        guestNameSettingListNode.setListListener(mListOptionsNodeCallback);
        ListOptionsNode welcomeSettingListNode = (ListOptionsNode) mOptionsManager.findOptionsNodeById(R.id.welcome_setting);
        welcomeSettingListNode.setListListener(mListOptionsNodeCallback);
        ListOptionsNode dateSettingListNode = (ListOptionsNode) mOptionsManager.findOptionsNodeById(R.id.date_setting);
        dateSettingListNode.setListListener(mListOptionsNodeCallback);
        ListOptionsNode timeSettingListNode = (ListOptionsNode) mOptionsManager.findOptionsNodeById(R.id.time_setting);
        timeSettingListNode.setListListener(mListOptionsNodeCallback);
        ListOptionsNode weatherSettingListNode = (ListOptionsNode) mOptionsManager.findOptionsNodeById(R.id.weather_setting);
        weatherSettingListNode.setListListener(mListOptionsNodeCallback);

        ListOptionsNode philipsDefaultSettingListNode = (ListOptionsNode) mOptionsManager.findOptionsNodeById(R.id.use_philips_default);
        philipsDefaultSettingListNode.setListListener(mListOptionsNodeCallback);

//        ListOptionsNode mainBackgroundEnabledSettingListNode = (ListOptionsNode) mOptionsManager.findOptionsNodeById(R.id.user_interface_main_background_enabled_setting);
//        mainBackgroundEnabledSettingListNode.setListListener(mListOptionsNodeCallback);

//        mSidePanelBackgroundColorNode = (SidePanelBackgroundColorNode) mOptionsManager.findOptionsNodeById(R.id.side_panel_background_color);
//        mSidePanelHighlightedTextColorNode = (SidePanelHighlightedTextColorNode) mOptionsManager.findOptionsNodeById(R.id.side_panel_highlighted_text_color);
//        mSidePanelNonHighlightedTextColorNode = (SidePanelNonHighlightedTextColorNode) mOptionsManager.findOptionsNodeById(R.id.side_panel_non_highlighted_text_color);
   //   mBackgroundImageColorFilterNode = (BackgroundImageColorFilterNode) mOptionsManager.findOptionsNodeById(R.id.user_interface_main_background_color_filter);
    }
    
    class ListOptionsNodeCallback implements ListOptionsNodeListener {

        @Override
        public void onListItemSelected(int nodeResId, int dataId,
                                       AdapterView<?> parent, View view, int position, long id) {
        }

        @Override
        public void onListNothingSelected(int nodeResId, AdapterView<?> parent) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean onListItemLongClick(int nodeResId, int dataId, AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onListItemClick(int nodeResId, int dataId,
                                    AdapterView<?> parent, View view, int position, long id) {
           /* if (nodeResId == R.id.user_interface_main_background_enabled_setting) {
                boolean mainBackgroundEnabledOldValue = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_MAIN_BACKGROUND_ENABLED,
                        mMainBackgroundEnabledDefaultValue);

                boolean enableMainBackground = dataId == 102;

                if (enableMainBackground != mainBackgroundEnabledOldValue) {
                    mDefaultSharedPreferencesEditor.putBoolean(Constants.PREF_KEY_MAIN_BACKGROUND_ENABLED, enableMainBackground);
                    mDefaultSharedPreferencesEditor.apply();

                    if (enableMainBackground) {
                    //  mWelcomeDataManager.applyMainBackground();
                    } else {
                        mWelcomeDataManager.clearBackground();
                    }
                }

                return;
            }

            if (nodeResId == R.id.show_account_icon_setting) {
                boolean showAccountIconOldValue = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_SHOW_ACCOUNT_ICON,
                        mShowAccountIconDefaultValue);

                boolean showAccountIcon = dataId == 102;

                if (showAccountIcon != showAccountIconOldValue) {
                    mDefaultSharedPreferencesEditor.putBoolean(Constants.PREF_KEY_SHOW_ACCOUNT_ICON, showAccountIcon);
                    mDefaultSharedPreferencesEditor.apply();

                    mWelcomeDataManager.showAccountIcon(showAccountIcon);//update when change item's value
                }
                return;
            }*/
            if (nodeResId == R.id.premises_name_setting) {
                boolean olsStatus = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_PREMISES_NAME_SETTING,
                		mCommonSettingDefaultValue);

                boolean isOn = dataId == 102;

                if (isOn != olsStatus) {
                    mDefaultSharedPreferencesEditor.putBoolean(Constants.PREF_KEY_PREMISES_NAME_SETTING, isOn);
                    mDefaultSharedPreferencesEditor.apply();

                }
                return;
            }
            if (nodeResId == R.id.guest_name_setting) {
                boolean olsStatus = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_GUEST_NAME_SETTING,
                		mCommonSettingDefaultValue);

                boolean isOn = dataId == 102;

                if (isOn != olsStatus) {
                    mDefaultSharedPreferencesEditor.putBoolean(Constants.PREF_KEY_GUEST_NAME_SETTING, isOn);
                    mDefaultSharedPreferencesEditor.apply();

                }
                return;
            }
            if (nodeResId == R.id.welcome_setting) {
                boolean olsStatus = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_WELCOME_SETTING,
                		mCommonSettingDefaultValue);

                boolean isOn = dataId == 102;

                if (isOn != olsStatus) {
                    mDefaultSharedPreferencesEditor.putBoolean(Constants.PREF_KEY_WELCOME_SETTING, isOn);
                    mDefaultSharedPreferencesEditor.apply();

                }
                return;
            }
            if (nodeResId == R.id.date_setting) {
                boolean olsStatus = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_DATE_SETTING,
                		mCommonSettingDefaultValue);

                boolean isOn = dataId == 102;

                if (isOn != olsStatus) {
                    mDefaultSharedPreferencesEditor.putBoolean(Constants.PREF_KEY_DATE_SETTING, isOn);
                    mDefaultSharedPreferencesEditor.apply();

                }
                return;
            }
            if (nodeResId == R.id.time_setting) {
                boolean olsStatus = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_TIME_SETTING,
                		mCommonSettingDefaultValue);

                boolean isOn = dataId == 102;

                if (isOn != olsStatus) {
                    mDefaultSharedPreferencesEditor.putBoolean(Constants.PREF_KEY_TIME_SETTING, isOn);
                    mDefaultSharedPreferencesEditor.apply();

                }
                return;
            }
            if (nodeResId == R.id.weather_setting) {
                boolean olsStatus = mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_WEATHER_SETTING,
                		mCommonSettingDefaultValue);

                boolean isOn = dataId == 102;

                if (isOn != olsStatus) {
                    mDefaultSharedPreferencesEditor.putBoolean(Constants.PREF_KEY_WEATHER_SETTING, isOn);
                    mDefaultSharedPreferencesEditor.apply();

                }
                return;
            }
            
            if (nodeResId == R.id.use_philips_default) {
                int olsStatus = mDefaultSharedPreferences.getInt(Constants.PREF_KEY_DEFAULT_SOURCE_SETTING,
                		mSourceDefaultValue);//= 101;

                if (dataId != olsStatus) {
                    mDefaultSharedPreferencesEditor.putInt(Constants.PREF_KEY_DEFAULT_SOURCE_SETTING, dataId);
                    mDefaultSharedPreferencesEditor.apply();
                    Log.d(TAG, "use_philips_default: dataId = " + dataId);
                }
                return;
            }
        }

        @Override
        public boolean isListItemAvailable(int nodeResId, int dataId) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean isListItemControllable(int nodeResId, int dataId) {
            return true;
        }

        @Override
        public int getItemSelectionWhenFocused(int nodeResId) {
            /*if (nodeResId == R.id.show_account_icon_setting) {
                return mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_SHOW_ACCOUNT_ICON, mShowAccountIconDefaultValue)
                        ? 102 : 101;
            }

            if (nodeResId == R.id.user_interface_main_background_enabled_setting) {
                return mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_MAIN_BACKGROUND_ENABLED, mMainBackgroundEnabledDefaultValue)
                        ? 102 : 101;
            }*/
            if (nodeResId == R.id.premises_name_setting) {
                return mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_PREMISES_NAME_SETTING, mCommonSettingDefaultValue)
                        ? 102 : 101;
            }
            if (nodeResId == R.id.guest_name_setting) {
                return mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_GUEST_NAME_SETTING, mCommonSettingDefaultValue)
                        ? 102 : 101;
            }
            if (nodeResId == R.id.welcome_setting) {
                return mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_WELCOME_SETTING, mCommonSettingDefaultValue)
                        ? 102 : 101;
            }
            if (nodeResId == R.id.date_setting) {
                return mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_DATE_SETTING, mCommonSettingDefaultValue)
                        ? 102 : 101;
            }
            if (nodeResId == R.id.time_setting) {
                return mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_TIME_SETTING, mCommonSettingDefaultValue)
                        ? 102 : 101;
            }
            if (nodeResId == R.id.weather_setting) {
                return mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_WEATHER_SETTING, mCommonSettingDefaultValue)
                        ? 102 : 101;
            }

            if (nodeResId == R.id.use_philips_default) {
                return mDefaultSharedPreferences.getInt(Constants.PREF_KEY_DEFAULT_SOURCE_SETTING, mSourceDefaultValue);
            }

            return 0;
        }
    }

    @Override
    public void revertSessionChanges() {
        if (mSidePanelBackgroundColorNode != null) {
            mSidePanelBackgroundColorNode.setCurrentColor(mWelcomeDataManager.getSidePanelBackgroundColor());
        }

        if (mSidePanelNonHighlightedTextColorNode != null) {
            mSidePanelNonHighlightedTextColorNode.setCurrentColor(mWelcomeDataManager.getSidePanelNonHighlightedTextColor());
        }

        if (mSidePanelHighlightedTextColorNode != null) {
            mSidePanelHighlightedTextColorNode.setCurrentColor(mWelcomeDataManager.getSidePanelHighlightedTextColor());
        }

        /* if (mBackgroundImageColorFilterNode != null) {
            mBackgroundImageColorFilterNode.setCurrentColor(mWelcomeDataManager.getMainBackgroundColorFilter());
        }

         if (mMainBackgroundThumbnailBrowserNode != null) {
            mMainBackgroundThumbnailBrowserNode.resetConfigurationSessionChanges();
        }

      	if (mHotelLogoThumbnailBrowserNode != null) {
            mHotelLogoThumbnailBrowserNode.resetConfigurationSessionChanges();
        }*/
    }

   @Override
    public void resetSettings() {
        // do nothing as all settings are already reset by now and updates have been performed by revertSessionChanges() from DashboardDataManager.
    }
   
    class OptionsListener implements OptionsListener2, IOptionsMenuCallBack, IOptionsNodeStateCallBack {

        @Override
        public boolean canSelectNodeItem(int nodeResId) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public void onNodeItemSelected(int nodeResId) {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean canMoveToNextNode(int nodeResId) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean getAvailabilityOfNode(int nodeResId) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean getControllablityOfNode(int nodeResId) {
            /*if (nodeResId == R.id.user_interface_main_background_image) {
                return mDefaultSharedPreferences.getBoolean(Constants.PREF_KEY_MAIN_BACKGROUND_ENABLED, mMainBackgroundEnabledDefaultValue);
            }*/
//            if (nodeResId == R.id.revert_session_changes) {
//                return hasConfigurationSessionChanges();
//            }
//            if (nodeResId == R.id.reset_settings) {
//                return hasSavedConfiguration();
//            }
            return true;
        }

        @Override
        public void onNodeItemClicked(int nodeResId) {
           /* switch (nodeResId) {
                case R.id.recommended_apps:
                  //  startRecommendedAppsActivity();
                    break;
                case R.id.app_recommendations:
                  //  startAppRecommendationsActivity();
                    break;
                case R.id.reset_settings:
                  //  showResetSettingsConfirmationDialog();
                    break;
                case R.id.revert_session_changes:
                   // showRevertSessionChangesConfirmationDialog();
                    break;
            }*/
        }

        @Override
        public void onMovingBackwardFromNode(int nodeResId) {
            // TODO Auto-generated method stub
        }

        @Override
        public int getSelectionIndex() {
            return 0;
        }

        @Override
        public boolean dispatchUnhandledKeys(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
                mOptionsManager.hide();
                return true;
            }
            return false;
        }

        @Override
        public boolean onOptionsMenuDismissed() {
           /* if (mMainBackgroundThumbnailBrowserNode != null) {
                mMainBackgroundThumbnailBrowserNode.onDismissed();
            }
            if (mHotelLogoThumbnailBrowserNode != null) {
                mHotelLogoThumbnailBrowserNode.onDismissed();
            }*/
            if (mSidePanelBackgroundColorNode != null) {
                mSidePanelBackgroundColorNode.onDismissed();
            }
            if (mSidePanelHighlightedTextColorNode != null) {
                mSidePanelHighlightedTextColorNode.onDismissed();
            }
            if (mSidePanelNonHighlightedTextColorNode != null) {
                mSidePanelNonHighlightedTextColorNode.onDismissed();
            }
/*            if (mBackgroundImageColorFilterNode != null) {
                mBackgroundImageColorFilterNode.onDismissed();
            }*/

            mWelcomeDataManager.removeConfigurationResetListener(EditWelcomeMenu.this);

            return false;
        }

        @Override
        public void onOptionNodeEntered(OptionsNode node) {
           /* if (node.getId() == R.id.user_interface_main_background_image) {
                mMainBackgroundThumbnailBrowserNode = (MainBackgroundThumbnailBrowserNode) node;
                mMainBackgroundThumbnailBrowserNode.onNodeEntered();
            } else if (node.getId() == R.id.side_panel_hotel_logo) {
                mHotelLogoThumbnailBrowserNode = (HotelLogoThumbnailBrowserNode) node;
                mHotelLogoThumbnailBrowserNode.onNodeEntered();
            } else 
        	if (node.getId() == R.id.user_interface_side_panel) {
                mWelcomeDataManager.showSidePanel();
            }*/
        }

        @Override
        public void onOptionNodeExited(OptionsNode node) {
           /* if (node.getId() == R.id.user_interface_main_background_image ||
                    node.getId() == R.id.side_panel_hotel_logo) {
                ((ThumbnailBrowserNode) node).onNodeExited();
            } else
        	if (node.getId() == R.id.user_interface_side_panel) {
                mWelcomeDataManager.hideSidePanel();
            }*/
        }

 /*       private void startRecommendedAppsActivity() {
            Intent intent = new Intent(mContext, RecommendedAppsActivity.class);
            mContext.startActivity(intent);
        }

        private void startAppRecommendationsActivity() {
            Intent intent = new Intent(mContext, AppRecommendationsActivity.class);
            mContext.startActivity(intent);
        }
*/

/*        private void showResetSettingsConfirmationDialog() {
            final ModalDialog.Builder builder = new ModalDialog.Builder(mContext, ModalDialog.HEADING_TYPE_DEFAULT);
            builder.setHeading(mContext.getString(org.droidtv.ui.strings.R.string.HTV_DDB_ITEM_CONFIGURE_DASHBOARD), "");
            builder.setMessage(mContext.getString(org.droidtv.ui.strings.R.string.HTV_MAIN_RESET_SETTING));
            final ModalDialog modalDialog = builder.build();
            ModalDialogInterface.OnClickListener onCancelClickListener = new ModalDialogInterface.OnClickListener() {
                @Override
                public void onClick(ModalDialogInterface modalDialogInterface, int i) {
                    modalDialog.dismiss();
                }
            };
            ModalDialogInterface.OnClickListener onOkClickListener = new ModalDialogInterface.OnClickListener() {
                @Override
                public void onClick(ModalDialogInterface modalDialogInterface, int i) {
                    mWelcomeDataManager.resetSettings();
                }
            };
            builder.setButton(ModalDialog.BUTTON_RIGHT, new ModalDialogFooterButtonProp(true, mContext.getString(org.droidtv.ui.strings.R.string.HTV_THEME_OK), onOkClickListener));
            builder.setButton(ModalDialog.BUTTON_MID_RIGHT, new ModalDialogFooterButtonProp(true, mContext.getString(org.droidtv.ui.strings.R.string.HTV_THEME_CANCEL), onCancelClickListener));
            modalDialog.show();
        }*/

  /*      private void showRevertSessionChangesConfirmationDialog() {
            final ModalDialog.Builder builder = new ModalDialog.Builder(mContext, ModalDialog.HEADING_TYPE_DEFAULT);
            builder.setHeading(mContext.getString(org.droidtv.ui.strings.R.string.HTV_DDB_ITEM_CONFIGURE_DASHBOARD), "");
            builder.setMessage(mContext.getString(org.droidtv.ui.strings.R.string.HTV_MAIN_CANCEL_SETTING));
            final ModalDialog modalDialog = builder.build();
            ModalDialogInterface.OnClickListener onCancelClickListener = new ModalDialogInterface.OnClickListener() {
                @Override
                public void onClick(ModalDialogInterface modalDialogInterface, int i) {
                    modalDialog.dismiss();
                }
            };
            ModalDialogInterface.OnClickListener onOkClickListener = new ModalDialogInterface.OnClickListener() {
                @Override
                public void onClick(ModalDialogInterface modalDialogInterface, int i) {
                    mWelcomeDataManager.revertSessionChanges();
                }
            };
            builder.setButton(ModalDialog.BUTTON_RIGHT, new ModalDialogFooterButtonProp(true, mContext.getString(org.droidtv.ui.strings.R.string.HTV_THEME_OK), onOkClickListener));
            builder.setButton(ModalDialog.BUTTON_MID_RIGHT, new ModalDialogFooterButtonProp(true, mContext.getString(org.droidtv.ui.strings.R.string.HTV_THEME_CANCEL), onCancelClickListener));
            modalDialog.show();
        }
    }*/

    private boolean hasConfigurationSessionChanges() {
        if (mWelcomeDataManager.getSidePanelBackgroundColor() != mSidePanelBackgroundColorNode.getCurrentColor()) {
            return true;
        }
        if (mWelcomeDataManager.getSidePanelHighlightedTextColor() != mSidePanelHighlightedTextColorNode.getCurrentColor()) {
            return true;
        }
        if (mWelcomeDataManager.getSidePanelNonHighlightedTextColor() != mSidePanelNonHighlightedTextColorNode.getCurrentColor()) {
            return true;
        }
    /*    if (mWelcomeDataManager.getMainBackgroundColorFilter() != mBackgroundImageColorFilterNode.getCurrentColor()) {
            return true;
        }*/
        if (mWelcomeDataManager.isMainBackgroundEnabled() != mIsMainBackgroundEnabledAtSessionStart) {
            return true;
        }
    /*    if (mMainBackgroundThumbnailBrowserNode != null && mMainBackgroundThumbnailBrowserNode.hasConfigurationSessionChanges()) {
            return true;
        }
        if (mHotelLogoThumbnailBrowserNode != null && mHotelLogoThumbnailBrowserNode.hasConfigurationSessionChanges()) {
            return true;
        }*/

        return false;
    }

    private boolean hasSavedConfiguration() {
        return hasConfigurationSessionChanges() || mWelcomeDataManager.hasSavedConfiguration();
    }

  }
}