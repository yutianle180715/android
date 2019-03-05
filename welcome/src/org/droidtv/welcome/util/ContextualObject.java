package org.droidtv.welcome.util;

import android.content.Context;

public abstract class ContextualObject {
	private Context mContext;

    protected ContextualObject(Context context) {
        mContext = context;
    }

    protected Context getContext() {
        return mContext;
    }
}
