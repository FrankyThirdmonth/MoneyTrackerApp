package com.frankydev.moneytrackerapp.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.frankydev.moneytrackerapp.model.Goal;
import com.frankydev.moneytrackerapp.model.Transaction;


@Database(entities = {Transaction.class, Goal.class}, version = 2)
public abstract class TransactionDatabase extends RoomDatabase {

    public static TransactionDatabase instance;

    public abstract TransactionDao chargeDao();

    public static synchronized TransactionDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), TransactionDatabase.class, "database").fallbackToDestructiveMigration().build();
        }
        return instance;
    }




}
