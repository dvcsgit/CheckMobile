package com.jfdimarzio.check.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jfdimarzio.check.R;

public class PinLockFragment extends DialogFragment {

    private View mRootView;

    String userEntered="";
    String userPin="8888";
    final int PIN_LENGTH=4;

    boolean keyPadLockedFlag=false;

    TextView statusView;

    Button button0;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Button button9;
    Button buttonExit;
    private Button mbtn_back;

    TextView passwordInput;

    public PinLockFragment(){}

    public static PinLockFragment newInstance(){
        PinLockFragment frag=new PinLockFragment();
        Bundle args=new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        mRootView=inflater.inflate(R.layout.pin_lock,container,false);
        statusView=mRootView.findViewById(R.id.statusview);
        passwordInput=mRootView.findViewById(R.id.txt_pin_password);
        mbtn_back=mRootView.findViewById(R.id.btn_back);
        buttonExit=mRootView.findViewById(R.id.buttonExit);
        mbtn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(passwordInput.getText().toString())){
                    String temp=passwordInput.getText().toString().substring(0,passwordInput.getText().toString().length()-1);
                    userEntered=temp;
                    passwordInput.setText(temp);
                }
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.cancel();
                }
            }
        });

        View.OnClickListener pinButtonHandler=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyPadLockedFlag==true){
                    return;
                }
                Button pressedButton=(Button)v;
                if(userEntered.length()<PIN_LENGTH){
                    userEntered=userEntered+pressedButton.getText();
                    passwordInput.setText(passwordInput.getText().toString()+"*");
                    if(userEntered.length()==PIN_LENGTH){
                        if(userEntered.equals(userPin)){
                            statusView.setTextColor(Color.GREEN);
                            statusView.setText(R.string.sys_pin_lock_correct);
                            if(mListener!=null){
                                mListener.pass();
                            }
                        }
                        else {
                            statusView.setTextColor(Color.RED);
                            statusView.setText(R.string.sys_pin_lock_fail);
                            keyPadLockedFlag=true;
                            new LockKeyPadOperation().execute("");
                        }
                    }
                }
                else {
                    passwordInput.setText("");
                    userEntered="";
                    statusView.setText("");
                    userEntered=userEntered+pressedButton.getText();
                    passwordInput.setText("8");
                }
            }
        };
        button0=mRootView.findViewById(R.id.button0);
        button0.setOnClickListener(pinButtonHandler);
        button1=mRootView.findViewById(R.id.button1);
        button1.setOnClickListener(pinButtonHandler);
        button2=mRootView.findViewById(R.id.button2);
        button2.setOnClickListener(pinButtonHandler);
        button3=mRootView.findViewById(R.id.button3);
        button3.setOnClickListener(pinButtonHandler);
        button4=mRootView.findViewById(R.id.button4);
        button4.setOnClickListener(pinButtonHandler);
        button5=mRootView.findViewById(R.id.button5);
        button5.setOnClickListener(pinButtonHandler);
        button6=mRootView.findViewById(R.id.button6);
        button6.setOnClickListener(pinButtonHandler);
        button7=mRootView.findViewById(R.id.button7);
        button7.setOnClickListener(pinButtonHandler);
        button8=mRootView.findViewById(R.id.button8);
        button8.setOnClickListener(pinButtonHandler);
        button9=mRootView.findViewById(R.id.button9);
        button9.setOnClickListener(pinButtonHandler);

//        if(Build.VERSION.SDK_INT>=Build.VERSION.LOLLIPOP){
//            passwordInput.setShowSoftInputOnFocus(false);
//        }

        return mRootView;
    }

    public interface PinLockListener{
        void pass();
        void cancel();
    }
    private PinLockListener mListener;
    public void setmListener(PinLockListener mListener){
        this.mListener=mListener;
    }

    private class LockKeyPadOperation extends AsyncTask<String,Void,String>{
//        @Override
//        protected void onPreExcute(){
//
//        }
        @Override
        protected String doInBackground(String... params){
            for(int i=0;i<2;i++){
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return "Executed";
        }
        @Override
        protected void onProgressUpdate(Void... values){

        }
        @Override
        protected void onPostExecute(String result){
            statusView.setText("");
            passwordInput.setText("");
            userEntered="";
            keyPadLockedFlag=false;
        }
    }
}
