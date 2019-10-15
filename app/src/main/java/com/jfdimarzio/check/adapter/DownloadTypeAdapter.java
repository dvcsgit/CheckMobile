package com.jfdimarzio.check.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jfdimarzio.check.BaseActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.model.DownloadTypeItem;
import com.jfdimarzio.check.model.enumtype.MRFormType;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadTypeAdapter extends RecyclerView.Adapter<DownloadTypeAdapter.ViewHolder>{

    private final BaseActivity mActivity;
    private final int disableColor;
    private ArrayList<DownloadTypeItem> mDataSource;
    private OnItemClickListener mOnItemClickListener;

    public DownloadTypeAdapter(final BaseActivity context,ArrayList<DownloadTypeItem> mDataSource){
        this.mActivity=context;
        this.mDataSource=mDataSource;
        disableColor= ContextCompat.getColor(context, R.color.grey_500);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cardview_download_type,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position){
        final DownloadTypeItem item=mDataSource.get(position);

        if(item.getTotal()!=0){
            holder.lb_name.setText(item.getTagName()+"("+String.valueOf(item.getTotal()+")"));
        }else{
            holder.lb_name.setText(item.getTagName());
        }

        if(item.isSelected()){
            holder.lb_name.setBackgroundColor(item.getColor());
        }else{
            holder.lb_name.setBackgroundColor(disableColor);
        }

        if(mOnItemClickListener!=null){
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(item,v,position);
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DownloadTypeItem item,View view,int position);
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener=mOnItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView lb_name;
        public View view;
        public ViewHolder(View view){
            super(view);

            this.view=view;
            this.lb_name=view.findViewById(R.id.lb_name);
        }
    }

    @Override
    public int getItemCount(){
        return (mDataSource!=null?mDataSource.size():0);
    }

    public void resetCount(){
        if(mDataSource.size()>0){
            for(DownloadTypeItem item:mDataSource){
                item.setTotal(0);
            }
        }
        this.notifyDataSetChanged();
    }

    public void updateCount(HashMap<MRFormType,Integer> formTypeCountMap){
        for(DownloadTypeItem item:mDataSource){
            if(formTypeCountMap.containsKey(item.getType())) {
                item.setTotal(formTypeCountMap.get(item.getType()));
            }
        }
        this.notifyDataSetChanged();
    }

    public HashMap<MRFormType,MRFormType> getSelectedList(){
        HashMap<MRFormType,MRFormType> result=new HashMap<>();
        for(DownloadTypeItem item:mDataSource){
            if(item.isSelected()){
                result.put(item.getType(),item.getType());
            }
        }
        return result;
    }
}
