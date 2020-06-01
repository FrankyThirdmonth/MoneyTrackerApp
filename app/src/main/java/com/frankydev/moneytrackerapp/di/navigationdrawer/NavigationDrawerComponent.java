package com.frankydev.moneytrackerapp.di.navigationdrawer;

import com.frankydev.moneytrackerapp.MainActivity;

import dagger.Component;

@Component(modules = {NavigationDrawerModule.class, DrawerModule.class})
public interface NavigationDrawerComponent {

    void inject (MainActivity mainActivity);

}
