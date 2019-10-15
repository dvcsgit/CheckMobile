package com.jfdimarzio.check.fragment.dialog.rform;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.jfdimarzio.check.BaseActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.adapter.rform.SelectListItemAdapter;
import com.jfdimarzio.check.model.rform.RFormMaterialItem;
import com.jfdimarzio.check.model.rform.SelectListItem;

import java.util.ArrayList;

public class RepairMaterialDialogNormalFragment extends DialogFragment {
    private RFormMaterialItem mARG_RFormMaterialItem;
    private static final String ARG_RFormMaterialItem="ARG_RForm_MaterialItem";
    private int mARG_POS;
    private static final String ARG_POS="ARG_POS";
    private BaseActivity mActivity;
    private Spinner msp_quantity;
    private SelectListItemAdapter mSpQuantityAdapter;

    public interface DialogListener{
        void onDone(int qty,RFormMaterialItem data);
    }
    private DialogListener mListener;
    public void setmListener(DialogListener mListener){
        this.mListener=mListener;
    }

    public static RepairMaterialDialogNormalFragment newInstance(RFormMaterialItem rFormMaterialItem, int pos) {
        RepairMaterialDialogNormalFragment frag = new RepairMaterialDialogNormalFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RFormMaterialItem,rFormMaterialItem);
        args.putInt(ARG_POS, pos);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.findFragmentByTag(tag) == null) {
            super.show(manager, tag);
        }
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        if(getArguments()!=null){
            mARG_RFormMaterialItem= (RFormMaterialItem) getArguments().getSerializable(ARG_RFormMaterialItem);
            mARG_POS=getArguments().getInt(ARG_POS,0);
        }
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View mRootView=inflater.inflate(R.layout.dialog_material_normal,null);
        mActivity=(BaseActivity)getActivity();

        msp_quantity=mRootView.findViewById(R.id.sp_quantity);
        mSpQuantityAdapter=new SelectListItemAdapter(mActivity,new ArrayList<SelectListItem>(),android.R.layout.simple_spinner_dropdown_item);
        msp_quantity.setAdapter(mSpQuantityAdapter);

        ArrayList<SelectListItem> q_source=new ArrayList<>();
        q_source.add(new SelectListItem(getString(R.string.lb_hint_choice),"-1"));
        for(int i=0;i<=mARG_RFormMaterialItem.getQuantity();i++){
            String value=String.valueOf(i);
            boolean isChecked=false;
            if(mARG_RFormMaterialItem.getResultQuantity()==i){
                isChecked=true;
            }
            SelectListItem selectListItem=new SelectListItem(value,value);
            selectListItem.setIsSelected(isChecked);
            q_source.add(selectListItem);
        }
        mSpQuantityAdapter.setItemList(q_source);
        mSpQuantityAdapter.notifyDataSetChanged();

        String dialog_title=getString(R.string.lb_change_material_prefix)+mARG_RFormMaterialItem.getMaterialName();
        AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
        builder.setView(mRootView);
        builder.setTitle(dialog_title);
        builder.setPositiveButton(android.R.string.ok,null).setNegativeButton(android.R.string.cancel,null);
        this.setCancelable(false);
        final AlertDialog alertDialog=builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnOK=alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mListener!=null){
                            SelectListItem selectListItem= (SelectListItem) msp_quantity.getSelectedItem();
                            int qty=Integer.parseInt(selectListItem.getValue());
                            mListener.onDone(qty,null);
                        }
                    }
                });
            }
        });
        if(alertDialog==null){
            super.setShowsDialog(false);
        }
        return alertDialog;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
