package com.jfdimarzio.check.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Message;
import android.util.TypedValue;

import com.jfdimarzio.check.R;

public class AlertDialogFragment extends DialogFragment{
    private static final String ARG_TITLE="ARG_TITLE";
    private static final String ARG_MESSAGE="ARG_MESSAGE";
    private OnClickListener mListener;

    public AlertDialogFragment(){}

    public static AlertDialogFragment newInstance(String message){
        AlertDialogFragment frag=new AlertDialogFragment();
        Bundle args=new Bundle();
        args.putString("ARG_MESSAGE",message);
        frag.setArguments(args);
        return frag;
    }

    public static AlertDialogFragment newInstance(String title,String message){
        AlertDialogFragment frag=new AlertDialogFragment();
        Bundle args=new Bundle();
        args.putString("ARG_MESSAGE",message);
        args.putString("ARG_TITLE",title);
        frag.setArguments(args);
        return frag;
    }

    public void addListener(OnClickListener listener){this.mListener=listener;}

    public Dialog onCreateDialog(Bundle savedInstanceState){
        String message=this.getArguments().getString("ARG_MESSAGE");
        String title=this.getArguments().getString("ARG_TITLE");
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this.getActivity());
        TypedValue typedValue=new TypedValue();
        this.getActivity().getTheme().resolveAttribute(android.R.attr.icon,typedValue,true);
        alertDialogBuilder.setIcon(typedValue.resourceId);
        if(title!=null){
            alertDialogBuilder.setTitle(title);
        }else{
            alertDialogBuilder.setTitle(this.getResources().getString(R.string.system_dialog_title));
        }

        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(android.R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(AlertDialogFragment.this.mListener!=null){
                    AlertDialogFragment.this.mListener.onClick(dialog,which);
                }
                dialog.dismiss();
            }
        });
        this.setCancelable(false);
        return alertDialogBuilder.create();
    }

    public void onDestroyView(){
        if(this.getDialog()!=null&&this.getRetainInstance()){
            this.getDialog().setDismissMessage((Message)null);
        }
        super.onDestroyView();
    }

    public void show(FragmentManager manager,String tag){
        if(manager.findFragmentByTag(tag)==null){
            super.show(manager,tag);
        }
    }
}
