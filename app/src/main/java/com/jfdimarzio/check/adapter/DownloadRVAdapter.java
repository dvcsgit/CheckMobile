package com.jfdimarzio.check.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.model.CheckDateFormatItem;
import com.jfdimarzio.check.model.DownloadFilter;
import com.jfdimarzio.check.model.enumtype.MRFormType;
import com.jfdimarzio.check.model.response.DownloadFormModel;
import com.jfdimarzio.check.model.response.DownloadParameter;
import com.jfdimarzio.check.model.response.RequestJobListItem;
import com.jfdimarzio.check.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;

public class DownloadRVAdapter extends BaseRvAdapter<DownloadRVAdapter.ViewHolder,RequestJobListItem> implements Filterable {
    private final Context mActivity;
    private final Gson mgson;
    private final int M_NORMAL=100;
    private final int M_REPAIR=200;
    private final int M_MFORM=300;
    private final String mlb_rform_date_range_prefix;
    private final String mlb_mform_date_range_prefix;
    private final String mlb_normal_date_range_prefix;

    private ArrayList<RequestJobListItem> mFilterDataSource;
    protected OnCusItemClickListener mListener;

    public DownloadRVAdapter(final Context context){
        this.mActivity=context;
        this.mDataSource=new ArrayList<>();
        this.mFilterDataSource=new ArrayList<>();
        this.mgson=new Gson();
        this.mlb_rform_date_range_prefix=mActivity.getString(R.string.lb_rform_date_range_prefix);
        this.mlb_mform_date_range_prefix=mActivity.getString(R.string.lb_mform_date_range_prefix);
        this.mlb_normal_date_range_prefix=mActivity.getString(R.string.lb_normal_date_range_prefix);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView;
        if(viewType==M_REPAIR){
            itemView= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_row_download_rform,parent,false);
        }else if(viewType==M_MFORM){
            itemView=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_row_download_mform,parent,false);
        }else {
            itemView=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_row_download_job,parent,false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position){
        final RequestJobListItem item=mDataSource.get(position);
        if(item.getMRFormType().equals(MRFormType.Repair)){
            holder.lb_form_type.setText(item.getFormType());
            holder.lb_vhno.setText(item.getVHNO());
            holder.lb_sub_title.setText(item.getSubject());

            if(!TextUtils.isEmpty(item.getEquipment())){
                holder.lb_equip_info.setVisibility(View.VISIBLE);
                holder.lb_equip_info.setText(item.getEquipment());
            }else {
                holder.lb_equip_info.setVisibility(View.GONE);
            }

            holder.lb_date_range.setText(mlb_rform_date_range_prefix+String.format("%1$s ~ %2$s",item.getBeginDateString(),item.getEndDateString()));
        }else if(item.getMRFormType().equals(MRFormType.MForm)){
            holder.lb_form_type.setText(item.getFormType());
            holder.lb_vhno.setText(item.getVHNO());
            holder.lb_sub_title.setText(item.getSubject());
            holder.lb_equip_info.setText(item.getEquipment());
            holder.lb_date_range.setText(mlb_mform_date_range_prefix+String.format("%1$s ~ %2$s",item.getBeginDateString(),item.getEndDateString()));
        }else{
            holder.lb_description.setText(item.getDescription());
            holder.lb_complete_rate.setText(item.getComplateRate());

            holder.switch_except.setChecked(item.isExceptChecked());

            holder.switch_except.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setIsExceptChecked(isChecked);
                }
            });
            String time_info="";
            if(!TextUtils.isEmpty(item.getBeginDate())){
                time_info+=item.getBeginTime();
            }
            if(!TextUtils.isEmpty(item.getEndDate())){
                time_info+="~"+item.getEndDate();
            }
            if(!TextUtils.isEmpty(time_info)){
                holder.lb_time_range.setText(mlb_normal_date_range_prefix+time_info);
                holder.lb_time_range.setVisibility(View.VISIBLE);
            }else{
                holder.lb_time_range.setText("");
                holder.lb_time_range.setVisibility(View.GONE);
            }
        }
        //Share
        holder.cb_jobId.setChecked(item.isSelected());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean new_checked=!item.isSelected();
                holder.cb_jobId.setChecked(new_checked);
                item.setIsSelected(new_checked);
                if(mListener!=null){
                    mListener.onItemClick(item,position);
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        //route
        public TextView lb_description;
        public TextView lb_complete_rate;
        public CheckBox cb_jobId;
        public Switch switch_except;
        public RelativeLayout area_route;
        public RelativeLayout area_repair;
        public TextView lb_time_range;
        //repair
        public TextView lb_vhno;
        public TextView lb_sub_title;
        public TextView lb_equip_info;
        public TextView lb_date_range;
        public TextView lb_form_type;

        public View view;

        public ViewHolder(View view){
            super(view);

            this.view=view;
            this.lb_description=view.findViewById(R.id.lb_description);
            this.lb_complete_rate=view.findViewById(R.id.lb_complete_rate);
            this.lb_time_range=view.findViewById(R.id.lb_time_range);
            this.cb_jobId=view.findViewById(R.id.cb_jobId);
            this.switch_except=view.findViewById(R.id.switch_except);
            this.area_route=view.findViewById(R.id.area_route);
            this.area_repair=view.findViewById(R.id.area_repair);

            this.lb_vhno=view.findViewById(R.id.lb_vhno);
            this.lb_sub_title=view.findViewById(R.id.lb_sub_title);
            this.lb_equip_info=view.findViewById(R.id.lb_equip_info);
            this.lb_date_range=view.findViewById(R.id.lb_date_range);
            this.lb_form_type=view.findViewById(R.id.lb_form_type);
        }
    }

    @Override
    public int getItemViewType(int position){
        int result=M_NORMAL;
        RequestJobListItem item=mDataSource.get(position);
        if(item.getMRFormType().equals(MRFormType.MForm)){
            result=M_MFORM;
        }else if(item.getMRFormType().equals(MRFormType.Repair)){
            result=M_REPAIR;
        }
        return result;
    }

    @Override
    public Filter getFilter(){
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint){
                final FilterResults oReturn=new FilterResults();
                final ArrayList<RequestJobListItem> results=new ArrayList<>();

                if(mFilterDataSource==null)
                    mFilterDataSource=mDataSource;
                if(!TextUtils.isEmpty(constraint)){
                    String critialRaw=constraint.toString();
                    //Timber.d("critialRaw:%s",critialRaw);

                    try{
                        DownloadFilter downloadFilter=mgson.fromJson(critialRaw,DownloadFilter.class);
                        if(downloadFilter.isCurrent()){
                            CheckDateFormatItem checkDateFormatItem=new CheckDateFormatItem();
                            int standMins=2*60;
                            Date maxDate= DateUtils.addMinsToDate(checkDateFormatItem.getDate(),standMins);
                            Date minDate=DateUtils.minusMinsToDate(checkDateFormatItem.getDate(),standMins);
                            if(mFilterDataSource!=null&mFilterDataSource.size()>0){
                                for(final RequestJobListItem item:mFilterDataSource){
                                    if(!TextUtils.isEmpty(item.getBeginDate())){
                                        String temp_date_str=checkDateFormatItem.getFORMAT_YYYYMMDD()+" "+item.getBeginDate();
//                                        Timber.d("temp_date_str:%s",temp_date_str);
                                        Date data_date=DateUtils.converStringToDate(temp_date_str,DateUtils.FORMAT_YYYYMMDD+" "+DateUtils.FORMAT_HHMM);
                                        if(data_date!=null){
                                            if(data_date.after(minDate)&&data_date.before(maxDate)){
                                                if(downloadFilter.getTypeFilter().containsKey(item.getMRFormType())){
                                                    results.add(item);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            oReturn.values=results;
                        }else{
                            if(mFilterDataSource!=null&mFilterDataSource.size()>0){
                                for(final RequestJobListItem item:mFilterDataSource){
                                    if(downloadFilter.getTypeFilter().containsKey(item.getMRFormType())){
                                        results.add(item);
                                    }
                                }
                            }
                            oReturn.values=results;
                        }
                    }catch (Exception e){
                        oReturn.values=mFilterDataSource;
                    }
                }else {
                    oReturn.values=mFilterDataSource;
                }
                return oReturn;
            }
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results){
                mDataSource=(ArrayList<RequestJobListItem>)results.values;
                notifyDataSetChanged();
                if(mListener!=null){
                    mListener.onFilterDone();
                }
            }
        };
    }

    public interface OnCusItemClickListener{
        void onItemClick(RequestJobListItem data,int position);
        void onFilterDone();
    }

    public void setCusmListener(OnCusItemClickListener mListener){
        this.mListener=mListener;
    }

    public void setmDataSource(ArrayList<RequestJobListItem> mDataSource){
        this.mDataSource=mDataSource;
        this.mFilterDataSource=mDataSource;
    }

    public int getSelectedCount(){
        int result=0;
        for(RequestJobListItem item:mDataSource){
            if(item.isSelected()){
                result+=1;
            }
        }
        return result;
    }

    public void setAllSelected(){
        for(RequestJobListItem item:mDataSource){
            item.setIsSelected(true);
        }
    }

    public void setAllUnSelected(){
        for(RequestJobListItem item:mDataSource){
            item.setIsSelected(false);
        }
    }

    public void reset(){
        if(mDataSource.size()>0){
            mDataSource.clear();
        }
        if(mFilterDataSource.size()>0){
            mFilterDataSource.clear();
        }
        this.notifyDataSetChanged();
    }

    public int getFilterItemCount(){
        return(mFilterDataSource!=null?mFilterDataSource.size():0);
    }

    public DownloadFormModel getDownloadFormModel(String checkDate){
        DownloadFormModel result=null;
        ArrayList<DownloadParameter> downloadParameterArrayList=new ArrayList<>();
        downloadParameterArrayList.add(new DownloadParameter("Manufacture"));
        if(downloadParameterArrayList.size()>0){
            result=new DownloadFormModel(checkDate,downloadParameterArrayList);
        }
        return result;
    }
}
