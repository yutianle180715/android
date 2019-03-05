package org.droidtv.welcome.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class WelcomeProvider extends ContentProvider {
	static final String AUTHORITY = WelcomeContract.CONTENT_PROVIDER_AUTHORITY;
	public static final int INCOMING_SCHEDULER_COLLECTION = 1;
	public static final int INCOMING_SCHEDULER_SINGLE = 2;

	// private SchedulerDbHelper mSchedulerDbHelper;
	private WelcomeDBAdapter mWelcomeDBAdapter;

	static class SqlArguments {
		public final String table;
		public final String where;
		public final String[] args;

		SqlArguments(Uri url, String where, String[] args) {
			if (url.getPathSegments().size() == 1) {
				this.table = url.getPathSegments().get(0);
				this.where = where;
				this.args = args;
			} else if (url.getPathSegments().size() != 2) {
				throw new IllegalArgumentException("Invalid URI: " + url);
			} else if (!TextUtils.isEmpty(where)) {
				throw new UnsupportedOperationException(
						"WHERE clause not supported: " + url);
			} else {
				this.table = url.getPathSegments().get(0);
				this.where = "_id=" + ContentUris.parseId(url);
				this.args = null;
			}
		}

		SqlArguments(Uri url) {
			if (url.getPathSegments().size() == 1) {
				table = url.getPathSegments().get(0);
				where = null;
				args = null;
			} else {
				throw new IllegalArgumentException("Invalid URI: " + url);
			}
		}

	}
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mWelcomeDBAdapter = new WelcomeDBAdapter(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(args.table);

		SQLiteDatabase db = mWelcomeDBAdapter.getDb(true);
		Cursor result = qb.query(db, projection, args.where, args.args, null,
				null, sortOrder);
		result.setNotificationUri(getContext().getContentResolver(), uri);

		return result;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		SqlArguments args = new SqlArguments(uri, null, null);
		if (TextUtils.isEmpty(args.where)) {
			return "vnd.android.cursor.dir/" + args.table;
		} else {
			return "vnd.android.cursor.item/" + args.table;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = mWelcomeDBAdapter.getDb(true);
		long rowId = db.insert(WelcomeContract.TABLE_NAME, null, values);
		if (rowId <= 0)
			return uri;

		Uri insertedUserUri = ContentUris.withAppendedId(
				WelcomeContract.CONTENT_URI, rowId);
		sendNotify(insertedUserUri);
		return insertedUserUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

		SQLiteDatabase db = mWelcomeDBAdapter.getDb(true);
		int count = db.delete(args.table, args.where, args.args);
		if (count > 0) {
			sendNotify(uri);
		}
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		SqlArguments arg = new SqlArguments(uri, selection, selectionArgs);

		SQLiteDatabase db = mWelcomeDBAdapter.getDb(true);
		int count = db.update(arg.table, values, arg.where, arg.args);
		if (count > 0) {
			sendNotify(uri);
		}
		return count;
	}

	private void sendNotify(Uri uri) {
		// TODO Auto-generated method stub
		getContext().getContentResolver().notifyChange(uri, null);
	}

}
