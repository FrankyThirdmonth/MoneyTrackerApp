package com.frankydev.moneytrackerapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final Application mApplication;

    public ViewModelFactory(Application application) {
        this.mApplication = application;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == TransactionViewModel.class) {
            return (T) new TransactionViewModel(mApplication);
        } else if (modelClass == GoalViewModel.class) {
            return (T) new GoalViewModel(mApplication);
        } else {
            throw new RuntimeException("Invalid view model class");
        }
    }
}
