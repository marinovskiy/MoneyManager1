package com.example.alex.moneymanager.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alex.moneymanager.R;
import com.example.alex.moneymanager.application.MoneyManagerApplication;
import com.example.alex.moneymanager.utils.ModelConverter;
import com.example.alex.moneymanager.utils.PreferenceUtil;
import com.example.alex.moneymanager.utils.SystemUtils;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;

    @Inject
    SystemUtils systemUtils;
    @Inject
    PreferenceUtil preferenceUtil;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((MoneyManagerApplication) getApplication()).getAppComponent().inject(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = fAuth -> {
            if (fAuth.getCurrentUser() != null) {
                Log.d(TAG, "onCreate: " + fAuth.getCurrentUser().toString());

                preferenceUtil.setUser(ModelConverter.convertFirebaseUserToUser(
                        fAuth.getCurrentUser())
                );

                startMainActivity();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @OnClick({R.id.btn_sign_in, R.id.tv_sign_up})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                if (systemUtils.isConnected()) {
                    if (isValid()) {
                        progressDialog.show();

                        signIn();
                    } else {
                        Toast.makeText(this, "Data is not valid", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_sign_up:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void signIn() {
        firebaseAuth.signInWithEmailAndPassword(getEmail(), getPassword())
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();

                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Wrong email/password", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "signIn: error: " + task.getException());
                    }
                });
    }

    private boolean isValid() {
        boolean isEmailValid = !getEmail().isEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches();
        boolean isPasswordValid = !getPassword().isEmpty();

        return isEmailValid && isPasswordValid;
    }

    private String getEmail() {
        return etEmail.getText().toString().trim();
    }

    private String getPassword() {
        return etPassword.getText().toString().trim();
    }
}