package com.frankydev.moneytrackerapp.di.navigationdrawer;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.frankydev.moneytrackerapp.R;

import dagger.Module;
import dagger.Provides;

@Module
public class DrawerModule {

    private DrawerLayout drawerLayout;

    public DrawerModule(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }

    @Provides
    AppBarConfiguration.Builder provideAppBarConfigurationBuilder() {
        return new AppBarConfiguration.Builder(R.id.homeFragment, R.id.chargesLogFragment, R.id.dateBalanceFragment).setDrawerLayout(drawerLayout);
    }

    @Provides
    AppBarConfiguration provideAppBarConfiguration(AppBarConfiguration.Builder builder) {
        return builder.build();
    }


}
