package com.example.alex.moneymanager.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.example.alex.moneymanager.R;
import com.example.alex.moneymanager.application.MoneyManagerApplication;
import com.example.alex.moneymanager.db.RealmManager;
import com.example.alex.moneymanager.entities.Category;
import com.example.alex.moneymanager.entities.Operation;
import com.example.alex.moneymanager.utils.SystemUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.RealmResults;

public class AddOperationActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar_add_operation)
    Toolbar toolbar;
    @BindView(R.id.et_sum)
    EditText etSum;
    @BindView(R.id.et_description)
    EditText etDescription;
    @BindView(R.id.spinner_category_incomes)
    Spinner spinnerCategoryIncomes;
    @BindView(R.id.spinner_category_expenses)
    Spinner spinnerCategoryExpenses;
    @BindView(R.id.rg_operation_type)
    RadioGroup radioGroup;
    @BindView(R.id.rb_incomes)
    RadioButton rbIncomes;
    @BindView(R.id.rb_expenses)
    RadioButton rbExpenses;

    @Inject
    SystemUtils systemUtils;
    @Inject
    RealmManager realmManager;

    private String type;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_operation);
        ((MoneyManagerApplication) getApplication()).getAppComponent().inject(this);

        setupToolbar();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        rbIncomes.setChecked(true);
        rbIncomes.setOnClickListener(this);
        rbExpenses.setOnClickListener(this);

        type = "income";

        RealmResults<Category> categoriesIncome = realmManager.getRealm()
                .where(Category.class)
                .equalTo("type", "income")
                .findAll();

        RealmResults<Category> categoriesExpense = realmManager.getRealm()
                .where(Category.class)
                .equalTo("type", "expense")
                .findAll();

        List<Category> incomes = realmManager.getRealm().copyFromRealm(categoriesIncome);
        List<Category> expenses = realmManager.getRealm().copyFromRealm(categoriesExpense);

        String[] incomesData = new String[incomes.size()];
        String[] expensesData = new String[expenses.size()];

        for (int i = 0; i < incomes.size(); i++) {
            incomesData[i] = incomes.get(i).getName();
        }
        for (int i = 0; i < expenses.size(); i++) {
            expensesData[i] = expenses.get(i).getName();
        }

        ArrayAdapter<String> adapterIncomes = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                incomesData
        );
        ArrayAdapter<String> adapterExpenses = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                expensesData
        );
        adapterIncomes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterExpenses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategoryIncomes.setAdapter(adapterIncomes);
        spinnerCategoryExpenses.setAdapter(adapterExpenses);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_incomes:
                type = "income";

                spinnerCategoryIncomes.setVisibility(View.VISIBLE);
                spinnerCategoryExpenses.setVisibility(View.GONE);
                break;
            case R.id.rb_expenses:
                type = "expense";

                spinnerCategoryIncomes.setVisibility(View.GONE);
                spinnerCategoryExpenses.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_add_operation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            case R.id.action_done:
                if (systemUtils.isConnected()) {
                    if (isValid()) {
                        addOperation();
                    } else {
                        Toast.makeText(this, "Data is not valid", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    private boolean isValid() {
        boolean isSumValid = !getSum().isEmpty();
        boolean isDescriptionValid = !getDescription().isEmpty();

        return isSumValid && isDescriptionValid;
    }

    private String getSum() {
        return etSum.getText().toString().trim();
    }

    private String getDescription() {
        return etDescription.getText().toString().trim();
    }

    private void addOperation() {
        progressDialog.show();

        Operation newOperation = new Operation();
        newOperation.setSum(Double.parseDouble(getSum()));
        newOperation.setDescription(getDescription());
        newOperation.setType(type);
        if (type.equals("income")) {
            newOperation.setCategory(spinnerCategoryIncomes.getSelectedItem().toString());
        } else if (type.equals("expense")) {
            newOperation.setCategory(spinnerCategoryExpenses.getSelectedItem().toString());
        }
        newOperation.setCreatedAt(System.currentTimeMillis());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        String newOperationKey = databaseReference.child("users")
                .child(preferenceUtil.getUser().getId())
                .child("operations")
                .push()
                .getKey();

        newOperation.setId(newOperationKey);
        Map<String, Object> operationValues = newOperation.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("users/" + preferenceUtil.getUser().getId() + "/operations/" + newOperationKey, operationValues);

        databaseReference.updateChildren(childUpdates)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();

                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                });
    }
}