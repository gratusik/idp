package com.gratus.idp.di.modules;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.gratus.idp.view.adapter.ReportListAdapter;
import com.gratus.idp.view.fragment.ReportListFragment;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

@Module
public class RepostListAdapterModule {
    @Provides
    ReportListAdapter provideReportListAdapter() {
        return new ReportListAdapter(new ArrayList<>());
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(ReportListFragment fragment) {
        return new LinearLayoutManager(fragment.getActivity());
    }
}
