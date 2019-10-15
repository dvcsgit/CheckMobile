package com.jfdimarzio.check;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jfdimarzio.check.model.UserSession;
import com.jfdimarzio.check.provider.APPDBHelper;
import com.jfdimarzio.check.provider.EquipCheckHelper;
import com.jfdimarzio.check.util.PreferenceUtils;
import com.jfdimarzio.check.util.SessionManager;

public class BaseActivity extends AppCompatActivity{
    protected PreferenceUtils mPreferenceUtils;
    protected APPDBHelper mAPPDBHelper;
    protected EquipCheckHelper mEquipCheckHelper;
    protected UserSession mUserSession;
    protected SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        AppController appController=AppController.getInstance();
        mAPPDBHelper=appController.getAPPDBHelper();
        mPreferenceUtils=new PreferenceUtils(this);
        mEquipCheckHelper=appController.getEquipCheckHelper();

        mSessionManager=new SessionManager(this);
        mUserSession=mSessionManager.getUserSession();
    }

    public PreferenceUtils getmPreferenceUtils() {
        return mPreferenceUtils;
    }

    public APPDBHelper getmAPPDBHelper() {
        return mAPPDBHelper;
    }

    public EquipCheckHelper getmEquipCheckHelper(){return mEquipCheckHelper;}

    public UserSession getmUserSession() {
        return mUserSession;
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

//    protected void signOut(){
//        String warningMessage = getString(R.string.system_message_sure_to_leave);
//        boolean isWarn = false;
////        您有未上傳的資料，
//        try
//        {
//            ArrayList<UploadJobItem> uploadJobItems = new ArrayList<>();
//            ArrayList<JobItem> jobItems = this.getmEquipCheckHelper().getJobItems();
//            HashMap<String, DoneTotalItem> getJobDoneMap = this.getmEquipCheckHelper().getJobDoneMap(null);
//            HashMap<String, DoneTotalItem> getMFormJobDoneMap = this.getmEquipCheckHelper().getMFormJobDoneMap();
//            for (JobItem item : jobItems) {
//
//                UploadJobItem new_item = new UploadJobItem(
//                        item.getUniqueID(),
//                        item.getDescription(),
//                        item.getRemark(),
//                        item.getBeginTime(),
//                        item.getEndTime(),
//                        item.isCheckBySeq(),
//                        item.isShowPrevRecord(),
//                        item.isRepairForm(),
//                        true
//                );
//
//
//
//                if (getJobDoneMap.containsKey(item.getUniqueID())) {
//                    DoneTotalItem doneTotalItem = getJobDoneMap.get(item.getUniqueID());
//                    new_item.setUIDoneCount(doneTotalItem.getDone());
//                    new_item.setUITotalCount(doneTotalItem.getTotal());
//                }
//
//                if(new_item.getUIDoneCount() > 0 || new_item.getUnPatrolRecordItem() != null)
//                {
//                    uploadJobItems.add(new_item);
//                }
//                else
//                {
//                    if(getMFormJobDoneMap.containsKey(item.getUniqueID()))
//                    {
//                        uploadJobItems.add(new_item);
//                    }
//                }
//
//            }
//
//            if(uploadJobItems.size()>0)
//            {
//                String warn_message = getString(R.string.system_message_warning_has_data_not_upload);
//
//                warningMessage = warn_message + "\n" + warningMessage;
//                isWarn = true;
//            }
//        }catch (Exception ex)
//        {
//            Timber.d("signOut Error:%s",ex.getMessage());
//        }
//
//        final boolean finalIsWarn = isWarn;
//        new AlertDialog.Builder(this)
//                .setTitle(getString(R.string.system_dialog_title))
//                .setMessage(warningMessage)
//                .setNegativeButton(android.R.string.cancel, null)
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        if(finalIsWarn)
//                        {
//                            ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.lb_sys_warn_again),getString(R.string.sys_warn_leave_will_remove));
//                            confirmDialogFragment.addListener(new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    mSessionManager.logoutUser();
//                                    finish();
//                                }
//                            });
//                            confirmDialogFragment.show(getFragmentManager(), FragmentTagConstants.ConfirmDialogFragment);
//                        }
//                        else
//                        {
//                            mSessionManager.logoutUser();
//                            finish();
//                        }
//                    }
//
//                }).show();
//    }
}
