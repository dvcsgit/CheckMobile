package com.jfdimarzio.check.adapter.rform;

import android.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jfdimarzio.check.ExecuteRFormActivity;
import com.jfdimarzio.check.R;
import com.jfdimarzio.check.adapter.BaseRvAdapter;
import com.jfdimarzio.check.model.rform.RFormMaterialItem;

import java.util.ArrayList;

public class RFormMaterialAdapter extends BaseRvAdapter<RFormMaterialAdapter.ViewHolder,RFormMaterialItem>{
    private final ExecuteRFormActivity mActivity;
    private final FragmentManager mFragmentManager;
    private String prefix_quantity;
    private ArrayList<RFormMaterialItem> mbeforeDataSource;

    public interface onViewClickListener{
        void viewClick(RFormMaterialItem checkItem,int position);
        void takePhotoClick(RFormMaterialItem checkItem,int position);
        void refreshProgress();
    }
    private onViewClickListener mOnViewClickListener;
    public void setmOnViewClickListener(onViewClickListener mOnViewClickListener){
        this.mOnViewClickListener=mOnViewClickListener;
    }

    public RFormMaterialAdapter(final ExecuteRFormActivity context, ArrayList<RFormMaterialItem> mDataSource, ArrayList<RFormMaterialItem> beforeDataSource) {
        this.mActivity = context;
        this.mDataSource = mDataSource;
        this.prefix_quantity = mActivity.getResources().getString(R.string.prefix_quantity);
        mFragmentManager = mActivity.getFragmentManager();
        this.mbeforeDataSource = beforeDataSource;
    }

    @Override
    public RFormMaterialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cardview_rform_material,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RFormMaterialItem item=mDataSource.get(position);
        holder.lb_description.setText(item.getMaterialID()+"-"+item.getMaterialName());
        String value_display="N\\A";
        if(item.getResultQuantity()>-1){
            value_display=String.valueOf(item.getResultQuantity());
        }
        holder.lb_result_value.setText(prefix_quantity+value_display);
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
        }else {
            holder.lb_change.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (mDataSource!=null?mDataSource.size():0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

    /**
     * 檢查有沒有異動過
     * @param pos　位置
     */
    public void checkIsChanged(int pos)
    {
        RFormMaterialItem new_RFormMaterialItem = mDataSource.get(pos);
        checkIsChanged(new_RFormMaterialItem,pos);
    }

    /**
     * 檢查抄表資料有沒有異動過
     * 修改前就要先執行
     * 會直接改 checkItem 物件
     * @param checkItem 修改後的資料
     */
    private void checkIsChanged(RFormMaterialItem checkItem,int pos)
    {

        RFormMaterialItem org = mbeforeDataSource.get(pos);

        if(org.getResultQuantity() != checkItem.getResultQuantity())
        {
            checkItem.setChanged(true);
        }
        else {
            checkItem.setChanged(false);
        }

        if(mOnViewClickListener != null)
        {
            mOnViewClickListener.refreshProgress();
        }
    }

    public boolean hasChangedData()
    {
        boolean result = false;
        for (RFormMaterialItem RFormMaterialItem : mDataSource)
        {
            if(RFormMaterialItem.isChanged())
            {
                result = true;
                break;
            }
        }
        return result;
    }

    public ArrayList<RFormMaterialItem> getmDataSource() {
        return mDataSource;
    }
}
