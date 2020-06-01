package com.frankydev.moneytrackerapp.di.viewmodel;


import android.app.Application;

import com.frankydev.moneytrackerapp.viewmodel.ViewModelFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModelModule {


    private Application application;

    public ViewModelModule(Application application) {
        this.application = application;
    }

    @Singleton
    @Provides
    Application provideApplication() {
        return application;
    }


    @Provides
    ViewModelFactory provideViewModelFactory(Application application) {
        return new ViewModelFactory(application);
    }





}
