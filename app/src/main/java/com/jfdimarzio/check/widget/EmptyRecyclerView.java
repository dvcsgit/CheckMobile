package com.jfdimarzio.check.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class EmptyRecyclerView extends RecyclerView {
    private View emptyView;

    public EmptyRecyclerView(Context context){
        super(context);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs){
        super(context,attrs);
    }

    public EmptyRecyclerView(Context context,AttributeSet attrs,int defStyle){
        super(context,attrs,defStyle);
    }

    private final AdapterDataObserver observer=new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            EmptyRecyclerView.this.checkIfEmpty();
        }
        @Override
        public void onItemRangeInserted(int positionStart,int itemCount){
            EmptyRecyclerView.this.checkIfEmpty();
        }
        @Override
        public void onItemRangeRemoved(int positionStart,int itemCount){
            EmptyRecyclerView.this.checkIfEmpty();
        }
    };

    void checkIfEmpty(){
        if(this.emptyView!=null&&this.getAdapter()!=null){
            boolean emptyViewVisible=this.getAdapter().getItemCount()==0;
            this.emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            this.setVisibility(emptyViewVisible ? GONE:VISIBLE);
        }
    }

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
}
