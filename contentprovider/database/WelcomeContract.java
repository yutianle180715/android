package org.droidtv.welcome.database;

import android.net.Uri;
import android.provider.BaseColumns;

public final class WelcomeContract {
	public static final String CONTENT_PROVIDER_AUTHORITY = "org.droidtv.htv.philipswelcome";
	public static final String TABLE_NAME = "welcomes_stored_files";

	public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_PROVIDER_AUTHORITY + "/" + TABLE_NAME);
	
	public static class WelcomeEntry{ //implements BaseColumns {
		public static final String COLUMN_ID = "_id";
		public static final String COLUMN_PATH = "path";
		public static final String COLUMN_NAME = "name";
		public static final String COLUMN_MIME_TYPE = "mime_type";
		public static final String COLUMN_RES_TYPE = "res_type";
		public static final String COLUMN_STATUS = "status";
		public static final String COLUMN_ORDER = "orderindex";
		public static final String COLUMN_WIDTH = "width";
		public static final String COLUMN_HEIGHT = "height";
	}
}
