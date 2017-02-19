package com.example.alex.moneymanager.di.modules;

import android.content.Context;

import com.example.alex.moneymanager.utils.SystemUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SystemUtilsModule {

    @Provides
    @Singleton
    SystemUtils provideSystemUtils(Context context) {
        return new SystemUtils(context);
    }
}