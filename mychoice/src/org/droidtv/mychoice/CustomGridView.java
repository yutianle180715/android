package org.droidtv.mychoice;

import android.widget.GridView;
import android.content.Context;
import android.util.AttributeSet;

public class CustomGridView extends GridView {

	public CustomGridView(Context context) {
		super(context);
	}

	public CustomGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/* ADD THIS */
	@Override
	public int computeVerticalScrollOffset() {
		return super.computeVerticalScrollOffset();
	}

	public int computeHorizontalScrollOffset() {
		return super.computeVerticalScrollOffset();
	}
}



