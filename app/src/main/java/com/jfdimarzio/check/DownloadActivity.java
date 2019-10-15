package com.jfdimarzio.check;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jfdimarzio.check.adapter.DownloadRVAdapter;
import com.jfdimarzio.check.adapter.DownloadTypeAdapter;
import com.jfdimarzio.check.async.DownloadTask;
import com.jfdimarzio.check.async.volleyrequest.CheckServerRequest;
import com.jfdimarzio.check.fragment.AlertDialogFragment;
import com.jfdimarzio.check.fragment.ConfirmDialogFragment;
import com.jfdimarzio.check.model.CheckDateFormatItem;
import com.jfdimarzio.check.model.DownloadFilter;
import com.jfdimarzio.check.model.DownloadTypeItem;
import com.jfdimarzio.check.model.enumtype.MRFormType;
import com.jfdimarzio.check.model.response.DownloadFormModel;
import com.jfdimarzio.check.model.response.RequestJobListItem;
import com.jfdimarzio.check.model.response.ServerGenericsResponse;
import com.jfdimarzio.check.model.response.ServerResponse;
import com.jfdimarzio.check.util.AppUtils;
import com.jfdimarzio.check.util.DateUtils;
import com.jfdimarzio.check.util.consts.FragmentTagConstants;
import com.jfdimarzio.check.util.consts.IntentActionConsts;
import com.jfdimarzio.check.util.consts.SystemConstants;
import com.jfdimarzio.check.widget.CusEmptyRecyclerView;
import com.jfdimarzio.check.widget.SimpleDividerItemDecoration;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import timber.log.Timber;

import static com.jfdimarzio.check.util.LogUtils.makeLogTag;

public class DownloadActivity extends BaseActivity {

//    private static final String TAG=makeLogTag(DownloadActivity.class);

    private boolean IS_QUERY_NOW=false;
    private DownloadRVAdapter mdownloadRVAdapter;

    private ArrayAdapter<String> mUserIDSuggestAdapter;
    private String mtemplate_download_total;
    private String mtemplate_download_selected;

    //region UI
    private ImageView mimg_connect_status;
    private ImageButton mbtn_query;
    private AutoCompleteTextView mtxt_userid;
    private TextView mlb_checkdate;
    private Button mbtn_download;
    private LinearLayout marea_progress;
    private CusEmptyRecyclerView mrv_download;
    private TextView mlb_total_count;
    private TextView mlb_select_count;
    private View marea_info;
    private SwitchCompat mswitch_all;
    private SwitchCompat mswitch_time;
    //endregion

    private String hdUserID;
    private String hdCheckDate;

    private RecyclerView mrv_type;
    private DownloadTypeAdapter mDownloadTypeAdapter;
    private Gson mgson=new Gson();
    private DownloadFilter mDownloadFilter=new DownloadFilter(false,new HashMap<MRFormType, MRFormType>());

    private Handler mHandler;
    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        //const
        this.mtemplate_download_total=this.getResources().getString(R.string.template_download_total);
        this.mtemplate_download_selected=this.getResources().getString(R.string.template_download_selected  );

        //region UI
        marea_info=findViewById(R.id.area_info);
        mlb_total_count=findViewById(R.id.lb_total_count);
        mlb_select_count=findViewById(R.id.lb_select_count);
        mlb_checkdate=findViewById(R.id.lb_checkdate);
        mtxt_userid=findViewById(R.id.txt_userid);
        mimg_connect_status=findViewById(R.id.img_connect_status);
        mbtn_query=findViewById(R.id.btn_query);
        mbtn_download=findViewById(R.id.btn_download);
        mrv_type=findViewById(R.id.rv_type);
        marea_progress=findViewById(R.id.area_progress);
        mswitch_all=findViewById(R.id.switch_all);
        mswitch_time=findViewById(R.id.switch_time);
        mrv_download=findViewById(R.id.rv_download);
        mrv_download.addItemDecoration(new SimpleDividerItemDecoration(this));
        mrv_download.setLayoutManager(new LinearLayoutManager(this));
        mrv_download.setItemAnimator(new DefaultItemAnimator());
        mrv_download.setEmptyView(findViewById(R.id.vs_empty));
        //endregion

        //region Type filter
        mrv_type=findViewById(R.id.rv_type);
        LinearLayoutManager mLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mrv_type.setLayoutManager(mLayoutManager);
        ArrayList<DownloadTypeItem> typeItemArrayList=new ArrayList<>();
        HashMap<Integer,Integer> colorMaps= AppUtils.getColorMap(this);
        typeItemArrayList.add(new DownloadTypeItem(getString(R.string.lb_type_patrol),MRFormType.None,colorMaps.get(0)));
        typeItemArrayList.add(new DownloadTypeItem(getString(R.string.lb_type_mform),MRFormType.MForm,colorMaps.get(1)));
        typeItemArrayList.add(new DownloadTypeItem(getString(R.string.lb_type_rform),MRFormType.Repair,colorMaps.get(2)));

        mDownloadTypeAdapter=new DownloadTypeAdapter(this,typeItemArrayList);
        mrv_type.setAdapter(mDownloadTypeAdapter);
        mDownloadTypeAdapter.setmOnItemClickListener(new DownloadTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DownloadTypeItem item, View view, int position) {
                boolean new_checked=!item.isSelected();
                item.setSelected(new_checked);

                mDownloadFilter.setTypeFilter(mDownloadTypeAdapter.getSelectedList());
                String new_json_reason=mgson.toJson(mDownloadFilter);
                mdownloadRVAdapter.getFilter().filter(new_json_reason);

                mDownloadTypeAdapter.notifyDataSetChanged();
            }
        });
        //endregion

        //region Check internet.
        mHandler=new Handler();
        startRepeatingTask();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(IntentActionConsts.CheckServerConnect);
        mLocalBroadcastManager=LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mbroadcastReceiver,intentFilter);
        //endregion

        //switch
//        mswitch_all.setOnCheckedChangeListener(this);
//        mswitch_time.setOnCheckedChangeListener(this);
        mswitch_time.setChecked(getmPreferenceUtils().getShowCurrentTime());

        //Check the resource of last time
        String lastUserID=mAPPDBHelper.getLastUserID();
        if(!TextUtils.isEmpty(lastUserID)){
            mtxt_userid.setText(lastUserID);
            mtxt_userid.setSelection(lastUserID.length());
        }
        mdownloadRVAdapter=new DownloadRVAdapter(this);
        mdownloadRVAdapter.setCusmListener(new DownloadRVAdapter.OnCusItemClickListener() {
            @Override
            public void onItemClick(RequestJobListItem data, int position) {
                updateCountInfo();
                checkIfNeedUpdateSW();
            }

            @Override
            public void onFilterDone() {
                updateCountInfo();
                checkIfNeedUpdateSW();
            }
        });

        mrv_download.setAdapter(mdownloadRVAdapter);

        //Query
        mbtn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!IS_QUERY_NOW){
                    boolean cancel=false;
                    View focusView=null;
                    String str_userid=mtxt_userid.getText().toString();
                    String str_checkdate=mlb_checkdate.getText().toString();
                    if(TextUtils.isEmpty(str_userid)){
                        mtxt_userid.setError(getString(R.string.error_field_required));
                        focusView=mtxt_userid;
                        cancel=true;
                    }
                    if(TextUtils.isEmpty(str_checkdate)){
                        mlb_checkdate.setError(getString(R.string.error_field_required));
                        focusView=mlb_checkdate;
                        cancel=true;
                    }
                    if(cancel){
                        focusView.requestFocus();
                    }else{
                        Date checkDate= DateUtils.converStringToDate(str_checkdate,DateUtils.FORMAT_DIS_YYYYMMDD);
                        String strCheckDate=DateUtils.format(checkDate,DateUtils.FORMAT_YYYYMMDD);
                        String download_url=mPreferenceUtils.getJobListUrl()+"?UserID="+str_userid+"&CheckDate="+strCheckDate;
//                        LOGD(TAG,"download_url:"+download_url);
                        requestJob(download_url,str_userid,str_checkdate);
                    }
                }
            }
        });

        mbtn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String download_url=mPreferenceUtils.getDownloadUrl();
//                LOGD(TAG,"download_url:"+download_url);
                //if(mdownloadRVAdapter.getSelectedCount()>0){
                    ConfirmDialogFragment confirmDialogFragment=ConfirmDialogFragment.newInstance(String.format(getString(R.string.sys_sure_to_download_these),mdownloadRVAdapter.getSelectedCount()));
                    confirmDialogFragment.addListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DownloadFormModel downloadFormModel=mdownloadRVAdapter.getDownloadFormModel(hdCheckDate.toString().replace("/",""));
                            if(downloadFormModel!=null){
                                DownloadTask downloadTask=new DownloadTask(DownloadActivity.this,downloadFormModel,hdUserID);
                                downloadTask.execute(download_url);
                            }
//                            DownloadActivity.super.checkAppUpdate(DownloadActivity.this);
                        }
                    });
                    confirmDialogFragment.show(DownloadActivity.this.getFragmentManager(),FragmentTagConstants.ConfirmDialogFragment);
//                }else{
//                    AlertDialogFragment alertDialogFragment=AlertDialogFragment.newInstance(getString(R.string.sys_at_lease_choice_one));
//                    alertDialogFragment.show(getFragmentManager(),FragmentTagConstants.AlertDialogFragment);
//                }
            }
        });

        //Setting suggestion
        mUserIDSuggestAdapter=new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,mAPPDBHelper.getUserIDArrayList());
        mtxt_userid.setAdapter(mUserIDSuggestAdapter);

        //mbtn_download.setVisibility(View.GONE);

        updateCountInfo();

        //set date
        CheckDateFormatItem checkDateFormatItem=new CheckDateFormatItem();
        hdCheckDate=checkDateFormatItem.getFORMAT_DIS_YYYYMMDD();
        mlb_checkdate.setText(hdCheckDate);
    }

    private void checkIfNeedUpdateSW(){
        if(mdownloadRVAdapter.getSelectedCount()>0&&mdownloadRVAdapter.getSelectedCount()==mdownloadRVAdapter.getItemCount()){
            mswitch_all.setTag("Hit");
            mswitch_all.setChecked(true);
        }else{
            mswitch_all.setTag("Hit");
            mswitch_all.setChecked(false);
        }
    }
    private void startRepeatingTask(){
        mStatusChecker.run();
    }

    private void stopRepeatingTask(){
        mHandler.removeCallbacks(mStatusChecker);
    }

    Runnable mStatusChecker=new Runnable() {
        @Override
        public void run() {
            try{
                AppController.getInstance().addToRequestQueue(CheckServerRequest.checkConnectRequest(DownloadActivity.this),"GetServerTimeRequest");
//                Timber.d("I go to check.");
            }finally {
                mHandler.postDelayed(mStatusChecker, SystemConstants.INTERVAL_REFRESH_CHECK);
            }
        }
    };

    @Override
    protected void onStop(){
        super.onStop();
//        Timber.d("dash-life onStop");
    }

    @Override
    public void onDestroy(){
//        Timber.d("onDestroy");
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver((mbroadcastReceiver));
        stopRepeatingTask();
    }

    private BroadcastReceiver mbroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
//            Timber.d("Receiver action : %s",action);
            if(action.equals(IntentActionConsts.CheckServerConnect)){
                boolean flag=intent.getBooleanExtra(IntentActionConsts.KEY_CHECK_SERVER_CONNECT,false);
//                Timber.d("Sync_data:call! %s",flag);
                changeConnectStatusIcon(flag);
            }
        }
    };

    private void changeConnectStatusIcon(boolean isConn){
        if(isConn){
            mimg_connect_status.setImageDrawable(ContextCompat.getDrawable(DownloadActivity.this,R.drawable.ic_signal_wifi_4_bar_green_800_36dp));
        }else{
            mimg_connect_status.setImageDrawable(ContextCompat.getDrawable(DownloadActivity.this,R.drawable.ic_signal_wifi_off_red_800_36dp));
        }
    }

    private void requestJob(String url,final String userID,final String checkDate){
        //Hide keyboard
        final InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mtxt_userid.getWindowToken(),0);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                mDownloadTypeAdapter.resetCount();
                HashMap<MRFormType, Integer> formTypeCountMap = new HashMap<>();
                //mbtn_download.setVisibility(View.GONE);

                //注：TypeToken的构造方法是protected修饰的,所以上面才会写成new TypeToken<List<String>>() {}.getType() 而不是 new TypeToken<List<String>>().getType()
                Type listType = new TypeToken<ServerGenericsResponse<RequestJobListItem>>() {}.getType();
                Gson gson = new Gson();
                ServerGenericsResponse<RequestJobListItem> serverGenericsResponse = gson.fromJson(jsonObject.toString(), listType);

                if (serverGenericsResponse.isSuccess()) {
                    ArrayList<RequestJobListItem> requestJobListItems = serverGenericsResponse.getData();
                    if (requestJobListItems.size() == 0) {
                        String message = String.format(getString(R.string.template_no_data_can_download), userID);
                        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(message);
                        alertDialogFragment.show(getFragmentManager(), FragmentTagConstants.AlertDialogFragment);
                        updateCountInfo();
                    } else {
                        boolean is_default_except = mPreferenceUtils.getIS_DEFAULT_EXCEPT();
                        Collections.sort(requestJobListItems, new JobComparator());
                        for (RequestJobListItem item : requestJobListItems) {
                            if (!TextUtils.isEmpty(item.getMaintanenceFormUniqueID())) {
                                item.setMRFormType(MRFormType.MForm);
                            } else if (!TextUtils.isEmpty(item.getRepairFormUniqueID())) {
                                item.setMRFormType(MRFormType.Repair);
                            } else {
                                item.setMRFormType(MRFormType.None);
                                item.setIsExceptChecked(is_default_except);
                            }

                            if (formTypeCountMap.containsKey(item.getMRFormType())) {
                                formTypeCountMap.put(item.getMRFormType(), formTypeCountMap.get(item.getMRFormType()) + 1);
                            } else {
                                formTypeCountMap.put(item.getMRFormType(), 1);
                            }
                        }

                        mdownloadRVAdapter.setmDataSource(requestJobListItems);
                        if (formTypeCountMap.size() > 0) {
                            mDownloadTypeAdapter.updateCount(formTypeCountMap);
                        }
                        if (mPreferenceUtils.getShowCurrentTime()) {
                            mDownloadFilter.setCurrent(mPreferenceUtils.getShowCurrentTime());
                            String new_json_reason = mgson.toJson(mDownloadFilter);
                            mdownloadRVAdapter.getFilter().filter(new_json_reason);
                        }
                        mdownloadRVAdapter.notifyDataSetChanged();
                        setAllChecked();

                        hdUserID = userID;
                        hdCheckDate = checkDate;

                        mbtn_download.setVisibility(View.VISIBLE);
                    }
                }
                asyncRequestDone();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //LOGD(TAG,"VolleyError:"+volleyError.getMessage());
                String json=null;
                String message="";
                NetworkResponse response=volleyError.networkResponse;
                if(response!=null&&response.data!=null){
                    //LOGD(TAG,new String(response.data));
                    switch (response.statusCode){
                        case 500:
                        case 400:
                            json=new String(response.data);
                            Gson gson=new Gson();
                            ServerResponse serverResponse=gson.fromJson(json,ServerResponse.class);
                            message=serverResponse.getMessage();
                            break;
                        case 404:
                            message=getString(R.string.system_msg_connection_error);
                    }
                }else if(volleyError instanceof NoConnectionError){
                    message="NoConnectionError:"+getString(R.string.system_msg_connection_error);
                }else if(volleyError instanceof TimeoutError){
                    message="TimeoutError:"+getString(R.string.system_msg_connection_error);
                }

                AlertDialogFragment alertDialogFragment=AlertDialogFragment.newInstance(getString(R.string.system_title_download_fail),getString(R.string.system_msg_fail_prefix)+message);
                alertDialogFragment.show(getFragmentManager(),FragmentTagConstants.AlertDialogFragment);

                asyncRequestDone();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(2000,5,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        System.setProperty("http.keepAlive","false");
        String req_tag="query_job";
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().cancelPendingRequests(req_tag);
        IS_QUERY_NOW=true;
        AppController.getInstance().addToRequestQueue(jsonObjectRequest,req_tag);
        mrv_download.setCanShowEmptyView(false);
        mrv_download.hideEmpty();
        mdownloadRVAdapter.reset();
        showProgress(true);
    }

    private void updateCountInfo(){
        if(mdownloadRVAdapter.getFilterItemCount()>0){
            Timber.d("updateCountInfo:%d",mdownloadRVAdapter.getFilterItemCount());
            mlb_total_count.setText(String.format(mtemplate_download_total,mdownloadRVAdapter.getItemCount()));

            int selectedCount=mdownloadRVAdapter.getSelectedCount();
            if(selectedCount==0){
                mlb_select_count.setVisibility(View.GONE);
            }else{
                mlb_select_count.setText(String.format(mtemplate_download_selected,selectedCount));
                mlb_select_count.setVisibility(View.VISIBLE);
            }
            marea_info.setVisibility(View.VISIBLE);
        }else{
            marea_info.setVisibility(View.GONE);
        }
    }

    class JobComparator implements Comparator<RequestJobListItem>{
        @Override
        public int compare(RequestJobListItem lhs,RequestJobListItem rhs){
            return lhs.getDescription().compareToIgnoreCase(rhs.getDescription());
        }
    }

    private void setAllChecked(){
        mdownloadRVAdapter.setAllSelected();
        updateCountInfo();
        mdownloadRVAdapter.notifyDataSetChanged();
    }

    private void asyncRequestDone(){
        showProgress(false);
        IS_QUERY_NOW=false;
        mrv_download.setCanShowEmptyView(true);
        mrv_download.checkIfEmpty();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB_MR2){
            int shortAnimTime=getResources().getInteger(android.R.integer.config_shortAnimTime);
            marea_progress.setVisibility(View.VISIBLE);
            marea_progress.animate().setDuration(shortAnimTime).alpha(show?1:0).setListener(new AnimatorListenerAdapter(){
                @Override
                public void onAnimationEnd(Animator animator){
                    marea_progress.setVisibility(show?View.VISIBLE:View.GONE);
                }
            });
        }else{
            marea_progress.setVisibility(show?View.VISIBLE:View.GONE);
        }
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm=getFragmentManager();
        if(fm.getBackStackEntryCount()>0){
            //LOGI(TAG,"popping backstack");
            fm.popBackStack();
        }else{
           // LOGI(TAG,"nothing on backstack,calling super");
            super.onBackPressed();
        }
    }
}
