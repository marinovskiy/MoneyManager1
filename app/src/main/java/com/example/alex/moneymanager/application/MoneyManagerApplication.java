package com.example.alex.moneymanager.application;

import android.app.Application;
import android.content.Context;

import com.example.alex.moneymanager.di.component.AppComponent;
import com.example.alex.moneymanager.di.component.DaggerAppComponent;
import com.example.alex.moneymanager.di.modules.AppModule;

public class MoneyManagerApplication extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = buildComponent();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    private AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }
}