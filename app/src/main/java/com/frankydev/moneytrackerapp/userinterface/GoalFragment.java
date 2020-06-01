package com.frankydev.moneytrackerapp.userinterface;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.frankydev.moneytrackerapp.BaseApplication;
import com.frankydev.moneytrackerapp.BaseFragment;
import com.frankydev.moneytrackerapp.R;
import com.frankydev.moneytrackerapp.model.Goal;
import com.frankydev.moneytrackerapp.utility.FragmentImplementations;
import com.frankydev.moneytrackerapp.viewmodel.GoalViewModel;
import com.frankydev.moneytrackerapp.viewmodel.ViewModelFactory;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoalFragment extends BaseFragment implements
        FragmentImplementations,
        View.OnClickListener,
        Observer<Goal> {

    private static final String TAG = "Tracking";
    /* Views*/
    private View mRootView;
    private TextView textViewCurrentGoal;
    private TextInputEditText editTextSetCapAmount;
    private Button btnSetNewGoal;
    private Button btnDeleteGoal;

    /* NavController */
    private NavController navController;

    /* ViewModel*/

    @Inject
    public ViewModelFactory viewModelFactory;
    private GoalViewModel goalViewModel;


    public GoalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRootView = inflater.inflate(R.layout.fragment_date_balance, container, false);
        textViewCurrentGoal = findViewById(R.id.text_view_current_goal);
        editTextSetCapAmount = findViewById(R.id.edit_text_cap_amount);
        btnSetNewGoal = findViewById(R.id.button_set_new_goal);
        btnSetNewGoal.setOnClickListener(this);
        btnDeleteGoal = findViewById(R.id.button_delete_goal);
        btnDeleteGoal.setOnClickListener(this);

        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        navController = getNavController();
        getBaseApplication().getViewModelComponent().inject(this);
        goalViewModel = new ViewModelProvider(this, viewModelFactory).get(GoalViewModel.class);
        goalViewModel.getCurrentGoal().observe(this, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideKeyboard(getViewContext(), getRootView());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideKeyboard(getViewContext(), getRootView());
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


    @Override
    public void onClick(View v) {

        int id = v.getId();

        hideKeyboard(getViewContext(), getRootView());
        final Goal oldGoal = goalViewModel.getCurrentGoal().getValue();

        switch (id) {
            case R.id.button_set_new_goal:

                if (editTextSetCapAmount.getText().toString().isEmpty()) {
                    Toast.makeText(getViewContext(), "Empty Fields", Toast.LENGTH_SHORT).show();
                    break;
                }

                Goal newGoal = new Goal(Double.valueOf(editTextSetCapAmount.getText().toString()));
                if (oldGoal != null) {
                    newGoal.setId(oldGoal.getId());
                    goalViewModel.updateGoal(newGoal);
                    setUpdateSnackbar(oldGoal);
                    break;
                }
                goalViewModel.insertGoal(newGoal);
                setUpdateSnackbar(oldGoal);
                break;

            case R.id.button_delete_goal:
                goalViewModel.deleteAllGoals();

                Snackbar.make(getRootView(), "Goal free", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (oldGoal != null) {
                            goalViewModel.insertGoal(oldGoal);
                        }

                    }
                }).show();
        }
    }

    @Override
    public void onChanged(Goal goal) {

        Log.d(TAG, "onChanged: ");
        if (goal != null) {
            textViewCurrentGoal.setText("Goal set: " + String.format("%.2f", goal.getGoalAmount()) + " €");
        } else {
            textViewCurrentGoal.setText("No goal set");
        }
    }

    public void setUpdateSnackbar(final Goal oldGoal) {

        if (oldGoal == null) {
            Snackbar.make(getRootView(), "New goal set", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goalViewModel.deleteAllGoals();
                }
            }).show();
            return;
        }
        Snackbar.make(getRootView(), "New goal set", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goalViewModel.updateGoal(oldGoal);
            }
        }).show();
    }


}
