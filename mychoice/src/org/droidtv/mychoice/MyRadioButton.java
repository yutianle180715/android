package org.droidtv.mychoice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CompoundButton;

public class MyRadioButton extends CompoundButton {

	public MyRadioButton(Context context) {
		this(context, null);
	}

	public MyRadioButton(Context context, AttributeSet attrs) {
		this(context, attrs, com.android.internal.R.attr.radioButtonStyle);
		// TODO Auto-generated constructor stub
	}

	public MyRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, 0);
		// TODO Auto-generated constructor stub
	}

	public MyRadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Drawable[] drawables = getCompoundDrawables();
		Drawable drawable = drawables[0];
		int gravity = getGravity();
		int offset = 10;
		int left = 0;
		int top = 0;
		if (gravity == Gravity.CENTER) {
//			left = ((int) (getWidth() - drawable.getIntrinsicWidth() - getPaint().measureText(getText().toString()))/2);
//			top = ((int) (getHeight() - drawable.getIntrinsicHeight() - getPaint().measureText(getText().toString()))/2);
			left = ((int) (getWidth() - drawable.getIntrinsicWidth())/2);
			top = ((int) (getHeight() - drawable.getIntrinsicHeight())/2);
		}
		drawable.setBounds(left, top, left + (drawable.getIntrinsicWidth()/2) + offset, top + offset +(drawable.getIntrinsicHeight()/2));
	}

}
