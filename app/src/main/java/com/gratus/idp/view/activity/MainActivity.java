package com.gratus.idp.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gratus.idp.BaseApplication;
import com.gratus.idp.R;
import com.gratus.idp.databinding.ActivityMainBinding;
import com.gratus.idp.di.modules.InternetModule;
import com.gratus.idp.di.modules.PrefModule;
import com.gratus.idp.util.BottomNavigationViewBehavior;
import com.gratus.idp.util.pref.AppPreferencesHelper;
import com.gratus.idp.view.base.BaseActivity;
import com.gratus.idp.view.fragment.HomeFragment;
import com.gratus.idp.view.fragment.ReportListFragment;
import com.gratus.idp.view.fragment.SettingsFragment;
import com.gratus.idp.viewModel.activity.MainViewModel;

import javax.inject.Inject;

import static com.gratus.idp.util.constants.AppConstants.FRAGMENT_HOME;
import static com.gratus.idp.util.constants.AppConstants.FRAGMENT_OTHER;


public class MainActivity extends BaseActivity  {

    private ActivityMainBinding activityMainBinding;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private MainViewModel mainViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        ((BaseApplication) getApplicationContext()).getAppComponent()
//                .inject(BaseApplication)
//                .inject(this);
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel.class);
        activityMainBinding.setMainViewModel(mainViewModel);
        activityMainBinding.setLifecycleOwner(this);
        viewFragment(new HomeFragment(),FRAGMENT_HOME);
        setupNavMenu();
    }

    private void setupNavMenu() {
       activityMainBinding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
           switch (item.getItemId()) {
               case R.id.home:
                   viewFragment(new HomeFragment(), FRAGMENT_HOME);
                   return true;
               case R.id.list:
                   viewFragment(new ReportListFragment(), FRAGMENT_OTHER);
                   return true;
               case R.id.settings:
                   viewFragment(new SettingsFragment(), FRAGMENT_OTHER);
                   return true;
           }
           return false;
       });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected, activityMainBinding.parent);
        setIntial(false);
    }

    private void viewFragment(Fragment fragment, String name){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container,fragment);
        /*if(name.equals( FRAGMENT_OTHER) ) {
            fragmentTransaction.sta
            fragmentTransaction.addToBackStack(name);
        }*/
        fragmentTransaction.commit();
    }
}