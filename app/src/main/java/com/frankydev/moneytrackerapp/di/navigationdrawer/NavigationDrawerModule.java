package com.frankydev.moneytrackerapp.di.navigationdrawer;

import android.app.Activity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import dagger.Module;
import dagger.Provides;

@Module
public class NavigationDrawerModule {

    private Activity activity;
    private int layoutResource;

    public NavigationDrawerModule(Activity activity, int layoutResource) {
        this.activity = activity;
        this.layoutResource = layoutResource;
    }

    @Provides
    NavController provideNavController() {
        return Navigation.findNavController(activity, layoutResource);
    }

}
