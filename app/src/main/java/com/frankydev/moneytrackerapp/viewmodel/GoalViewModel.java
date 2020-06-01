package com.frankydev.moneytrackerapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.frankydev.moneytrackerapp.model.Goal;
import com.frankydev.moneytrackerapp.model.Transaction;
import com.frankydev.moneytrackerapp.repository.AppRepository;

import java.util.List;

public class GoalViewModel extends ViewModel {

    private AppRepository mRepo;
    private LiveData<Goal> goal;

    public GoalViewModel(Application application) {
        mRepo = new AppRepository(application);
        goal = mRepo.getCurrentGoal();
    }

    public void insertGoal(Goal goal) {
        mRepo.insertGoal(goal);
    }

    public void updateGoal (Goal goal) {
        mRepo.updateGoal(goal);
    }

    public void deleteAllGoals() {
        mRepo.deleteAllGoals();
    }

    public LiveData<Goal> getCurrentGoal() {
        return goal;
    }


}
