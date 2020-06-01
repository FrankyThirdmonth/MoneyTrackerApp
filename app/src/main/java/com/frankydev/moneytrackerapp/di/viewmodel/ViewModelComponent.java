package com.frankydev.moneytrackerapp.di.viewmodel;


import com.frankydev.moneytrackerapp.userinterface.ChargesLogFragment;
import com.frankydev.moneytrackerapp.userinterface.GoalFragment;
import com.frankydev.moneytrackerapp.userinterface.HomeFragment;
import com.frankydev.moneytrackerapp.userinterface.NewTransactionFragment;
import com.frankydev.moneytrackerapp.userinterface.TrackingFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ViewModelModule.class})
public interface ViewModelComponent {

    void inject (HomeFragment homeFragment);
    void inject (NewTransactionFragment transactionFragment);
    void inject (ChargesLogFragment chargesLogFragment);
    void inject (GoalFragment dateBalanceFragment);
    void inject (TrackingFragment trackingFragment);


}
