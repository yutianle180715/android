package org.droidtv.mychoice.util;

import org.droidtv.mychoice.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	private static final String TAG = ListViewAdapter.class.getName();

	private String[] mItemDataList;
	private Context mCtx;
	private int mResourceLayout = 0;
	private int mResourceSelectedStyle = 0;
	private int mResourceNonSelectedStyle = 0;
	private int mRoundedRectangle_SelectedStyle = 0;
	private int mRoundedRectangle_NonSelectedStyle = 0;
	private int mResourceCheckedIcon = 0;
	private boolean mIsEnableCheckedIcon = false;
	private boolean mIsItemSelected = false;
	private boolean mIsEnableItemWithIcon = false;
	private int mItemSelected = 0;
	private int[] mItemCheckedList = null;
	private int[] mIconForItemDataList = null;

	public ListViewAdapter(Context context, String[] itemsList) {
		this.mCtx = context;
		this.mItemDataList = itemsList;
	}

	public ListViewAdapter(Context context, String[] itemsList, int layout, int font_selectedStyle,
			int font_nonSelectedStyle) {
		this.mCtx = context;
		this.mItemDataList = itemsList;
		this.mResourceLayout = layout;
		this.mResourceSelectedStyle = font_selectedStyle;
		this.mResourceNonSelectedStyle = font_nonSelectedStyle;
		this.mRoundedRectangle_SelectedStyle = R.drawable.background_view_rounded_selected;
		this.mRoundedRectangle_NonSelectedStyle = R.drawable.background_view_rounded_non_selected;
	}

	@Override
	public int getCount() {
		int counter = 0;
		for (; counter < mItemDataList.length; ++counter) {
			if (mItemDataList[counter] == null)
				break;
		}
		return counter;
	}

	@Override
	public Object getItem(int arg0) {
		return mItemDataList[arg0];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/*
		View view = null;
		if ((mItemDataList.length > position) && (position >= 0)) {
			view = View.inflate(mCtx, mResourceLayout, null);

			TextView textView = (TextView) view.findViewById(R.id.list_item);
			textView.setText(mItemDataList[position]);

			if (mIsEnableCheckedIcon) {
				if (mItemCheckedList[position] > 0) {
					Drawable drawable = mCtx.getResources().getDrawable(mResourceCheckedIcon);
					drawable.setBounds(0, 0, 24, 24);
					textView.setCompoundDrawables(drawable, null, null, null);
					textView.setCompoundDrawablePadding(0);
				}
			}

			if (mIsItemSelected && (mItemSelected == position)) {
				textView.setTextAppearance(mCtx, mResourceSelectedStyle);
				textView.setSelected(true);
				textView.setBackgroundResource(mRoundedRectangle_SelectedStyle);
			} else {
				textView.setSelected(false);
				textView.setTextAppearance(mCtx, mResourceNonSelectedStyle);
				textView.setBackgroundResource(mRoundedRectangle_NonSelectedStyle);
			}

			int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			textView.measure(w, h);
			int width = ((TextView) view.findViewById(R.id.list_item)).getMeasuredWidth();
			textView.setWidth(width + 50);
		}
		*/
		View view = null;
		//Log.d("BEN","getView --- 3");
		if ((mItemDataList.length > position) && (position >= 0)) {
			view = View.inflate(mCtx, mResourceLayout, null);

			TextView textView = (TextView) view.findViewById(R.id.list_item);
			textView.setText(mItemDataList[position]);

			if (mIsEnableCheckedIcon) {
				if (mItemCheckedList[position] > 0) {
					Drawable drawable = mCtx.getResources().getDrawable(mResourceCheckedIcon);
					drawable.setBounds(0, 0, 21, 21);
					textView.setCompoundDrawables(drawable, null, null, null);
					if (mItemDataList[position].length() < 47) {
						textView.setCompoundDrawablePadding(-14);
						textView.setPaddingRelative(20, 0, 0, 0);
					} else {
						textView.setCompoundDrawablePadding(10);
						textView.setPaddingRelative(20, 0, 20, 0);
					}
				} else if (mItemDataList[position].length() > 50) {
					textView.setPaddingRelative(20, 0, 20, 0);
				}
			} else if (mIsEnableItemWithIcon) {
					Drawable drawable = mCtx.getResources().getDrawable(mIconForItemDataList[position]);
					drawable.setBounds(0, 0, 21, 21);
					textView.setCompoundDrawables(drawable, null, null, null);
					textView.setCompoundDrawablePadding(-14);
					textView.setPaddingRelative(20, 0, 0, 0);
			}

			if (mIsItemSelected && (mItemSelected == position)) {
				textView.setTextAppearance(mCtx, mResourceSelectedStyle);
				textView.setSelected(true);
				textView.setBackgroundResource(mRoundedRectangle_SelectedStyle);
			} else {
				textView.setSelected(false);
				textView.setTextAppearance(mCtx, mResourceNonSelectedStyle);
				textView.setBackgroundResource(mRoundedRectangle_NonSelectedStyle);
			}

			int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
			textView.measure(w, h);
			int width = ((TextView) view.findViewById(R.id.list_item)).getMeasuredWidth();
			textView.setWidth(width + 50);
		}

		return view;
	}

	public void setItemSelected(int position) {
		mIsItemSelected = (position < 0) ? false : true;
		mItemSelected = position;
	}

	public void setItemCheckedList(int[] checkedList, int resourceCheckedIcon) {
		mItemCheckedList = checkedList;
		mResourceCheckedIcon = resourceCheckedIcon;
		mIsEnableCheckedIcon = true;
	}

	public void setItemWithIcon(int[] iconList) {
		mIconForItemDataList = iconList;
		mIsEnableItemWithIcon = true;
	}
}
