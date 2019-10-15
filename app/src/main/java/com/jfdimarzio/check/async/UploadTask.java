package com.jfdimarzio.check.async;

import android.content.DialogInterface;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.jfdimarzio.check.AppController;
import com.jfdimarzio.check.BaseActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.fragment.AlertDialogFragment;
import com.jfdimarzio.check.fragment.ProgressDialogFragment;
import com.jfdimarzio.check.model.ApiFormInput;
import com.jfdimarzio.check.model.response.ServerResponse;
import com.jfdimarzio.check.util.FileZipUtils;
import com.jfdimarzio.check.util.PreferenceUtils;
import com.jfdimarzio.check.util.consts.FragmentTagConstants;
import com.jfdimarzio.check.util.consts.SystemConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.jfdimarzio.check.util.LogUtils.makeLogTag;

public class UploadTask extends AsyncTask<String,Integer,ServerResponse> {
    private static final String TAG = makeLogTag(UploadTask.class);
    private final BaseActivity mContext;

    private static final String FILE_PART_NAME = "file";
    private static final String STRING_PART_NAME = "text";
    private final String mUploadTempPath;
    private final String mFilePath;
    private final String mUploadUrl;
    private final String mPicPath;
    private final String mTempPath;
    private final ApiFormInput mFormModel;
    private ArrayList<String> muploadIDList;
    private ProgressDialogFragment mDialog;
    private ArrayList<String> mpackingFileList = new ArrayList<>();
    public interface UploadHandler {
        void done(ArrayList<String> mForm);
    }
    private UploadHandler mUploadHandler;

    public UploadTask(BaseActivity mContext, ApiFormInput model, ArrayList<String> uploadIDList,UploadHandler uploadHandler) {
        this.mContext = mContext;
        this.mDialog = ProgressDialogFragment.newInstance(mContext.getString(R.string.sys_upload), true);
        this.muploadIDList = uploadIDList;
        PreferenceUtils mPreferenceUtils = this.mContext.getmPreferenceUtils();
        this.mUploadTempPath = mPreferenceUtils.getUploadTempPath();
        this.mTempPath = mPreferenceUtils.getTempPath();
        this.mPicPath = mPreferenceUtils.getPicPath();
        this.mFilePath = mPreferenceUtils.getFilePath();
        this.mUploadUrl = mPreferenceUtils.getUploadUrl();
        this.mFormModel = model;
        this.mUploadHandler = uploadHandler;

    }

    @Override
    protected ServerResponse doInBackground(String... strings) {
        ServerResponse reuslt = new ServerResponse();
        final Gson gson = new Gson();
        String msgTitle = "上傳資料準備中...\n";

        //Flow0 UploadDefine 先清掉 寫入這次的
        boolean insert_flag = mContext.getmEquipCheckHelper().deleteInsertUploadDefine(muploadIDList);
//        LOGD(TAG, "insert_flag:" + insert_flag);

        //Flow1 清除暫存檔
//        LOGD(TAG, "mFilePath:" + mFilePath);
//        LOGD(TAG,"mUploadTempPath:" + mUploadTempPath);

        File directory = new File(mUploadTempPath);
        File[] tempFileList = directory.listFiles();
//        LOGD(TAG,"刪除暫存..資料筆數:" + tempFileList.length);
        for(File temp_file : tempFileList)
        {
            boolean deleted_flag = temp_file.delete();
        }
//        LOGD(TAG,"刪除暫存完成!");

//        Timber.d("mTempPath: %s" + mTempPath);
        File temp_dir = new File(mTempPath);
        File[] temp_dir_file_list = temp_dir.listFiles();
//        LOGD(TAG,"刪除暫存..資料筆數:" + temp_dir_file_list.length);
        for(File temp_file : temp_dir_file_list)
        {
            boolean deleted_flag = temp_file.delete();
        }

        //Flow2 將資料庫copy
        String srcDBFilePath = this.mFilePath + File.separator + SystemConstants.SQLITE_EQUIPCHECK;
        String targetDBFilePath = this.mUploadTempPath + File.separator + SystemConstants.SQLITE_EQUIPCHECK;
        //複製資料庫
        copyfile(srcDBFilePath, targetDBFilePath);
        //
        //Flow3 照片copy
        int totalArrivePhotoCount = 0;
        int currentArrivePhotoCount = 0;
        mDialog.updateProgress(0);
        ArrayList<String> arriveRecordPhotoList = mContext.getmEquipCheckHelper().getArriveRecordPhotoList(muploadIDList);
        totalArrivePhotoCount = arriveRecordPhotoList.size();
        updateDialogMessage(mContext.getString(R.string.sys_packing_photo));
        for (String srcPhoto : arriveRecordPhotoList) {
            File srcPhotoFile = new File(srcPhoto);
            if (srcPhotoFile.exists()) {
                copyfile(srcPhotoFile.getPath(), mUploadTempPath + File.separator + srcPhotoFile.getName());
                mpackingFileList.add(srcPhotoFile.getPath());
            }
            //處理進度條
            int copyPicPercent = (int) (currentArrivePhotoCount * 100 / totalArrivePhotoCount);
            mDialog.updateProgress(copyPicPercent);
            currentArrivePhotoCount++;
        }

        updateDialogMessage(mContext.getString(R.string.sys_pack_check_photo));
        int totalResultPhotoCount = 0;
        int currentResultPhotoCount = 0;
        mDialog.updateProgress(0);
        ArrayList<String> checkResultPhotoList = new ArrayList<>();

        //點檢照片
        ArrayList<String> check_photos = mContext.getmEquipCheckHelper().getCheckResultPhotoList(muploadIDList);
        checkResultPhotoList.addAll(check_photos);
        //
        ArrayList<String> mform_check_photos = mContext.getmEquipCheckHelper().getMFormCheckResultPhotoList(muploadIDList);
        checkResultPhotoList.addAll(mform_check_photos);

        totalResultPhotoCount = checkResultPhotoList.size();

        for (String srcPhoto : checkResultPhotoList) {
            File srcPhotoFile = new File(srcPhoto);
            if (srcPhotoFile.exists()) {
                copyfile(srcPhotoFile.getPath(), mUploadTempPath + File.separator + srcPhotoFile.getName());
            }
            //處理進度條
            int copyPicPercent = (int) (currentResultPhotoCount * 100 / totalResultPhotoCount);
            mDialog.updateProgress(copyPicPercent);
            currentResultPhotoCount++;
        }

        //FLOW 2.1 搬移日誌
        if(AppController.getInstance().getPreferenceUtils().getENABLE_LOG())
        {
            String src_log_db_file_path = this.mFilePath + File.separator + SystemConstants.SQLITE_LOG;
            String target_log_db_file_path = this.mUploadTempPath + File.separator + SystemConstants.SQLITE_LOG;
            //複製資料庫
            copyfile(src_log_db_file_path, target_log_db_file_path);
        }

        //Flow3 壓縮檔案
        mDialog.updateProgress(0);//進行到下一個階段
        String targetFilePath = mFilePath + "/UploadFile.zip"; //打包完的壓縮檔
        FileZipUtils.zip(mUploadTempPath, targetFilePath);
//        FileZipUtils.zip(mUploadTempPath, targetFilePath, mDialog);

        //Flow4 上傳檔案
//        LOGD(TAG,"mUploadUrl:" + mUploadUrl);
        mDialog.updateProgress(0);//進行到下一個階段
        msgTitle = mContext.getString(R.string.sys_upload_file_to_server);
        updateDialogMessage(msgTitle);
        File upload_file = new File(targetFilePath);

        for (int i = 0; i < 5; i++) {

            try {

                if(i>0)
                {
                    updateDialogMessage(msgTitle + "retry..." + i+ "..");
                }
                MultipartUtility multipart = new MultipartUtility(mContext, mUploadUrl, "UTF-8", mDialog);
                //Map<String, String> params = new HashMap<>();
                //params.put("UserID", mUserID);

                //JSONObject param = new JSONObject(params);
                //String param_str = param.toString();

                String json_param = gson.toJson(mFormModel);
                Timber.d("json_param : %s", json_param);

                multipart.addFormField("text", json_param);
                multipart.addFilePart("fileUpload", upload_file);

                List<String> response = multipart.finish();

                System.out.println("SERVER REPLIED:");

                String serverResult = response.get(0);

                for (String line : response) {
                    System.out.println(line);
                }

                reuslt = gson.fromJson(serverResult, ServerResponse.class);
                reuslt.setSuccess(true);

                break;

            }catch (Exception e) {
                e.printStackTrace();
//                LOGE(TAG, "發生錯誤:" + e.getMessage());
                reuslt.setMessage(e.getMessage());
            }
        }

        return reuslt;
    }

    /**
     * 顯示進度條
     */
    private void showProgress() {

        mDialog.show(mContext.getSupportFragmentManager(), FragmentTagConstants.ProgressDialogFragment);
    }

    @Override
    protected void onPreExecute() {
        showProgress();
    }

    @Override
    protected void onPostExecute(ServerResponse serverResponse) {

        if(serverResponse.isSuccess())
        {
            //清除抄表資料與照片資料夾
            mContext.getmEquipCheckHelper().deleteUploadRelateRecord(muploadIDList);

            //清除 已經上傳成功的照片
//            DeleteImageTask deleteImageTask = new DeleteImageTask(mPicPath);
//            deleteImageTask.execute();

            // 刪掉日誌
            if(AppController.getInstance().getPreferenceUtils().getENABLE_LOG())
            {
                AppController.getInstance().getLOGDBHelper().resetTables();
            }

            // 這邊要加檢查

            AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(mContext.getString(R.string.sys_upload_ok));
            alertDialogFragment.show(mContext.getFragmentManager(), FragmentTagConstants.AlertDialogFragment);
            alertDialogFragment.addListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if(mUploadHandler != null) {
                        mUploadHandler.done(muploadIDList);
                    }

                }
            });

        }else
        {
            String message = serverResponse.getMessage();
            AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(mContext.getString(R.string.sys_upload_fail), mContext.getString(R.string.sys_upload_fail_prefix) + message);
            alertDialogFragment.show(mContext.getFragmentManager(),FragmentTagConstants.AlertDialogFragment);
        }

        mDialog.dismiss();
    }

    /**
     * 複製檔案
     * @param srFile 來源
     * @param dtFile 目表
     */
    private void copyfile(String srFile, String dtFile) {
        try {
//            LOGD(TAG,"copyfile 來源:"+ srFile);
//            LOGD(TAG,"copyfile 目標:"+ dtFile);
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            InputStream in = new FileInputStream(f1);

            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

        } catch (FileNotFoundException e) {
//            LOGE(TAG,e.getMessage());

        } catch (IOException e) {
//            LOGE(TAG,e.getMessage());
        }
    }

    /**
     * 更新UI的訊息
     * @param message
     */
    private void updateDialogMessage(String message)
    {
        mContext.runOnUiThread(mDialog.updateMesssageRunnable(message));
    }

    /**
     * 非同步 刪除掉存放在暫存區的照片
     * 離開頁面時觸發
     */
    private class DeleteImageTask extends AsyncTask<Void, Void, Boolean> {

        private String mTempFolder = "";

        public DeleteImageTask(String tempFolder) {
            this.mTempFolder = tempFolder;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;
            try
            {
                File directory = new File(mTempFolder);
                File[] tempFileList = directory.listFiles();
//                LOGD(TAG,"刪除照片中..資料筆數:" + tempFileList.length);
                for(File temp_file : tempFileList)
                {
                    boolean deleted_flag = temp_file.delete();
                }
//                LOGD(TAG,"刪除照片完成!");
            }
            catch (Exception ex)
            {
//                LOGD(TAG,"刪除檔案發生錯誤:" + ex.getMessage());
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean status) {

        }
    }
}
