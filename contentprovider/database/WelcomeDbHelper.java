package org.droidtv.welcome.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.droidtv.welcome.database.WelcomeContract;
import org.droidtv.welcome.database.WelcomeContract.WelcomeEntry;

public class WelcomeDbHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "htvwelcome";

	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS "
			+ WelcomeContract.TABLE_NAME
			+ " ("
			+ WelcomeEntry.COLUMN_ID
			+ " INTEGER DEFAULT 0,"
			+ WelcomeEntry.COLUMN_PATH
			+ " TEXT DEFAULT '',"
			+ WelcomeEntry.COLUMN_NAME
			+ " TEXT PRIMARY KEY,"
			+ WelcomeEntry.COLUMN_MIME_TYPE
			+ " TEXT DEFAULT '',"
			+ WelcomeEntry.COLUMN_RES_TYPE
			+ " INTEGER DEFAULT 0,"
			+ WelcomeEntry.COLUMN_STATUS
			+ " INTEGER DEFAULT 0,"
			+ WelcomeEntry.COLUMN_ORDER
			+ " INTEGER DEFAULT 0,"
			+ WelcomeEntry.COLUMN_WIDTH
			+ " TEXT DEFAULT '',"
			+ WelcomeEntry.COLUMN_HEIGHT
			+ " TEXT DEFAULT ''"
			+ ");";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ WelcomeContract.TABLE_NAME;

	public WelcomeDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

}
