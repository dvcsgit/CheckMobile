package com.jfdimarzio.check;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jfdimarzio.check.dao.Person;
import com.jfdimarzio.check.fragment.AlertDialogFragment;
import com.jfdimarzio.check.fragment.PinLockFragment;
import com.jfdimarzio.check.model.enumtype.LoginStatus;
import com.jfdimarzio.check.util.consts.FragmentTagConstants;
import com.jfdimarzio.check.util.consts.RequestCodeConstants;

public class LoginActivity extends BaseActivity {
//    private static final String TAG=makeLogTag(LoginActivity.class);

    private UserLoginTask mAuthTask=null;

    //region UI
    private AutoCompleteTextView mtxtAccount;
    private EditText mtxtPassword;
    private View mProgressView;
    private View mLoginFormView;
    private Button mbtn_setting;
    private Button mbtn_download;
    private Button mbtn_login;
    private TextView mlb_version;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mbtn_setting=findViewById(R.id.btn_setting);
        mbtn_download=findViewById(R.id.btn_download);
        mLoginFormView=findViewById(R.id.login_form);
        mProgressView=findViewById(R.id.login_progress);
        mlb_version=findViewById(R.id.lb_version);
        mtxtAccount=findViewById(R.id.txtAccount);
        mtxtPassword=findViewById(R.id.txtPassword);
        mbtn_login=findViewById(R.id.btn_login);

//        mtxtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId==R.id.login||actionId== EditorInfo.IME_NULL){
//                    return true;
//                }
//                return false;
//            }
//        });

        mbtn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        mbtn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PinLockFragment pinLockFragment= PinLockFragment.newInstance();
                pinLockFragment.show(LoginActivity.this.getSupportFragmentManager(), FragmentTagConstants.AlertDialogFragment);
                pinLockFragment.setmListener(new PinLockFragment.PinLockListener() {
                    @Override
                    public void pass() {
                        Intent intent=new Intent(getApplicationContext(), SettingsActivity.class);
                        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,SettingsActivity.GeneralPreferenceFragment.class.getName());
                        intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS,true);
                        startActivity(intent);
                        pinLockFragment.dismiss();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        });

        mbtn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),DownloadActivity.class);
                startActivityForResult(intent, RequestCodeConstants.Download);
            }
        });
    }

    private void attemptLogin(){
        if(mAuthTask!=null){
            return;
        }

        mtxtAccount.setError(null);
        mtxtPassword.setError(null);

        String userID=mtxtAccount.getText().toString();
        String password=mtxtPassword.getText().toString();

        boolean cancel=false;
        View focusView=null;

        // 驗證帳號有沒有填
        if (TextUtils.isEmpty(userID)) {
            mtxtAccount.setError(getString(R.string.error_field_required));
            focusView = mtxtAccount;
            cancel = true;
        }

        // 驗證密碼有沒有填
        if (TextUtils.isEmpty(password)) {
            mtxtPassword.setError(getString(R.string.error_field_required));
            focusView = mtxtPassword;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            showProgress(true);
            mAuthTask = new UserLoginTask(userID, password, true);
            mAuthTask.execute();
        }
    }

    public class UserLoginTask extends AsyncTask<String,Void,LoginStatus>{
        private boolean mIsLoginByForm;
        private final String pId;
        private final String mPassword;

        public UserLoginTask(String pId,String mPassword,boolean mIsLoginByForm){
            this.pId=pId;
            this.mPassword=mPassword;
            this.mIsLoginByForm=mIsLoginByForm;
        }

        @Override
        protected LoginStatus doInBackground(String... params){
            Person person;
//        if(this.mIsLoginByForm){
            person=mEquipCheckHelper.getPerson(pId);
            if(person==null){
                return LoginStatus.NotExist;
            }else if(!person.getPassword().equals(mPassword)){
                return LoginStatus.PWD_Invalid;
            }else{
                mSessionManager.createLoginSession(person.getPId(),person.getName());
                return LoginStatus.Success;
            }
//        }else {
//            String uid=params[0];
//            Login by reading the card.
//            user=mEquipCheckHelper.getUserByTag(uid);
//            if(user==null{
//                return LoginStatus.UnknowTAG;
//            }else{
//                mSessionManager.createLoginSession(user.getID(),user.getName());
//                return LoginStatus.Success;
//            }
//        }
        }

        @Override
        protected void onPostExecute(final LoginStatus status){
            mAuthTask=null;
            showProgress(false);
            if(status==LoginStatus.Success){
                resetForm();
                Intent intent=new Intent(getApplicationContext(),DashboardActivity.class);
                startActivity(intent);
            }else if(status==LoginStatus.NotExist){
                mtxtAccount.setError(getString(R.string.error_incorrect_user));
                mtxtAccount.requestFocus();
            }else if(status==LoginStatus.PWD_Invalid){
                mtxtPassword.setError(getString(R.string.error_incorrect_password));
                mtxtPassword.requestFocus();
            }else if(status==LoginStatus.UnknowTAG){
                AlertDialogFragment alertDialogFragment=AlertDialogFragment.newInstance(getString(R.string.system_msg_unknown_tag));
                alertDialogFragment.show(LoginActivity.this.getFragmentManager(),FragmentTagConstants.AlertDialogFragment);
            }
        }

        @Override
        protected void onCancelled(){
            mAuthTask=null;
            showProgress(false);
        }
    }

    private void resetForm(){
        mtxtAccount.setError(null);
        mtxtPassword.setError(null);
        mtxtPassword.setText(null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
