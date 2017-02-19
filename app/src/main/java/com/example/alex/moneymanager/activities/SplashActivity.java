package com.example.alex.moneymanager.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.alex.moneymanager.application.MoneyManagerApplication;
import com.example.alex.moneymanager.utils.PreferenceUtil;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity {

    @Inject
    PreferenceUtil preferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MoneyManagerApplication) getApplication()).getAppComponent().inject(this);

        if (preferenceUtil.getUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}