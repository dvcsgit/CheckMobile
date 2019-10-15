package com.jfdimarzio.check.fragment.rform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jfdimarzio.check.ExecuteRFormActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.adapter.rform.RFormCheckAdapter;
import com.jfdimarzio.check.fragment.dialog.rform.CheckFillFragment;
import com.jfdimarzio.check.model.rform.RFormCheckItem;
import com.jfdimarzio.check.model.rform.RFormViewModel;
import com.jfdimarzio.check.util.consts.FragmentTagConstants;
import com.jfdimarzio.check.widget.EmptyRecyclerView;

import java.util.ArrayList;

public class RFormCheckFragment extends Fragment {

    private static final String ARG_VIEWMODEL="ARG_VIEWMODEL";
    private RFormViewModel mARG_VIEWMODEL;
    private ExecuteRFormActivity mActivity;
    private View mRootView;
    private ArrayList<RFormCheckItem> before_source;
    private ArrayList<RFormCheckItem> will_source;
    private EmptyRecyclerView mrv_check;
    public RFormCheckAdapter mRFormCheckAdapter;

    public RFormCheckFragment(){}

    public static RFormCheckFragment newInstance(RFormViewModel rFormViewModel){
        RFormCheckFragment rFormCheckFragment=new RFormCheckFragment();
        Bundle args=new Bundle();
        args.putSerializable(ARG_VIEWMODEL, rFormViewModel);
        rFormCheckFragment.setArguments(args);
        return rFormCheckFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mARG_VIEWMODEL= (RFormViewModel) getArguments().getSerializable(ARG_VIEWMODEL);
        mActivity= (ExecuteRFormActivity) getActivity();
        mRootView=inflater.inflate(R.layout.fragment_rform_check,container,false);
        before_source=mActivity.getmEquipCheckHelper().getRFormCheckItems(mARG_VIEWMODEL.getJobItem().getUniqueID());
        will_source=mActivity.getmEquipCheckHelper().getRFormCheckItems(mARG_VIEWMODEL.getJobItem().getUniqueID());
        mARG_VIEWMODEL.setRFormCheckItems(will_source);

        mrv_check=mRootView.findViewById(R.id.rv_check);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false);
        mrv_check.setLayoutManager(linearLayoutManager);
        mrv_check.setItemAnimator(null);
        mrv_check.setItemAnimator(new DefaultItemAnimator());
        mrv_check.setEmptyView(mRootView.findViewById(R.id.vs_empty));
        mrv_check.setNestedScrollingEnabled(false);
        mRFormCheckAdapter=new RFormCheckAdapter(mActivity,mARG_VIEWMODEL.getRFormCheckItems(),before_source);
        mrv_check.setAdapter(mRFormCheckAdapter);
        mRFormCheckAdapter.setmOnViewClickListener(new RFormCheckAdapter.onViewClickListener() {
            @Override
            public void viewClick(RFormCheckItem checkItem, final int position) {
                final CheckFillFragment checkFillFragment=CheckFillFragment.newInstance(checkItem,position);
                checkFillFragment.addListener(new CheckFillFragment.onListener() {
                    @Override
                    public void onDone(int pos) {
                        checkFillFragment.dismiss();
                        mRFormCheckAdapter.checkIsChanged(position);
                        mRFormCheckAdapter.notifyDataSetChanged();
                    }
                });
                checkFillFragment.show(mActivity.getFragmentManager(), FragmentTagConstants.CheckFillFragment);
            }

            @Override
            public void takePhoteClick(RFormCheckItem checkItem, int position) {

            }

            @Override
            public void refreshProgress() {

            }
        });

        return mRootView;
    }

    private void refreshTopProgress()
    {
        mActivity.checkIfNeedSave();
    }

}
