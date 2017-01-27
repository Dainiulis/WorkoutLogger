package com.dainavahood.workoutlogger;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.dainavahood.workoutlogger.db.DatabaseContract;
import com.dainavahood.workoutlogger.db.WorkoutsDataSource;
import com.dainavahood.workoutlogger.exercises.ExerciseGroupActivity;
import com.dainavahood.workoutlogger.extras.ExitAppDialog;
import com.dainavahood.workoutlogger.history.WorkoutsHistoryListActivity;
import com.dainavahood.workoutlogger.workouts.WorkoutsActivity;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout mainLayout;
    private CoordinatorLayout coordinatorLayout;
    private String selectedBackup;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private static final int PERMISSIONS_REQUEST_W_STORAGE = 11;
    private static final int PERMISSIONS_REQUEST_R_STORAGE = 12;
    private static final String LOGTAG = "WORKOUTLOGGER";
    WorkoutsDataSource workoutsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        workoutsDataSource = new WorkoutsDataSource(this);
        workoutsDataSource.open();
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_R_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadBackUp();
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                }
        }
        if (requestCode == PERMISSIONS_REQUEST_W_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveBackUp();
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Toast.makeText(MainActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_save_backup:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_W_STORAGE);
                } else {
                    saveBackUp();
                }
                break;

            case R.id.action_load_backup:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_R_STORAGE);
                } else {
                    loadBackUp();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadBackUp() {
        layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final ViewGroup container2 = (ViewGroup) layoutInflater.inflate(R.layout.load_backup, null);
        popupWindow = new PopupWindow(container2, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        Button button2 = (Button) container2.findViewById(R.id.loadBackup);
        String path = Environment.getExternalStorageDirectory().toString() + "/workoutLoggerBackup";
        File f = new File(path);
        File file[] = f.listFiles();
        List<String> files = new ArrayList<>();
        if (file != null) {
            for (int i = 0; i < file.length; i++) {
                files.add(file[i].getName().toString());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                android.R.id.text1, files);

        Spinner elv = (Spinner) container2.findViewById(R.id.backupSpinner);
        elv.setAdapter(adapter);

        elv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBackup = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDatabase(selectedBackup);
                Toast.makeText(MainActivity.this, "Database " + selectedBackup + " loaded successfully", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
    }

    private void saveBackUp() {
        layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.save_backup, null);
        popupWindow = new PopupWindow(container, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        Button button = (Button) container.findViewById(R.id.saveButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) container.findViewById(R.id.backupName);
                String backupName = et.getText().toString();
                saveDatabase(backupName + ".db");
                Toast.makeText(MainActivity.this, "Database " + backupName + " saved successfully", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
    }

    public void saveDatabase(String dbName) {
        try {
            File extFolder = new File(Environment.getExternalStorageDirectory(), "workoutLoggerBackup");
            if (!extFolder.exists()) {
                extFolder.mkdirs();
            }

            File databaseStorage = getDatabasePath(DatabaseContract.DATABASE_NAME);

            if (extFolder.canWrite()) {
                File currentDB = new File(databaseStorage.toString());
                File backupDB = new File(extFolder, dbName);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {}
    }

    public void loadDatabase(String dbName) {
        try {
            File extFolder = new File(Environment.getExternalStorageDirectory(), "workoutLoggerBackup");
            if (!extFolder.exists()) {
                extFolder.mkdirs();
            }
            File databaseStorage = getDatabasePath(DatabaseContract.DATABASE_NAME);

            if (extFolder.canRead()) {
                File currentDB = new File(databaseStorage.toString());
                File backupDB = new File(extFolder, dbName);

                if (backupDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(currentDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {}
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            ExitAppDialog exitAppDialog = new ExitAppDialog(this);
            exitAppDialog.show();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case R.id.nav_workouts:
                Intent intent1 = new Intent(this, WorkoutsActivity.class);
                startActivity(intent1);
                finish();
                overridePendingTransition(0, 0);
                break;

            case R.id.nav_history:
                Intent intent2 = new Intent(this, WorkoutsHistoryListActivity.class);
                startActivity(intent2);
                finish();
                overridePendingTransition(0, 0);
                break;

            case R.id.nav_exercises:
                Intent intent3 = new Intent(this, ExerciseGroupActivity.class);
                startActivity(intent3);
                finish();
                overridePendingTransition(0, 0);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        workoutsDataSource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        workoutsDataSource.open();
    }
}
