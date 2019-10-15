package com.jfdimarzio.check.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jfdimarzio.check.DashboardActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.model.JobItem;
import com.jfdimarzio.check.model.enumtype.MRFormType;
import com.jfdimarzio.check.widget.ArcProgress;

import java.util.ArrayList;

import timber.log.Timber;

public class Step1JobAdapter extends BaseRvAdapter<Step1JobAdapter.ViewHolder,JobItem> implements Filterable {
    private final String prefix_mform;
    private final String prefix_repair;
    private final int M_NORMAL=100;
    private final int M_REPAIR=200;
    private final int M_MFORM=300;
    private final DashboardActivity mActivity;
    private final int mColorResultOK;
    private final int mColorResultDefault;
    private final String mtemplate_should_arrive_time;
    private final String mtemplate_actual_arrive_time;
    private final String mtemplate_progress;
    private final Gson mgson;
    private final String mlb_rform_date_range_prefix;
    private final String mlb_mform_date_range_prefix;
    private ArrayList<JobItem>mFilterDataSource;

    public interface OnItemClickListener{
        void onItemClick(JobItem data,int position);
        void onItemLongClick(JobItem data,int position);
    }
    protected OnItemClickListener mCusListener;
    public void setmCusListener(OnItemClickListener mCusListener){
        this.mCusListener=mCusListener;
    }

    public Step1JobAdapter(final DashboardActivity context,ArrayList<JobItem> mDataSource){
        this.mActivity=context;
        this.mDataSource=mDataSource;
        this.mtemplate_should_arrive_time=mActivity.getResources().getString(R.string.template_should_arrive_time);
        this.mtemplate_actual_arrive_time=mActivity.getResources().getString(R.string.template_actual_arrive_time);
        this.mtemplate_progress=mActivity.getResources().getString(R.string.template_progress);
        this.prefix_mform=mActivity.getResources().getString(R.string.prefix_mform);
        this.prefix_repair=mActivity.getResources().getString(R.string.prefix_rform);
        this.mlb_mform_date_range_prefix=mActivity.getResources().getString(R.string.lb_mform_date_range_prefix);
        this.mlb_rform_date_range_prefix=mActivity.getResources().getString(R.string.lb_rform_date_range_prefix);
        this.mColorResultOK=context.getResources().getColor(R.color.result_ok);
        this.mColorResultDefault=context.getColor(R.color.result_default);
        this.mFilterDataSource=mDataSource;
        this.mgson=new Gson();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View itemView=null;
        if(viewType==M_REPAIR){
            itemView=LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_cardview_step1_rform,viewGroup,false);
        }else if(viewType==M_MFORM){

        }else{

        }
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position){
        int result=M_NORMAL;
        JobItem item=mDataSource.get(position);
        if(item.getMRFormType().equals(MRFormType.MForm)){
            result=M_MFORM;
        }else if(item.getMRFormType().equals(MRFormType.Repair)){
            result=M_REPAIR;
        }
        return result;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position){
        final JobItem item=this.getItem(position);
        //Repair
        holder.lb_form_type.setText(item.getFormType());
        holder.lb_vhno.setText(item.getVHNO());
        holder.lb_sub_title.setText(item.getSubject());
        if(!TextUtils.isEmpty(item.getEquipment())){
            holder.lb_equip_info.setVisibility(View.VISIBLE);
            holder.lb_equip_info.setText(item.getEquipment());
        }else {
            holder.lb_equip_info.setVisibility(View.GONE);
        }
        holder.lb_date_range.setText(mlb_rform_date_range_prefix+String.format("%1$s ~%2$s",item.getBegDate(),item.getEndDate()));

        if(this.mCusListener!=null){
            holder.view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mCusListener.onItemClick(item,position);
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn=new FilterResults();
                final ArrayList<JobItem> results=new ArrayList<>();
                if(mFilterDataSource==null){
                    mFilterDataSource=mDataSource;
                }
                if(!TextUtils.isEmpty(constraint)){
                    String critialRaw=constraint.toString();
                    Timber.d("critialRaw:%s",critialRaw);
                }
                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView bl_route_name;
        public TextView lb_should_arrive_time;
        public TextView lb_actual_arrive_time;
        public TextView lb_unpatrol_reason;

        public ArcProgress arcProgress;
        public View area_status;
        public View view;

        public RelativeLayout area_route;
        public RelativeLayout area_repair;

        //Repair download.
        public TextView lb_vhno;
        public TextView lb_sub_title;
        public TextView lb_equip_info;
        public TextView lb_date_range;
        public TextView lb_form_type;

        public ViewHolder(View view){
            super(view);
            this.view=view;
            this.arcProgress=view.findViewById(R.id.arc_progress);
            this.area_status=view.findViewById(R.id.area_status);
            this.area_repair=view.findViewById(R.id.area_repair);
            this.lb_vhno=view.findViewById(R.id.lb_vhno);
            this.lb_sub_title=view.findViewById(R.id.lb_sub_title);
            this.lb_equip_info=view.findViewById(R.id.lb_equip_info);
            this.lb_date_range=view.findViewById(R.id.lb_date_range);
            this.lb_form_type=view.findViewById(R.id.lb_form_type);
        }
    }
}
