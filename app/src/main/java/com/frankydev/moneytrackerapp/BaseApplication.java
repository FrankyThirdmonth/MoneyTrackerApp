package com.frankydev.moneytrackerapp;

import android.app.Activity;
import android.app.Application;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;

import com.frankydev.moneytrackerapp.di.navigationdrawer.DaggerNavigationDrawerComponent;
import com.frankydev.moneytrackerapp.di.navigationdrawer.DrawerModule;
import com.frankydev.moneytrackerapp.di.navigationdrawer.NavigationDrawerComponent;
import com.frankydev.moneytrackerapp.di.navigationdrawer.NavigationDrawerModule;
import com.frankydev.moneytrackerapp.di.viewmodel.DaggerViewModelComponent;
import com.frankydev.moneytrackerapp.di.viewmodel.ViewModelComponent;
import com.frankydev.moneytrackerapp.di.viewmodel.ViewModelModule;

public class BaseApplication extends Application {

    private ViewModelComponent viewModelComponent;
    private NavController navController;

    private static final String TAG = "Tracking";

    @Override
    public void onCreate() {
        super.onCreate();

        viewModelComponent = DaggerViewModelComponent.builder().viewModelModule(new ViewModelModule(this)).build();

    }

    public ViewModelComponent getViewModelComponent() {
        return viewModelComponent;
    }

    public NavigationDrawerComponent getNavigationDrawerComponent(Activity activity, int layoutResource, DrawerLayout drawerLayout) {
        return DaggerNavigationDrawerComponent.builder().navigationDrawerModule(new NavigationDrawerModule(activity, layoutResource))
                .drawerModule(new DrawerModule(drawerLayout)).build();
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public NavController getNavController() {
        return navController;
    }


}
