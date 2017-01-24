package com.dainavahood.workoutlogger.workouts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dainavahood.workoutlogger.MainActivity;
import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.db.WorkoutsDataSource;
import com.dainavahood.workoutlogger.exercises.ExerciseGroupActivity;
import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.extras.ExitAppDialog;
import com.dainavahood.workoutlogger.history.WorkoutsHistoryListActivity;
import com.dainavahood.workoutlogger.model.Workout;

import java.util.List;

public class WorkoutsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String WORKOUT_ACTIVITY = "WORKOUT_ACTIVITY";

    private Workout workout;
    private List<Workout> workouts;
    private ArrayAdapter<Workout> adapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private WorkoutsDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataSource = new WorkoutsDataSource(this);
        dataSource.open();

        ListView lv = refreshListView();
        itemClick(lv);
        itemLongClick(lv);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkoutsActivity.this, CreateWorkoutActivity.class);
                startActivity(intent);
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void itemClick(ListView lv) {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                workout = (Workout) parent.getItemAtPosition(position);

                Intent intent = new Intent(WorkoutsActivity.this, CreateWorkoutActivity.class);
                intent.putExtra(Constants.WORKOUT_PACKAGE_NAME, workout);
                intent.putExtra(WORKOUT_ACTIVITY, Constants.WORKOUT_ACTIVITY);

                startActivity(intent);

            }
        });
    }

    private void itemLongClick(ListView lv) {
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                workout = (Workout) parent.getItemAtPosition(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(WorkoutsActivity.this);
                alert.setTitle("Confirm delete");
                alert.setMessage("Delete " + workout.getName() + " workout? " +
                        "\nAll set groups will be deleted");

                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataSource.removeWorkout(workout);
                        refreshListView();
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();

                return true;
            }
        });
    }


    //Atnajint list view
    private ListView refreshListView() {
        workouts = dataSource.findAllWorkouts();
        adapter = new ArrayAdapter<Workout>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, workouts);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
        return lv;
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

            case R.id.nav_home:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.nav_history:
                Intent intent2 = new Intent(this, WorkoutsHistoryListActivity.class);
                startActivity(intent2);
                finish();
                break;

            case R.id.nav_exercises:
                Intent intent3 = new Intent(this, ExerciseGroupActivity.class);
                startActivity(intent3);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
        refreshListView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }
}
