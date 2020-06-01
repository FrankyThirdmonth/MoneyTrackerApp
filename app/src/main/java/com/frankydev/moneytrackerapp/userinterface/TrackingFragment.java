package com.frankydev.moneytrackerapp.userinterface;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.frankydev.moneytrackerapp.BaseApplication;
import com.frankydev.moneytrackerapp.BaseFragment;
import com.frankydev.moneytrackerapp.R;
import com.frankydev.moneytrackerapp.model.GraphData;
import com.frankydev.moneytrackerapp.model.Transaction;
import com.frankydev.moneytrackerapp.utility.FragmentImplementations;
import com.frankydev.moneytrackerapp.viewmodel.TransactionViewModel;
import com.frankydev.moneytrackerapp.viewmodel.ViewModelFactory;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrackingFragment extends BaseFragment implements FragmentImplementations {


    private static final String TAG = "TrackingFragment";

    /* Views */

    private View mRootView;
    private EditText editTextTrendingYear;

    /*View Model */

    @Inject
    public ViewModelFactory viewModelFactory;
    private TransactionViewModel transactionViewModel;

    /* Private Variables */

    private List<Transaction> yearTransactions;

    /* Chart */
    private LineChart chart;

    /* Calendar */
    private Calendar calendar = Calendar.getInstance();
    private int currentYear = calendar.get(Calendar.YEAR);

    /*Alert Builder*/

    private AlertDialog.Builder alertBuilder;
    private DatePickerDialog.Builder datePickerBuilder;


    public TrackingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        alertBuilder = new AlertDialog.Builder(context);
        datePickerBuilder = new DatePickerDialog.Builder(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBaseApplication().getViewModelComponent().inject(this);
        transactionViewModel = new ViewModelProvider(this, viewModelFactory).get(TransactionViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootView = inflater.inflate(R.layout.fragment_tracking, container, false);
        chart = findViewById(R.id.line_chart_custom);
        editTextTrendingYear = findViewById(R.id.edit_text_trending_year);

        if (editTextTrendingYear.getText().toString().isEmpty()) {
            editTextTrendingYear.setText("2020");
            observeViewModels();
        }

        editTextTrendingYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String currentYearString = s.toString();

                if (currentYearString.isEmpty()) {
                    return;
                }

                currentYear = Integer.parseInt(currentYearString);
                observeViewModels();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideKeyboard(getViewContext(),getRootView());
    }

    private void observeViewModels() {

        transactionViewModel.getSelectedYearTransaction(currentYear).observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                yearTransactions = new ArrayList<>(transactions);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateGraph(yearTransactions);
                    }
                }).start();
            }
        });
    }

    private void updateGraph(List<Transaction> yearTransactions) {

        List<GraphData> graphDataList = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();

        long sumJan = 0;
        long sumFeb = 0;
        long sumMar = 0;
        long sumApr = 0;
        long sumMay = 0;
        long sumJun = 0;
        long sumJul = 0;
        long sumAug = 0;
        long sumSep = 0;
        long sumOct = 0;
        long sumNov = 0;
        long sumDec = 0;

        for (Transaction transaction : yearTransactions) {
            switch (transaction.getMonth()) {
                case 1:
                    sumJan += transaction.getAmount();
                    break;
                case 2:
                    sumFeb += transaction.getAmount();
                    break;
                case 3:
                    sumMar += transaction.getAmount();
                    break;
                case 4:
                    sumApr += transaction.getAmount();
                    break;
                case 5:
                    sumMay += transaction.getAmount();
                    break;
                case 6:
                    sumJun += transaction.getAmount();
                    break;
                case 7:
                    sumJul += transaction.getAmount();
                    break;
                case 8:
                    sumAug += transaction.getAmount();
                    break;
                case 9:
                    sumSep += transaction.getAmount();
                    break;
                case 10:
                    sumOct += transaction.getAmount();
                    break;
                case 11:
                    sumNov += transaction.getAmount();
                    break;
                case 12:
                    sumDec += transaction.getAmount();
                    break;
            }
        }

        List<Long> monthSumList = new ArrayList(Arrays.asList(
                sumJan,
                sumFeb,
                sumMar,
                sumApr,
                sumMay,
                sumJun,
                sumJul,
                sumAug,
                sumSep,
                sumOct,
                sumNov,
                sumDec));

        for (int i = 1; i <= 12; i++) {
            graphDataList.add(new GraphData(i, monthSumList.get(i - 1)));
        }

        for (int i = 1; i <= 11; i++) {
            entries.add(new Entry(graphDataList.get(i).getValueX(), graphDataList.get(i).getValueY()));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "Trending year");
        lineDataSet.setLineWidth(20);
        lineDataSet.setCircleRadius(10);
        lineDataSet.setCircleHoleRadius(8);
        lineDataSet.setValueTextSize(10);
        LineData lineData = new LineData(lineDataSet);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new MyAxis());
        xAxis.setAxisMinimum(1);

        chart.setData(lineData);
        chart.invalidate();

    }

    @Override
    public void onStart() {
        super.onStart();
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
        return (BaseApplication) getActivity().getApplication();
    }

    @Override
    public NavController getNavController() {
        return getBaseApplication().getNavController();
    }

    public class MyAxis implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            axis.setLabelCount(12, true);
            return "" + (int) value;
        }
    }

}
