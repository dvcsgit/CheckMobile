package com.jfdimarzio.check.fragment.rform;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jfdimarzio.check.ExecuteRFormActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.model.rform.RFormViewModel;
import com.jfdimarzio.check.util.DateUtils;
import com.jfdimarzio.check.util.DecimalDigitsInputFilter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class RFormWorkingHourFragment extends Fragment {

    private RFormViewModel mARG_VIEWMODEL;
    private static final String ARG_VIEWMODEL="ARG_VIEWMODEL";
    private ExecuteRFormActivity mActivity;
    private View mRootView;
    private EditText mtxt_work_hour;
    private TextView mlb_begin_date;
    private TextView mlb_end_date;

    public RFormWorkingHourFragment() {
    }

    public static RFormWorkingHourFragment newInstance(RFormViewModel RFormViewModel) {
        RFormWorkingHourFragment frag = new RFormWorkingHourFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VIEWMODEL, RFormViewModel);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mARG_VIEWMODEL= (RFormViewModel) getArguments().getSerializable(ARG_VIEWMODEL);
        mActivity= (ExecuteRFormActivity) getActivity();

        mRootView=inflater.inflate(R.layout.fragment_rform_working_hour,container,false);
        mtxt_work_hour=mRootView.findViewById(R.id.txt_work_hour);
        mlb_begin_date=mRootView.findViewById(R.id.lb_begin_date);
        mlb_end_date=mRootView.findViewById(R.id.lb_end_date);
        mtxt_work_hour.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4,1)});

        mlb_begin_date.setText(mARG_VIEWMODEL.getRFormWorkingHour().getBeginDate());
        mlb_end_date.setText(mARG_VIEWMODEL.getRFormWorkingHour().getEndDate());
        Float working_hour=mARG_VIEWMODEL.getRFormWorkingHour().getWorkingHour();
        if(working_hour!=null){
            mtxt_work_hour.setText(String.valueOf(working_hour));
        }

        //Event
        mlb_begin_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar= Calendar.getInstance();
                new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year,month,dayOfMonth);
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(DateUtils.FORMAT_DIS_YYYYMMDD);
                        mlb_begin_date.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mlb_end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(mActivity,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                                  int dayOfMonth) {

                                calendar.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtils.FORMAT_DIS_YYYYMMDD);
                                mlb_end_date.setText(simpleDateFormat.format(calendar.getTime()));
                            }
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // event
        mlb_begin_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mARG_VIEWMODEL.getRFormWorkingHour().setBeginDate(mlb_begin_date.getText().toString());
                refreshTopProgress();
            }
        });
        mlb_end_date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mARG_VIEWMODEL.getRFormWorkingHour().setEndDate(mlb_end_date.getText().toString());
                refreshTopProgress();
            }
        });
        mtxt_work_hour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String userTypeValue = s.toString();

                if (!userTypeValue.equals("")) {
                    try {

                        Float tempResultValue = Float.parseFloat(userTypeValue);

                        mARG_VIEWMODEL.getRFormWorkingHour().setWorkingHour(tempResultValue);

                    } catch (NumberFormatException nfe) {


                        mARG_VIEWMODEL.getRFormWorkingHour().setWorkingHour(null);
                    }


                } else {
                    mARG_VIEWMODEL.getRFormWorkingHour().setWorkingHour(null);
                }
                refreshTopProgress();
            }
        });

        return mRootView;
    }

    private void refreshTopProgress() {
        mActivity.checkIfNeedSave();
    }

    /**
     * 檢查資料有沒有異動過
     */
    public boolean checkIsChanged() {
        boolean result = false;
        Gson gson = new Gson();
//        Gson gson = new GsonBuilder().serializeNulls().create();
        if (mARG_VIEWMODEL != null) {
            String new_json_reason = gson.toJson(mARG_VIEWMODEL.getRFormWorkingHour());
            String org_json_reason = gson.toJson(mARG_VIEWMODEL.getOrgRFormWorkingHour());

            if (!Objects.equals(new_json_reason, org_json_reason)) {
                result = true;
            }
        }

        return result;
    }
}
