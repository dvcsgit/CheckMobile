package com.jfdimarzio.check.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jfdimarzio.check.BaseActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.model.UploadJobItem;
import com.jfdimarzio.check.model.enumtype.MRFormType;
import com.jfdimarzio.check.util.StringUtils;
import com.jfdimarzio.check.widget.ArcProgress;

import java.util.ArrayList;

public class UploadListAdapter extends BaseRvAdapter<UploadListAdapter.ViewHolder,UploadJobItem> {
    private final String prefix_mform;
    private final String prefix_repair;
    private final int M_NORMAL = 100;
    private final int M_REPAIR = 200;
    private final int M_MFORM = 300;

    private final BaseActivity mActivity;

    private final String mtemplate_actual_arrive_time;
    private final String mtemplate_should_arrive_time;
    private final String mtemplate_progress;
    private final String mlb_rform_date_range_prefix;
    private final String mlb_mform_date_range_prefix;

    public UploadListAdapter(final BaseActivity context, ArrayList<UploadJobItem> mDataSource) {
        this.mActivity = context;
        this.mDataSource = mDataSource;
        this.prefix_mform = mActivity.getResources().getString(R.string.prefix_mform);
        this.prefix_repair = mActivity.getResources().getString(R.string.prefix_rform);
        this.mtemplate_should_arrive_time = mActivity.getResources().getString(R.string.template_should_arrive_time);
        this.mtemplate_actual_arrive_time = mActivity.getResources().getString(R.string.template_actual_arrive_time);
        this.mtemplate_progress = mActivity.getResources().getString(R.string.template_progress);
        this.mlb_rform_date_range_prefix = mActivity.getString(R.string.lb_rform_date_range_prefix);
        this.mlb_mform_date_range_prefix = mActivity.getString(R.string.lb_mform_date_range_prefix);
    }

    @Override
    public int getItemViewType(int position) {
        int result = M_NORMAL;
        UploadJobItem item = mDataSource.get(position);
        if (item.getMRFormType().equals(MRFormType.MForm)) {
            result = M_MFORM;
        } else if (item.getMRFormType().equals(MRFormType.Repair)) {
            result = M_REPAIR;
        }
        return result;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == M_REPAIR) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_cardview_upload_rform, parent, false);
        } else if (viewType == M_MFORM) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_cardview_upload_mform, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_cardview_upload_job, parent, false);
        }

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final UploadJobItem item = this.getItem(position);
        if (item.isRepairForm()) {
            if (item.getMRFormType().equals(MRFormType.MForm)) {
                //定保單
                holder.lb_vhno.setText(item.getVHNO());
                holder.lb_equip_info.setText(item.getEquipmentName());
                holder.lb_description.setText(item.getDescription());

                if (TextUtils.isEmpty(item.getPartDescription())) {
                    holder.lb_part_description.setVisibility(View.GONE);
                } else {
                    holder.lb_part_description.setText(item.getPartDescription());
                    holder.lb_part_description.setVisibility(View.VISIBLE);
                }
                holder.lb_date_range.setText(mlb_mform_date_range_prefix + String.format("%1$s ~ %2$s", item.getBegDate(), item.getEndDate()));

            } else {
                //修復單
                holder.lb_vhno.setText(prefix_repair + ":" + item.getVHNO());
                holder.lb_sub_title.setText(item.getDescription());
//                holder.lb_description.setText(item.getDescription());
                if (!TextUtils.isEmpty(item.getSubject())) {
                    holder.lb_equip_info.setVisibility(View.VISIBLE);
                    holder.lb_equip_info.setText(String.format("%1$s/%2$s", item.getSubject(), item.getEquipment()));
                } else {
                    holder.lb_equip_info.setVisibility(View.GONE);
                }
                holder.lb_date_range.setText(mlb_rform_date_range_prefix + String.format("%1$s ~ %2$s", item.getBegDate(), item.getEndDate()));
                holder.lb_form_type.setText(item.getFormType());
            }

        } else {
            //派工
            holder.lb_route_name.setText(item.getDescription());

            String getBeginTime = item.getBeginTime();
            String getEndTime = item.getEndTime();

            //應到時間
            if (!TextUtils.isEmpty(getBeginTime) || !TextUtils.isEmpty(getEndTime)) {
                String temp_begin = !TextUtils.isEmpty(getBeginTime) ? StringUtils.toBeautyTime(getBeginTime) : " N/A ";
                String temp_end = !TextUtils.isEmpty(getEndTime) ? StringUtils.toBeautyTime(getEndTime) : " N/A ";
                holder.lb_should_arrive_time.setVisibility(View.VISIBLE);
                holder.lb_should_arrive_time.setText(String.format(mtemplate_should_arrive_time, temp_begin, temp_end));
            } else {
                holder.lb_should_arrive_time.setVisibility(View.GONE);
            }

            if (item.getArriveTime() != null) {
            } else {
                holder.lb_actual_arrive_time.setVisibility(View.GONE);
            }

            //未巡檢原因
            if (item.getUnPatrolRecordItem() != null) {
                holder.lb_unpatrol_reason.setVisibility(View.VISIBLE);
                holder.lb_unpatrol_reason.setText(item.getUnPatrolRecordItem().getDisplayReason());
            } else {
                holder.lb_unpatrol_reason.setVisibility(View.GONE);
                holder.lb_unpatrol_reason.setText("");
            }
        }

        // 通用
        holder.cb_jobId.setChecked(item.isSelected());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean new_checked = !item.isSelected();

                holder.cb_jobId.setChecked(new_checked);
                item.setIsSelected(new_checked);
                if (mListener != null) {
                    mListener.onItemClick(item, v, position);
                }
            }
        });

        holder.arc_progress.setProgress(item.getProgress());
        holder.arc_progress.setBottomText(String.format(mtemplate_progress, item.getUIDoneCount(), item.getUITotalCount()));
        if (item.getUIDoneCount() > 0) {

        } else {

        }
    }

    /**
     * 取得使用者勾選要上傳的資料
     *
     * @return
     */
    public ArrayList<String> getUploadIDList() {
        ArrayList<String> result = new ArrayList<>();
        for (UploadJobItem item : mDataSource) {
            if (item.isSelected()) {
                result.add(item.getUniqueID());
            }
        }
        return result;
    }

    public ArrayList<String> getUploadMFormIDList() {
        ArrayList<String> result = new ArrayList<>();
        for (UploadJobItem item : mDataSource) {
            if (item.isSelected() && item.getMRFormType().equals(MRFormType.MForm)) {
                result.add(item.getUniqueID());
            }
        }

        return result;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView lb_part_description;
        private final TextView lb_description;

        public CheckBox cb_jobId;
        public TextView lb_route_name;
        public TextView lb_should_arrive_time;
        public TextView lb_actual_arrive_time;
        public TextView lb_unpatrol_reason;
        public ArcProgress arc_progress;

        public View view;

        public RelativeLayout area_route;
        public RelativeLayout area_repair;

        //修復單下載
        public TextView lb_vhno;
        public TextView lb_sub_title;
        public TextView lb_equip_info;
        public TextView lb_date_range;
        public TextView lb_form_type;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.cb_jobId = (CheckBox) view.findViewById(R.id.cb_jobId);
            this.lb_route_name = (TextView) view.findViewById(R.id.lb_route_name);
            this.lb_should_arrive_time = (TextView) view.findViewById(R.id.lb_should_arrive_time);
            this.lb_actual_arrive_time = (TextView) view.findViewById(R.id.lb_actual_arrive_time);
            this.lb_unpatrol_reason = (TextView) view.findViewById(R.id.lb_unpatrol_reason);
            this.arc_progress = (ArcProgress) view.findViewById(R.id.arc_progress);

            this.area_route = (RelativeLayout) view.findViewById(R.id.area_route);
            this.area_repair = (RelativeLayout) view.findViewById(R.id.area_repair);

            this.lb_vhno = (TextView) view.findViewById(R.id.lb_vhno);
            this.lb_sub_title = (TextView) view.findViewById(R.id.lb_sub_title);
            this.lb_equip_info = (TextView) view.findViewById(R.id.lb_equip_info);
            this.lb_date_range = (TextView) view.findViewById(R.id.lb_date_range);
            this.lb_form_type = (TextView) view.findViewById(R.id.lb_form_type);
            this.lb_part_description = (TextView) view.findViewById(R.id.lb_part_description);
            this.lb_description = (TextView) view.findViewById(R.id.lb_description);
        }
    }
}
