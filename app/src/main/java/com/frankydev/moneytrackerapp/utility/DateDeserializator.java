package com.frankydev.moneytrackerapp.utility;

public class DateDeserializator {

    private int day;
    private int month;
    private int year;


    public void deserializeDate (String date)
    {
        String [] strings = date.split("-");
        day = Integer.valueOf(strings[0]);
        month = Integer.valueOf(strings[1]);
        year = Integer.valueOf(strings[2]);
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
