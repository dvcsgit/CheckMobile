package com.jfdimarzio.check.fragment.dialog.rform;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jfdimarzio.check.ExecuteRFormActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.adapter.rform.SelectListItemAdapter;
import com.jfdimarzio.check.model.rform.RFormCheckItem;
import com.jfdimarzio.check.model.rform.SelectListItem;

import java.util.ArrayList;

public class CheckFillFragment extends DialogFragment {
    private static final String ARG_CHECKITEMITEM = "ARG_CHECKITEMITEM";
    private RFormCheckItem mARG_CHECKITEMITEM;
    private int mARG_POS;
    private static final String ARG_POS = "ARG_POS";
    private ExecuteRFormActivity mActivity;
    private SelectListItemAdapter selectListItemAdapter;

    public interface onListener {
        void onDone(int pos);
    }

    private onListener mListener;

    public void addListener(onListener listener) {
        this.mListener = listener;
    }

    public static CheckFillFragment newInstance(RFormCheckItem RFormCheckItem, int pos) {
        CheckFillFragment frag = new CheckFillFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHECKITEMITEM, RFormCheckItem);
        args.putInt(ARG_POS, pos);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);

//        if (manager.findFragmentByTag(tag) == null) {
//            super.show(manager, tag);
//        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mARG_CHECKITEMITEM = (RFormCheckItem) getArguments().getSerializable(ARG_CHECKITEMITEM);
        mARG_POS = getArguments().getInt(ARG_POS, 0);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View custom_view = inflater.inflate(R.layout.dialog_check_fill, null);
        mActivity = (ExecuteRFormActivity) getActivity();

        //ui
        final EditText mtxt_value = custom_view.findViewById(R.id.txt_value);
        final Spinner sp_option = custom_view.findViewById(R.id.sp_option);
        ArrayList<SelectListItem> column_options;

        //fill data
        if (mARG_CHECKITEMITEM.isOption()) {
            mtxt_value.setVisibility(View.GONE);
            sp_option.setVisibility(View.VISIBLE);
            column_options = mActivity.getmEquipCheckHelper().getColumnOptions(mARG_CHECKITEMITEM.getColumnUniqueID());
            selectListItemAdapter = new SelectListItemAdapter(mActivity, column_options, android.R.layout.simple_spinner_dropdown_item);
            sp_option.setAdapter(selectListItemAdapter);

            //get selection
            if (!TextUtils.isEmpty(mARG_CHECKITEMITEM.getColumnOptionUniqueID())) {
                int select_pos = 0;
                for (SelectListItem item : column_options) {
                    if (item.getValue().equals(mARG_CHECKITEMITEM.getColumnOptionUniqueID())) {
                        sp_option.setSelection(select_pos);
                        break;
                    }
                    select_pos++;
                }
            }
        } else {
            mtxt_value.setText(mARG_CHECKITEMITEM.getResultValue());
            mtxt_value.setSelection(mtxt_value.getText().length());
            mtxt_value.setVisibility(View.VISIBLE);
            sp_option.setVisibility(View.GONE);
        }

        //Setting data
        String dialog_title = mARG_CHECKITEMITEM.getColumnDescription();

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setView(custom_view);
        builder.setTitle(dialog_title);
        builder.setPositiveButton(android.R.string.ok, null).setNegativeButton(android.R.string.cancel, null);
        this.setCancelable(false);
        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnOK = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean cancel = false;
                        View focusView = null;
                        //                        String error_message = "";
//                        if(TextUtils.isEmpty(mtxt_qrcode.getText()))
//                        {
//                            focusView = mtxt_qrcode;
//                            mtxt_qrcode.setError("請輸入條碼內容");
////                            error_message = "請輸入條碼內容";
//                            cancel = true;
//                        }
                        if (cancel) {
                            if (focusView != null) {
                                focusView.requestFocus();
                            } else {
                                Toast.makeText(mActivity, "errormessage", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (!mARG_CHECKITEMITEM.isOption()) {
                                String str_value = mtxt_value.getText().toString();
                                mARG_CHECKITEMITEM.setResultValue(str_value);
                                mARG_CHECKITEMITEM.setResultColumnOptionUniqueID(null);
                            } else {
                                SelectListItem selectListItem = (SelectListItem) sp_option.getSelectedItem();
                                String uniqueID = null;
                                String text = "";
                                if (selectListItem != null) {
                                    uniqueID = selectListItem.getValue();
                                    if (!TextUtils.isEmpty(uniqueID)) {
                                        text = selectListItem.getText();
                                    } else {
                                        text = "";
                                    }
                                }
                                mARG_CHECKITEMITEM.setResultValue(null);
                                mARG_CHECKITEMITEM.setResultColumnOptionUniqueID(uniqueID);
                                mARG_CHECKITEMITEM.setUIOptionResult(text);
                            }
                            if (mListener != null) {
                                mListener.onDone(mARG_POS);
                            }
                        }
                    }
                });
            }
        });
        if (alertDialog == null) {
            super.setShowsDialog(false);
        }
        return alertDialog;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null & getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }


}
