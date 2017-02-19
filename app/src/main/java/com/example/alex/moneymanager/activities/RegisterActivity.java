package com.example.alex.moneymanager.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alex.moneymanager.R;
import com.example.alex.moneymanager.application.MoneyManagerApplication;
import com.example.alex.moneymanager.entities.User;
import com.example.alex.moneymanager.utils.ModelConverter;
import com.example.alex.moneymanager.utils.SystemUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    @BindView(R.id.toolbar_register)
    Toolbar toolbar;
    @BindView(R.id.et_first_name)
    EditText etFirstName;
    @BindView(R.id.et_last_name)
    EditText etLastName;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_password_confirm)
    EditText etPasswordConfirm;

    @Inject
    SystemUtils systemUtils;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ((MoneyManagerApplication) getApplication()).getAppComponent().inject(this);

        setupToolbar();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = fAuth -> {
            if (fAuth.getCurrentUser() != null) {
                Log.d(TAG, "onCreate: " + fAuth.getCurrentUser().toString());

                FirebaseUser firebaseUser = fAuth.getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(String.format("%s %s", getFirstName(), getLastName()))
                        .build();

                firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");

                        User user = ModelConverter.convertFirebaseUserToUser(
                                fAuth.getCurrentUser()
                        );

                        preferenceUtil.setUser(user);
                        saveUserToFirebase(user);

                        startMainActivity();
                    }
                });
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @OnClick(R.id.btn_sign_up)
    public void onClick() {
        if (systemUtils.isConnected()) {
            if (isValid()) {
                progressDialog.show();

                signUp();
            } else Toast.makeText(this, "Data is not valid", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    private void signUp() {
        firebaseAuth.createUserWithEmailAndPassword(getEmail(), getPassword())
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();

                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Wrong data", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "signUp: error: " + task.getException());
                    }
                });
    }

    private boolean isValid() {
        boolean isFirstNameValid = !getFirstName().isEmpty();
        boolean isLastNameValid = !getLastName().isEmpty();
        boolean isEmailValid = !getEmail().isEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches();
        boolean isPasswordValid = !getPassword().isEmpty() && !getPasswordConfirmation().isEmpty()
                && getPassword().equals(getPasswordConfirmation());

        return isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid;
    }

    private String getFirstName() {
        return etFirstName.getText().toString().trim();
    }

    private String getLastName() {
        return etLastName.getText().toString().trim();
    }

    private String getEmail() {
        return etEmail.getText().toString().trim();
    }

    private String getPassword() {
        return etPassword.getText().toString().trim();
    }

    private String getPasswordConfirmation() {
        return etPasswordConfirm.getText().toString().trim();
    }
}