package com.jfdimarzio.check.fragment.rform;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jfdimarzio.check.ExecuteRFormActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.adapter.rform.RFormMaterialAdapter;
import com.jfdimarzio.check.fragment.dialog.rform.RepairMaterialDialogNormalFragment;
import com.jfdimarzio.check.model.rform.RFormMaterialItem;
import com.jfdimarzio.check.model.rform.RFormViewModel;
import com.jfdimarzio.check.util.consts.FragmentTagConstants;
import com.jfdimarzio.check.widget.EmptyRecyclerView;

import java.util.ArrayList;

public class RFormMaterialFragment extends Fragment {
    private RFormViewModel mARG_VIEWMODEL;
    private static final String ARG_VIEWMODEL="ARG_VIEWMODEL";
    private ExecuteRFormActivity mActivity;
    private int mColorWhite;
    private Drawable mbtn_selector_default;
    private Drawable mbtn_selector_primary2;
    private View mRootView;
    private TextView mlb_message;
    private ArrayList<RFormMaterialItem> mmaterialItems;
    private EmptyRecyclerView mrv_check;
    public RFormMaterialAdapter mRFormMaterialAdapter;

    public RFormMaterialFragment() {
    }

    public static RFormMaterialFragment newInstance(RFormViewModel RFormViewModel) {
        RFormMaterialFragment frag = new RFormMaterialFragment();
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
        this.mColorWhite=mActivity.getResources().getColor(R.color.white);
        this.mbtn_selector_default= ContextCompat.getDrawable(mActivity,R.drawable.btn_selector_default);
        this.mbtn_selector_primary2=ContextCompat.getDrawable(mActivity,R.drawable.btn_selector_primary2);

        mRootView=inflater.inflate(R.layout.fragment_rform_material,container,false);

        mlb_message=mRootView.findViewById(R.id.lb_message);

        mmaterialItems=mActivity.getmEquipCheckHelper().getRFormMaterialItems(mARG_VIEWMODEL.getJobItem().getUniqueID());
        mrv_check=mRootView.findViewById(R.id.rv_check);
        mrv_check.setLayoutManager(new GridLayoutManager(mActivity,2));
        mrv_check.setItemAnimator(null);
        mrv_check.setItemAnimator(new DefaultItemAnimator());
        mrv_check.setEmptyView(mRootView.findViewById(R.id.vs_empty));
        mrv_check.setNestedScrollingEnabled(false);

        mRFormMaterialAdapter=new RFormMaterialAdapter(mActivity,mARG_VIEWMODEL.getRFormMaterialItems(),mmaterialItems);
        mrv_check.setAdapter(mRFormMaterialAdapter);
        mRFormMaterialAdapter.setmOnViewClickListener(new RFormMaterialAdapter.onViewClickListener() {
            @Override
            public void viewClick(final RFormMaterialItem checkItem, final int position) {
                final RepairMaterialDialogNormalFragment normalFragment=RepairMaterialDialogNormalFragment.newInstance(checkItem,position);
                normalFragment.setmListener(new RepairMaterialDialogNormalFragment.DialogListener() {
                    @Override
                    public void onDone(int qty, RFormMaterialItem data) {
                        Toast.makeText(mActivity,String.valueOf(qty),Toast.LENGTH_SHORT).show();
                        checkItem.setResultQuantity(qty);
                        mRFormMaterialAdapter.checkIsChanged(position);
                        mRFormMaterialAdapter.notifyItemChanged(position);
                        normalFragment.dismiss();
                    }
                });
                normalFragment.show(mActivity.getFragmentManager(), FragmentTagConstants.MaterialDialogNormalFragment);
            }

            @Override
            public void takePhotoClick(RFormMaterialItem checkItem, int position) {

            }

            @Override
            public void refreshProgress() {
                refreshTopProgress();
            }
        });

        return mRootView;
    }

    private void refreshTopProgress(){mActivity.checkIfNeedSave();}
}
