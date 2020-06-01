package com.frankydev.moneytrackerapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.frankydev.moneytrackerapp.model.Transaction;
import com.frankydev.moneytrackerapp.repository.AppRepository;

import java.util.List;

public class TransactionViewModel extends ViewModel {

    private static final String TAG = "Tracking";

    private AppRepository mRepo;
    private LiveData<List<Transaction>> allTransactions;

    public TransactionViewModel(Application application) {
        mRepo = new AppRepository(application);
        allTransactions = mRepo.getAllTransactions();
    }

    public void insertTransaction(Transaction transaction) {
        mRepo.insertTransaction(transaction);
    }

    public void insertAllTransaction (List<Transaction> transactionList) {
        mRepo.insertAllTransactions(transactionList);
    }

    public void updateTransaction(Transaction transaction) {
        mRepo.updateTransaction(transaction);
    }

    public void deleteTransaction(Transaction transaction) {
        mRepo.deleteTransaction(transaction);
    }

    public void deleteAllTransactions() {
        mRepo.deleteAllTransactions();
    }

    public LiveData<List<Transaction>> getCurrentMonthCharges(String transaction) {
        return mRepo.getCurrentMonthTransactions(transaction);
    }

    public LiveData<List<Transaction>> getSelectedMonthTransaction(int currentMonth) {
        return mRepo.getSelectedMonthTransactions(currentMonth);
    }
    public LiveData<List<Transaction>> getSelectedYearTransaction(int currentYear) {
        return mRepo.getSelectedYearTransactions(currentYear);
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }





}
