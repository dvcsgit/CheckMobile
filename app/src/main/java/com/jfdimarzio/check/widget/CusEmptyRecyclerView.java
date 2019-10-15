package com.jfdimarzio.check.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class CusEmptyRecyclerView extends RecyclerView {
    private View emptyView;
    private boolean canShowEmptyView=true;

    public boolean isCanShowEmptyView(){return canShowEmptyView;}
    public void setCanShowEmptyView(boolean canShowEmptyView){this.canShowEmptyView=canShowEmptyView;}

    public CusEmptyRecyclerView(Context context){super(context);}
    public CusEmptyRecyclerView(Context context, AttributeSet attrs){super(context,attrs);}
    public CusEmptyRecyclerView(Context context, AttributeSet attrs, int defStyle){super(context,attrs,defStyle);}

    private final AdapterDataObserver observer=new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            CusEmptyRecyclerView.this.checkIfEmpty();
        }
        @Override
        public void onItemRangeInserted(int positionStart,int itemCount){
            CusEmptyRecyclerView.this.checkIfEmpty();
        }
        @Override
        public void onItemRangeRemoved(int positionStart,int itemCount){
            CusEmptyRecyclerView.this.checkIfEmpty();
        }
    };

    public void checkIfEmpty(){
        if(emptyView!=null&&getAdapter()!=null&&canShowEmptyView){
            final boolean emptyViewVisible=getAdapter().getItemCount()==0;
            emptyView.setVisibility(emptyViewVisible?VISIBLE:GONE);
            setVisibility(emptyViewVisible?GONE:VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter){
        Adapter oldAdapter=this.getAdapter();
        if(oldAdapter!=null){
            oldAdapter.unregisterAdapterDataObserver(this.observer);
        }
        super.setAdapter(adapter);
        if(adapter!=null){
            adapter.registerAdapterDataObserver(this.observer);
        }
        this.checkIfEmpty();
    }

    public void setEmptyView(View emptyView){
        this.emptyView=emptyView;
        this.checkIfEmpty();
    }

    public void hideEmpty(){
        if(emptyView!=null){
            emptyView.setVisibility(GONE);
        }
    }
}
