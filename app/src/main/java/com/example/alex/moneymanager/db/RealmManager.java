package com.example.alex.moneymanager.db;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmManager {

    private RealmConfiguration realmConfiguration;

    public RealmManager(Context context) {
        Realm.init(context);
        realmConfiguration = new RealmConfiguration.Builder()
                .name(DbFields.DB_NAME)
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(DbFields.DB_VERSION)
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public void close() {
        getRealm().close();
    }
}