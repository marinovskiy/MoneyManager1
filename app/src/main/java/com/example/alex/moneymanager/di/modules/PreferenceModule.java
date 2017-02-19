package com.example.alex.moneymanager.di.modules;

import android.content.Context;

import com.example.alex.moneymanager.utils.PreferenceUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PreferenceModule {

    @Provides
    @Singleton
    PreferenceUtil providePreferenceUtil(Context context) {
        return new PreferenceUtil(context);
    }
}