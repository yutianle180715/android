package org.droidtv.welcome.util;

import android.net.Uri;
import android.util.Pair;

public interface CloneFileUriListener {
	
	void cloneInFileUriCreated(Pair<Uri, Uri> uri);

	void cloneOutFileUriCreated(Pair<Uri, Uri> uri);

}
