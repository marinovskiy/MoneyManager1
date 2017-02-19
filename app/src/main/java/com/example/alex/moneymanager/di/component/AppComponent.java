package com.example.alex.moneymanager.di.component;

import com.example.alex.moneymanager.activities.AddOperationActivity;
import com.example.alex.moneymanager.activities.BaseActivity;
import com.example.alex.moneymanager.activities.LoginActivity;
import com.example.alex.moneymanager.activities.MainActivity;
import com.example.alex.moneymanager.activities.RegisterActivity;
import com.example.alex.moneymanager.activities.SplashActivity;
import com.example.alex.moneymanager.application.MoneyManagerApplication;
import com.example.alex.moneymanager.di.modules.AppModule;
import com.example.alex.moneymanager.di.modules.PreferenceModule;
import com.example.alex.moneymanager.di.modules.RealmModule;
import com.example.alex.moneymanager.di.modules.SystemUtilsModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        AppModule.class,
        PreferenceModule.class,
        RealmModule.class,
        SystemUtilsModule.class
})
@Singleton
public interface AppComponent {

    // application
    void inject(MoneyManagerApplication moneyManagerApplication);

    // activities
    void inject(BaseActivity baseActivity);

    void inject(SplashActivity splashActivity);

    void inject(LoginActivity loginActivity);

    void inject(RegisterActivity registerActivity);

    void inject(MainActivity mainActivity);

    void inject(AddOperationActivity addOperationActivity);

    // fragments

    // getters
}