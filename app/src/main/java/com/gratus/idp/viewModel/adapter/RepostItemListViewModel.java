package com.gratus.idp.viewModel.adapter;

import com.gratus.idp.model.common.CyclePathNew;
import com.gratus.idp.view.adapter.ReportListAdapter;
import com.gratus.idp.view.interfaces.viewModel.ReportListItemViewModelListener;

public class RepostItemListViewModel {
    public final ReportListItemViewModelListener mListener;
    private final CyclePathNew cyclePathNew;

    public RepostItemListViewModel(CyclePathNew cyclePathNews, ReportListItemViewModelListener listener) {
        this.cyclePathNew = cyclePathNews;
        this.mListener = listener;
    }

    public CyclePathNew getCyclePathNew() {
        return cyclePathNew;
    }

    public void onItemClick() {
        mListener.onItemClick();
    }
}
