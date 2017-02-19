package com.example.alex.moneymanager.di.modules;

import android.content.Context;

import com.example.alex.moneymanager.db.RealmManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RealmModule {

    @Provides
    @Singleton
    RealmManager provideRealmManager(Context context) {
        return new RealmManager(context);
    }
}