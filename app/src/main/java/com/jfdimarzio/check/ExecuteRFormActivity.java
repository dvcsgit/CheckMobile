package com.jfdimarzio.check;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jfdimarzio.check.dao.rform.RFormMaterialResult;
import com.jfdimarzio.check.fragment.rform.RFormCheckFragment;
import com.jfdimarzio.check.fragment.rform.RFormMaterialFragment;
import com.jfdimarzio.check.fragment.rform.RFormWorkingHourFragment;
import com.jfdimarzio.check.model.rform.RFormCheckItem;
import com.jfdimarzio.check.model.rform.RFormMaterialItem;
import com.jfdimarzio.check.model.rform.RFormViewModel;
import com.jfdimarzio.check.model.rform.RFormWorkingHour;
import com.jfdimarzio.check.util.CompatibleUtils;
import com.jfdimarzio.check.util.consts.IntentKeyConstants;
import com.jfdimarzio.check.widget.ArcProgress;

import java.util.ArrayList;
import java.util.List;

public class ExecuteRFormActivity extends BaseActivity {
    private static final String ARG_VIEWMODEL = "ARG_VIEWMODEL";

    private Drawable mbtn_selector_default;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button mbtn_save;
    private int m_color_white;
    private Drawable mbtn_selector_primary2;
    private Button mbtn_edit;
    private TextView mlb_title_remark;
    private TextView mlb_remark;
    private RFormViewModel mARG_VIEWMODEL;
    private int mPOSITION;
    private String mtemplate_progress;
    private TextView mlb_sub_message;
    private ArcProgress marc_progress;
    private TextView mlb_current_title;
    private TextView mlb_current_progress;
    private String mprefix;
    private ExecuteRFormActivity.ViewPagerAdapter madapter;
    private RFormCheckFragment mRFormCheckFragment;
    private RFormMaterialFragment mRFormMaterialFragment;
    private RFormWorkingHourFragment mRFormWorkingHourFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rform_execute);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Display the symbol "<-" on the top left of the interface
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        mbtn_save = findViewById(R.id.btn_save);

        this.m_color_white = ContextCompat.getColor(this, R.color.white);
        this.mbtn_selector_default = ContextCompat.getDrawable(this, R.drawable.btn_selector_default);
        this.mbtn_selector_primary2 = ContextCompat.getDrawable(this, R.drawable.btn_selector_primary2);

        mbtn_edit=findViewById(R.id.btn_edit);
        mlb_title_remark=findViewById(R.id.lb_title_remark);
        mlb_remark=findViewById(R.id.lb_remark);

        Intent intent=getIntent();
        mARG_VIEWMODEL= (RFormViewModel) intent.getSerializableExtra(ARG_VIEWMODEL);
        mPOSITION=intent.getIntExtra(IntentKeyConstants.POSITION,-1);

        ArrayList<RFormMaterialItem> materialItems=getmEquipCheckHelper().getRFormMaterialItems(mARG_VIEWMODEL.getJobItem().getUniqueID());
        mARG_VIEWMODEL.setRFormMaterialItems(materialItems);

        RFormWorkingHour mRFormWorkingHour=getmEquipCheckHelper().getRFormWorkingHour(mARG_VIEWMODEL.getJobItem().getUniqueID());
        RFormWorkingHour mRFormWorkingHourOrg=getmEquipCheckHelper().getRFormWorkingHour(mARG_VIEWMODEL.getJobItem().getUniqueID());
        mARG_VIEWMODEL.setRFormWorkingHour(mRFormWorkingHour);
        mARG_VIEWMODEL.setOrgRFormWorkingHour(mRFormWorkingHourOrg);

        this.mtemplate_progress=getResources().getString(R.string.template_current_progress);

        getSupportActionBar().setTitle("");

        //ui
//        mlb_sub_message=findViewById(R.id.lb_sub_message);
        marc_progress=findViewById(R.id.arc_progress);

        mlb_current_title=findViewById(R.id.lb_current_title);
        mlb_current_progress=findViewById(R.id.lb_current_progress);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        //event
        updateProgress();

        mbtn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveForm();
            }
        });

        //Change main interface.
        this.mprefix = this.getResources().getString(R.string.prefix_rform);

        this.setTitle(mprefix + "-" + mARG_VIEWMODEL.getJobItem().getVHNO());
        this.setSubMessage(mARG_VIEWMODEL.getJobItem().getSubject());

//        checkIfNeedSave();
    }

    public void checkIfNeedSave()
    {
        if(
                (mRFormCheckFragment.mRFormCheckAdapter!= null && mRFormCheckFragment.mRFormCheckAdapter.hasChangedData()) ||
                        (mRFormMaterialFragment.mRFormMaterialAdapter != null && mRFormMaterialFragment.mRFormMaterialAdapter.hasChangedData())||
//                        (!mARG_VIEWMODEL.getRemark().equals(mARG_VIEWMODEL.getOrgRemark())) ||
                        mRFormWorkingHourFragment.checkIsChanged()
                )
        {
            mbtn_save.setEnabled(true);
            CompatibleUtils.setViewBackground(mbtn_save, mbtn_selector_primary2);
        }
        else
        {
            mbtn_save.setEnabled(false);
            CompatibleUtils.setViewBackground(mbtn_save, mbtn_selector_default);
        }
    }

    private void saveForm() {
        ArrayList<RFormCheckItem> check_souce=mRFormCheckFragment.mRFormCheckAdapter.getmDataSource();
        ArrayList<RFormCheckItem> checkResults=new ArrayList<>();
        for(RFormCheckItem item:check_souce){
            if(item.isChanged()){
                checkResults.add(item);
            }
        }
        ArrayList<RFormMaterialResult> mRFormMaterialResult=new ArrayList<>();
        ArrayList<RFormMaterialItem> material_source=mRFormMaterialFragment.mRFormMaterialAdapter.getmDataSource();
        for(RFormMaterialItem item:material_source){
            mRFormMaterialResult.add(new RFormMaterialResult(item.getRFormUniqueID(),item.getMaterialUniqueID(),item.getResultQuantity()));
        }

        String userID=mSessionManager.getUserSession().getID();

        boolean result_flag=this.getmEquipCheckHelper().insertRFormResultData(
                mARG_VIEWMODEL.getJobItem().getUniqueID(),
                checkResults,
                mRFormMaterialResult,
                userID,
                mARG_VIEWMODEL.getRFormWorkingHour()
        );
        if(result_flag){
            Toast.makeText(this,getString(R.string.system_message_save_success),Toast.LENGTH_SHORT).show();

            for(RFormCheckItem item:check_souce){
                if(item.isChanged()){
                    item.setChanged(false);
                }
            }
            this.onBackPressed();
        }else{
            Toast.makeText(this, getString(R.string.system_message_save_fail), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProgress() {
        mlb_remark.setText(mARG_VIEWMODEL.getRemark());
    }

    private void setupViewPager(ViewPager viewPager){
        madapter=new ExecuteRFormActivity.ViewPagerAdapter(getSupportFragmentManager());
        mRFormCheckFragment= RFormCheckFragment.newInstance(mARG_VIEWMODEL);
        mRFormWorkingHourFragment = RFormWorkingHourFragment.newInstance(mARG_VIEWMODEL);
        mRFormMaterialFragment = RFormMaterialFragment.newInstance(mARG_VIEWMODEL);
        madapter.addFragment(mRFormCheckFragment, getString(R.string.lb_tab_rform_remark));
        madapter.addFragment(mRFormMaterialFragment, getString(R.string.lb_tab_rform_material));
        madapter.addFragment(mRFormWorkingHourFragment, getString(R.string.lb_tab_rform_working_hour));

        viewPager.setAdapter(madapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void setTitle(String title)
    {
        getSupportActionBar().setTitle(title);
    }

    public void setSubMessage(String message)
    {
        //TODO FIX
        mlb_current_title.setText(message);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
//        else if (id == R.id.action_emgtel)
//        {
//            Intent intent = new Intent(this, EmgTelActivity.class);
//            intent.putExtra(StringConst.ARG_JOB_UNIQUEID,mARG_VIEWMODEL.getJobItem().getUniqueID());
//            startActivity(intent);
//        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_execute, menu);
        return true;
    }

//    /**
//     * 上方選單是否呈現
//     * @param menu
//     * @return
//     */
//    public boolean onPrepareOptionsMenu(Menu menu)
//    {
//        String tag = getCurrentFragmentTag();
//        // 是否需要顯示緊急聯絡人電話
//        MenuItem action_emgtel = menu.findItem(R.id.action_emgtel);
//
//        if (mARG_VIEWMODEL.getJobItem() != null && mARG_VIEWMODEL.getJobItem().isHaveEmgTel()) {
//            action_emgtel.setVisible(true);
//        } else {
//            action_emgtel.setVisible(false);
//        }
//        return true;
//    }
}
