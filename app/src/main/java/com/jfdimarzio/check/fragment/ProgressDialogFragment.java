package com.jfdimarzio.check.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.jfdimarzio.check.R;

public class ProgressDialogFragment extends DialogFragment {
    private static final String ARG_TITLE="ARG_TITLE";
    private static final String ARG_MESSAGE="ARG_MESSAGE";
    private static final String ARG_CANCELABLE="ARG_CANCELABLE";
    private ProgressDialog mProgressDialog;

    public ProgressDialogFragment(){}

    public static ProgressDialogFragment newInstance(String message,boolean cancelable){
        return newInstance("",message,cancelable);
    }

    public static ProgressDialogFragment newInstance(String title,String message,boolean cancelable){
        ProgressDialogFragment frag=new ProgressDialogFragment();
        Bundle args=new Bundle();
        args.putString("ARG_TITLE",title);
        args.putString("ARG_MESSAGE",message);
        args.putBoolean("ARG_CANCELABLE",cancelable);
        frag.setArguments(args);
        return frag;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        String title=this.getArguments().getString("ARG_TITLE");
        String message=this.getArguments().getString("ARG_MESSAGE");
        boolean cancelable=this.getArguments().getBoolean("ARG_CANCELABLE");
        if(title.isEmpty()){
            title=this.getString(R.string.system_dialog_title);
        }
        this.mProgressDialog=new ProgressDialog(this.getActivity());
        this.mProgressDialog.setTitle(title);
        this.mProgressDialog.setMessage(message);
        this.mProgressDialog.setIndeterminate(false);
        this.mProgressDialog.setProgressStyle(1);
        this.setCancelable(false);
        return this.mProgressDialog;
    }

    public void updateProgress(int value){
        ProgressDialog dialog=(ProgressDialog)this.getDialog();
        if(dialog!=null){
            dialog.setProgress(value);
        }
    }

    public Runnable updateMessageRunnable(final String message){
        Runnable aRunnable=new Runnable(){
            public  void run(){
                ProgressDialogFragment.this.mProgressDialog.setMessage(message);
            }
        };
        return aRunnable;
    }

    public void onCancel(DialogInterface dialog){
        super.onCancel(dialog);
        if(this.getProgressDialogFragmentListener()!=null){
            this.getProgressDialogFragmentListener().onProgressCancelled();
        }
    }

    private ProgressDialogFragment.ProgressDialogFragmentListener getProgressDialogFragmentListener(){
        if(this.getActivity()==null){
            return null;
        }else{
            return this.getActivity() instanceof ProgressDialogFragment.ProgressDialogFragmentListener?(ProgressDialogFragment.ProgressDialogFragmentListener)this.getActivity():null;
        }
    }

    public interface ProgressDialogFragmentListener{
        void onProgressCancelled();
    }

    public Runnable updateMesssageRunnable(final String message) {
        Runnable aRunnable = new Runnable() {
            public void run() {
                ProgressDialogFragment.this.mProgressDialog.setMessage(message);
            }
        };
        return aRunnable;
    }
}
