package com.jfdimarzio.check.async;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.jfdimarzio.check.DownloadActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.fragment.AlertDialogFragment;
import com.jfdimarzio.check.fragment.ProgressDialogFragment;
import com.jfdimarzio.check.model.CheckDateFormatItem;
import com.jfdimarzio.check.model.response.DownloadFormModel;
import com.jfdimarzio.check.model.response.ServerResponse;
import com.jfdimarzio.check.provider.EquipCheckHelper;
import com.jfdimarzio.check.util.FileZipUtils;
import com.jfdimarzio.check.util.PreferenceUtils;
import com.jfdimarzio.check.util.consts.FragmentTagConstants;
import com.jfdimarzio.check.util.consts.SystemConstants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.jfdimarzio.check.util.LogUtils.makeLogTag;

public class DownloadTask extends AsyncTask<String,Void,ServerResponse> {
//    private static final String TAG=makeLogTag(DownloadTask.class);
    private static final int BUFFER_SIZE=4096;
    private final String mUserID;
    private DownloadFormModel mDownloadFormModel;
    private DownloadActivity mContext;
    private ProgressDialogFragment mDialog;

    public DownloadTask(FragmentActivity mContext,DownloadFormModel downloadFormModel,String userID){
        this.mContext=(DownloadActivity)mContext;
        this.mDialog=ProgressDialogFragment.newInstance(mContext.getString(R.string.sys_download_prefix),true);
        this.mDownloadFormModel=downloadFormModel;
        this.mUserID=userID;
    }

    @Override
    protected void onPreExecute(){
        showProgress();
    }

    private void showProgress(){
        mDialog.show(mContext.getSupportFragmentManager(), FragmentTagConstants.ProgressDialogFragment);
    }

    @Override
    protected ServerResponse doInBackground(String... download_url){
        ServerResponse result=new ServerResponse();
        String downloadUrl=download_url[0];
        Gson gson=new Gson();
        try{
            PreferenceUtils preferenceUtils=mContext.getmPreferenceUtils();
            String file_folder=preferenceUtils.getFilePath();// /storage/emulated/0/com.jfdimarzio.check
            String temp_folder=preferenceUtils.getTempPath();// /storage/emulated/0/com.jfdimarzio.check/Temp
            String zip_file_save_path=file_folder+ File.separator+ SystemConstants.SQLITE_EQUIPCHECK_DOWNLOAD_ZIP;// /storage/emulated/0/com.jfdimarzio.check/EquipCheck.Job.db.zip
            String temp_job_file_path=temp_folder+File.separator+SystemConstants.SQLITE_EQUIPCHECK;// /storage/emulated/0/com.jfdimarzio.check/Temp/Check.db

            DataOutputStream printout;
            HttpURLConnection httpConn=null;
            URL url=new URL(downloadUrl);// http://10.214.226.118/WebAPI/api/Download/Post
            httpConn=(HttpURLConnection)url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type","application/json");

            String param=gson.toJson(mDownloadFormModel);
            printout=new DataOutputStream(httpConn.getOutputStream());
            printout.writeBytes(param);
            printout.flush();
            printout.close();

            int responseCode=httpConn.getResponseCode();
            if(responseCode==HttpURLConnection.HTTP_OK){
                String fileName="";
                String disposition=httpConn.getHeaderField("Content-Disposition");
                String contentType=httpConn.getContentType();
                int contentLength=httpConn.getContentLength();

                InputStream inputStream=httpConn.getInputStream();

                FileOutputStream outputStream=new FileOutputStream(zip_file_save_path);

                int bytesRead=-1;
                byte[] buffer=new byte[BUFFER_SIZE];
                long total=0;
                while((bytesRead=inputStream.read(buffer))!=-1){
                    outputStream.write(buffer,0,bytesRead);
                    total+=bytesRead;
                    int percent=(int)((total*100)/contentLength);
                    mDialog.updateProgress(percent);
                }
                outputStream.close();
                inputStream.close();

                FileZipUtils.unzip(zip_file_save_path,temp_folder+File.separator,"");
                File temp_job_file=new File(temp_job_file_path);
                if(temp_job_file.exists()){
                    EquipCheckHelper entryCheckHelper=mContext.getmEquipCheckHelper();
                    entryCheckHelper.copyTempTable(temp_job_file_path,true);
                    if(!entryCheckHelper.checkIfUploadTableExist()){
                        entryCheckHelper.copyTempTable(temp_job_file_path,false);
                    }
                    result.setSuccess(true);
                }else {
                    throw new Exception(mContext.getString(R.string.sys_unzip_fail));
                }
            }else{
                BufferedReader in=new BufferedReader(new InputStreamReader(httpConn.getErrorStream(),"UTF-8"));
                String raw_string=in.readLine();
                ServerResponse tempServerResponse=gson.fromJson(raw_string,ServerResponse.class);
//                LOGE(TAG,"ERROR:"+raw_string);
                if(tempServerResponse!=null){
                    result.setMessage(tempServerResponse.getMessage());
                }
            }
            httpConn.disconnect();
        }catch (Exception ex){
            String errorMessage=ex.getMessage();
            if(ex instanceof ConnectException){
                errorMessage=mContext.getString(R.string.sys_plz_check_internet);
            }
//            LOGE(TAG,"DownloadFileFromURL error:"+errorMessage);
            result.setMessage(errorMessage);
        }
        return result;
    }

    @Override
    protected void onPostExecute(ServerResponse serverResponse){
        if(serverResponse.isSuccess()){
            CheckDateFormatItem checkDateFormatItem=new CheckDateFormatItem();
            final String currentDateTimeStr=checkDateFormatItem.getFORMAT_YYYYMMDD_HHMMSS();
            mContext.getmAPPDBHelper().insertOrUpdateUserID(mUserID,currentDateTimeStr);

            AlertDialogFragment alertDialogFragment=AlertDialogFragment.newInstance(mContext.getString(R.string.sys_download_ok));
            alertDialogFragment.show(mContext.getFragmentManager(),FragmentTagConstants.AlertDialogFragment);
            alertDialogFragment.addListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }else{
            String message=serverResponse.getMessage();
            AlertDialogFragment alertDialogFragment=AlertDialogFragment.newInstance(mContext.getString(R.string.sys_download_fail),mContext.getString(R.string.system_msg_fail_prefix)+message);
            alertDialogFragment.show(mContext.getFragmentManager(),FragmentTagConstants.AlertDialogFragment);
        }
        mDialog.dismiss();
    }
}
