package com.jfdimarzio.check;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ExecuteActivity extends BaseActivity {
//    private static final String TAG=makeLogTag(DashboardActivity.class);
    private static final String ARG_VIEWMODEL="ARG_VIEWMODEL";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execute_new);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
