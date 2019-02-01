package org.droidtv.mychoicecontrol;

import android.os.IBinder;

interface IMyChoiceInVokedService {
	void setResult(int result, int keycode);
	void registerMyChoiceListener(IBinder listener);
	void unRegisterMyChoiceListener(IBinder listener);

}
