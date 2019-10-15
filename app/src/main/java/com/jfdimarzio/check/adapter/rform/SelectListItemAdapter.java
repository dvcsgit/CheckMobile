package com.jfdimarzio.check.adapter.rform;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.jfdimarzio.check.model.rform.SelectListItem;

import java.util.ArrayList;

public class SelectListItemAdapter extends ArrayAdapter<SelectListItem> {
    //    private static final String TAG = LogUtils.makeLogTag(SelectListItemAdapter.class);
    private ArrayList<SelectListItem> mDataSource;
    private final int mLayoutId;

    public SelectListItemAdapter(Activity context, ArrayList<SelectListItem> dataSource, int layoutId) {
        super(context, 0);
        this.mLayoutId = layoutId;
        this.mDataSource = dataSource;
    }

    public int getCount() {
        return this.mDataSource != null ? this.mDataSource.size() : 0;
    }

    public SelectListItem getItem(int position) {
        return this.mDataSource != null ? (SelectListItem) this.mDataSource.get(position) : null;
    }

    public long getItemId(int position) {
        return this.mDataSource != null ? (long) ((SelectListItem) this.mDataSource.get(position)).hashCode() : 0L;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        SelectListItemAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(this.mLayoutId, parent, false);
            holder = new SelectListItemAdapter.ViewHolder();
            holder.text1 = (CheckedTextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (SelectListItemAdapter.ViewHolder) convertView.getTag();
        }

        SelectListItem dataHolder = (SelectListItem) this.mDataSource.get(position);
        holder.text1.setText(dataHolder.getText());
        return convertView;
    }

    public void setItemList(ArrayList<SelectListItem> itemList) {
        this.mDataSource = itemList;
    }

    public ArrayList<SelectListItem> getItemList() {
        return this.mDataSource;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        SelectListItemAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(this.mLayoutId, parent, false);
            holder = new SelectListItemAdapter.ViewHolder();
            holder.text1 = (CheckedTextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (SelectListItemAdapter.ViewHolder) convertView.getTag();
        }

        SelectListItem dataHolder = (SelectListItem) this.mDataSource.get(position);
        holder.text1.setText(dataHolder.getText());
        return convertView;
    }

    public static class ViewHolder {
        public CheckedTextView text1;
        int position;

        public ViewHolder() {
        }
    }
}
