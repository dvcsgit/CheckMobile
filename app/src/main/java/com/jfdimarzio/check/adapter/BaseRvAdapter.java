package com.jfdimarzio.check.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class BaseRvAdapter<VH extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<VH> {
    protected ArrayList<T> mDataSource;
    protected OnItemClickListener mListener;

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
    }

    @Override
    public int getItemCount() {
        return (mDataSource != null ? mDataSource.size() : 0);
    }

    public T getItem(int position) {
        return (mDataSource != null ? mDataSource.get(position) : null);
    }

    public void setmListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    public interface OnItemClickListener<T> {
        //        void onItemClick(T data,int position);
        void onItemClick(T var1, View var2, int var3);
    }
}
