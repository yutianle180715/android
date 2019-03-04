package org.droidtv.welcome.database;

import org.droidtv.welcome.database.WelcomeContract.WelcomeEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class WelcomeDBAdapter {
	public static final String TAG = "WelcomeDBAdapter";

//	public static final String KEY_ID = WelcomeEntry._ID;
	private Context mContext;

	private SQLiteDatabase db;
	private WelcomeDbHelper mWelcomeDbHelper;

	public WelcomeDBAdapter(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mWelcomeDbHelper = new WelcomeDbHelper(mContext);
	}

	public SQLiteDatabase getDb(boolean writeable) {
		this.open(writeable);
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

	public void open(boolean writeable) throws SQLiteException {
		if (writeable) {
			try {
				db = mWelcomeDbHelper.getWritableDatabase();
			} catch (SQLiteException e) {
				db = mWelcomeDbHelper.getReadableDatabase();
			}
		} else {
			db = mWelcomeDbHelper.getReadableDatabase();
		}
	}

	public void close() {
		if (db != null) {
			db.close();
			db = null;
		}
	}

}
