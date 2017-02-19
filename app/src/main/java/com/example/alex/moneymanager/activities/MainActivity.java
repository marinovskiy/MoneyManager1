package com.example.alex.moneymanager.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.moneymanager.R;
import com.example.alex.moneymanager.adapters.OperationExpandableRecyclerViewAdapter;
import com.example.alex.moneymanager.application.MoneyManagerApplication;
import com.example.alex.moneymanager.db.RealmManager;
import com.example.alex.moneymanager.entities.Category;
import com.example.alex.moneymanager.entities.CategoryUi;
import com.example.alex.moneymanager.entities.Operation;
import com.example.alex.moneymanager.utils.PreferenceUtil;
import com.example.alex.moneymanager.utils.SystemUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE = 7776;

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar_main)
    Toolbar toolbar;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    //    @BindView(R.id.vp_operations)
//    ViewPager vpOperations;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.rv_operations)
    RecyclerView rvOperations;

    @Inject
    PreferenceUtil preferenceUtil;
    @Inject
    RealmManager realmManager;
    @Inject
    SystemUtils systemUtils;

    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    private OperationExpandableRecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((MoneyManagerApplication) getApplication()).getAppComponent().inject(this);

        setupToolbar();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeader = navigationView.getHeaderView(0);
        TextView tvUserName = ButterKnife.findById(navigationHeader, R.id.tv_username);
        TextView tvUserMail = ButterKnife.findById(navigationHeader, R.id.tv_useremail);
        tvUserName.setText(preferenceUtil.getUser().getName());
        tvUserMail.setText(preferenceUtil.getUser().getEmail());

        tvBalance.setText(String.format("%s $", preferenceUtil.getBalance()));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Category> categories = new ArrayList<>();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    categories.add(categorySnapshot.getValue(Category.class));
                }
                realmManager.getRealm().executeTransactionAsync(realm -> {
                    realm.copyToRealmOrUpdate(categories);
                }, () -> {
                    loadData();
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @OnClick(R.id.fab)
    public void onClick() {
        Intent intent = new Intent(this, AddOperationActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                Toast.makeText(this, "Operation added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_drawer_logout:
                FirebaseAuth.getInstance().signOut();
                preferenceUtil.setUser(null);
                startLoginActivity();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            default:
                return false;
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
    }

    private void loadData() {
        progressDialog.show();

        if (systemUtils.isConnected()) {
            loadDataFromNetwork();
        } else {
            loadDataFromDb();
        }
    }

    private void loadDataFromNetwork() {
        databaseReference.child("users")
                .child(preferenceUtil.getUser().getId())
                .child("operations")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Operation> operations = new ArrayList<>();
                        for (DataSnapshot operationSnapshot : dataSnapshot.getChildren()) {
                            operations.add(operationSnapshot.getValue(Operation.class));
                        }
                        realmManager.getRealm().executeTransactionAsync(realm -> {
                            realm.copyToRealmOrUpdate(operations);
                        }, () -> {
                            updateUi(operations);
                            progressDialog.dismiss();
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private void loadDataFromDb() {
        RealmResults<Operation> operations = realmManager.getRealm()
                .where(Operation.class)
                .findAll();

        List<Operation> operationList = realmManager.getRealm().copyFromRealm(operations);

        updateUi(operationList);
    }

    private void updateUi(List<Operation> operations) {
        if (rvOperations.getAdapter() == null) {
            rvOperations.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewAdapter = new OperationExpandableRecyclerViewAdapter(convertList(operations));
            rvOperations.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.setOnItemClickListener(this::showPopupMenu);
        } else {
            recyclerViewAdapter.update(convertList(operations));
        }
    }

    private List<Object> convertList(List<Operation> operations) {
        List<Object> objects = new ArrayList<>();
        Set<String> categories = new HashSet<>();
        for (Operation operation : operations) {
            categories.add(operation.getCategory());
        }

        for (String category : categories) {
            List<Operation> operationsForCategory = new ArrayList<>();
            for (Operation operation : operations) {
                if (operation.getCategory().equals(category)) {
                    operationsForCategory.add(operation);
                }
            }
            double sum = 0;
            for (Operation operation : operationsForCategory) {
                sum = sum + operation.getSum();
            }
            objects.add(new CategoryUi(operationsForCategory.get(0).getType(), category, String.valueOf(sum)));
            objects.addAll(operationsForCategory);
        }

        return objects;
    }

    private void showPopupMenu(View v, String operationId) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menu_delete_operation);
        popupMenu.setOnMenuItemClickListener(item -> {
            databaseReference.child("users").child(preferenceUtil.getUser().getId())
                    .child("operations")
                    .child(operationId)
                    .removeValue((databaseError, databaseReference1) -> {
                        loadData();
                    });
            return true;
        });
        popupMenu.show();
    }
}