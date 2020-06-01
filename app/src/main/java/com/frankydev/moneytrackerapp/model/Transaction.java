package com.frankydev.moneytrackerapp.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "transactions_table")
public class Transaction implements Parcelable {

    @Ignore
    private Calendar calendar = Calendar.getInstance();

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double amount;

    private String motivation;

    private String transaction;

    private int year;

    private int month;

    private int day;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public Transaction(double amount, String motivation, String transaction, int year, int month, int day) {
        this.amount = amount;
        this.motivation = motivation;
        this.transaction = transaction;
        this.year = year;
        this.month = month;
        this.day = day;


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected Transaction(Parcel in) {
        id = in.readInt();
        amount = in.readDouble();
        motivation = in.readString();
        transaction = in.readString();

        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeDouble(amount);
        dest.writeString(motivation);
        dest.writeString(transaction);
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
    }
}
