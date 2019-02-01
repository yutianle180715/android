package org.droidtv.mychoice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.mtp.MtpConstants;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.droidtv.tv.mychoice.MyChoiceManager;
import org.droidtv.ui.tvwidget2k15.tvtoast.TvToast;
import org.droidtv.ui.tvwidget2k15.tvtoast.TvToastMessenger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.droidtv.tv.persistentstorage.TvSettingsDefinitions;
import org.droidtv.htv.provider.HtvContract;
import org.droidtv.mychoice.util.MyChoiceJsonCommand;
import org.droidtv.tv.persistentstorage.TvSettingsConstants;
import org.droidtv.tv.persistentstorage.ITvSettingsManager;

/**
 * @author daemon.yu
 *
 */
public class MyChoiceService extends Service {
	private static final String TAG = "MyChoiceService";
	//clone service
	private static final String MYCHOICE_JSON_PATH = "/data/misc/HTV/Clone/Clone_data/MyChoice/MyChoice.json";
	private static final String MYCHOICE_COMMAND_MYCHOICE = "MyChoice";
	private static final String MYCHOICE_COMMAND_DETAILS = "CommandDetails";
	private static final String MYCHOICE_PARAMETERS = "MyChoiceParameters";
	private static final String OFFLINESERVICE_PARAMETERS = "OfflineServiceParameters";
	private static final String MYCHOICE_JSON_FILE_READY = "org.droidtv.intent.action.clone.CLONE_IN_MYCHOICE_START";
	private static final String MYCHOICE_JSON_FILE_DONE = "org.droidtv.intent.action.clone.CLONE_IN_MYCHOICE_DONE";
	private static final String MYCHOICE_JSON_FILE_FAIL = "org.droidtv.intent.action.clone.CLONE_IN_MYCHOICE_FAIL";
	private static final String MYCHOICE_JSON_FILE_NOT_AVAILABLE = "org.droidtv.intent.action.clone.CLONE_IN_MYCHOICE_NOT_AVAILABLE";
	//japit service
	private static final String MYCHOICE_JAPIT_COMMAND_RESULT = "org.droidtv.intent.action.japit.JAPIT_TO_MYCHOICE_RESULT";
	private static final String MYCHOICE_STATUS_CHANGED = "org.droidtv.intent.action.japit.MYCHOICE_STATUS_CHANGED";
	private static final String MYCHOICE_STATUS_EXPIRED = "org.droidtv.mychoice.STATUS_EXPIRED";
	private static final String JAPIT_COMMAND_MYCHOICE_STATUS = "org.droidtv.tv.intent.extra.MYCHOICE_STATUS";
	private static final String MYCHOICE_JAPIT_COMMAND_READY = "org.droidtv.tv.intent.action.japit.JAPIT_TO_MYCHOICE_ACTION";
	private static final String JAPIT_COMMAND_MYCHOICE_ACTION = "org.droidtv.tv.intent.extra.MYCHOICE_ACTION";
	private static final String JAPIT_COMMAND_MYCHOICE_MYCHOICEPIN  = "org.droidtv.tv.intent.extra.MyChoicePIN";
	private static final String JAPIT_COMMAND_MYCHOICE_STARTDATE = "org.droidtv.tv.intent.extra.StartDate";
	private static final String JAPIT_COMMAND_MYCHOICE_STARTTIME = "org.droidtv.tv.intent.extra.StartTime";
	private static final String JAPIT_COMMAND_MYCHOICE_ENDDATE = "org.droidtv.tv.intent.extra.EndDate";
	private static final String JAPIT_COMMAND_MYCHOICE_ENDTIME = "org.droidtv.tv.intent.extra.EndTime";
	public final String[] keyitemsofjapit = {JAPIT_COMMAND_MYCHOICE_ACTION, JAPIT_COMMAND_MYCHOICE_MYCHOICEPIN, JAPIT_COMMAND_MYCHOICE_STARTDATE
			, JAPIT_COMMAND_MYCHOICE_STARTTIME, JAPIT_COMMAND_MYCHOICE_ENDDATE, JAPIT_COMMAND_MYCHOICE_ENDTIME};
	private static final String JAPIT_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
	public static final SimpleDateFormat sdf = new SimpleDateFormat(JAPIT_TIME_FORMAT);

	private static final int TYPE_JSON_FROM_FILE = 1;
	private static final int TYPE_JSON_FROM_JAPIT = 2;
	private static final int EXECUTE_COMMAND = 3;
	private static final int MYCHOICE_PIN_INVALID = 4;
	private static final int MYCHOICE_PIN_VALID = 5;
	private static final int MYCHOICE_STOP_SUCCESS = 6;
	private static final int MYCHOICE_RESET_SUCCESS = 7;
	private static final int MYCHOICE_INVALID = 0;

	/*
	 * command keys
	 */
	public final String COMMAND_ACTION = "Action";
	public final String COMMAND_MYCHOICEPIN = "MyChoicePIN";
	public final String COMMAND_STARTDATE = "StartDate";
	public final String COMMAND_STARTTIME = "StartTime";
	public final String COMMAND_STOPDATE = "StopDate";
	public final String COMMAND_STOPTIME = "StopTime";
	public final String[] keyitems = {COMMAND_ACTION, COMMAND_MYCHOICEPIN, COMMAND_STARTDATE
			, COMMAND_STARTTIME, COMMAND_STOPDATE, COMMAND_STOPTIME};

	public static final String STARTMYCHOICE = "STARTMYCHOICE";
	public static final String STOPMYCHOICE = "STOPMYCHOICE";
	public static final String RESETMYCHOICE = "RESETMYCHOICE";

	private TimerTask localProgTimerTask = null;
	private Timer localProgTimer = null;
	private MyChoiceManager mMyChoiceManager = new MyChoiceManager();
	private MyChoiceJsonCommand mMyChoiceJsonCommand =  null;

	private Runnable mGetMyChoiceJsonInfoRunnable = null;
	private ContentProviderClient mHtvContentProviderClient = null;
	private static ITvSettingsManager mTvSettingMgr = null;

	Context mContext;
	private static int MyChoice_id = 0;
	private boolean mychoiceon = false;
	private Handler mainHandler;
	private TvToastMessenger msgToast = null;
	private TvToast timeOutTvToast = null;

	int PINS_LENGTH = 6;
	int resourceofjson = 0;
	String actionname = null;
	boolean timeobserver =  false;
	boolean mychoicestatusobserver = false;
	boolean timerisrunning =  false;
	boolean isAvailable = true;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this;

		Log.v(TAG, "onCreate");
		mTvSettingMgr = ITvSettingsManager.Instance.getInterface();
		if (msgToast == null) {
			msgToast = TvToastMessenger.getInstance(this);
		}

		mainHandler = new Handler() {
			@Override
			public synchronized void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case EXECUTE_COMMAND:
					boolean result = executeMyChoiceJsonCommand(mMyChoiceJsonCommand, resourceofjson);
					//clone need result
					if (resourceofjson == TYPE_JSON_FROM_FILE) {
						if (!isAvailable) {
							Intent it = new Intent(MYCHOICE_JSON_FILE_NOT_AVAILABLE);
							sendBroadcast(it);
							isAvailable = true;
						} else {
							if (result) {
								Intent it = new Intent(MYCHOICE_JSON_FILE_DONE);
								sendBroadcast(it);
							} else {
								Intent it = new Intent(MYCHOICE_JSON_FILE_FAIL);
								sendBroadcast(it);
							}
						}
					}
					break;

				default:
					super.handleMessage(msg);
					break;
				}
			}
		};

		mychoiceon = getMyChoiceStatus();
		MyChoiceService_init();
	}

	private BroadcastReceiver myChoiceStatusBR =  new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(MYCHOICE_STATUS_EXPIRED)) {
				realeaseMyChoiceTimer();
//				Log.v(TAG, "MYCHOICE_STATUS_EXPIRED");
			}
		}
	};

	private void registerMyChoiceStatusObserver() {
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(MYCHOICE_STATUS_EXPIRED);
		mContext.registerReceiver(myChoiceStatusBR, mIntentFilter);
		mychoicestatusobserver = true;
//		Log.v(TAG, "registerMyChoiceStatusObserver");
	}

	private void unregisterMyChoiceStatusObserver() {
		if (myChoiceStatusBR != null && mychoicestatusobserver) {
			mContext.unregisterReceiver(myChoiceStatusBR);
			mychoicestatusobserver = false;
		}
	}

	private BroadcastReceiver mBroadcastReceiver =  new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_TIME_TICK)) {
				adjustTime(false);
			} else if (action.equals(Intent.ACTION_TIME_CHANGED)) {
				adjustTime(true);
			}
		}
	};

	private void adjustTime(boolean changeall) {
		long current_time = (long) System.currentTimeMillis() / 1000;
		Log.v(TAG, "current time: "+ current_time);
		if (changeall) {
			long pre_start_time = ((long) mTvSettingMgr.getInt(TvSettingsConstants.MYCHOICE_STARTED_TIME_HIGH, 0,
					0) << 16) | ((long) mTvSettingMgr.getInt(TvSettingsConstants.MYCHOICE_STARTED_TIME_LOW, 0, 0));
			long stop_time = ((long) mTvSettingMgr.getInt(TvSettingsConstants.MYCHOICE_EXPIRED_TIME_HIGH, 0, 0) << 16)
					| ((long) mTvSettingMgr.getInt(TvSettingsConstants.MYCHOICE_EXPIRED_TIME_LOW, 0, 0));
			long change_value = pre_start_time - current_time;

			stop_time = stop_time - change_value;
			mTvSettingMgr.putInt(TvSettingsConstants.MYCHOICE_EXPIRED_TIME_HIGH, 0, (int) (stop_time >> 16));
			mTvSettingMgr.putInt(TvSettingsConstants.MYCHOICE_EXPIRED_TIME_LOW, 0, (int) (stop_time & 0xFFFF));
		}
		mTvSettingMgr.putInt(TvSettingsConstants.MYCHOICE_STARTED_TIME_HIGH, 0, (int) (current_time >> 16));
		mTvSettingMgr.putInt(TvSettingsConstants.MYCHOICE_STARTED_TIME_LOW, 0, (int) (current_time & 0xFFFF));
	}

	private boolean getMyChoiceStatus() {
		return mTvSettingMgr.getInt(TvSettingsConstants.PBSMGR_PROPERTY_MY_CHOICE_MYCHOICE, 0, 0)
				== TvSettingsDefinitions.PbsMyChoiceConstants.PBSMGR_MY_CHOICE_OFF ? false : true;
	}

	private void MyChoiceService_init() {
		// TODO Auto-generated method stub
		IntentFilter intentMyChoice = new IntentFilter();
		intentMyChoice.addAction("MyChoice");
		intentMyChoice.addAction(MYCHOICE_JSON_FILE_READY);
		intentMyChoice.addAction(MYCHOICE_JAPIT_COMMAND_READY);
		registerReceiver(MyChoiceReceiver, intentMyChoice);
		registerMyChoiceStatusObserver();
		setMyChoiceTimer(mMyChoiceManager.getBdsString(2));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.v(TAG,"onStartCommand");
//		return super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.v(TAG,"onDestroy");
		unregisterReceiver(MyChoiceReceiver);
		unregisterMyChoiceStatusObserver();
		realeaseMyChoiceTimer();
		//unregisterTimeObserver();
		if (mainHandler != null) {
			mainHandler.removeCallbacksAndMessages(null);
			mainHandler = null;
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void realeaseMyChoiceTimer() {
		if (localProgTimerTask != null) {
			localProgTimerTask.cancel();
			localProgTimerTask = null;
		}

		if (localProgTimer != null) {
			localProgTimer.cancel();
			localProgTimer = null;
		}

		if (timerisrunning) {
			myChoiceStatusChanged();
			timerisrunning = false;
		} else {
			if (mMyChoiceManager.getBdsStatus(2) > 0) {
				myChoiceStatusChanged();
				timerisrunning = true;
			}
		}
	}

	private boolean isValidTimeFormat(String timeformat) {
		boolean isValid = true;
		try {
			sdf.setLenient(false);
			sdf.parse(timeformat);
		} catch (Exception e) {
			// TODO: handle exception
			isValid =  false;
		}
		return isValid;
	}

	private void updateMyChoiceStopTime(long st) {
		mTvSettingMgr.putInt(TvSettingsConstants.MYCHOICE_EXPIRED_TIME_HIGH, 0, (int) (st >> 16));
		mTvSettingMgr.putInt(TvSettingsConstants.MYCHOICE_EXPIRED_TIME_LOW, 0, (int) (st & 0xFFFF));
	}

	// only pin validation successes by japit/clone, we do the update.
	private boolean updateMyChoiceTime(MyChoiceJsonCommand mjc) {
		String starttime = null;
		String stoptime = null;
		Date starttimedate = null;
		Date stoptimedate = null;

		starttime = mjc.getCommandItemValue(mjc.COMMAND_STARTDATE) + " "
				+ mjc.getCommandItemValue(mjc.COMMAND_STARTTIME);
		stoptime = mjc.getCommandItemValue(mjc.COMMAND_STOPDATE) + " " + mjc.getCommandItemValue(mjc.COMMAND_STOPTIME);
		Log.v(TAG, "starttime: " + starttime + "stoptime: " + stoptime);

		if (!isValidTimeFormat(starttime) || !isValidTimeFormat(stoptime))
			return false;

		try {
			starttimedate = sdf.parse(starttime);
			stoptimedate = sdf.parse(stoptime);
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		long starttimets = starttimedate.getTime() / 1000;
		long stoptimets = stoptimedate.getTime() / 1000;
		long currentts = System.currentTimeMillis() / 1000;
		long stl = 0L;

		if (stoptimets < currentts || stoptimets < starttimets || stoptimets <= 0) {
			return false;
		} else {
			String st = mMyChoiceManager.getStopTime();
			try {
				stl = Long.valueOf(st);
			} catch (NumberFormatException e) {
				// TODO: handle exception
				e.printStackTrace();
				stl = 0L;
			}

			if (stoptimets < stl && stl != 0L) {
				updateMyChoiceStopTime(stoptimets);
			}
		}

		return true;
	}

	private boolean executeMyChoiceJsonCommand(MyChoiceJsonCommand mjc, int resource) {
		boolean ret = false;
		//isAvailable = true;
		if (mjc == null) {
			Log.v(TAG, "mMyChoiceJsonCommand is not valid.");
			return false;
		} else {
			Log.v(TAG, mMyChoiceJsonCommand.toString());
			int x;
			String action = mjc.getCommandItemValue(COMMAND_ACTION);
			if (action != null) {
				action = action.trim().toUpperCase();
			} else {
				Log.w(TAG, "MyChoiceJsonCommand action is null, pl check!");
				return ret;
			}
			switch (action) {
			case STARTMYCHOICE:
				if (!getMyChoiceStatus() || !checkMyChoiceEnable()) {
					isAvailable = false;
					showMyChoiceMsg(MYCHOICE_INVALID);
					return false;
				} else {
					actionname = STARTMYCHOICE;
					String pins = mjc.getCommandItemValue(COMMAND_MYCHOICEPIN).trim();
					if (!checkMyChoicePinInJson(pins)) {
						showMyChoiceMsg(MYCHOICE_PIN_INVALID);
						Log.v(TAG, "pins is illegal.");
						return false;
					}
					char[] enteredPin = new char[PINS_LENGTH];
					enteredPin = pins.toCharArray();
					x = mMyChoiceManager.setBdsString(1, enteredPin, enteredPin.length);

					if ((x == 1) || (x == 2)) {
						//success and set the time
						ret = true;
//						if (resource == TYPE_JSON_FROM_FILE) {
//							Intent it =  new Intent(MYCHOICE_JSON_FILE_DONE);
//							sendBroadcast(it);
//						} else if (resource == TYPE_JSON_FROM_JAPIT) {
//							Intent it =  new Intent(MYCHOICE_JAPIT_COMMAND_RESULT);
//							it.putExtra(JAPIT_COMMAND_MYCHOICE_STATUS, true);
//							sendBroadcast(it);
//						}
						updateMyChoiceTime(mjc);

						showMyChoiceMsg(MYCHOICE_PIN_VALID);
						//reset mychoice timer
						setMyChoiceTimer(mMyChoiceManager.getBdsString(2));
					} else {
						//fail
						ret = false;
						showMyChoiceMsg(MYCHOICE_PIN_INVALID);
//						if (resource == TYPE_JSON_FROM_FILE) {
//							Intent it =  new Intent(MYCHOICE_JSON_FILE_FAIL);
//							sendBroadcast(it);
//						} else if (resource == TYPE_JSON_FROM_JAPIT) {
//							Intent it =  new Intent(MYCHOICE_JAPIT_COMMAND_RESULT);
//							it.putExtra(JAPIT_COMMAND_MYCHOICE_STATUS, false);
//							sendBroadcast(it);
//						}
					}
				}
				break;

			case STOPMYCHOICE:
				if (!getMyChoiceStatus() || !checkMyChoiceEnable()) {
					isAvailable = false;
					showMyChoiceMsg(MYCHOICE_INVALID);
					return false;
				}
				if (mMyChoiceManager.getBdsStatus(2) == 0) {
					return false;
				}
				mMyChoiceManager.getBdsStatus(4);
				actionname = STOPMYCHOICE;
				//only JAPIT control post the toast
				if (resource == TYPE_JSON_FROM_JAPIT) {
					showMyChoiceMsg(MYCHOICE_STOP_SUCCESS);
				}
				//release mychoice timer
				ret = true;
				realeaseMyChoiceTimer();
				//unregisterTimeObserver();
				showPinEnterActivity();
				break;

			case RESETMYCHOICE:
				if (!getMyChoiceStatus() || !checkMyChoiceEnable()) {
					isAvailable = false;
					showMyChoiceMsg(MYCHOICE_INVALID);
					return false;
				}
				//set time expired, regenerate pin file and recover key indexes
				int unlockstatus = mMyChoiceManager.getBdsStatus(2);
//				showMyChoiceMsg(MYCHOICE_RESET_SUCCESS);

				if (unlockstatus != 0) {
					mMyChoiceManager.getBdsStatus(4);
					//release mychoice timer
					realeaseMyChoiceTimer();
					// unregisterTimeObserver();
					showPinEnterActivity();
				}
				mMyChoiceManager.getBdsStatus(1);
				actionname = RESETMYCHOICE;
				ret = true;
				break;

			default:
				Log.v(TAG, "do nothing!");
				ret = false;
				break;
			}
		}

		return ret;
	}

	private boolean isNumeric(String pins) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(pins);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	private boolean checkMyChoicePinInJson(String pins) {
		// TODO Auto-generated method stub
		return (pins.length() == PINS_LENGTH) && isNumeric(pins);
	}

	private void showMyChoiceMsg(int type) {
		// TODO Auto-generated method stub
		Log.v(TAG, "showMyChoiceMsg type: " + type);
		switch (type) {
		/*case MYCHOICE_PIN_INVALID:
			timeOutTvToast = TvToastMessenger.makeTvToastMessage(TvToastMessenger.TYPE_TIME_OUT,
					"Pin is invalid, please check if the pin is legal.",
					org.droidtv.ui.tvwidget2k15.R.drawable.icon_punched_push_m_alert_n_36x36);
			break;

		case MYCHOICE_PIN_VALID:
			String remainingTime = mMyChoiceManager.getBdsString(2);
			String mNotificationMessage = "";

			mNotificationMessage += " Unlock. Pin is valid, remaining time: ";

			if (!remainingTime.substring(0, 2).equals("00")) {
				mNotificationMessage += remainingTime.substring(0, 2) + " days, ";
				mNotificationMessage += remainingTime.substring(2, 4) + " hours ";
			} else if (!remainingTime.substring(2, 4).equals("00")) {
				mNotificationMessage += remainingTime.substring(2, 4) + " hours ";
			}
			mNotificationMessage += remainingTime.substring(4, 6) + " minutes";

			timeOutTvToast = TvToastMessenger.makeTvToastMessage(TvToastMessenger.TYPE_TIME_OUT, mNotificationMessage,
					org.droidtv.ui.tvwidget2k15.R.drawable.icon_punched_push_m_alert_n_36x36);
			break;

		case MYCHOICE_RESET_SUCCESS:*/
		case MYCHOICE_STOP_SUCCESS:
			timeOutTvToast = TvToastMessenger.makeTvToastMessage(TvToastMessenger.TYPE_TIME_OUT,
                                        getResources().getString(org.droidtv.ui.strings.R.string.HTV_MYCHOICE_NOT_AVAILABLE),
                                        org.droidtv.ui.tvwidget2k15.R.drawable.icon_punched_push_m_alert_n_36x36);
			msgToast.showTvToastMessage(timeOutTvToast);
			break;

		default:
			/*timeOutTvToast = TvToastMessenger.makeTvToastMessage(TvToastMessenger.TYPE_TIME_OUT,
					"MyChoice is not available at the moment.",
					org.droidtv.ui.tvwidget2k15.R.drawable.icon_punched_push_m_alert_n_36x36);*/
			break;
		}


	}

	private boolean checkMyChoiceEnable() {
		// TODO Auto-generated method stub
		int retval = mMyChoiceManager.getBdsStatus(0);

		if ((retval != 0) && (retval != 7) && (retval != 3)) {
			return false;
		}

		return true;
	}

	private BroadcastReceiver MyChoiceReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, final Intent intent) {
			if ("MyChoice".equals(intent.getAction())) {
				int tmpPowerOn = intent.getIntExtra("PowerOn", 0);
				Log.v(TAG, "MyChoiceReceiver tmpPowerOn: " + tmpPowerOn);
				if (tmpPowerOn == 1) {
					// check time remained and init mychoice values when power
					// on --daemon.yu
					mMyChoiceManager.getBdsStatus(3);
				}
				setMyChoiceTimer(mMyChoiceManager.getBdsString(2));
			}
			if (MYCHOICE_JSON_FILE_READY.equals(intent.getAction())) {
				mGetMyChoiceJsonInfoRunnable = new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						resourceofjson = TYPE_JSON_FROM_FILE;
						mMyChoiceJsonCommand = parseMyChoiceJson(MYCHOICE_JSON_PATH, resourceofjson);

//						if (mMyChoiceJsonCommand == null) {
//							Log.v(TAG, "mMyChoiceJsonCommand is null.");
//						} else {
							Message msg = Message.obtain();
							msg.what = EXECUTE_COMMAND;
							mainHandler.sendMessage(msg);
//						}
					}
				};
				mGetMyChoiceJsonInfoRunnable.run();
			}
			if (MYCHOICE_JAPIT_COMMAND_READY.equals(intent.getAction())) {

				mGetMyChoiceJsonInfoRunnable = new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						resourceofjson = TYPE_JSON_FROM_JAPIT;
						Map<String, String> commandData = new HashMap<String, String>();
						String values = intent.getStringExtra(JAPIT_COMMAND_MYCHOICE_ACTION);
						if (values != null){
							commandData.put(COMMAND_ACTION, values);
							values = values.trim().toUpperCase();
							switch (values) {
							case STARTMYCHOICE:
								int i = 0;
								for (String temp: keyitemsofjapit) {
									values = intent.getStringExtra(temp);
									if (values != null) {
										commandData.put(keyitems[i], values);
									}
									i++;
								}
								break;
							case STOPMYCHOICE:
							case RESETMYCHOICE:
								break;

							default:
								break;
							}
						}
						mMyChoiceJsonCommand = new MyChoiceJsonCommand(commandData);
						if (mMyChoiceJsonCommand == null) {
							Log.v(TAG, "mMyChoiceJsonCommand is null.");
						} else {
							Message msg = Message.obtain();
							msg.what = EXECUTE_COMMAND;
							mainHandler.sendMessage(msg);
						}
					}
				};
				mGetMyChoiceJsonInfoRunnable.run();
			}
		}
	};

	public String getRoomID() {
		String sfill = "00000";
		String sFormatRoomId = "00000";
		String sRoomId = mTvSettingMgr.getString(TvSettingsConstants.PBSMGR_PROPERTY_ROOM_ID, 0, "");

		if (sRoomId.length() < 5) {
			sFormatRoomId = sfill.substring(0, 5 - sRoomId.length()) + sRoomId;
		} else {
			sFormatRoomId = sRoomId;
		}

		return sFormatRoomId;
	}

	@SuppressWarnings("unchecked")
	private MyChoiceJsonCommand parseMyChoiceJson(String content, int resource) {
		MyChoiceJsonCommand temp =  null;
		String jsoncontent = null;
		Map<String, String> commandData = new HashMap<String, String>();
		temp = new MyChoiceJsonCommand(commandData);
		String valuetemp = null;
		switch (resource) {
		case TYPE_JSON_FROM_FILE:
			jsoncontent = getMychoiceJsonFileInfo(content);
			if (jsoncontent == null) {
				Log.v(TAG, "jsoncontent is null");
				temp =  null;
				return temp;
			}
			try {
				JSONObject root =  new JSONObject(jsoncontent);
//				JSONObject mychoicejsoncommand = root.getJSONObject(MYCHOICE_COMMAND_DETAILS).getJSONObject(MYCHOICE_PARAMETERS);
				JSONArray mychoicejsonarray = root.getJSONArray(MYCHOICE_COMMAND_MYCHOICE);
				JSONObject mychoicejsoncommand = mychoicejsonarray.getJSONObject(0).getJSONObject(MYCHOICE_COMMAND_DETAILS).getJSONObject(MYCHOICE_PARAMETERS);
				JSONObject OfflineServiceParameters = mychoicejsonarray.getJSONObject(0).getJSONObject(MYCHOICE_COMMAND_DETAILS).getJSONObject(OFFLINESERVICE_PARAMETERS);
				String RoomID = OfflineServiceParameters.getString("RoomID");
				if (!getRoomID().equals(RoomID)) {
					isAvailable = false;
					temp = null;
				} else {
					// using HashMap to ensure MyChoiceJsonCommand can be used
					for (String keyitem : keyitems) {
						if (mychoicejsoncommand.has(keyitem)) {
							valuetemp = mychoicejsoncommand.getString(keyitem);
							temp.setCommandItemValue(keyitem, valuetemp);
						}
					}
				}

			} catch (JSONException  e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			break;
		case TYPE_JSON_FROM_JAPIT:
			jsoncontent = content;

			try {
				JSONObject root =  new JSONObject(jsoncontent);
				//using HashMap to ensure MyChoiceJsonCommand can be used
				for (String keyitem : keyitems) {
					if (root.has(keyitem)) {
						valuetemp = root.getString(keyitem);
						temp.setCommandItemValue(keyitem, valuetemp);
					}
				}

			} catch (JSONException  e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			break;

		default:
			temp = null;
			break;
		}

		return temp;
	}

	private String getMychoiceJsonFileInfo(String filepath) {
		// TODO Auto-generated method stub
		if (filepath == null || filepath.equals(""))
			return null;
		String content = null;
		InputStream instream = null;
		File mychoicejson = new File(filepath);
		if (mychoicejson.exists() && mychoicejson.canRead()) {
			try {
				instream = new FileInputStream(mychoicejson);
				if (instream != null) {
					BufferedReader buffreader = new BufferedReader(new InputStreamReader(instream));
					String line = null;
					StringBuilder sb = new StringBuilder();
					while ((line = buffreader.readLine()) != null) {
						sb.append(line);
					}
					content = sb.toString();
					instream.close();
				}
			} catch (FileNotFoundException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		} else {
			Log.w(TAG, "MyChoice.json - intput file is null!");
		}
		return content;
	}

	private boolean checkMychoise() {
		int tmpValue = 0;
		if (TvSettingsDefinitions.PbsProfessionalModeConstants.PBSMGR_PROFESSIONAL_MODE_ON == 1) {
			tmpValue = mTvSettingMgr.getInt(TvSettingsConstants.PBSMGR_PROPERTY_MY_CHOICE_MYCHOICE, 0, 0);
			if (tmpValue != TvSettingsDefinitions.PbsMyChoiceConstants.PBSMGR_MY_CHOICE_OFF) {// my
																								// choice
				// Star Pan add =
				MyChoice_id = getMyChoiceID();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private static final String[] HTV_PROJECTION = { HtvContract.HtvChannelSettingBaseColumns.COLUMN_PAYPKG1,
			HtvContract.HtvChannelSettingBaseColumns.COLUMN_PAYPKG2,
			HtvContract.HtvChannelSettingBaseColumns.COLUMN_BLANK };

	public ContentProviderClient getHtvContentProviderClient() {
		try {
			mHtvContentProviderClient.getType(HtvContract.HtvChannelList.CONTENT_URI);
		} catch (Exception r) {
			Log.d(TAG, "Exception caught" + r.getLocalizedMessage());
			// mHtvContentProviderClient =
			// MainSetting.mContext.getContentResolver().acquireContentProviderClient(HtvContract.AUTHORITY);
			mHtvContentProviderClient = mContext.getContentResolver()
					.acquireContentProviderClient(HtvContract.AUTHORITY);// tesst
		}
		return mHtvContentProviderClient;
	}

	public int IsMyChoicePackage(int presetid) {
		int isMyChoice = 0;
		String[] selectionArgs = { Integer.toString(presetid) };
		String selection = HtvContract.HtvChannelSettingBaseColumns._ID + "=?";
		ContentProviderClient providerclient = getHtvContentProviderClient();
		Cursor cursor = null;

		if (providerclient != null) {
			try {
				cursor = providerclient.query(HtvContract.HtvChannelSetting.CONTENT_URI, HTV_PROJECTION, selection,
						selectionArgs, null);
			} catch (RemoteException e) {
				Log.d(TAG, "RemoteException:IsMyChoicePackage():" + e);
			}
		}

		if (cursor != null) {
			if ((cursor.getCount() > 0) && (cursor.moveToFirst())) {
				try {
					int isPackage1 = cursor
							.getInt(cursor.getColumnIndex(HtvContract.HtvChannelSettingBaseColumns.COLUMN_PAYPKG1));
					int isPackage2 = cursor
							.getInt(cursor.getColumnIndex(HtvContract.HtvChannelSettingBaseColumns.COLUMN_PAYPKG2));

					if ((isPackage1 == 1) && (isPackage2 == 1)) {
						isMyChoice = 3;
					} else if (isPackage1 == 1) {
						isMyChoice = 1;
					} else if (isPackage2 == 1) {
						isMyChoice = 2;
					} else {
						isMyChoice = 0;
					}
				} catch (Exception e) {
					Log.d(TAG, "IsMyChoicePackage :" + e.toString());
				}
			}
			cursor.close();
		}
		providerclient.release();
		return isMyChoice;
	}

	public int getMyChoiceID() {
		Cursor cursor = null;
		int mychoiceid = -1;
		int id;
		String type;
		String[] mProjection = { HtvContract.HtvChannelSetting._ID, HtvContract.HtvChannelSetting.COLUMN_MEDIA_TYPE };
		try {
			cursor = this.getContentResolver().query(HtvContract.HtvChannelSetting.CONTENT_URI, mProjection, null, null,
					null, null);
			if (cursor != null) {
				cursor.moveToFirst();
				do {
					id = cursor.getInt(cursor.getColumnIndex(HtvContract.HtvChannelSetting._ID));
					type = cursor.getString(cursor.getColumnIndex(HtvContract.HtvChannelSetting.COLUMN_MEDIA_TYPE));
					if (type.equals("TYPE_APPS")) {
						mychoiceid = id;
					}
				} while (cursor.moveToNext() == true);
				cursor.close();
			}
			cursor = null;

		} catch (Exception e) {
			Log.d(TAG, "get my choice data error");
			e.printStackTrace();
			return mychoiceid;
		}
		return mychoiceid;
	}

	public boolean OnLockStatusChanged(int pressID) {
		int isMyChoicePackage = IsMyChoicePackage(pressID);
		boolean status = false;

		Log.d(TAG, "pressID = " + pressID + ",isMyChoicePackage = " + isMyChoicePackage);
		if (isMyChoicePackage != 0) {
			int mychoiceMode = ITvSettingsManager.Instance.getInterface()
					.getInt(TvSettingsConstants.PBSMGR_PROPERTY_MY_CHOICE_MYCHOICE, 0, 0);
			int retval = mMyChoiceManager.getBdsStatus(2);
			Log.d(TAG, "isMyChoicePackage = " + isMyChoicePackage + ", mychoiceMode = " + mychoiceMode + ", retval = "
					+ retval);
			if ((mychoiceMode == TvSettingsDefinitions.PbsMyChoiceConstants.PBSMGR_MY_CHOICE_OFF)
					|| (((isMyChoicePackage == 1) || (isMyChoicePackage == 3)) && (retval == 1))
					|| (((isMyChoicePackage == 2) || (isMyChoicePackage == 3)) && (retval == 2))) {
				Log.d(TAG, "OnLockStatusChanged no lock ");
				status = true;
			} else {
				Intent i = new Intent("org.droidtv.intent.action.MYCHOICE_PIN_DIALOG");

				int[] KeyArray = { KeyEvent.KEYCODE_CHANNEL_UP, KeyEvent.KEYCODE_CHANNEL_DOWN,
						KeyEvent.KEYCODE_TELETEXT, KeyEvent.KEYCODE_TV };
				i.putExtra("Keys", KeyArray);
				startActivitySafty(i);
				Log.d(TAG, "OnLockStatusChanged LockOSD");
				IntentFilter mychioseapp = new IntentFilter();
				mychioseapp.addAction("org.droidtv.intent.action.mychoice_pin_result");
				mContext.registerReceiver(HtvMychoiceAppReceiver, mychioseapp);

			}
		} else {
			status = true;
		}
		return status;
	}

	public void startActivitySafty(Intent intent) {
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		mContext.startActivity(intent);
	}

	private BroadcastReceiver HtvMychoiceAppReceiver = new BroadcastReceiver() {
		private final static String MY_MESSAGE = "org.droidtv.intent.action.mychoice_pin_result";

		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equalsIgnoreCase(MY_MESSAGE)) {
				String results = intent.getStringExtra("mychoice_result");
				if (results.equals("ok")) {
					Log.d(TAG, "pin code verify ok");
				} else {
					Log.d(TAG, "pin code verify invalid");
				}
			}

		}
	};

	private void registerTimeObserver() {
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
		mContext.registerReceiver(mBroadcastReceiver, mIntentFilter);
		timeobserver = true;
	}

	private void unregisterTimeObserver() {
		if (mBroadcastReceiver != null && timeobserver) {
			unregisterReceiver(mBroadcastReceiver);
			timeobserver =  false;
		}
	}

	private void myChoiceStatusChanged() {
		Intent it =  new Intent(MYCHOICE_STATUS_CHANGED);
		sendBroadcast(it);
		Log.v(TAG, "myChoiceStatusChanged");
	}

	public void setMyChoiceTimer(String timerDuration) {
		if (!getMyChoiceStatus() || (timerDuration.length() != 8)) {
			return;
		}

		// init time observer statuse
//		long temp_time = (long) System.currentTimeMillis() / 1000;
//		mTvSettingMgr.putInt(TvSettingsConstants.MYCHOICE_STARTED_TIME_HIGH, 0, (int) (temp_time >> 16));
//		mTvSettingMgr.putInt(TvSettingsConstants.MYCHOICE_STARTED_TIME_LOW, 0, (int) (temp_time & 0xFFFF));

		Log.v(TAG, "timerDuration: " + timerDuration);

		if ((!timerDuration.equals("00000000"))) {
			//start observe time change
			//registerTimeObserver();
			realeaseMyChoiceTimer();
			timerisrunning = true;

			int intValue = Integer.valueOf(timerDuration);
			int intMSec = (intValue % 100 * 1000);
			intValue = (intValue / 1000000 * 24 * 60) + (intValue % 1000000 / 10000 * 60) + (intValue % 10000 / 100);

			Log.v(TAG, "intMSec: " + intMSec);

			localProgTimerTask = new TimerTask() {
				@Override
				public void run() {
					int index = mTvSettingMgr.getInt(TvSettingsConstants.MYCHOICE_PACKAGE_IDX, 0, 0);
					String remainingTime = mMyChoiceManager.getBdsString(2);
					String mNotificationMessage = "MyChoice Package" + Integer.toString(index) + " expires in "
							+ remainingTime.substring(4, 6)
							+ " minutes. Please get a new PIN for uninterrupted MyChoice usage";

					if (timeOutTvToast != null) {
						msgToast.cancelTvToastMessage(timeOutTvToast);
					}
					msgToast = TvToastMessenger.getInstance(mContext);
					timeOutTvToast = TvToastMessenger.makeTvToastMessage(TvToastMessenger.TYPE_TIME_OUT,
							mNotificationMessage,
							org.droidtv.ui.tvwidget2k15.R.drawable.icon_punched_push_m_alert_n_36x36);
					msgToast.showTvToastMessage(timeOutTvToast);
					stopMyChoiceUsageTimer(remainingTime);
				}
			};
			localProgTimer = new Timer();

			if (intValue > 30) {
				long convertmill = java.util.concurrent.TimeUnit.MILLISECONDS.convert((intValue - 30),
						java.util.concurrent.TimeUnit.MINUTES);
				localProgTimer.schedule(localProgTimerTask, convertmill + intMSec);
			} else {
				localProgTimer.schedule(localProgTimerTask, 5000);
			}
		} else {

		}
	}

	private void showPinEnterActivity() {
		Intent guesttrigger = new Intent();
		guesttrigger.setAction("org.droidtv.intent.action.MYCHOICE_PIN_DIALOG");
		guesttrigger.putExtra("Header", "MyChoice");
		guesttrigger.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(guesttrigger);
	}

	private void stopMyChoiceUsageTimer(String timerDuration) {

		int intValue = Integer.valueOf(timerDuration);
		int intMSec = (intValue % 100 * 1000);
		intValue = (intValue / 1000000 * 24 * 60) + (intValue % 1000000 / 10000 * 60) + (intValue % 10000 / 100);

		localProgTimerTask = new TimerTask() {
			@Override
			public void run() {
				// add by Daemon.yu
				mMyChoiceManager.getBdsStatus(3);
				//unregisterTimeObserver();
				realeaseMyChoiceTimer();
				timerisrunning =  false;
				showPinEnterActivity();
			}
		};
		localProgTimer = new Timer();
		long convertmill = java.util.concurrent.TimeUnit.MILLISECONDS.convert(intValue,
				java.util.concurrent.TimeUnit.MINUTES);
		localProgTimer.schedule(localProgTimerTask, convertmill + intMSec);
	}
}
