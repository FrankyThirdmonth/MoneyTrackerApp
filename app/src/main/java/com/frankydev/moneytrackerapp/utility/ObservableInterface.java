package com.frankydev.moneytrackerapp.utility;

import com.frankydev.moneytrackerapp.model.Transaction;

public interface ObservableInterface {

    interface Listener {
        void onChargeClicked(Transaction transaction);
    }

    void registerListener(Listener listener);

    void unregisterListener(Listener listener);
}
