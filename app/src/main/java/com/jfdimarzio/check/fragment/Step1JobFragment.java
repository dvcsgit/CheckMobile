package com.jfdimarzio.check.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.gson.Gson;
import com.jfdimarzio.check.DashboardActivity;
import com.jfdimarzio.check.ExecuteRFormActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.adapter.DownloadTypeAdapter;
import com.jfdimarzio.check.adapter.Step1JobAdapter;
import com.jfdimarzio.check.model.DownloadFilter;
import com.jfdimarzio.check.model.DownloadTypeItem;
import com.jfdimarzio.check.model.JobItem;
import com.jfdimarzio.check.model.enumtype.MRFormType;
import com.jfdimarzio.check.model.rform.RFormViewModel;
import com.jfdimarzio.check.util.AppUtils;
import com.jfdimarzio.check.util.consts.IntentKeyConstants;
import com.jfdimarzio.check.util.consts.RequestCodeConstants;
import com.jfdimarzio.check.widget.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

public class Step1JobFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    private static final String ARG_VIEWMODEL="ARG_VIEWMODEL";

    private View mRootView;
    private DashboardActivity mActivity;
    private EmptyRecyclerView mrv_job_repair;
    private Step1JobAdapter mStep1JobAdapter;
    private SwitchCompat mswitch_time;
    private EditText mtxt_qrcode;
    private RecyclerView mrv_type;
    private DownloadTypeAdapter mDownloadTypeAdapter;
    private DownloadFilter mDownloadFilter=new DownloadFilter(false,new HashMap<MRFormType, MRFormType>());
    private Gson mgson=new Gson();

    public Step1JobFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mActivity=(DashboardActivity)getActivity();
        mRootView=inflater.inflate(R.layout.fragment_step1_job,container,false);
        mrv_job_repair=mRootView.findViewById(R.id.rv_job_repair);
        mrv_job_repair.setLayoutManager(new LinearLayoutManager(mActivity));
        mrv_job_repair.setItemAnimator(new DefaultItemAnimator());
        mrv_job_repair.setEmptyView(mRootView.findViewById(R.id.vs_empty));
        mswitch_time=mRootView.findViewById(R.id.switch_time);
        mtxt_qrcode=mRootView.findViewById(R.id.txt_qrcode);

        //Type
        mrv_type=mRootView.findViewById(R.id.rv_type);
        LinearLayoutManager mLayoutManager=new LinearLayoutManager(mActivity,LinearLayoutManager.HORIZONTAL,false);
        mrv_type.setLayoutManager(mLayoutManager);
        ArrayList<DownloadTypeItem> typeItemArrayList=new ArrayList<>();
        HashMap<Integer,Integer> colorMaps= AppUtils.getColorMap(this.mActivity);
        typeItemArrayList.add(new DownloadTypeItem(getString(R.string.lb_type_patrol),MRFormType.None,colorMaps.get(0)));
        typeItemArrayList.add(new DownloadTypeItem(getString(R.string.lb_type_mform),MRFormType.MForm,colorMaps.get(1)));
        typeItemArrayList.add(new DownloadTypeItem(getString(R.string.lb_type_rform),MRFormType.Repair,colorMaps.get(2)));

        ArrayList<JobItem> jobItems=mActivity.getmEquipCheckHelper().getJobItems();

        HashMap<String,String> getJobEmgTelMap=mActivity.getmEquipCheckHelper().getJobEmgTelMap();
        for(JobItem jobItem:jobItems){
            if(getJobEmgTelMap.containsKey(jobItem.getUniqueID())){
                jobItem.setIsHaveEmgTel(true);
            }
        }

        mDownloadTypeAdapter=new DownloadTypeAdapter(mActivity,typeItemArrayList);
        mrv_type.setAdapter(mDownloadTypeAdapter);
        mDownloadTypeAdapter.setmOnItemClickListener(new DownloadTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DownloadTypeItem item, View view, int position) {
                boolean new_checked=!item.isSelected();
                item.setSelected(new_checked);

                mDownloadFilter.setTypeFilter(mDownloadTypeAdapter.getSelectedList());
                String new_json_reason=mgson.toJson(mDownloadFilter);
//                mStep1JobAdapter.getFilter().filter(new_json_reason);
                mDownloadTypeAdapter.notifyDataSetChanged();
            }
        });

        mStep1JobAdapter=new Step1JobAdapter(mActivity,jobItems);
        mStep1JobAdapter.setmCusListener(new Step1JobAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(JobItem data, int position) {
                if(data.isRepairForm()){
                    if(data.getMRFormType().equals(MRFormType.MForm)){
                        goToMForm(data,position);
                    }else{
                        goToRForm(data,position);
                    }
                }else{

                }
            }

            @Override
            public void onItemLongClick(JobItem data, int position) {

            }
        });

        mrv_job_repair.setAdapter(mStep1JobAdapter);

        return mRootView;
    }

    private void goToMForm(JobItem item,int position){

    }

    private void goToRForm(JobItem item,int position){
        Intent intent=new Intent(mActivity, ExecuteRFormActivity.class);
        RFormViewModel viewModel=new RFormViewModel(item);
        intent.putExtra(ARG_VIEWMODEL,viewModel);
        intent.putExtra(IntentKeyConstants.POSITION,position);
        startActivityForResult(intent, RequestCodeConstants.BackThenUpdate);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_time:
                Timber.d("onCheckedChanged call");
//                mActivity.getmPreferenceUtils().setShowCurrentTime(isChecked);
                mDownloadFilter.setCurrent(isChecked);
//                if (mStep1JobAdapter.getFilterItemCount() > 0)
            {
                if(isChecked)
                {

                    buttonView.setText(R.string.lb_filter_show_current);
                }
                else
                {

                    mDownloadFilter.setCurrent(isChecked);
                    buttonView.setText(R.string.lb_filter_show_all);
                }
            }
            String new_json_reason = mgson.toJson(mDownloadFilter);
//            mStep1JobAdapter.getFilter().filter(new_json_reason);
            break;
        }
    }

    public void refresh(ArrayList<String> mFormListID) {

        //全取-進度條
//        HashMap<String, DoneTotalItem> getJobDoneMap = mActivity.getmEquipCheckHelper().getJobDoneMap(null);
//        //全取-未巡檢原因
//        HashMap<String,UnPatrolRecordItem> getUnpatrolRecordMap = mActivity.getmEquipCheckHelper().getUnPatrolRecordMap(null);
//        mStep1JobAdapter.refreshByMap(getJobDoneMap,getUnpatrolRecordMap);
//
//        // mFormListID 這個有資料 表示已經刪掉的 mForm 清單,只是卡在 畫面 mDataSource
//
//        if (mFormListID.size()>0){
//            mStep1JobAdapter.removeMForm(mFormListID);
//        }
//
//        mStep1JobAdapter.notifyDataSetChanged();

    }
}
