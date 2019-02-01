package org.droidtv.mychoice;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import org.droidtv.ui.tvwidget2k15.utils.LogHelper;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.droidtv.tv.persistentstorage.TvSettingsConstants;
import org.droidtv.tv.persistentstorage.ITvSettingsManager;
import org.droidtv.tv.mychoice.MyChoiceManager;
import android.widget.ImageView;
import android.content.DialogInterface.OnDismissListener;
import org.droidtv.ui.tvwidget2k15.dialog.ModalDialog;
import org.droidtv.ui.tvwidget2k15.dialog.ModalDialogInterface;
import android.text.method.NumberKeyListener;
import android.text.InputType;
import android.text.InputFilter;
import android.text.Spanned;
import android.os.Message;
import android.view.Window;
import android.os.SystemProperties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import org.droidtv.ui.tvwidget2k15.tvtoast.TvToast;
import org.droidtv.ui.tvwidget2k15.tvtoast.TvToastMessenger;

import org.droidtv.tv.logger.ILogger;
import org.droidtv.tv.logger.ILogger.MyChoicePINValidation;
import org.droidtv.mychoice.osds.MyChoiceLogger;

import org.droidtv.ui.tvwidget2k15.inputPicker.InputPicker.IInputPickerValueChangeListener;
import org.droidtv.ui.tvwidget2k15.inputPicker.InputPicker.IInputPickerValueConfirmation;
import org.droidtv.ui.tvwidget2k15.inputPicker.InputPicker.IInputPickerValueEntered;
import org.droidtv.ui.tvwidget2k15.inputPicker.InputPicker;

import org.droidtv.htv.provider.HtvContract.HtvMyChoiceChannelList;
import org.droidtv.htv.provider.HtvContract.HtvChannelSetting;
import org.droidtv.htv.provider.HtvContract.HtvThemeTvSetting;
import org.droidtv.htv.provider.HtvContract;
import android.media.tv.TvContract.Channels;
import android.media.tv.TvContract;
import android.database.Cursor;

/* ============ BEGIN HTV Changed for Application Control ============ */
import org.droidtv.ui.tvwidget2k15.utils.LogHelper;
import org.droidtv.htv.japit.IJapitApplicationControl;
import org.droidtv.htv.japit.IJapitApplicationControl.ApplicationType;
import org.droidtv.htv.japit.IJapitApplicationControl.AppAvailableStatus;
import org.droidtv.htv.japit.IJapitApplicationControl.AppRequestState;
import org.droidtv.htv.japit.IJapitApplicationControl.IJapitApplicationControlCallBack;
/* ============ END HTV Changed for Application Control ============ */

import org.droidtv.htv.htvmychoice.IMychoiceControl;

public class MyChoicePinDialog extends Activity implements OnClickListener, OnDismissListener {

	private static String TAG = "MyChoicePinDialog";
	int MAX_LENGTH = 6;
	Bundle mextras;
	// ModalDialog msgDialog;
	InputPicker mIp00, mIp01, mIp02, mIp03, mIp04, mIp05;
	List<InputPicker> mInputPickers;
	int cursorofip = 0;
	// InputPicker[] mInputPickers;
	TextView TitleRemainTime, TitleWrongPin;
	String trt = "", twp = "";
	Button btn_cancel, btn_ok1;
	ITvSettingsManager lSettingsngr;
	private ITvSettingsManager mtvSettings = ITvSettingsManager.Instance.getInterface();

	// TD: where should these constants be defined
	public static final int SUCCESS = 7;
	public static final int PIN_NOT_SET = 1;
	public static final int CANCELLED = 2;
	public static final int EXITKEYPRESSED = 3;
	public static final int CLOSED_ON_KEY = 4;
	private static final int MSG_WHAT_UNLOCK = 5;
	public static final int CANCELLED_ONPAUSE = 6;

	public static final String START_FROM_PBSSERVER = "pbsserver";
	public static final String START_FROM_PLAYTV = "playtv";
	public static final int DEFAULT_KEYCODE = -1;

	// TD: Request Code to start the activity
	public static final int REQUEST_CODE = 1;
	private static final String MYCHOICE_JAPIT_COMMAND_READY = "org.droidtv.tv.intent.action.japit.JAPIT_TO_MYCHOICE_ACTION";
	private static final String MYCHOICE_STATUS_CHANGED = "org.droidtv.intent.action.japit.MYCHOICE_STATUS_CHANGED";
	private static final String MYCHOICE_PIN_DIALOGUE_DISMISS = "org.droidtv.mychoice.MYCHOICE_PIN_DIALOGUE_DISMISS";

	private int[] KeysRecieved;
	private String mEnteredPassword = "";
	private boolean isFinishing;
	private boolean isActStarted = false;
	private MyChoiceManager mMyChoiceManager = new MyChoiceManager();
	private TvToastMessenger msgToast = null;
	private TvToast timeOutTvToast = null;
	private TvToast timeDuringToast = null;
	private Context mContext;
	private boolean isRegisteredForMyChoiceStatusBc = false;
	/* ============ BEGIN HTV Changed for Application Control ============ */
	private final int MODE_CONSUMER = 0;
	private final int MODE_GUEST = 1;
	private final int MODE_PBS = 2;
	private int mnActNow = MODE_GUEST; /* default GUEST mode */
	private IJapitApplicationControl mJapitAppControl = null;
	int appType[] = { ApplicationType.APP_TYPE_MYCHOICE_PIN_ENTRY.ordinal() };
	private int currentAppType = ApplicationType.APP_TYPE_MYCHOICE_PIN_ENTRY.ordinal();
	/* ============ END HTV Changed for Application Control ============ */
	private IMychoiceControl mIMychoiceControl = null;
	private boolean isMyChoiceInVokedServiceBinded = false;
	private boolean isResultSet = false;
	private String headertext = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		lSettingsngr = ITvSettingsManager.Instance.getInterface();
		MyChoiceLogger.getInstance(this).bindToLoggingService();
		mContext = this;

		mextras = getIntent().getExtras();
		if (mextras == null) {
			LogHelper.d("MyChoicePinDialog", "no extras");
			finish();
			return;
		}
		headertext = mextras.getString("Header");
		KeysRecieved = mextras.getIntArray("Keys");
		LogHelper.d("MyChoicePinDialog", "extra is:" + mextras);

		if (msgToast == null) {
			msgToast = TvToastMessenger.getInstance(this);
		}

		int retval = mMyChoiceManager.getBdsStatus(0);

		if (((retval != 0) && (retval != 7) && (retval != 3)) || !isClockValid()) {
			timeOutTvToast = TvToastMessenger.makeTvToastMessage(TvToastMessenger.TYPE_TIME_OUT,
					getResources().getString(org.droidtv.ui.strings.R.string.HTV_MYCHOICE_NOT_AVAILABLE),
					org.droidtv.ui.tvwidget2k15.R.drawable.icon_punched_push_m_alert_n_36x36);
			msgToast.showTvToastMessage(timeOutTvToast);
			Log.v(TAG, "isClockValid: " + isClockValid());
			if (!isResultSet) {
				commonSetResult(CANCELLED, DEFAULT_KEYCODE);
				Intent it = new Intent("org.droidtv.intent.action.mychoice_pin_result");
				it.putExtra("mychoice_result", "notavailable");
				sendBroadcast(it);
			}
			finish();
		} else {
			if (needBindService(headertext)) {
				isMyChoiceInVokedServiceBinded = bindMyChoiceInVokedService();
			}
			showPinCodeDialog();
			registerMyChoiceStatusObserver();
		}
	}

	private boolean needBindService(String headertext) {
		// TODO Auto-generated method stub
		boolean temp = false;
		if (headertext != null) {
			if (headertext.equalsIgnoreCase(START_FROM_PLAYTV)) {
				temp = true;
			}
		} else {
			temp = false;
		}

		return temp;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			Intent resIntent = new Intent();
			int lKeyCode = event.getKeyCode();

			if (lKeyCode == KeyEvent.KEYCODE_BACK) {
				commonSetResult(CANCELLED, DEFAULT_KEYCODE);
				isFinishing = true;
				finish();
				return true;
			} else if (KeysRecieved != null) {
				for (int i = 0; i < KeysRecieved.length; i++) {
					if (lKeyCode == KeysRecieved[i]) {
						resIntent.putExtra("Keys", lKeyCode);
						/*start*/
						setResult(CLOSED_ON_KEY, resIntent);
						if (mIMychoiceControl != null) {
							mIMychoiceControl.setResult(CLOSED_ON_KEY, lKeyCode);
							Log.v(TAG, "result: " + CLOSED_ON_KEY + ", keycode: " + lKeyCode);
						} else {
							Log.v(TAG, "mIMychoiceControl is null!");
						}
						isResultSet = true;
						/*end */
						isFinishing = true;
						finish();
						return true;
					}
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE) {
			isActStarted = false;

			if (resultCode == CANCELLED) {
				LogHelper.d(TAG, "resultCode = " + resultCode);
				commonSetResult(CANCELLED, DEFAULT_KEYCODE);
				isFinishing = true;
				finish();
			} else {
				showPinCodeDialog();
			}
		}
	}

	private boolean isClockValid() {
		String clockSource = SystemProperties.get("sys.droidtv.clock.source");
		Log.d(TAG, "current clock source = " + clockSource);
		if ((clockSource == null) || clockSource.isEmpty() || clockSource.equals("-1")) {
			return false;
		} else {
			return true;
		}
	}

	public void showPinCodeDialog() {
		SystemProperties.set("sys.htv.mychoice_pin_page", "ON");
		mInputPickers = new ArrayList<InputPicker>();
		LayoutInflater li = getLayoutInflater();
		View vw = li.inflate(R.layout.mychoice_dialog, null);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(vw);

		if (getIntent().getBooleanExtra("Remove", false) == true) {
			commonSetResult(CANCELLED, DEFAULT_KEYCODE);
			finish();
		}

		mIp00 = (InputPicker) findViewById(R.id.inputpicker00);
		mInputPickers.add(mIp00);
		mIp01 = (InputPicker) findViewById(R.id.inputpicker01);
		mInputPickers.add(mIp01);
		mIp02 = (InputPicker) findViewById(R.id.inputpicker02);
		mInputPickers.add(mIp02);
		mIp03 = (InputPicker) findViewById(R.id.inputpicker03);
		mInputPickers.add(mIp03);
		mIp04 = (InputPicker) findViewById(R.id.inputpicker04);
		mInputPickers.add(mIp04);
		mIp05 = (InputPicker) findViewById(R.id.inputpicker05);
		mInputPickers.add(mIp05);
		btn_cancel = (Button) findViewById(R.id.mychoice_pin_entry_cancel_button);
		btn_cancel.setOnClickListener(this);
		btn_ok1 = (Button) findViewById(R.id.mychoice_pin_entry_ok_button);
		btn_ok1.setOnClickListener(this);
		TitleRemainTime = (TextView) findViewById(R.id.title_remaining_time);
		TitleWrongPin = (TextView) findViewById(R.id.title_wrong_pin);
		initTv();
		initInputPickers();

		if(mJapitAppControl == null){bindToJapitApplicationControlService();}
	}

	private void registerMyChoiceStatusObserver() {
		IntentFilter mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(MYCHOICE_STATUS_CHANGED);
		mIntentFilter.addAction(MYCHOICE_PIN_DIALOGUE_DISMISS);
		mContext.registerReceiver(mBroadcastReceiver, mIntentFilter);
		isRegisteredForMyChoiceStatusBc = true;
//		Log.v("EAGLE", "registerMyChoiceStatusObserver");
	}

	private void unregisterMyChoiceStatusObserver() {
		if (mBroadcastReceiver != null) {
			unregisterReceiver(mBroadcastReceiver);
			isRegisteredForMyChoiceStatusBc = false;
		}
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(MYCHOICE_STATUS_CHANGED)) {
				int unlockstatus = mMyChoiceManager.getBdsStatus(2);
				Log.v(TAG,"unlockstatus: "+ unlockstatus);
				if (unlockstatus > 0) {
					MyChoicePinDialog.this.commonSetResult(7 + unlockstatus, DEFAULT_KEYCODE);
					finish();
				}
			} else if (action.equals(MYCHOICE_PIN_DIALOGUE_DISMISS)) {
				MyChoicePinDialog.this.commonSetResult(CANCELLED, DEFAULT_KEYCODE);
				finish();
			}
		}
	};

	private void initTv() {
		//000000 default value
		String remainingTime = mMyChoiceManager.getRemainTime();
		//0 locked; 1 pkg1 unlocked; 2 pkg2 unlocked
		int unlockstatus = mMyChoiceManager.getUnlockStatus();
		if (unlockstatus == 1) {
			trt = getResources().getString(org.droidtv.ui.strings.R.string.HTV_MYCHOICE_PKG1) + ":";
		} else if (unlockstatus == 2){
			trt = getResources().getString(org.droidtv.ui.strings.R.string.HTV_MYCHOICE_PKG2) + ":";
		}
		trt += getResources().getString(org.droidtv.ui.strings.R.string.HTV_MYCHOICE_VALID_PIN_MSG2);

		String regEx = "0[0-1]";
		Pattern pattern =  Pattern.compile(regEx);

		if (!remainingTime.substring(0, 2).equals("00")) {
			if (!pattern.matcher(remainingTime.substring(0, 2)).matches()) {
				trt += remainingTime.substring(0, 2) + " " + getResources().getString(org.droidtv.ui.strings.R.string.MAIN_DAYS) + " ";
			} else {
				trt += remainingTime.substring(0, 2) + " " + getResources().getString(org.droidtv.ui.strings.R.string.MAIN_DAY) + " ";
			}
			if (!pattern.matcher(remainingTime.substring(2, 4)).matches()) {
				trt += remainingTime.substring(2, 4) + " "
					+ getResources().getString(org.droidtv.ui.strings.R.string.MAIN_GUIDANCE_HOURS) + " ";
			} else {
				trt += remainingTime.substring(2, 4) + " "
					+ getResources().getString(org.droidtv.ui.strings.R.string.MAIN_GUIDANCE_HOUR) + " ";
			}
			if (!pattern.matcher(remainingTime.substring(4, 6)).matches()) {
				trt += remainingTime.substring(4, 6) + " "
					+ getResources().getString(org.droidtv.ui.strings.R.string.MAIN_MINUTES);
			} else {
				trt += remainingTime.substring(4, 6) + " "
					+ getResources().getString(org.droidtv.ui.strings.R.string.MAIN_MINUTE);
			}
		} else {
			if (!pattern.matcher(remainingTime.substring(2, 4)).matches()) {
				trt += remainingTime.substring(2, 4) + " "
					+ getResources().getString(org.droidtv.ui.strings.R.string.MAIN_GUIDANCE_HOURS) + " ";
			} else {
				trt += remainingTime.substring(2, 4) + " "
					+ getResources().getString(org.droidtv.ui.strings.R.string.MAIN_GUIDANCE_HOUR) + " ";
			}
			if (!pattern.matcher(remainingTime.substring(4, 6)).matches()) {
				trt += remainingTime.substring(4, 6) + " "
					+ getResources().getString(org.droidtv.ui.strings.R.string.MAIN_MINUTES);
			} else {
				trt += remainingTime.substring(4, 6) + " "
					+ getResources().getString(org.droidtv.ui.strings.R.string.MAIN_MINUTE);
			}
		}
		/*if (!remainingTime.substring(2, 4).equals("00")) {
			trt += remainingTime.substring(2, 4) + " "
					+ getResources().getString(org.droidtv.ui.strings.R.string.MAIN_GUIDANCE_HOURS) + " ";
		} else if (!remainingTime.substring(4, 6).equals("00")) {
			trt += remainingTime.substring(4, 6) + " " + getResources().getString(org.droidtv.ui.strings.R.string.MAIN_MINUTES);
		} else {
			trt += " 00:00:00";
		}*/

		TitleRemainTime.setText(trt);

	}

	private void initInputPickers() {
		Iterator it = mInputPickers.iterator();
		while (it.hasNext()) {
			InputPicker ip = (InputPicker) it.next();
			ip.setCurrValue(".");
			ip.setMinValue(0);
			ip.setMaxValue(9);
		}

		mIp00.setInputPickerValueChangeListener(new IInputPickerValueChangeListener() {
			@Override
			public void onInputPickerValueChanged(int value) {
				TitleWrongPin.setVisibility(View.INVISIBLE);
			}

		});
		mIp00.setValueConfirmListener(new IInputPickerValueConfirmation() {
			@Override
			public void onInputPickerValueConfirm(int value) {
				mIp01.requestFocus();
				TitleWrongPin.setVisibility(View.INVISIBLE);
			}
		});
		mIp00.setValueEnteredListener(new IInputPickerValueEntered() {
			@Override
			public void onInputPickerValueEntered(int value) {
				mIp01.requestFocus();
				TitleWrongPin.setVisibility(View.INVISIBLE);
			}
		});

		mIp01.setValueConfirmListener(new IInputPickerValueConfirmation() {
			@Override
			public void onInputPickerValueConfirm(int value) {
				mIp02.requestFocus();
			}
		});
		mIp01.setValueEnteredListener(new IInputPickerValueEntered() {
			@Override
			public void onInputPickerValueEntered(int value) {
				mIp02.requestFocus();
			}
		});

		mIp02.setValueConfirmListener(new IInputPickerValueConfirmation() {
			@Override
			public void onInputPickerValueConfirm(int value) {
				mIp03.requestFocus();
			}
		});
		mIp02.setValueEnteredListener(new IInputPickerValueEntered() {
			@Override
			public void onInputPickerValueEntered(int value) {
				mIp03.requestFocus();
			}
		});

		mIp03.setValueConfirmListener(new IInputPickerValueConfirmation() {
			@Override
			public void onInputPickerValueConfirm(int value) {
				mIp04.requestFocus();
			}
		});
		mIp03.setValueEnteredListener(new IInputPickerValueEntered() {
			@Override
			public void onInputPickerValueEntered(int value) {
				mIp04.requestFocus();
			}
		});

		mIp04.setValueConfirmListener(new IInputPickerValueConfirmation() {
			@Override
			public void onInputPickerValueConfirm(int value) {
				mIp05.requestFocus();
			}
		});
		mIp04.setValueEnteredListener(new IInputPickerValueEntered() {
			@Override
			public void onInputPickerValueEntered(int value) {
				mIp05.requestFocus();
			}
		});

		mIp05.setValueConfirmListener(new IInputPickerValueConfirmation() {
			@Override
			public void onInputPickerValueConfirm(int value) {
				btn_ok1.requestFocus();
			}
		});
		mIp05.setValueEnteredListener(new IInputPickerValueEntered() {
			@Override
			public void onInputPickerValueEntered(int value) {
				btn_ok1.requestFocus();
			}
		});
	}

	public void onClick(View v) {
		if (v.getId() == R.id.mychoice_pin_entry_cancel_button) {
			cancleButton();
		} else if (v.getId() == R.id.mychoice_pin_entry_ok_button) {
			mEnteredPassword = String.valueOf(mIp00.getCurrValue()) + String.valueOf(mIp01.getCurrValue()) + String.valueOf(mIp02.getCurrValue())
							+ String.valueOf(mIp03.getCurrValue()) + String.valueOf(mIp04.getCurrValue()) + String.valueOf(mIp05.getCurrValue());
			onPinEntered(mEnteredPassword);
		}
	}

	public void cancleButton() {
		commonSetResult(CANCELLED, DEFAULT_KEYCODE);
		isFinishing = true;
		Intent iPinResult = new Intent("org.droidtv.intent.action.mychoice_pin_result");
		iPinResult.putExtra("mychoice_result", "cancelbyuser");
		sendBroadcast(iPinResult);
		finish();
	}

	public void onPinEntered(String string) {
		char[] enteredPin = new char[MAX_LENGTH];
		int x = 0;
		// Remote Dianogisc
		int pkg1ok, pkg2ok, pkg1fail, pkg2fail;
		pkg1ok = lSettingsngr.getInt(TvSettingsConstants.MYCHOICE_DIANOGISC_PKG1_SUCESS, 0, 0);
		pkg2ok = lSettingsngr.getInt(TvSettingsConstants.MYCHOICE_DIANOGISC_PKG2_SUCESS, 0, 0);
		pkg1fail = lSettingsngr.getInt(TvSettingsConstants.MYCHOICE_DIANOGISC_PKG1_ERROR, 0, 0);
		pkg2fail = lSettingsngr.getInt(TvSettingsConstants.MYCHOICE_DIANOGISC_PKG2_ERROR, 0, 0);

		if (string != "") {
			enteredPin = string.toCharArray();
			// long startTime = System.currentTimeMillis();
			x = mMyChoiceManager.setBdsString(1, enteredPin, enteredPin.length);
			// long endTime = System.currentTimeMillis();
			/*
			 * timeDuringToast =
			 * TvToastMessenger.makeTvToastMessage(TvToastMessenger.
			 * TYPE_TIME_OUT, "check time: " + (endTime - startTime),
			 * org.droidtv.ui.tvwidget2k15.R.drawable.
			 * icon_punched_push_m_alert_n_36x36);
			 * msgToast.showTvToastMessage(timeDuringToast);
			 */
			// Log.v("EAGLE","during time: "+ (endTime - startTime) + "ms");
		}

		Intent iPinResult = new Intent("org.droidtv.intent.action.mychoice_pin_result");
		if ((x == 1) || (x == 2)) {
			String remainingTime = mMyChoiceManager.getBdsString(2);
			// String mNotificationMessage = "MyChoice
			// Package"+Integer.toString(x)+" Unlock. Pin is valid, remaining
			// time: ";
			String mNotificationMessage = getResources().getString(org.droidtv.ui.strings.R.string.HTV_MYCHOICE_VALID_PIN_MSG);;

			// Remote Dianogisc
			/*if (x == 1) {
				mNotificationMessage = getResources().getString(org.droidtv.ui.strings.R.string.HTV_MYCHOICE_PKG1);
				lSettingsngr.putInt(TvSettingsConstants.MYCHOICE_DIANOGISC_PKG1_SUCESS, 0, pkg1ok + 1);
			} else {
				mNotificationMessage = getResources().getString(org.droidtv.ui.strings.R.string.HTV_MYCHOICE_PKG2);
				lSettingsngr.putInt(TvSettingsConstants.MYCHOICE_DIANOGISC_PKG2_SUCESS, 0, pkg2ok + 1);
			}

			mNotificationMessage += " Unlock. Pin is valid, remaining time: ";

			if (!remainingTime.substring(0, 2).equals("00")) {
				mNotificationMessage += remainingTime.substring(0, 2) + " days, ";
				mNotificationMessage += remainingTime.substring(2, 4) + " hours ";
			} else if (!remainingTime.substring(2, 4).equals("00")) {
				mNotificationMessage += remainingTime.substring(2, 4) + " hours ";
			}
			mNotificationMessage += remainingTime.substring(4, 6) + " minutes";*/

			timeOutTvToast = TvToastMessenger.makeTvToastMessage(TvToastMessenger.TYPE_TIME_OUT, mNotificationMessage,
					org.droidtv.ui.tvwidget2k15.R.drawable.icon_punched_push_m_alert_n_36x36);
			msgToast.showTvToastMessage(timeOutTvToast);

			Intent intent_mychoice = new Intent("MyChoice");
			intent_mychoice.putExtra("PowerOn", 0);
			sendStickyBroadcast(intent_mychoice);

			commonSetResult(7 + x, DEFAULT_KEYCODE);

			iPinResult.putExtra("mychoice_result", "ok");
			sendBroadcast(iPinResult);
			finish();
		} else {
			// Remote Dianogisc
			lSettingsngr.putInt(TvSettingsConstants.MYCHOICE_DIANOGISC_PKG1_ERROR, 0, pkg1fail + 1);
			lSettingsngr.putInt(TvSettingsConstants.MYCHOICE_DIANOGISC_PKG2_ERROR, 0, pkg2fail + 1);
			// ui change
			mIp00.requestFocus();
			twp = getResources().getString(org.droidtv.ui.strings.R.string.HTV_MYCHOICE_WRONG_PIN_MSG);
			TitleWrongPin.setVisibility(View.VISIBLE);
			TitleWrongPin.setText(twp);

			// other change
			mEnteredPassword = "";
			iPinResult.putExtra("mychoice_result", "invalid");
			sendBroadcast(iPinResult);
		}

//		sendMyChoicePinLog();
	}

	@Override
	protected void onPause() {
		SystemProperties.set("sys.htv.mychoice_pin_page", "OFF");
		if (!isActStarted) {
			// if (msgDialog != null) {
			// msgDialog.hide();
			// }

			LogHelper.d(TAG, "isFinishing : " + isFinishing);
			if (isFinishing == true) {
				commonSetResult(CANCELLED, DEFAULT_KEYCODE);
			}
			// added for pbsserver to know Dialog is finished.
			if (!isResultSet) {
				commonSetResult(CANCELLED_ONPAUSE, DEFAULT_KEYCODE);
				Intent it = new Intent("org.droidtv.intent.action.mychoice_pin_result");
				it.putExtra("mychoice_result", "cancelonpause");
				sendBroadcast(it);
			}
			finish();
		}
		super.onPause();
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		LogHelper.d(TAG, "onDismiss()");
		commonSetResult(CANCELLED, DEFAULT_KEYCODE);
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		SystemProperties.set("sys.htv.mychoice_pin_page", "OFF");
		if (isRegisteredForMyChoiceStatusBc) {
			unregisterMyChoiceStatusObserver();
		}
		//unbind MyChoiceInVokedService
		unbindMyChoiceInVokedService();

		 /* ============ BEGIN HTV Changed for Application Control ============ */
        if(mJapitAppControl != null){
		  mJapitAppControl.setApplicationAvailability(currentAppType, AppAvailableStatus.APP_UNAVAILABLE.ordinal());
            mJapitAppControl.unRegisterJapitApplicationControlListener(appType);
            unbindService(servConn);
            mJapitAppControl = null;
        }
        /* ============ END HTV Changed for Application Control ============ */
	}

	/* ============ BEGIN HTV Changed for Application Control ============ */
	private void bindToJapitApplicationControlService() {
		if (mJapitAppControl != null) {
			Log.d(TAG, "has already bindToJapitApplicationControlService");
			return;
		}
		try {
			boolean isBindSuccess = false;
			Intent intent = new Intent(IJapitApplicationControl.JAPIT_APPLICATION_CONTROL_SERVICE_INTENT_ACTION);
			intent.setClassName("org.droidtv.japit", "org.droidtv.japit.services.JapitApplicationControlService");
			isBindSuccess = mContext.bindService(intent, servConn, Context.BIND_AUTO_CREATE);
			Log.d(TAG, "isBindSuccess: " + isBindSuccess);
		} catch (Exception e) {
			Log.d(TAG, "Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private ServiceConnection servConn = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			if (service != null) {
				Log.d(TAG, "setApplicationAvailability APP_AVAILABLE");
				mJapitAppControl = IJapitApplicationControl.Instance.asInterface(service);
				mJapitAppControl.registerJapitApplicationControlListener(appType, new AppControlCallback());
				mJapitAppControl.setApplicationAvailability(currentAppType, AppAvailableStatus.APP_AVAILABLE.ordinal());
			} else {
				LogHelper.d(TAG, "onServiceConnected : null binder");
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mJapitAppControl = null;
		}
	};

	private class AppControlCallback extends IJapitApplicationControlCallBack {
		@Override
		public int getApplicationAvailability(int appType) {
			if (currentAppType == appType) {
				return AppAvailableStatus.APP_AVAILABLE.ordinal();
			} else {
				return AppAvailableStatus.APP_UNAVAILABLE.ordinal();
			}
		}

		@Override
		public int getApplicationStatus(int appType) {
			return 0;
		}

		@Override
		public void requestApplicationStateChange(int appType, int appRequestState) {
			if ((appType == currentAppType) && (appRequestState == AppRequestState.APP_REQUEST_STOP.ordinal())) {
				if (MODE_GUEST == mnActNow) {
					finish();
				}
				return;
			}
		}
	}
	/* ============ END HTV Changed for Application Control ============ */

	private void sendMyChoicePinLog() {
		int pkg1 = 0, pkg2 = 0;
		Cursor cursor_mychoicepinlog = null;
		int pkg1ok, pkg2ok, pkg1fail, pkg2fail;

		pkg1ok = mtvSettings.getInt(TvSettingsConstants.MYCHOICE_DIANOGISC_PKG1_SUCESS, 0, 0);
		pkg2ok = mtvSettings.getInt(TvSettingsConstants.MYCHOICE_DIANOGISC_PKG2_SUCESS, 0, 0);
		pkg1fail = mtvSettings.getInt(TvSettingsConstants.MYCHOICE_DIANOGISC_PKG1_ERROR, 0, 0);
		pkg2fail = mtvSettings.getInt(TvSettingsConstants.MYCHOICE_DIANOGISC_PKG2_ERROR, 0, 0);

		String[] mProjection = { HtvChannelSetting.COLUMN_MEDIA_TYPE, HtvChannelSetting.COLUMN_PAYPKG1,
				HtvChannelSetting.COLUMN_PAYPKG2 };
		cursor_mychoicepinlog = getContentResolver().query(HtvContract.HtvMyChoiceChannelList.CONTENT_URI, mProjection,
				null, null, null);
		while (cursor_mychoicepinlog.moveToNext()) {
			String mediatype = cursor_mychoicepinlog.getString(0);
			boolean bTunerSrc = mediatype.contains("TYPE_TUNER");

			if (bTunerSrc) {
				if (cursor_mychoicepinlog.getInt(1) == 1)
					pkg1++;
				if (cursor_mychoicepinlog.getInt(2) == 1)
					pkg2++;
			}
		}

		ILogger.MyChoicePINValidation m = new ILogger.MyChoicePINValidation();
		m.list_name = "PKG1,PKG2";
		m.amt_channels = pkg1 + pkg2;// String.valueOf(pkg1) + "," +
										// String.valueOf(pkg2);
		m.pass_no_pin_tries = pkg1ok + pkg2ok;// String.valueOf(pkg1ok) + "," +
												// String.valueOf(pkg2ok);
		m.failed_no_pin_tries = pkg1fail + pkg2fail;// String.valueOf(pkg1fail)
													// + "," +
													// String.valueOf(pkg2fail);

		MyChoiceLogger.logMyChoicePin(m);
	}

	private void commonSetResult(int result, int keycode) {
		if (!isResultSet) {
			isResultSet = true;
			setResult(result);
			if (mIMychoiceControl != null) {
				mIMychoiceControl.setResult(result, keycode);
				Log.v(TAG, "result: " + result + ", keycode: " + keycode);
			} else {
				Log.v(TAG, "mIMychoiceControl is null!");
			}
		}
	}

	private boolean bindMyChoiceInVokedService() {
		// TODO Auto-generated method stub
		boolean temp = false;
		Intent it = new Intent("org.droidtv.mychoice.MyChoiceInVokedService");
		it.setPackage("org.droidtv.mychoice");
		temp = mContext.bindService(it, mServiceConnection, Context.BIND_AUTO_CREATE);
		return temp;
	}
	
	private void unbindMyChoiceInVokedService() {
		if (mIMychoiceControl != null && isMyChoiceInVokedServiceBinded) {
			if (mContext != null && mServiceConnection != null) {
				mContext.unbindService(mServiceConnection);
			}
		}
	}
	// bind to service MyChoiceInVokedService
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			if (mIMychoiceControl != null) {
				mIMychoiceControl = null;
			}
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mIMychoiceControl = IMychoiceControl.Instance.asInterface(service);
			Log.v(TAG, "bind MyChoiceInVokedService.");
		}
	};
}
