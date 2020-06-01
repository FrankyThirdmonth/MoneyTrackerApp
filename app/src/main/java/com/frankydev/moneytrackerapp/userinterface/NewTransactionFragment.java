package com.frankydev.moneytrackerapp.userinterface;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.frankydev.moneytrackerapp.BaseApplication;
import com.frankydev.moneytrackerapp.BaseFragment;
import com.frankydev.moneytrackerapp.R;
import com.frankydev.moneytrackerapp.model.Transaction;
import com.frankydev.moneytrackerapp.model.DateSchema;
import com.frankydev.moneytrackerapp.utility.DateDeserializator;
import com.frankydev.moneytrackerapp.utility.FragmentImplementations;
import com.frankydev.moneytrackerapp.viewmodel.TransactionViewModel;
import com.frankydev.moneytrackerapp.viewmodel.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import static com.frankydev.moneytrackerapp.utility.Constants.KEY_CHARGE;
import static com.frankydev.moneytrackerapp.utility.Constants.KEY_SCREEN;
import static com.frankydev.moneytrackerapp.utility.Constants.KEY_TRANSACTION;
import static com.frankydev.moneytrackerapp.utility.Constants.VALUE_CHARGE_TRANSACTION;
import static com.frankydev.moneytrackerapp.utility.Constants.VALUE_ENTRY_TRANSACTION;
import static com.frankydev.moneytrackerapp.utility.Constants.VALUE_HOME_SCREEN;
import static com.frankydev.moneytrackerapp.utility.Constants.VALUE_NEW_TRANSACTION_SCREEN;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewTransactionFragment extends BaseFragment implements
        FragmentImplementations,
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener {

    /* Views*/

    private View mRootView;
    private TextInputEditText editTextAmount;
    private TextInputEditText editTextMotivation;
    private TextView textViewTitleLabel;
    private TextView textViewCurrentDate;

    /* Nav Controller */

    private NavController navController;

    /* View Model instance */


    public TransactionViewModel transactionViewModel;

    @Inject
    public ViewModelFactory chargeViewModelFactory;

    private String transactionType;

    /* Calendar */

    private Calendar calendar = Calendar.getInstance();
    private int currentYear = calendar.get(Calendar.YEAR);
    private int currentMonth = calendar.get(Calendar.MONTH) + 1;
    private int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    private DateSchema currentDate = new DateSchema(currentYear, currentMonth, currentDay);
    private DateSchema updatedDate;
    private Bundle bundle;

    /* Helper Classes */
    private DateDeserializator deserializator = new DateDeserializator();

    public NewTransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseApplication().getViewModelComponent().inject(this);
        transactionViewModel = new ViewModelProvider(this, chargeViewModelFactory).get(TransactionViewModel.class);
        transactionViewModel.getAllTransactions().observe(this, new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_new_transaction, container, false);
        setHasOptionsMenu(true);

        editTextAmount = findViewById(R.id.edit_text_amount);
        editTextMotivation = findViewById(R.id.edit_text_motivation);
        textViewTitleLabel = findViewById(R.id.new_transaction_title);
        textViewCurrentDate = findViewById(R.id.text_view_current_date);

        textViewCurrentDate.setOnClickListener(this);

        bundle = getArguments();

        checkBundleInitTitleAndDate();

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        navController = getNavController();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (bundle != null) {
            bundle.clear();
        }
    }

    /* Checking  whether the bundle comes from HomeFragment or Charges Log Fragment*/

    private void checkBundleInitTitleAndDate() {

        if (bundle != null) {
            String keyScreen = bundle.getString(KEY_SCREEN);

            // coming from Home fragment

            if (keyScreen.equals(VALUE_HOME_SCREEN)) {

                transactionType = bundle.getString(KEY_TRANSACTION);

                if (transactionType.equals(VALUE_ENTRY_TRANSACTION)) {
                    textViewTitleLabel.setText("New entry transaction");
                } else {
                    textViewTitleLabel.setText("New charge transaction");
                }
                textViewCurrentDate.setText(currentDate.getFinalDate());
                return;
            }

            // coming from new transaction screen

            Transaction transaction = bundle.getParcelable(KEY_CHARGE);

            transactionType = transaction.getTransaction();
            editTextAmount.setText(String.format("%.2f", transaction.getAmount())+"");
            editTextMotivation.setText(transaction.getMotivation());
            textViewCurrentDate.setText(transaction.getDay() + "-" + transaction.getMonth() + "-" + transaction.getYear());
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (bundle != null) {
            String keyScreen = bundle.getString(KEY_SCREEN);

            switch (keyScreen) {

                case VALUE_HOME_SCREEN:
                    inflater.inflate(R.menu.menu_new_transaction, menu);
                    break;
                case VALUE_NEW_TRANSACTION_SCREEN:
                    inflater.inflate(R.menu.menu_new_updated_transaction, menu);


            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        hideKeyboard(getViewContext(), getRootView());

        switch (id) {
            case R.id.save_transaction:
                return saveNewCharge(item);

            case R.id.update_transaction:
                return updateCharge(item);

            case R.id.delete_transaction:
                return deleteTransaction(item);
        }
        return false;

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean saveNewCharge(MenuItem item) {

        int updatedYear = 0;
        int updatedMonth = 0;
        int updatedDay = 0;
        String amountString = editTextAmount.getText().toString();
        String motivation = editTextMotivation.getText().toString();
        DateSchema updatedDate = getUpdatedDate();
        Transaction transaction = null;

        double amount = Double.parseDouble(amountString);

        if (amount == 0) {
            Toast.makeText(getViewContext(), "Input can't be 0 €", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (updatedDate == null) {
            updatedYear = currentYear;
            updatedMonth = currentMonth;
            updatedDay = currentDay;
        } else {
            updatedYear = updatedDate.getYear();
            updatedMonth = updatedDate.getMonth();
            updatedDay = getUpdatedDate().getDay();
        }

        // Check if fields are not empty

        if (amountString.isEmpty() || motivation.isEmpty()) {
            Toast.makeText(getViewContext(), "Empty fields", Toast.LENGTH_SHORT).show();
            return false;
        }


        // create a new Charge

        if (transactionType.equals(VALUE_ENTRY_TRANSACTION)) {
            transaction = new Transaction(amount, motivation, VALUE_ENTRY_TRANSACTION, updatedYear, updatedMonth, updatedDay);

        } else if (transactionType.equals(VALUE_CHARGE_TRANSACTION)) {
            transaction = new Transaction(amount, motivation, VALUE_CHARGE_TRANSACTION, updatedYear, updatedMonth, updatedDay);
        }
        // Send new Charge to other fragment

        transactionViewModel.insertTransaction(transaction);

        Snackbar.make(getRootView(), "New transaction saved", Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Transaction currentTransaction = transactionViewModel.getAllTransactions().getValue().get(0);  // getting the last value inserted
                        transactionViewModel.deleteTransaction(currentTransaction);
                        Toast.makeText(getContext(), "Not deleted", Toast.LENGTH_SHORT).show();
                    }
                }).show();

        navController.navigate(R.id.homeFragment);

        return super.onOptionsItemSelected(item);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean updateCharge(MenuItem item) {

        // Bundle bundle = getArguments();
        final Transaction transaction = bundle.getParcelable(KEY_CHARGE);
        int id = transaction.getId();

        double updatedAmount = Double.valueOf(editTextAmount.getText().toString());
        String updatedMotivation = editTextMotivation.getText().toString();
        String updatedDate = textViewCurrentDate.getText().toString();
        deserializator.deserializeDate(updatedDate);

        final Transaction updatedTransaction = new Transaction(updatedAmount, updatedMotivation,
                transactionType, deserializator.getYear(),
                deserializator.getMonth(), deserializator.getDay());

        updatedTransaction.setId(id);
        transactionViewModel.updateTransaction(updatedTransaction);

        Snackbar.make(getRootView(), "Updated", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        transactionViewModel.deleteTransaction(updatedTransaction);
                        transactionViewModel.insertTransaction(transaction);
                        Toast.makeText(getViewContext(), "Revoked", Toast.LENGTH_SHORT).show();
                    }
                }).show();

        navController.navigate(R.id.homeFragment);
        return super.onOptionsItemSelected(item);
    }

    private boolean deleteTransaction(MenuItem item) {

        final Transaction transaction = bundle.getParcelable(KEY_CHARGE);
        transactionViewModel.deleteTransaction(transaction);
        Snackbar.make(getRootView(), "Item deleted", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getViewContext(), "Revoked", Toast.LENGTH_SHORT).show();
                transactionViewModel.insertTransaction(transaction);
            }
        }).show();
        navController.navigate(R.id.homeFragment);
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.text_view_current_date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getViewContext(), this, currentYear, currentMonth - 1, currentDay);
                datePickerDialog.show();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        updatedDate = new DateSchema(year, month + 1, dayOfMonth);
        textViewCurrentDate.setText(updatedDate.getFinalDate());
    }

    public DateSchema getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public View getRootView() {
        return mRootView;
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
        return ((BaseApplication) getActivity().getApplication());
    }

    @Override
    public NavController getNavController() {
        return getBaseApplication().getNavController();
    }

}
