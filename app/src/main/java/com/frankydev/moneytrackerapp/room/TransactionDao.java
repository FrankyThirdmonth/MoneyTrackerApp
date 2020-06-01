package com.frankydev.moneytrackerapp.room;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.frankydev.moneytrackerapp.model.Goal;
import com.frankydev.moneytrackerapp.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    /* Transaction Section */

    @Insert
    void insertTransaction(Transaction transaction);

    @Insert
    void insertAllTransactions(List<Transaction> transactionList);

    @Delete
    void deleteTransaction(Transaction transaction);

    @Update
    void updateTransaction(Transaction transaction);

    @Query("SELECT * FROM transactions_table ORDER BY year AND month DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT * FROM transactions_table WHERE month LIKE :currentMonth AND `transaction` LIKE :transaction AND year LIKE :currentYear")
    LiveData<List<Transaction>> getCurrentMonthCharges(int currentMonth, String transaction, int currentYear);

    @Query("SELECT * FROM transactions_table WHERE month LIKE :monthSelected")
    LiveData<List<Transaction>> getSelectedMonthTransactions(int monthSelected);

    @Query("SELECT * FROM transactions_table WHERE year LIKE :currentYear ORDER BY month")
    LiveData<List<Transaction>> getSelectedYearTransactions(int currentYear);

    @Query("DELETE FROM transactions_table")
    void deleteAllTransactions();

    /* GOAL Section */

    @Insert
    void insertGoal(Goal goal);

    @Update
    void updateGoal(Goal goal);

    @Query("SELECT * FROM goal_table")
    LiveData<Goal> getCurrentGoal();

    @Query("DELETE FROM goal_table")
    void deleteAllGoals();

}
