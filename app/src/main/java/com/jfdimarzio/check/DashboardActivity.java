package com.jfdimarzio.check;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.jfdimarzio.check.fragment.ConfirmDialogFragment;
import com.jfdimarzio.check.fragment.Step1JobFragment;
import com.jfdimarzio.check.model.UserSession;
import com.jfdimarzio.check.util.consts.FragmentTagConstants;
import com.jfdimarzio.check.util.consts.RequestCodeConstants;

import java.util.ArrayList;

public class DashboardActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
//    private static final String TAG=makeLogTag(DashboardActivity.class);
    private TextView mlb_login_name;
    private TextView mlb_login_userid;
    private FragmentManager mFragmentManager;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout=findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeaderMainView=navigationView.inflateHeaderView(R.layout.nav_header_main);
        if(navHeaderMainView!=null){
            mlb_login_name=navHeaderMainView.findViewById(R.id.lb_login_name);
            mlb_login_userid=navHeaderMainView.findViewById(R.id.lb_login_userid);
        }
//UI
        if(savedInstanceState==null){
            mFragmentManager=getFragmentManager();
            FragmentTransaction fragmentTransaction=mFragmentManager.beginTransaction();
            Fragment fragment=new Step1JobFragment();
            fragmentTransaction.add(R.id.container,fragment,FragmentTagConstants.Step1JobFragment);
            fragmentTransaction.commit();
        }else {
            mFragmentManager=getFragmentManager();
        }
        //Setting Value
        UserSession userSession=this.getmUserSession();
        if(mlb_login_userid!=null&&mlb_login_name!=null){
            mlb_login_userid.setText(userSession.getID());
            mlb_login_name.setText(userSession.getName());
        }

        // check block
//        BlockStatus blockStatus = DashboardActivity.super.checkIfNeedBlockOldVersion(DashboardActivity.this);
//        if(!blockStatus.equals(BlockStatus.Pass))
//        {
//            super.showBlock(DashboardActivity.this,blockStatus);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //no inspection Simplifiable If Statement
        if (id == R.id.action_clear) {
            ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.lb_action_clear));
            confirmDialogFragment.addListener(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mEquipCheckHelper.resetTables();
                    Step1JobFragment step1JobFragment = (Step1JobFragment) mFragmentManager.findFragmentByTag(FragmentTagConstants.Step1JobFragment);
                    if(step1JobFragment != null)
                    {
                        step1JobFragment.refresh(new ArrayList<String>());
                    }
                }
            });
            confirmDialogFragment.show(this.getFragmentManager(), FragmentTagConstants.ConfirmDialogFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.nav_upload){
            Step1JobFragment step1JobFragment= (Step1JobFragment) mFragmentManager.findFragmentByTag(FragmentTagConstants.Step1JobFragment);
            if(step1JobFragment!=null&&step1JobFragment.isVisible()){
                Intent intent=new Intent(this,UploadActivity.class);
                step1JobFragment.startActivityForResult(intent, RequestCodeConstants.AfterUpload);
            }
        }else if(id==R.id.nav_setting) {

        }else if(id==R.id.nav_uid){

        }else if(id==R.id.nav_sign_out){
//            signOut();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
}
