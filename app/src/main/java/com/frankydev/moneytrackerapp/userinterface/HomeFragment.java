package com.frankydev.moneytrackerapp.userinterface;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frankydev.moneytrackerapp.BaseApplication;
import com.frankydev.moneytrackerapp.R;
import com.frankydev.moneytrackerapp.model.Goal;
import com.frankydev.moneytrackerapp.model.Transaction;
import com.frankydev.moneytrackerapp.userinterface.adapters.RecyclerViewChargeAdapter;
import com.frankydev.moneytrackerapp.utility.Constants;
import com.frankydev.moneytrackerapp.utility.FragmentImplementations;
import com.frankydev.moneytrackerapp.viewmodel.GoalViewModel;
import com.frankydev.moneytrackerapp.viewmodel.TransactionViewModel;
import com.frankydev.moneytrackerapp.viewmodel.ViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import javax.inject.Inject;

import static com.frankydev.moneytrackerapp.utility.Constants.KEY_SCREEN;
import static com.frankydev.moneytrackerapp.utility.Constants.KEY_TRANSACTION;
import static com.frankydev.moneytrackerapp.utility.Constants.VALUE_CHARGE_TRANSACTION;
import static com.frankydev.moneytrackerapp.utility.Constants.VALUE_ENTRY_TRANSACTION;
import static com.frankydev.moneytrackerapp.utility.Constants.VALUE_HOME_SCREEN;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements
        FragmentImplementations,
        View.OnClickListener {

    private static final String TAG = "Tracking";

    /* Views */
    private View rootView;
    private TextView textViewMoneySpent;
    private TextView textViewHomeCurrentGoal;
    private TextView textViewPercentage;
    private FloatingActionButton fabCharge;
    private FloatingActionButton fabEntry;
    private ProgressBar progressBar;

    /* Recycler View */
    private RecyclerView mRecyclerView;
    private RecyclerViewChargeAdapter adapter;

    /* NavController*/
    private NavController navController;

    /* View Model */
    private GoalViewModel goalViewModel;
    private TransactionViewModel transactionViewModel;
    @Inject
    public ViewModelFactory viewModelFactory;

    /* Handler*/
    private Handler mainHandler = new Handler(Looper.getMainLooper());


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseApplication().getViewModelComponent().inject(this);
        transactionViewModel = new ViewModelProvider(this, viewModelFactory).get(TransactionViewModel.class);
        goalViewModel = new ViewModelProvider(this, viewModelFactory).get(GoalViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        textViewMoneySpent = findViewById(R.id.text_view_money_spent);
        textViewHomeCurrentGoal = findViewById(R.id.text_view_monthly_goal);
        textViewPercentage = findViewById(R.id.text_view_percentage);
        fabEntry = findViewById(R.id.floating_action_bar_new_entry);
        fabCharge = findViewById(R.id.floating_action_bar_new_charge);
        mRecyclerView = findViewById(R.id.home_recycler_view);
        progressBar = findViewById(R.id.progressBar);

        fabEntry.setOnClickListener(this);
        fabCharge.setOnClickListener(this);
        textViewHomeCurrentGoal.setOnClickListener(this);

        observeViewModels();
        initRecyclerView();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        navController = getNavController();
        Log.d(TAG, "onStart: balance listener registered");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void observeViewModels() {


        transactionViewModel.getAllTransactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                adapter.setChargesList(transactions);
            }
        });

        transactionViewModel.getCurrentMonthCharges(VALUE_CHARGE_TRANSACTION).observe(getViewLifecycleOwner(),
                new Observer<List<Transaction>>() {
                    @Override
                    public void onChanged(final List<Transaction> transactions) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                double totalSum = 0;

                                for (int i = 0; i < transactions.size(); i++) {
                                    totalSum += transactions.get(i).getAmount();
                                }

                                final double finalTotalSum = totalSum;
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textViewMoneySpent.setText(finalTotalSum + " €");
                                        progressBar.setProgress((int) finalTotalSum);
                                        float percentage = (float) (finalTotalSum / progressBar.getMax() * 100);

                                        if (percentage <= 75) {
                                            textViewPercentage.setTextColor(getResources().getColor(R.color.green));
                                            textViewPercentage.setText(String.format("%.1f", percentage) + "%");
                                        } else if (percentage > 75 && percentage <= 100) {

                                            if (percentage == 100) {
                                                textViewPercentage.setTextColor(getResources().getColor(R.color.red));
                                            } else {
                                                textViewPercentage.setTextColor(getResources().getColor(R.color.orange));
                                            }

                                            textViewPercentage.setText(String.format("%.1f", percentage) + "%");
                                        } else {
                                            textViewPercentage.setTextColor(getResources().getColor(R.color.red));
                                            textViewPercentage.setText("-" + String.format("%.1f", (percentage - 100)) + "%");
                                        }
                                    }
                                });

                            }
                        }).start();

                    }
                });

        goalViewModel.getCurrentGoal().observe(getViewLifecycleOwner(), new Observer<Goal>() {
            @Override
            public void onChanged(Goal goal) {
                if (goal != null) {
                    Log.d(TAG, " progress bar: " + (int) goal.getGoalAmount());
                    progressBar.setMax((int) goal.getGoalAmount());
                    textViewHomeCurrentGoal.setText(String.format("%.2f", goal.getGoalAmount()) + "€");
                    return;
                }

                textViewHomeCurrentGoal.setText("Uncapped");

            }
        });
    }

    private void initRecyclerView() {
        adapter = new RecyclerViewChargeAdapter(Constants.PARTIAL_LIST);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public BaseApplication getBaseApplication() {
        return (BaseApplication) getActivity().getApplication();
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return getRootView().findViewById(id);
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    @Override
    public Context getViewContext() {
        return getRootView().getContext();
    }

    @Override
    public NavController getNavController() {
        return getBaseApplication().getNavController();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        Bundle bundle = new Bundle();

        switch (id) {
            case R.id.floating_action_bar_new_entry:
                bundle.putString(KEY_SCREEN, VALUE_HOME_SCREEN);
                bundle.putString(KEY_TRANSACTION, VALUE_ENTRY_TRANSACTION);
                navController.navigate(R.id.action_homeFragment_to_newTransactionFragment, bundle);
                break;

            case R.id.floating_action_bar_new_charge:
                bundle.putString(KEY_SCREEN, VALUE_HOME_SCREEN);
                bundle.putString(KEY_TRANSACTION, VALUE_CHARGE_TRANSACTION);
                navController.navigate(R.id.action_homeFragment_to_newTransactionFragment, bundle);
                break;

            case R.id.text_view_monthly_goal:
                navController.navigate(R.id.dateBalanceFragment);
        }
    }

}
