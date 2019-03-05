package org.droidtv.welcome.message;

import android.content.Context;
import android.util.Log;

import com.philips.professionaldisplaysolutions.jedi.jedifactory.JEDIFactory;
import com.philips.professionaldisplaysolutions.jedi.professionalsettings.IIdentificationSettings;
import com.philips.professionaldisplaysolutions.jedi.IPMS;
/*import com.philips.professionaldisplaysolutions.jedi.PMS;*/
import com.philips.professionaldisplaysolutions.jedi.IPMS.GuestDetails;
import com.philips.professionaldisplaysolutions.jedi.IPMS.GuestMessage;

public class MessageHelper {
	private MessageInfo mMsgInfo = null;
	private Context mContext;
	private String TAG = "WelcomeMessageHelper";
	public MessageHelper(Context context) {
		mContext = context;
		mMsgInfo = new MessageInfo();
	}

	public String getPremisesName(){
		mMsgInfo.sHotleName = "";
		mMsgInfo.sHotleName = JEDIFactory.getInstance(IIdentificationSettings.class).getPremisesName();
		return mMsgInfo.sHotleName;
	}

	public String getGuestName(IPMS pms){
	//	 IPMS.GuestDetails guestDetails = pms.getGuestDetails();
		mMsgInfo.sGuestName = "";
		GuestDetails guestDetails = null;
		try {
			 guestDetails = pms.getGuestDetails();
			 if(guestDetails != null){
				 mMsgInfo.sGuestName = guestDetails.displayName;
				 Log.d(TAG, "MessageHelper: getGuestName(PMS pms): " + mMsgInfo.sGuestName);
			 }else{
				 Log.d(TAG, "MessageHelper: getGuestName(PMS pms): is null");
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mMsgInfo.sGuestName;
	}

/*The IPMStatusNotification of JEDI's API has been changed to IPMStatusCallback,then registerCallback() and unRegisterCallback() don't used in welcome.
 * 	 public void registerCallback(Context context,IPMS pms,IPMS.IPMStatusNotification pmStatusNotification) {
         pms.registerCallback(context, pmStatusNotification);
    }

	 public void unRegisterCallback(Context context,IPMS pms,IPMS.IPMStatusNotification pmStatusNotification) {
		 pms.unRegisterCallback(context, pmStatusNotification);
	}*/

}
