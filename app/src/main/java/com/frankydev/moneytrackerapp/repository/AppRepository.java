package com.frankydev.moneytrackerapp.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.frankydev.moneytrackerapp.model.Goal;
import com.frankydev.moneytrackerapp.model.Transaction;
import com.frankydev.moneytrackerapp.room.TransactionDao;
import com.frankydev.moneytrackerapp.room.TransactionDatabase;

import java.util.Calendar;
import java.util.List;

public class AppRepository {

    private TransactionDao transactionDao;
    private LiveData<List<Transaction>> allTransactions;
    private LiveData<List<Transaction>> currentMonthTransactions;

    private static final String TAG = "AppRepository";

    public AppRepository(Application application) {

        TransactionDatabase transactionDatabase = TransactionDatabase.getInstance(application);

        transactionDao = transactionDatabase.chargeDao();
        allTransactions = transactionDao.getAllTransactions();
    }

    public void insertTransaction(final Transaction transaction) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                transactionDao.insertTransaction(transaction);
            }
        }).start();
    }

    public void insertAllTransactions(final List<Transaction> transactionList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                transactionDao.insertAllTransactions(transactionList);
            }
        }).start();
    }

    public void updateTransaction(final Transaction transaction) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                transactionDao.updateTransaction(transaction);
            }
        }).start();
    }

    public void deleteTransaction(final Transaction transaction) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                transactionDao.deleteTransaction(transaction);
                Log.d(TAG, "Deleted ");
            }
        }).start();
    }

    public void deleteAllTransactions() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                transactionDao.deleteAllTransactions();
            }
        }).start();
    }

    public LiveData<List<Transaction>> getCurrentMonthTransactions(String transaction) {

        currentMonthTransactions = transactionDao.getCurrentMonthCharges(Calendar.getInstance().get(Calendar.MONTH) + 1, transaction, Calendar.getInstance().get(Calendar.YEAR));
        return currentMonthTransactions;
    }

    public LiveData<List<Transaction>> getSelectedMonthTransactions(int selectedMonth) {

        return transactionDao.getSelectedMonthTransactions(selectedMonth);
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<List<Transaction>> getSelectedYearTransactions(int currentYear)
    {
        return transactionDao.getSelectedYearTransactions(currentYear);
    }

    /* GOAL Section */

    public void insertGoal(final Goal goal) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                transactionDao.insertGoal(goal);
            }
        }).start();
    }

    public void updateGoal (final Goal goal)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                transactionDao.updateGoal(goal);
            }
        }).start();
    }

    public LiveData<Goal> getCurrentGoal() {
        return transactionDao.getCurrentGoal();
    }

    public void deleteAllGoals() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                transactionDao.deleteAllGoals();
            }
        }).start();
    }
}
