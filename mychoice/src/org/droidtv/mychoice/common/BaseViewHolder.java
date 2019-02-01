package org.droidtv.mychoice.common;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseViewHolder extends ViewHolder implements View.OnClickListener, View.OnFocusChangeListener{

	/* SparseArray is more efficient than Hashmap
	*  but only store the data which key is the int type
	*/
	private SparseArray<View> viewSparseArray;
	private onItemCommonClickListener commonClickListener;
	private onItemCommonFocuseChangedListener CommonFocuseChanged;

	public interface onItemCommonClickListener {
		void onItemClickListener(View v);
	}

	public interface onItemCommonFocuseChangedListener {
		void onItemFocuseChanged(View v, boolean hasFocus);
	}

	public void setCommonClickListener(onItemCommonClickListener commonClickListener) {
		this.commonClickListener = commonClickListener;
	}

	public void setCommonFocuseChangedListener(onItemCommonFocuseChangedListener commonFocusListener) {
		this.CommonFocuseChanged = commonFocusListener;
	}

	public BaseViewHolder(View itemView) {
		super(itemView);
		// TODO Auto-generated constructor stub
		itemView.setOnClickListener(this);
		viewSparseArray =  new SparseArray<View>();
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewid) {
		View view = viewSparseArray.get(viewid);
		if (view == null) {
			view = itemView.findViewById(viewid);
			if (view.isClickable()) {
				view.setOnClickListener(this);
			}
			if (view.isFocusable()) {
				view.setOnFocusChangeListener(this);
			}
			viewSparseArray.put(viewid, view);
		}
		return (T) view;
	}

	public BaseViewHolder setText(int viewId, CharSequence text) {
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}

	public BaseViewHolder setTextColor(int viewId, int textcolor) {
		TextView tv = getView(viewId);
		tv.setTextColor(textcolor);
		return this;
	}

	public BaseViewHolder setTextColor(int viewId, String textcolor) {
		TextView tv = getView(viewId);
		tv.setTextColor(Color.parseColor(textcolor));
		return this;
	}

	public BaseViewHolder setChecked(int viewId, boolean checked) {
		CompoundButton mrb = getView(viewId);
		mrb.setChecked(checked);
		return this;
	}

	public BaseViewHolder setChecked(int viewId, int checked) {
		CompoundButton mrb = getView(viewId);
		if (checked > 0) {
			mrb.setChecked(true);
		} else {
			mrb.setChecked(false);
		}
		return this;
	}

	public boolean getChecked(int viewId) {
		CompoundButton mrb = getView(viewId);
		return mrb.isChecked();
	}

	public BaseViewHolder setImageResource(int viewId, int resourceId) {
		ImageView imageView = getView(viewId);
		imageView.setImageResource(resourceId);
		return this;
	}

	public BaseViewHolder setLayoutView(int viewId) {
		ViewGroup vg = getView(viewId);
		vg.setFocusable(true);
		return this;
	}

	public BaseViewHolder setFocusView(int viewId) {
		View vg = getView(viewId);
		if (vg.isFocusable()) vg.requestFocus();
		return this;
	}

	public BaseViewHolder setImageResource(int viewId, Bitmap resource) {
		ImageView imageView = getView(viewId);
		imageView.setImageBitmap(resource);
		return this;
	}

	@SuppressWarnings("deprecation")
	public BaseViewHolder setImageBackgroud(int viewId, Drawable resource) {
		ImageView imageView = getView(viewId);
		imageView.setBackgroundDrawable(resource);
		return this;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (commonClickListener != null) {
			commonClickListener.onItemClickListener(v);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (CommonFocuseChanged != null) {
			CommonFocuseChanged.onItemFocuseChanged(v, hasFocus);
		}
	}

}
