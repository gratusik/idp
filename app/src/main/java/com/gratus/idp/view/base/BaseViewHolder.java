package com.gratus.idp.view.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.gratus.idp.view.interfaces.adapter.ReportListListener;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBind(int position);

}
