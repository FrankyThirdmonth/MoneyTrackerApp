package com.frankydev.moneytrackerapp.userinterface;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frankydev.moneytrackerapp.BaseApplication;
import com.frankydev.moneytrackerapp.BaseFragment;
import com.frankydev.moneytrackerapp.R;
import com.frankydev.moneytrackerapp.model.Transaction;
import com.frankydev.moneytrackerapp.userinterface.adapters.RecyclerViewChargeAdapter;
import com.frankydev.moneytrackerapp.utility.Constants;
import com.frankydev.moneytrackerapp.utility.FragmentImplementations;
import com.frankydev.moneytrackerapp.viewmodel.TransactionViewModel;
import com.frankydev.moneytrackerapp.viewmodel.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static com.frankydev.moneytrackerapp.utility.Constants.KEY_CHARGE;
import static com.frankydev.moneytrackerapp.utility.Constants.KEY_SCREEN;
import static com.frankydev.moneytrackerapp.utility.Constants.VALUE_NEW_TRANSACTION_SCREEN;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChargesLogFragment extends BaseFragment implements FragmentImplementations,
        View.OnClickListener, RecyclerViewChargeAdapter.Listener {

    private static final String TAG = "Tracking";

    /* Views*/
    private View rootView;
    private RecyclerViewChargeAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView imageViewFilter;
    private TextView textViewMonthLog;
    private TextView textViewLabelMonthLog;

    /* View Model */
    private TransactionViewModel chargeViewModel;
    @Inject
    public ViewModelFactory chargeViewModelFactory;

    /* Private Variables */
    private int position;
    private List<String> arrayList;
    private List<Transaction> deletedList;


    /* Nav Controller */
    private NavController navController;

    /* Alert Dialog */
    public AlertDialog.Builder builder;

    public ChargesLogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseApplication().getViewModelComponent().inject(this);
        chargeViewModel = new ViewModelProvider(this, chargeViewModelFactory).get(TransactionViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        setHasOptionsMenu(true);

        rootView = inflater.inflate(R.layout.fragment_charges_log, container, false);

        textViewMonthLog = findViewById(R.id.text_view_month_log);
        textViewLabelMonthLog = findViewById(R.id.text_view_label_month_log);
        textViewLabelMonthLog.setOnClickListener(this);
        arrayList = Arrays.asList(getResources().getStringArray(R.array.filter_list));
        initRecycler();

        chargeViewModel.getAllTransactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                adapter.setChargesList(transactions);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        builder = new AlertDialog.Builder(getViewContext(), R.style.AlertDialog);
        navController = getNavController();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.unregisterListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.delete_all_option:
                chargeViewModel.deleteAllTransactions();
                deletedList = new ArrayList<>(chargeViewModel.getAllTransactions().getValue());
                setSnackBar("Delete all items");

                break;

        }

        return super.onOptionsItemSelected(item);

    }

    private void initRecycler() {

        recyclerView = findViewById(R.id.recycler_view_charges);
        adapter = new RecyclerViewChargeAdapter(Constants.FULL_LIST);
        adapter.registerListener(this);
        Log.d(TAG, "onCreateView: registered");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        position = -1;

        switch (id) {

            case R.id.text_view_label_month_log:

                AlertDialog alertDialog =
                        builder.setSingleChoiceItems(R.array.filter_list, -1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        position = which;
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        List<Transaction> showList = new ArrayList<>();

                                        final String monthSelected = arrayList.get(position);

                                        if (position == 0) {
                                            showList = chargeViewModel.getAllTransactions().getValue();
                                            adapter.setChargesList(showList);
                                            textViewMonthLog.setText(monthSelected);
                                            return;
                                        }
                                        textViewMonthLog.setText(monthSelected);
                                        chargeViewModel.getSelectedMonthTransaction(position).observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
                                            @Override
                                            public void onChanged(List<Transaction> chargeList) {
                                                adapter.setChargesList(chargeList);
                                            }
                                        });


                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Nothing to do here
                                    }
                                }).setTitle("Filter").create();


                alertDialog.show();

                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_logcharges, menu);
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
    public <T extends View> T findViewById(int id) {
        return getRootView().findViewById(id);
    }

    @Override
    public BaseApplication getBaseApplication() {
        return (BaseApplication) getActivity().getApplication();
    }

    @Override
    public NavController getNavController() {
        return getBaseApplication().getNavController();
    }

    @Override
    public void onChargeClicked(Transaction transaction) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SCREEN, VALUE_NEW_TRANSACTION_SCREEN);
        bundle.putParcelable(KEY_CHARGE , transaction);
        navController.navigate(R.id.action_chargesLogFragment_to_newTransactionFragment, bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    public void setSnackBar(String message)
    {
        Snackbar.make(getRootView(), message, Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargeViewModel.insertAllTransaction(deletedList);
                Toast.makeText(getViewContext(), "Revoked", Toast.LENGTH_SHORT).show();
            }
        }).show();
    }
}
