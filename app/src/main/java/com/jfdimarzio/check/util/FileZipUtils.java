package com.jfdimarzio.check.util;

import android.support.v4.app.FragmentActivity;

import com.jfdimarzio.check.fragment.ProgressDialogFragment;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileZipUtils {
//    private static final String TAG=LogUtils.makeLogTag(FileZipUtils.class);
    public FileZipUtils(){}

    public static void unzip(String source,String destination,String ignoreFile){
//        LogUtils.LOGI(TAG,"unzip src:"+source);
//        LogUtils.LOGI(TAG,"unzip dest:"+destination);
//        int BUFFER_SIZE=true;
        BufferedOutputStream bufferedOutputStream=null;
        try{
            FileInputStream fileInputStream=new FileInputStream(source);
            ZipInputStream zipInputStream=new ZipInputStream(new BufferedInputStream(fileInputStream));
            while (true){
                ZipEntry zipEntry;
                while ((zipEntry=zipInputStream.getNextEntry())!=null){
                    String zipEntryName=zipEntry.getName();
                    File file=new File(destination+zipEntryName);
                    String fileName=file.getName();
                    if(fileName.equals(ignoreFile)){
                        if(file.exists()){
//                            LogUtils.LOGI(TAG,,"This file"+fileName+"has existed,it will not create.");
                            continue;
                        }
                    }else if(file.exists()){
                        file.delete();
//                        LogUtils.LOGI(TAG,"unzip"+file.getAbsolutePath()+" Delete");
                    }

                    if(zipEntry.isDirectory()){
                        file.mkdirs();
                    }else {
                        byte[] buffer=new byte[4096];
                        FileOutputStream fileOutputStream=new FileOutputStream(file);
                        bufferedOutputStream=new BufferedOutputStream(fileOutputStream,4096);

                        int count;
                        while((count=zipInputStream.read(buffer,0,4096))!=-1){
                            bufferedOutputStream.write(buffer,0,count);
                        }
                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();
                    }
                }
                zipInputStream.close();
                break;
            }
        }catch (FileNotFoundException var14){
            var14.printStackTrace();
        }catch (IOException var15){
            var15.printStackTrace();
        }
    }

    public static void zip(ArrayList<String> source,String destination){
//        LogUtils.LOGI(TAG,"zip src:"+source);
//        LogUtils.LOGI(TAG,"zip dest:"+destination);
        try{
            byte[] buffer = new byte[1024];
            FileOutputStream fos = new FileOutputStream(destination);
            ZipOutputStream zos = new ZipOutputStream(fos);
            Iterator var5 = source.iterator();

            while(var5.hasNext()) {
                String temp_file_path = (String)var5.next();
                File temp_file = new File(temp_file_path);
//                LogUtils.LOGD(TAG, "Adding file: " + temp_file.getName());
                FileInputStream fis = new FileInputStream(temp_file);
                zos.putNextEntry(new ZipEntry(temp_file.getName()));

                int length;
                while((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();
                fis.close();
            }

            zos.close();

        }catch (IOException var10){
//            LogUtils.LOGE(TAG, var10.getMessage());
        }
    }

    public static void zip(String source, String destination) {
//        LogUtils.LOGI(TAG, "zip src:" + source);
//        LogUtils.LOGI(TAG, "zip dest:" + destination);

        try {
            byte[] buffer = new byte[1024];
            FileOutputStream fos = new FileOutputStream(destination);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File dir = new File(source);
            int i;
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();

                for(i = 0; i < files.length; ++i) {
//                    LogUtils.LOGD(TAG, "Adding file: " + files[i].getName());
                    FileInputStream fis = new FileInputStream(files[i]);
                    zos.putNextEntry(new ZipEntry(files[i].getName()));

                    int length;
                    while((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }

                    zos.closeEntry();
                    fis.close();
                }
            } else {
                FileInputStream fis = new FileInputStream(dir);
                zos.putNextEntry(new ZipEntry(dir.getName()));

                while((i = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, i);
                }

                zos.closeEntry();
                fis.close();
            }

            zos.close();
        } catch (IOException var10) {
//            LogUtils.LOGE(TAG, var10.getMessage());
        }

    }

    public static void zip(String source,String destination,final ProgressDialogFragment dialog)
    {
        //LOGI(TAG,"zip src:"+source);
        //LOGI(TAG,"zip dest:"+destination);
        try {
            FragmentActivity mContext = null;
            String msgTitle = "檔案壓縮中...\n";
            if(dialog != null)
            {
                mContext = dialog.getActivity();
            }


            // create byte buffer
            byte[] buffer = new byte[1024];
            FileOutputStream fos = new FileOutputStream(destination);
            ZipOutputStream zos = new ZipOutputStream(fos);
            File dir = new File(source);
            //檢查是檔案還是目錄
            if(dir.isDirectory())
            {
                File[] files = dir.listFiles();
                long folderFileSize = FileUtils.sizeOfDirectory(dir);//目前要壓縮的檔案大小
                long currentCount = 0;//目前處理量
                String totalFileSizeInfo = StringUtils.humanReadableByteCount(folderFileSize,true);
//                LOGD(TAG,"folderFileSize" + totalFileSizeInfo);
                String fileTitleFormat = "目前處理檔案大小:\n %s / " + totalFileSizeInfo;

                if(dialog != null)
                {
                    mContext.runOnUiThread(dialog.updateMesssageRunnable(msgTitle));
                }


                for (int i = 0; i < files.length; i++) {

                    //LOGD(TAG,"Adding file: " + files[i].getName());
                    FileInputStream fis = new FileInputStream(files[i]);
                    zos.putNextEntry(new ZipEntry(files[i].getName()));
                    int length;
                    String subTitle = String.format(fileTitleFormat, StringUtils.humanReadableByteCount(currentCount, true));
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                        currentCount += length;

                        if(dialog != null)
                        {
                            mContext.runOnUiThread(dialog.updateMesssageRunnable(msgTitle + subTitle));

                            //處理進度條
                            int copyPicPercent = (int)(currentCount*100/folderFileSize);
                            dialog.updateProgress(copyPicPercent);
                        }

                    }
                    zos.closeEntry();
                    // close the InputStream
                    fis.close();
                }
            }
            else
            {
                FileInputStream fis = new FileInputStream(dir);
                zos.putNextEntry(new ZipEntry(dir.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();

                // close the InputStream
                fis.close();
            }

            // close the ZipOutputStream
            zos.close();

        }
        catch (IOException ex) {
            //LOGE(TAG,ex.getMessage());
            ex.getMessage();
        }
    }
}
