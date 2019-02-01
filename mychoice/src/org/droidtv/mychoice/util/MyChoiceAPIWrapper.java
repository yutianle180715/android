package org.droidtv.mychoice.util;

import org.droidtv.mychoice.ChannelData;
//ben add for mychoice , remark this line import org.droidtv.mychoice.ManagerHandler;
import org.droidtv.mychoice.R;
import org.droidtv.mychoice.MyChoiceActivity;

import org.droidtv.htv.provider.HtvContract.HtvChannelList;
/*Ben add for MyChoice*/
import org.droidtv.htv.provider.HtvContract.HtvMyChoiceChannelList;
import org.droidtv.htv.provider.HtvContract.HtvChannelSetting;
import org.droidtv.htv.provider.HtvContract.HtvThemeTvSetting;
import org.droidtv.htv.provider.HtvContract;

import android.app.Activity;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.util.DisplayMetrics;
import android.view.Display;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.lang.Integer;
import java.util.Comparator;
import java.util.Collections;
import android.content.Context;
import java.util.Iterator;
import java.util.List;

import android.view.Window;
import android.widget.CheckedTextView;
import android.graphics.Color;
import android.content.ContentValues;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AbsListView;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.graphics.Bitmap;
import java.io.InputStream;
import android.net.Uri;
import java.io.FileNotFoundException;
import android.graphics.BitmapFactory;
import org.droidtv.tv.blur.BlurSurface;
import org.droidtv.ui.tvwidget2k15.blur.BlurSurfaceView;
import org.droidtv.ui.tvwidget2k15.blur.BlurSurfaceView.BlurHost;
import org.droidtv.ui.tvwidget2k15.blur.IBlurCallback;
import android.media.tv.TvContract.Channels;
import android.media.tv.TvContract;

import org.droidtv.tv.persistentstorage.TvSettingsDefinitions;
import org.droidtv.tv.persistentstorage.TvSettingsConstants;
import org.droidtv.tv.persistentstorage.ITvSettingsManager;

public class MyChoiceAPIWrapper {
	private static final String TAG = "MyChoiceAPIWrapper";

	private static MyChoiceAPIWrapper singletonInstance = null;
	private Context themeTVActivityCtx;
	public DisplayMetrics DM = null;
	public static int BlurMode = 2;

	public final String[] mAllThemeProjection = { HtvChannelSetting._ID, HtvChannelSetting.COLUMN_TTV1_ORDER,
			HtvChannelSetting.COLUMN_TTV2_ORDER, HtvChannelSetting.COLUMN_TTV3_ORDER,
			HtvChannelSetting.COLUMN_TTV4_ORDER, HtvChannelSetting.COLUMN_TTV5_ORDER,
			HtvChannelSetting.COLUMN_TTV6_ORDER, HtvChannelSetting.COLUMN_TTV7_ORDER,
			HtvChannelSetting.COLUMN_TTV8_ORDER, HtvChannelSetting.COLUMN_TTV9_ORDER,
			HtvChannelSetting.COLUMN_TTV10_ORDER, };
	public final String[] mAllThemeSettingProjection = { HtvThemeTvSetting._ID, HtvThemeTvSetting.COLUMN_NAME,
			HtvThemeTvSetting.COLUMN_MAPPED_THEME_FIELD, HtvThemeTvSetting.COLUMN_LOGO, };

	private MyChoiceAPIWrapper() {
	}

	public synchronized static MyChoiceAPIWrapper getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new MyChoiceAPIWrapper();
		}
		return singletonInstance;
	}

	public void setApplicationContext(Context context) {
//		if (context instanceof MyChoiceActivity) {
			themeTVActivityCtx =  context;
			DM = themeTVActivityCtx.getResources().getDisplayMetrics();
//		}
	}
//ben add for mychoice ,remark next body
/*
	public void sendMessages(Message msg) {
		if (msg.what < 0xD0) {
			themeTVActivityCtx.mMainHandler.sendMessage(msg);
		} else {
			((ManagerHandler) themeTVActivityCtx.getDialogHandler()).sendMessages(msg);
		}
	}
*/

	public int setThemeDataToDB(int dispNum, int [] mychoiceArray) {

		Cursor cursor = null;
		String[] mProjection = { HtvContract.HtvChannelSetting._ID,
				HtvContract.HtvChannelSetting.COLUMN_DISPLAY_NUMBER };

		try {
			cursor = themeTVActivityCtx.getContentResolver().query(HtvContract.HtvChannelSetting.CONTENT_URI,
					mProjection, null, null, null, null);
			int rows_num = cursor.getCount();

			ContentValues tVal = new ContentValues();

			tVal.put(HtvContract.HtvChannelSetting.COLUMN_FREEPKG, mychoiceArray[0]);
			tVal.put(HtvContract.HtvChannelSetting.COLUMN_PAYPKG1, mychoiceArray[1]);
			tVal.put(HtvContract.HtvChannelSetting.COLUMN_PAYPKG2, mychoiceArray[2]);


			themeTVActivityCtx.getContentResolver().update(HtvContract.HtvChannelSetting.CONTENT_URI, tVal,
					new StringBuilder(HtvContract.HtvChannelSetting.COLUMN_DISPLAY_NUMBER).append("=")
							.append(dispNum).toString(),
					null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			cursor = null;
		}
		//ben add for mychoice

		/*Cursor cursor_lock = null;
		String[] lockProjection = { TvContract.Channels.COLUMN_DISPLAY_NUMBER, TvContract.Channels.COLUMN_LOCKED };
		ContentValues lockVal = new ContentValues();

		try {
			cursor_lock = themeTVActivityCtx.getContentResolver().query(TvContract.Channels.CONTENT_URI,
			lockProjection, null, null, null, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.d(TAG, "cursor_lock cursor");
		}

		if (mychoiceArray[0]==1)//free
			lockVal.put(TvContract.Channels.COLUMN_LOCKED, 0);
		else //pay1/pay2
			lockVal.put(TvContract.Channels.COLUMN_LOCKED, 1);

		themeTVActivityCtx.getContentResolver().update(TvContract.Channels.CONTENT_URI, lockVal,
				new StringBuilder(TvContract.Channels.COLUMN_DISPLAY_NUMBER).append("=")
						.append(dispNum).toString(),null);*/

		return 0;
	}
//ben add for mychoice , reamark next body
/*
	public int deleteOneThemeFromDB(int themeField) {
		final String field = Integer.valueOf(themeField).toString();
		Thread thread = new Thread() {
			@Override
			public void run() {
				String[] args = { field };
				try {
					themeTVActivityCtx.getContentResolver().delete(HtvThemeTvSetting.CONTENT_URI,
							new StringBuilder(HtvThemeTvSetting.COLUMN_MAPPED_THEME_FIELD).append("=?").toString(),
							args);
					themeTVActivityCtx.getContentResolver().notifyChange(HtvThemeTvSetting.CONTENT_URI, null);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					this.interrupt();
				}
			}
		};
		thread.start();
		return 0;
	}

	public int renameOneThemeFromDB(int themeField, int themeLogo, String name) {
		final String newThemeName = name;
		final String newLogo = Integer.valueOf(themeLogo).toString();
		final String field = Integer.valueOf(themeField).toString();
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					String where = new StringBuilder(HtvThemeTvSetting.COLUMN_MAPPED_THEME_FIELD).append("=")
							.append(field).toString();
					ContentValues tVal1 = new ContentValues();
					tVal1.put(HtvThemeTvSetting.COLUMN_NAME, newThemeName);
					themeTVActivityCtx.getContentResolver().update(HtvThemeTvSetting.CONTENT_URI, tVal1, where, null);
					ContentValues tVal2 = new ContentValues();
					tVal2.put(HtvThemeTvSetting.COLUMN_LOGO, newLogo);
					themeTVActivityCtx.getContentResolver().update(HtvThemeTvSetting.CONTENT_URI, tVal2, where, null);
					themeTVActivityCtx.getContentResolver().notifyChange(HtvThemeTvSetting.CONTENT_URI, null);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					this.interrupt();
				}
			}
		};
		thread.start();
		return 0;
	}

	public void cloneThemeContent(int fromField, int toField) {
		Cursor themeCursor = null;
		String[] duplicateProjection = { mAllThemeProjection[fromField] };
		themeCursor = themeTVActivityCtx.getContentResolver().query(HtvChannelSetting.CONTENT_URI, duplicateProjection,
				null, null, null);

		int content = 0;
		int count = themeCursor.getCount();
		themeCursor.moveToFirst();
		for (int i = 0; i < count; ++i) {
			content = themeCursor.getInt(0);
			setThemeDataToDB(i, toField, content);
			themeCursor.moveToNext();
		}

		if (themeCursor != null)
			themeCursor.close();
		themeCursor = null;
	}
*/
	public int getAllThemeSetting(int[] fields, String[] names, int[] logos) {
		Cursor themeCursor = null;
		String sortByField = HtvThemeTvSetting.COLUMN_MAPPED_THEME_FIELD;

		themeCursor = themeTVActivityCtx.getContentResolver().query(HtvThemeTvSetting.CONTENT_URI,
				mAllThemeSettingProjection, null, null, sortByField);

		int ThemeNum = themeCursor.getCount();

		if ((fields != null) || (names != null) || (logos != null)) {
			themeCursor.moveToFirst();
			for (int i = 0; i < ThemeNum; ++i) {
				if (names != null)
					names[i] = themeCursor.getString(1);
				if (fields != null)
					fields[i] = themeCursor.getInt(2);
				if (logos != null)
					logos[i] = themeCursor.getInt(3);
				themeCursor.moveToNext();
			}
		}

		if (themeCursor != null)
			themeCursor.close();
		themeCursor = null;

		return ThemeNum;
	}

	public int getThemeSettingByField(String[] names, int[] logos, int[] filteredField) {

		int thmeNumOfFilter = filteredField.length;
		String[] themeName = new String[10];
		int[] themeLogo = new int[10];
		int[] themeField = new int[10];

		getAllThemeSetting(themeField, themeName, themeLogo);

		int p = 0;
		for (int i = 0; i < 10; ++i) {
			for (int j = 0; j < thmeNumOfFilter; ++j) {
				if (themeField[i] == filteredField[j]) {
					if (names != null)
						names[p] = themeName[i];
					if (logos != null)
						logos[p] = themeLogo[i];
					++p;
					break;
				}
			}
		}

		return p;
	}

	public int getThemeSmallestNumber() {
		int ThemeSmallestNumber = 1;
		Cursor theme_cursor = null;
		String[] mProjection3 = { HtvThemeTvSetting.COLUMN_MAPPED_THEME_FIELD };
		String sortOrder2 = HtvThemeTvSetting.COLUMN_MAPPED_THEME_FIELD;

		theme_cursor = themeTVActivityCtx.getContentResolver().query(HtvThemeTvSetting.CONTENT_URI, mProjection3, null,
				null, sortOrder2);

		int theme_number = theme_cursor.getCount();
		theme_cursor.moveToFirst();

		for (int i = 0; i < theme_number; ++i) {
			if (theme_cursor.getInt(0) == ThemeSmallestNumber) {
				ThemeSmallestNumber++;
			}
			theme_cursor.moveToNext();
		}

		if (theme_cursor != null)
			theme_cursor.close();
		theme_cursor = null;

		return ThemeSmallestNumber;
	}

	public int getThemeActive(int displaynumber, int theme) {
		//ben add for mychoice , reamark next body
		/*
		String[] mProjection = { HtvChannelSetting.COLUMN_DISPLAY_NUMBER, HtvChannelSetting.COLUMN_TTV1_ORDER,
				HtvChannelSetting.COLUMN_TTV2_ORDER, HtvChannelSetting.COLUMN_TTV3_ORDER,
				HtvChannelSetting.COLUMN_TTV4_ORDER, HtvChannelSetting.COLUMN_TTV5_ORDER,
				HtvChannelSetting.COLUMN_TTV6_ORDER, HtvChannelSetting.COLUMN_TTV7_ORDER,
				HtvChannelSetting.COLUMN_TTV8_ORDER, HtvChannelSetting.COLUMN_TTV9_ORDER,
				HtvChannelSetting.COLUMN_TTV10_ORDER, HtvChannelSetting._ID };
		*/
		//ben add for mychoice
		String[] mProjection = { HtvChannelSetting.COLUMN_DISPLAY_NUMBER, HtvChannelSetting.COLUMN_FREEPKG,
				HtvChannelSetting.COLUMN_PAYPKG1, HtvChannelSetting.COLUMN_PAYPKG2, HtvChannelSetting._ID };

		Cursor cursor = themeTVActivityCtx.getContentResolver().query(HtvChannelSetting.CONTENT_URI, mProjection,
				HtvChannelSetting.COLUMN_DISPLAY_NUMBER + "=?", new String[] { Integer.toString(displaynumber) }, null,
				null);
		cursor.moveToFirst();

		return cursor.getInt(theme);
	}
/* ben remark 20151104
	public ArrayList<ChannelData> getThemeChannelList() {
		Bitmap logo = null;
		Bitmap logotemp = null;
		BitmapDrawable temp = null;
		InputStream logoInput = null;
*/
		//ben add for mychoice , reamark next body
		/*
		String[] mProjection = { HtvChannelSetting.COLUMN_DISPLAY_NUMBER, HtvChannelSetting.COLUMN_TTV1_ORDER,
				HtvChannelSetting.COLUMN_TTV2_ORDER, HtvChannelSetting.COLUMN_TTV3_ORDER,
				HtvChannelSetting.COLUMN_TTV4_ORDER, HtvChannelSetting.COLUMN_TTV5_ORDER,
				HtvChannelSetting.COLUMN_TTV6_ORDER, HtvChannelSetting.COLUMN_TTV7_ORDER,
				HtvChannelSetting.COLUMN_TTV8_ORDER, HtvChannelSetting.COLUMN_TTV9_ORDER,
				HtvChannelSetting.COLUMN_TTV10_ORDER, HtvChannelSetting._ID };
		*/
		//ben add for mychoice
/* ben remark 20151104
		String[] mProjection = { HtvChannelSetting.COLUMN_DISPLAY_NUMBER, HtvChannelSetting.COLUMN_FREEPKG,
				HtvChannelSetting.COLUMN_PAYPKG1, HtvChannelSetting.COLUMN_PAYPKG2, HtvChannelSetting._ID };

		Cursor cursor = themeTVActivityCtx.getContentResolver().query(HtvChannelSetting.CONTENT_URI, mProjection, null,
				null, HtvChannelSetting._ID);
		ArrayList<ChannelData> res = new ArrayList<ChannelData>();

		while (cursor.moveToNext()) {
			//ben add for mychoice , remark next line
			//Uri logoUri = TvContract.buildChannelLogoUri(cursor.getInt(12));
			//ben add for mychoice
			Uri logoUri = TvContract.buildChannelLogoUri(cursor.getInt(4));
			try {
				logoInput = themeTVActivityCtx.getContentResolver().openInputStream(logoUri);
			} catch (FileNotFoundException fe) {
				Log.e("MyChoiceActivity",
						"There is no Logo for Channel: " + cursor.getString(cursor.getColumnIndex("display_number"))
								+ " channel_id:" + cursor.getInt(2));
			}
			if (logoInput != null) {
				logo = BitmapFactory.decodeStream(logoInput);
			} else {
				int width = (int) themeTVActivityCtx.getResources().getDimension(R.dimen.channel_logo_width);
				int height = (int) themeTVActivityCtx.getResources().getDimension(R.dimen.channel_logo_height);
				temp = (BitmapDrawable) themeTVActivityCtx.getResources()
						.getDrawable(R.drawable.channels_logo_container_highlighted);
				logotemp = temp.getBitmap();
				logo = Bitmap.createScaledBitmap(logotemp, width, height, false);
			}
			ChannelData data = new ChannelData(logo, Integer.valueOf(cursor.getString(0)), cursor.getString(1),
					cursor.getInt(2),cursor.getInt(3));
			res.add(data);
		}

		Collections.sort(res, new Comparator<ChannelData>() {
			@Override
			public int compare(ChannelData o1, ChannelData o2) {
				return o1.getChannelNum() - o2.getChannelNum();
			}
		});

		if (cursor != null) {
			cursor.close();
		}
		cursor = null;

		return res;
	}
*/
	public List<ChannelData> getChannelList(List<ChannelData> res) {
		Bitmap logo = null;
		Bitmap logotemp = null;
		Drawable temp = null;
		InputStream logoInput = null;
		String hdmi1 = "HW5";
		String hdmi2 = "HW6";
		String hdmi3 = "HW7";
		String[] mProjection = {
		// TvContract.Channels.Logo.CONTENT_DIRECTORY,
		TvContract.Channels.COLUMN_DISPLAY_NUMBER,
		TvContract.Channels.COLUMN_DISPLAY_NAME,
		TvContract.Channels._ID,
		TvContract.Channels.COLUMN_VERSION_NUMBER,
		HtvContract.HtvChannelViewExtColumns.COLUMN_DESCRIPTION,
		HtvContract.HtvChannelSettingBaseColumns.COLUMN_SKIP1,
		HtvContract.HtvChannelSettingBaseColumns.COLUMN_BLANK,
		HtvChannelSetting.COLUMN_MEDIA_TYPE,
		TvContract.Channels.COLUMN_BROWSABLE,
		TvContract.Channels.COLUMN_SERVICE_TYPE,
		HtvContract.HtvChannelSetting.COLUMN_FREEPKG,
		HtvContract.HtvChannelSetting.COLUMN_PAYPKG1,
		HtvContract.HtvChannelSetting.COLUMN_PAYPKG2,
		TvContract.Channels.COLUMN_INPUT_ID};
		//Log.d("MyChoiceActivity","query......(1)");
		//Log.d("MyChoiceActivity","URI = " + HtvContract.HtvMyChoiceChannelList.CONTENT_URI);
		Cursor cursor = themeTVActivityCtx.getContentResolver().query(HtvContract.HtvMyChoiceChannelList.CONTENT_URI, mProjection,
				null, null, TvContract.Channels._ID);
//		ArrayList<ChannelData> res = new ArrayList<ChannelData>();
		// Add apps/miracast/pta flag for enable/disable control
		ITvSettingsManager mtvSettings = ITvSettingsManager.Instance.getInterface();
		int apps_flag = mtvSettings.getInt(TvSettingsConstants.PBSMGR_PROPERTY_FEATURE_APPS_MODE,0,0);
		int miracast_flag = mtvSettings.getInt(TvSettingsConstants.INSTSETTINGSWIFIDISPLAY,0,0);
		int pta_flag = mtvSettings.getInt(TvSettingsConstants.PBSMGR_PROPERTY_PTA,0,0);
		/*START status of source enable*/
		boolean media_browser_flag = false;
		int selectedAVFlag = ITvSettingsManager.Instance.getInterface().getInt(TvSettingsConstants.PBSMGR_PROPERTY_SELECTABLE_AV, 0, 0);
		media_browser_flag = ((selectedAVFlag & TvSettingsDefinitions.PbsSelectableAvFlag.PBSMGR_SELECTABLE_USB_BROWSER_FLAG) !=0 );
		/*END */
		int internet_app_flag = mtvSettings.getInt(TvSettingsConstants.PBSMGR_INTERNET_BROWSER_APP_ENABLE, 0, 0);
		int itemType=0;
		//Log.d("BEN1","apps_flag = " + apps_flag);
		//Log.d("BEN1","miracast_flag = " + miracast_flag);
		//Log.d("BEN1","pta_flag = " + pta_flag);
		int tmpValue = mtvSettings.getInt(TvSettingsConstants.PBSMGR_PROPERTY_AV_MEDIA,0,0);
		boolean MediaChOnOff = true;
		if (tmpValue == TvSettingsDefinitions.PbsAvMediaConstants.PBSMGR_AV_MEDIA_ON){
			MediaChOnOff = true;
		}else{
			MediaChOnOff = false;
		}
		while (cursor.moveToNext()) {
			String mediatype = cursor.getString(7);
			//Log.d("MyChoiceActivity","mediatype = " + mediatype + "@@\n");

			boolean bExtSrc = mediatype.contains("TYPE_SOURCE");
			boolean bTunerSrc =  mediatype.contains("TYPE_TUNER");
			boolean bTIFSrc =  mediatype.contains("TYPE_TIF");
			boolean bTunerExtSrc =  bExtSrc | bTunerSrc | bTIFSrc;

			//Log.d("MyChoiceActivity","bTunerExtSrc = " + bTunerExtSrc + "@@\n");
			if (bTunerSrc || bTIFSrc) {
				Uri logoUri = TvContract.buildChannelLogoUri(cursor.getInt(2));
				try {
					logoInput = themeTVActivityCtx.getContentResolver().openInputStream(logoUri);
				} catch (FileNotFoundException fe) {
					Log.e("MyChoiceActivity",
							"There is no Logo for Channel: " + cursor.getString(cursor.getColumnIndex("display_number"))
									+ " channel_id:" + cursor.getInt(2)+ "logoInput ==" + logoInput);
					logoInput = null;
				} catch (SQLiteException se) {
					se.printStackTrace();
					logoInput = null;
					continue;
				}
			} else {
				logoInput = null;
			}
			/*
			Uri logoUri = TvContract.buildChannelLogoUri(cursor.getInt(2));
			try {
				logoInput = themeTVActivityCtx.getContentResolver().openInputStream(logoUri);
			} catch (FileNotFoundException fe) {
				Log.e("MyChoiceActivity",
						"There is no Logo for Channel: " + cursor.getString(cursor.getColumnIndex("display_number"))
								+ " channel_id:" + cursor.getInt(2));
			}
			*/
			int verID = cursor.getInt(3);
			String serviceType = cursor.getString(9);
			String ChName;

			if (verID==12 || verID==11){
				ChName =  cursor.getString(1);
			}else{
				ChName = (bTunerExtSrc==true)?cursor.getString(1):mediatype.substring(5);
			}

			if (logoInput != null) {
				logo = BitmapFactory.decodeStream(logoInput);
			} else {
				int width = (int) themeTVActivityCtx.getResources().getDimension(R.dimen.channel_logo_width);
				int height = (int) themeTVActivityCtx.getResources().getDimension(R.dimen.channel_logo_height);
				switch(verID){
					case 10:
						String inputid = cursor.getString(13);
						itemType = 1;
						if (inputid.endsWith(hdmi1) || inputid.endsWith(hdmi2) || inputid.endsWith(hdmi3)) {
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(org.droidtv.ui.tvwidget2k15.R.drawable.hdmi_d_ico_40x30_71);
						} else if (ChName.equals("YPbPr ")){
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(org.droidtv.ui.tvwidget2k15.R.drawable.video_input_d_ico_40x30_79);
						}else if (ChName.equals("SCART")){
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(org.droidtv.ui.tvwidget2k15.R.drawable.scart_d_ico_40x30_73);
						}else if ("VGA".equals(ChName)){
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(org.droidtv.ui.tvwidget2k15.R.drawable.vga_d_ico_40x30_75);
						}
						break;
					case 11:
					case 12:
						itemType = 0;
						temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(R.drawable.htv_tv_radio_icon_tv);
						break;
					default:
						if (mediatype.contains("TYPE_PTA")){
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(R.drawable.icon_308_philips_tv_app_d_36x36);
							itemType = 2;
						}else if (mediatype.contains("TYPE_MIRACAST")){
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(R.drawable.icon_307_miracast_d_36x36);
							itemType = 2;
						}else if (mediatype.contains("TYPE_GOOGLE_CAST")){
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(R.drawable.icon_307_miracast_d_36x36);
							itemType = 2;
						}else if (mediatype.contains(HtvContract.HtvBaseDefs.TYPE_DIRECTSHARE)){
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(org.droidtv.ui.tvwidget2k15.R.drawable.dlna_hl_ico_40x30_230);
							itemType = 2;
						}else if (mediatype.contains("TYPE_APPS")){
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(org.droidtv.ui.tvwidget2k15.R.drawable.apps_d_ico_40x30_88);
							itemType = 2;
						}else if (mediatype.contains("TYPE_MEDIABROWSER")){/*160826GaryYe spelling error spelling error browser*/
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(org.droidtv.ui.tvwidget2k15.R.drawable.browse_usb_d_ico_40x30_39);
							itemType = 2;
						}else if (mediatype.contains("TYPE_INTERNETBROWSER")){/*160826GaryYe spelling error spelling error browser*/
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(org.droidtv.ui.tvwidget2k15.R.drawable.internet_browser_d_ico_40x30_99);
							itemType = 2;
						}else if ("SERVICE_TYPE_AUDIO".equals(serviceType)){
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(R.drawable.htv_tv_radio_icon_radio);
							itemType = 0;
						}else{
							//temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(R.drawable.channels_logo_container_highlighted);
							temp = (Drawable) themeTVActivityCtx.getResources().getDrawable(R.drawable.htv_tv_radio_icon_tv);
							itemType = 0;
						}
						break;
				}
				BitmapDrawable bd = (BitmapDrawable) temp;
				logotemp = bd.getBitmap();
				logo = Bitmap.createScaledBitmap(logotemp, width, height, false);
			}

			String lScrambledString = cursor.getString(4);
			boolean lIsScrambled = false;
			boolean lIsHide = false;
			boolean lIsLock = false;
			boolean lIsEnable = false;
			boolean lIsSrc = false;
			if( (lScrambledString!=null) && (lScrambledString.equalsIgnoreCase("Scrambled")))
			{
				lIsScrambled = true;
			}
			if( cursor.getInt(5)==1)
			{
				lIsHide = true;
			}
			if( cursor.getInt(6)==1)
			{
				lIsLock = true;
			}
			if( cursor.getInt(8)==1)
			{
				lIsEnable = true;
			}
			if( cursor.getString(7).equals("TYPE_SOURCE"))
			{
				lIsSrc = true;
			}

			if (mediatype.contains(HtvContract.HtvBaseDefs.TYPE_DIRECTSHARE)) {
				boolean isDirectShareEnable = mtvSettings.getInt(TvSettingsConstants.PBSMGR_PROPERTY_SECURE_SHARING,0,0) != 1 ? true :  false;
				boolean isDirectShareSelected = false;
				int selected = mtvSettings.getInt(TvSettingsConstants.PBSMGR_PROPERTY_SELECTABLE_AV,0,0);
				if ((selected & TvSettingsDefinitions.PbsSelectableAvFlag.PBSMGR_SELECTABLE_DIRECTSHARE_MEDIA_BROWSER_FLAG) != 0){
					isDirectShareSelected = true;
				}
				if (!isDirectShareEnable || !isDirectShareSelected) {
					continue;
				}
			}
			if (mediatype.contains("TYPE_GOOGLE_CAST")) {
				boolean isGoogleCastEnable = mtvSettings.getInt(TvSettingsConstants.PBSMGR_PROPERTY_GOOGLE_CAST,0,0) == 1 ? true : false;
				if (!isGoogleCastEnable) {
					continue;
				}
			}

			if ((mediatype.contains("TYPE_PTA"))
					|| (mediatype.contains(HtvContract.HtvBaseDefs.TYPE_INTERNETHOTSPOT))
					|| (mediatype.contains(HtvContract.HtvBaseDefs.TYPE_YOUTUBE))
					|| (mediatype.contains("TYPE_MIRACAST") && miracast_flag == TvSettingsDefinitions.PbsWifiMiracast.PBSMGR_WIFI_MIRACAST_OFF)
					|| (mediatype.contains("TYPE_APPS") && apps_flag == TvSettingsDefinitions.PbsAppsModeConstants.PBSMGR_OFF)
					|| (mediatype.contains("TYPE_MEDIABROWSER") && !media_browser_flag)
					|| (mediatype.contains("TYPE_INTERNETBROWSER") && internet_app_flag == TvSettingsDefinitions.pbsInternetBrowserAppEnable.PBSMGR_INTERNET_BROWSER_APP_ENABLE_OFF))
			{
				//Log.d("BEN1","skip PTA or MIRACAST or APPS");
				continue;
			}
			else if (bExtSrc && lIsHide)
			{
				continue;
			}

			int ChNumber = Integer.valueOf(cursor.getString(0));

			/*ChannelData data = new ChannelData(logo, Integer.valueOf(cursor.getString(0)), ChName,
					cursor.getInt(2),cursor.getInt(3),lIsScrambled,lIsHide,lIsLock);*/
			ChannelData data = new ChannelData(logo, ChNumber, ChName, cursor.getInt(2),verID,lIsScrambled,lIsHide,lIsLock,itemType);
			data.setMychoicefree(cursor.getInt(10));
			data.setMychoicepkg1(cursor.getInt(11));
			data.setMychoicepkg2(cursor.getInt(12));

			if (verID == 12){
				if (MediaChOnOff == true){
					res.add(data);
				}
			}else{
				if (lIsSrc == true){
					if (lIsEnable == true)
						res.add(data);
				}else{
					res.add(data);
				}
			}
		}

		Collections.sort(res, new Comparator<ChannelData>() {
			@Override
			public int compare(ChannelData o1, ChannelData o2) {
				return o1.getChannelNum() - o2.getChannelNum();
			}
		});

		if (cursor != null) {
			cursor.close();
		}
		cursor = null;

		return res;
	}
}




