package org.droidtv.mychoicecontrol;

import org.droidtv.htv.htvcontext.IHtvInterface;
import android.os.IBinder;
import org.droidtv.mychoicecontrol.IMyChoiceInVokedService;
import org.droidtv.mychoicecontrol.IMyChoiceInVokedServiceCallBack;
import android.os.RemoteException;
import org.droidtv.htv.htvmychoice.IMychoiceControl;
import org.droidtv.htv.htvmychoice.IMychoiceControl.IMyChoiceResultListener;

/*
*@hide
*
*/
public interface MyChoiceControlImpl {
	public static final String TAG = "MyChoiceControlImpl";

	public class Proxy implements IMychoiceControl {
		IMyChoiceInVokedService remote = null;

		public Proxy(IBinder service) {
			remote = IMyChoiceInVokedService.Stub.asInterface(service);
		}

		@Override
		public void setResult(int result, int keycode){
			try {
				remote.setResult(result, keycode);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void registerListenerCallback(IMyChoiceResultListener listener) {
			try {
				remote.registerMyChoiceListener(new MyChoiceControlImpl.MyChoiceInterfaceNotify.Stub(listener).asBinder());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void unregisterListenerCallback(IMyChoiceResultListener listener) {
			try {
				remote.unRegisterMyChoiceListener(new MyChoiceControlImpl.MyChoiceInterfaceNotify.Stub(listener).asBinder());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public class Stub extends IMyChoiceInVokedService.Stub {
		IMychoiceControl intf = null;

		public Stub(IHtvInterface service) {
			intf = (IMychoiceControl) service;
		}

		@Override
		public void setResult(int result, int keycode) throws RemoteException{
			intf.setResult(result, keycode);
		}
		@Override
		public void registerMyChoiceListener(IBinder listener) throws RemoteException{
			intf.registerListenerCallback((IMyChoiceResultListener)(new MyChoiceInterfaceNotify.Proxy(listener)));
		}
		@Override
		public void unRegisterMyChoiceListener(IBinder listener) throws RemoteException{
			intf.unregisterListenerCallback((IMyChoiceResultListener)(new MyChoiceInterfaceNotify.Proxy(listener)));
		}

	}

	public interface MyChoiceInterfaceNotify {
		public class Stub extends IMyChoiceInVokedServiceCallBack.Stub {
			IMyChoiceResultListener intf = null;

			public Stub(IMyChoiceResultListener service) {
				intf = (IMyChoiceResultListener) service;
			}

			@Override
			public void onResult(int result, int keycode) {
				intf.onResult(result, keycode);
			}
		}
		public class Proxy extends IMyChoiceResultListener {
			IMyChoiceInVokedServiceCallBack remote = null;

			public Proxy(IBinder service) {
				remote = IMyChoiceInVokedServiceCallBack.Stub.asInterface(service);
			}

			public void onResult(int result, int keycode) {
				try {
					remote.onResult(result, keycode);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		} 
	}


}
