package com.jfdimarzio.check;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jfdimarzio.check.adapter.BaseRvAdapter;
import com.jfdimarzio.check.adapter.UploadListAdapter;
import com.jfdimarzio.check.async.UploadTask;
import com.jfdimarzio.check.fragment.AlertDialogFragment;
import com.jfdimarzio.check.fragment.ConfirmDialogFragment;
import com.jfdimarzio.check.model.ApiFormInput;
import com.jfdimarzio.check.model.DoneTotalItem;
import com.jfdimarzio.check.model.JobItem;
import com.jfdimarzio.check.model.UnPatrolRecordItem;
import com.jfdimarzio.check.model.UploadJobItem;
import com.jfdimarzio.check.util.CompatibleUtils;
import com.jfdimarzio.check.util.DeviceUtils;
import com.jfdimarzio.check.util.consts.FragmentTagConstants;
import com.jfdimarzio.check.widget.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class UploadActivity extends BaseActivity {

    private UploadActivity mActivity;
    private Drawable mbtn_selector_default;
    private Drawable mbtn_selector_primary2;
    private Button mbtn_upload;
    private EmptyRecyclerView mrv_upload;
    private UploadListAdapter muploadListAdapter;

    private String IMEI = "";
    private String MAC_ADDRESS;
    private LocalBroadcastManager mLocalBroadcastManager;
    private Handler mHandler;
    private Menu menu;
    private ArrayList<String> mMFormIDList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = this;

        this.mbtn_selector_default = ContextCompat.getDrawable(this, R.drawable.btn_selector_default);
        this.mbtn_selector_primary2 = ContextCompat.getDrawable(this, R.drawable.btn_selector_primary2);

        ArrayList<UploadJobItem> uploadJobItems = new ArrayList<>();
        ArrayList<JobItem> jobItems = this.getmEquipCheckHelper().getJobItems();

        HashMap<String, DoneTotalItem> getJobDoneMap = this.getmEquipCheckHelper().getJobDoneMap(null);
        HashMap<String, UnPatrolRecordItem> getUnpatrolRecordMap = this.getmEquipCheckHelper().getUnPatrolRecordMap(null);
        HashMap<String, DoneTotalItem> getMFormJobDoneMap = this.getmEquipCheckHelper().getMFormJobDoneMap();

        for (JobItem item : jobItems) {
            UploadJobItem new_item = new UploadJobItem(
                    item.getUniqueID(),
                    item.getDescription(),
                    item.getRemark(),
                    item.getBeginTime(),
                    item.getEndTime(),
                    item.isCheckBySeq(),
                    item.isShowPrevRecord(),
                    item.isRepairForm(),
                    true
            );
            new_item.setMRFormType(item.getMRFormType());
            new_item.setVHNO(item.getVHNO());
            new_item.setEquipmentID(item.getEquipmentID());
            new_item.setEquipmentName(item.getEquipmentName());
            new_item.setEquipment(item.getEquipment());
            new_item.setPartDescription(item.getPartDescription());
            new_item.setBegDate(item.getBegDate());
            new_item.setEndDate(item.getEndDate());

            if (getUnpatrolRecordMap.containsKey(item.getUniqueID())) {
                new_item.setUnPatrolRecordItem(getUnpatrolRecordMap.get(item.getUniqueID()));
            }

            if (getJobDoneMap.containsKey(item.getUniqueID())) {
                DoneTotalItem doneTotalItem = getJobDoneMap.get(item.getUniqueID());
                new_item.setUIDoneCount(doneTotalItem.getDone());
                new_item.setUITotalCount(doneTotalItem.getTotal());
            }

            if (new_item.getUIDoneCount() > 0 || new_item.getUnPatrolRecordItem() != null) {
                uploadJobItems.add(new_item);
            } else {
                if (getMFormJobDoneMap.containsKey(item.getUniqueID())) {
                    uploadJobItems.add(new_item);
                }
            }
        }

        // UI
        mbtn_upload = findViewById(R.id.btn_upload);

        mrv_upload = findViewById(R.id.rv_upload);
        mrv_upload.setLayoutManager(new LinearLayoutManager(this));
        mrv_upload.setItemAnimator(new DefaultItemAnimator());
        mrv_upload.setEmptyView(findViewById(R.id.vs_empty));

        muploadListAdapter = new UploadListAdapter(this, uploadJobItems);
        muploadListAdapter.setmListener(new BaseRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Object o, View view, int i) {
                UploadJobItem jobItem = (UploadJobItem) o;
//                refreshButton();
            }
        });
        mrv_upload.setAdapter(muploadListAdapter);

        // Setting Value

        // Event
        if (uploadJobItems.size() == 0) {
            mbtn_upload.setVisibility(View.VISIBLE);
            mbtn_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dialog_title = getString(R.string.title_activity_upload);
                    final ArrayList<String> getUploadIDList = muploadListAdapter.getUploadIDList();
                    final ArrayList<String> getUploadMFormIDList = muploadListAdapter.getUploadMFormIDList();
                    if (getUploadIDList.size() == 0) {
                        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(dialog_title, getString(R.string.sys_msg_upload_confirm));
                        confirmDialogFragment.addListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                mMFormIDList.clear();
                                String mUserID = mActivity.getmUserSession().getID();
                                String mVerName = DeviceUtils.getVerName(mActivity);

                                ApiFormInput model = new ApiFormInput(mUserID, IMEI, MAC_ADDRESS, mVerName);

                                UploadTask uploadTask = new UploadTask(UploadActivity.this, model, getUploadIDList, new UploadTask.UploadHandler() {
                                    @Override
                                    public void done(ArrayList<String> mForm) {
                                        UploadActivity.this.mMFormIDList = getUploadMFormIDList;
                                        UploadActivity.this.onBackPressed();
                                    }
                                });
                                uploadTask.execute();
                            }
                        });
                        confirmDialogFragment.show(getFragmentManager(), FragmentTagConstants.ConfirmDialogFragment);
                    } else {
                        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(dialog_title, getString(R.string.sys_at_lease_choice_one));
                        alertDialogFragment.show(getFragmentManager(), FragmentTagConstants.AlertDialogFragment);
                    }
                }
            });
        } else {
//            mbtn_upload.setVisibility(View.GONE);
            mbtn_upload.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshButton() {
        // 顯示 儲存
        final ArrayList<String> getUploadIDList = muploadListAdapter.getUploadIDList();

        if (getUploadIDList.size() > 0) {
            mbtn_upload.setEnabled(true);
            CompatibleUtils.setViewBackground(mbtn_upload, mbtn_selector_primary2);

        } else {
            mbtn_upload.setEnabled(false);
            CompatibleUtils.setViewBackground(mbtn_upload, mbtn_selector_default);
        }
    }
}
