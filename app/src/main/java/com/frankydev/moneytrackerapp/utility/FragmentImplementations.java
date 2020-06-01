package com.frankydev.moneytrackerapp.utility;

import android.content.Context;
import android.view.View;

import androidx.navigation.NavController;

import com.frankydev.moneytrackerapp.BaseApplication;

public interface FragmentImplementations {

    View getRootView ();

    Context getViewContext();

    <T extends View> T findViewById (int id);

    BaseApplication getBaseApplication ();

    NavController getNavController();




}
