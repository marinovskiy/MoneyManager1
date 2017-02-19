package com.example.alex.moneymanager.activities;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import com.example.alex.moneymanager.entities.User;
import com.example.alex.moneymanager.utils.PreferenceUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {

    @Inject
    PreferenceUtil preferenceUtil;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    protected void saveUserToFirebase(User user) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("users").child(user.getId()).setValue(user);
    }

    protected void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    protected void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}