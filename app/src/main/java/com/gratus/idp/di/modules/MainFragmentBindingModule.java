package com.gratus.idp.di.modules;

import com.gratus.idp.view.adapter.ReportListAdapter;
import com.gratus.idp.view.fragment.HomeFragment;
import com.gratus.idp.view.fragment.ReportListFragment;
import com.gratus.idp.view.fragment.SettingsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainFragmentBindingModule {

    @ContributesAndroidInjector
    abstract HomeFragment provideHomeFragment();

    @ContributesAndroidInjector(modules = RepostListAdapterModule.class)
    abstract ReportListFragment provideReportListFragment();

    @ContributesAndroidInjector
    abstract SettingsFragment provideSettingsFragment();
}