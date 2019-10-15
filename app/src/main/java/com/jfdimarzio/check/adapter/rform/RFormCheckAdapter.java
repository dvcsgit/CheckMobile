package com.jfdimarzio.check.adapter.rform;

import android.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jfdimarzio.check.ExecuteRFormActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.adapter.BaseRvAdapter;
import com.jfdimarzio.check.model.rform.RFormCheckItem;

import java.util.ArrayList;
import java.util.Objects;

public class RFormCheckAdapter extends BaseRvAdapter<RFormCheckAdapter.ViewHolder,RFormCheckItem> {

    private final ExecuteRFormActivity mActivity;
    private final Gson mgson;
    private final String prefix_quantity;
    private final FragmentManager mFragmentManager;
    private final ArrayList<RFormCheckItem> mbeforeDataSource;

    public interface onViewClickListener{
        void viewClick(RFormCheckItem checkItem,int position);
        void takePhoteClick(RFormCheckItem checkItem,int position);
        void refreshProgress();
    }
    private onViewClickListener mOnViewClickListener;
    public void setmOnViewClickListener(onViewClickListener mOnViewClickListener){
        this.mOnViewClickListener=mOnViewClickListener;
    }

    public RFormCheckAdapter(final ExecuteRFormActivity context, ArrayList<RFormCheckItem> mDataSource, ArrayList<RFormCheckItem> beforeDataSource){
        this.mActivity=context;
        this.mDataSource=mDataSource;
        this.mgson=new Gson();
        this.prefix_quantity=mActivity.getResources().getString(R.string.prefix_quantity);
        mFragmentManager=mActivity.getFragmentManager();
        this.mbeforeDataSource=beforeDataSource;
    }

    @Override
    public RFormCheckAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cardview_rform_material,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RFormCheckItem item=mDataSource.get(position);
        holder.lb_description.setText(item.getColumnDescription());
        String value_display;
        if(item.isOption()){
            value_display = item.getUIOptionResult();
        }else {
            value_display=item.getResultValue();
        }
        holder.lb_result_value.setText(value_display);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnViewClickListener!=null){
                    mOnViewClickListener.viewClick(item,position);
                }
            }
        });

        if(item.isChanged()){
            holder.lb_change.setVisibility(View.VISIBLE);
        }else{
            holder.lb_change.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final View view;
        private final TextView lb_description;
        private final TextView lb_result_value;
        private final TextView lb_change;

        public ViewHolder(View itemView) {
            super(itemView);
            this.view=itemView;
            this.lb_description=itemView.findViewById(R.id.lb_description);
            this.lb_result_value=itemView.findViewById(R.id.lb_result_value);
            this.lb_change=itemView.findViewById(R.id.lb_change);
        }
    }

    public ArrayList<RFormCheckItem> getmDataSource() {
        return mDataSource;
    }

    /**
     * 檢查有沒有異動過
     * @param pos　位置
     */
    public void checkIsChanged(int pos)
    {
        RFormCheckItem new_RFormMaterialItem = mDataSource.get(pos);
        checkIsChanged(new_RFormMaterialItem,pos);
    }

    /**
     * 檢查抄表資料有沒有異動過
     * 修改前就要先執行
     * 會直接改 checkItem 物件
     * @param checkItem 修改後的資料
     */
    private void checkIsChanged(RFormCheckItem checkItem,int pos)
    {
        RFormCheckItem org = mbeforeDataSource.get(pos);
        //TODO JSON
        String new_json_reason = mgson.toJson(checkItem);
        String org_json_reason = mgson.toJson(org);

        if(!Objects.equals(new_json_reason,org_json_reason))
        {
            checkItem.setChanged(true);
        }else
        {
            checkItem.setChanged(false);
        }

        if(mOnViewClickListener != null)
        {
            mOnViewClickListener.refreshProgress();
        }

    }

    /**
     * 有沒有異動的資料
     * @return
     */
    public boolean hasChangedData()
    {
        boolean result = false;
        for (RFormCheckItem RFormMaterialItem : mDataSource)
        {
            if(RFormMaterialItem.isChanged())
            {
                result = true;
                break;
            }
        }
        return result;
    }
}
