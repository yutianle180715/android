package org.droidtv.mychoice.common;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseRecyclerAdapter<T> extends Adapter<BaseViewHolder> {
	protected LayoutInflater layoutInflater;
	protected List<T> dataList = null;
	protected int layoutId;

	public BaseRecyclerAdapter(Context context, List<T> dataList, int layoutId) {
		super();
		this.layoutInflater = LayoutInflater.from(context);
		this.dataList = dataList;
		this.layoutId = layoutId;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public void onBindViewHolder(BaseViewHolder holder, int position) {
		// TODO Auto-generated method stub
		bindData(holder, dataList.get(position), position);
	}

	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// TODO Auto-generated method stub
		View itemView = layoutInflater.inflate(layoutId, parent, false);
		return new BaseViewHolder(itemView);
	}

	public abstract void bindData(BaseViewHolder holder, T data, int position);
}
