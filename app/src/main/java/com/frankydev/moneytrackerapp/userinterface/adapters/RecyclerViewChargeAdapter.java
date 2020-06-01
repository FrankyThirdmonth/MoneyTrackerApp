package com.frankydev.moneytrackerapp.userinterface.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frankydev.moneytrackerapp.R;
import com.frankydev.moneytrackerapp.model.Transaction;
import com.frankydev.moneytrackerapp.utility.ObservableInterface;

import java.util.ArrayList;
import java.util.List;

import static com.frankydev.moneytrackerapp.utility.Constants.VALUE_CHARGE_TRANSACTION;
import static com.frankydev.moneytrackerapp.utility.Constants.VALUE_ENTRY_TRANSACTION;
import static com.frankydev.moneytrackerapp.utility.Constants.FULL_LIST;
import static com.frankydev.moneytrackerapp.utility.Constants.PARTIAL_LIST;

public class RecyclerViewChargeAdapter extends RecyclerView.Adapter<RecyclerViewChargeAdapter.MyViewHolder> implements ObservableInterface {

    private static final String TAG = "RegistrationListeners";
    private List<Transaction> chargesList;
    private int checkRequest;

    public List<Listener> mListeners = new ArrayList<>();

    private Context context;

    public RecyclerViewChargeAdapter(int checkRequest) {
        this.checkRequest = checkRequest;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Transaction currentTransaction = chargesList.get(position);
        holder.textViewMotivation.setText(currentTransaction.getMotivation());

        if (currentTransaction.getTransaction().equals(VALUE_ENTRY_TRANSACTION)) {
            holder.textViewAmount.setTextColor(context.getResources().getColor(R.color.green));
        } else if (currentTransaction.getTransaction().equals(VALUE_CHARGE_TRANSACTION)) {
            holder.textViewAmount.setTextColor(context.getResources().getColor(R.color.red));
        }
        holder.textViewAmount.setText(String.format("%.2f", currentTransaction.getAmount()) + " €");

        String date = currentTransaction.getDay() + "-" + currentTransaction.getMonth() + "-" + currentTransaction.getYear();
        holder.textViewDate.setText(date);
    }

    @Override
    public int getItemCount() {
        if (chargesList == null) {
            return 0;
        }
        return chargesList.size();
    }

    public void setChargesList(List<Transaction> list) {

        if (checkRequest == FULL_LIST) {
            this.chargesList = list;
        } else if (checkRequest == PARTIAL_LIST) {
            if (list.size() <= 2) { // if the size of the list is < 3
                this.chargesList = list.subList(0, list.size()); // only the items in the list
            } else {
                this.chargesList = list.subList(0, 3); // only the first three items of the list
            }
        }

        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewAmount;
        private TextView textViewMotivation;
        private TextView textViewDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewAmount = itemView.findViewById(R.id.text_view_import);
            textViewMotivation = itemView.findViewById(R.id.text_view_reason);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            for (Listener listener : mListeners) {
                listener.onChargeClicked(chargesList.get(getAdapterPosition()));
            }
        }

    }

    @Override
    public void registerListener(Listener listener) {
        mListeners.add(listener);
    }

    @Override
    public void unregisterListener(Listener listener) {
        mListeners.remove(listener);
    }
}
