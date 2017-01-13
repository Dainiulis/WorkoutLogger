package com.dainavahood.workoutlogger.history;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.dainavahood.workoutlogger.MainActivity;
import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.db.WorkoutsDataSource;
import com.dainavahood.workoutlogger.exercises.ExerciseGroupActivity;
import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.extras.CustomWorkoutsHistoryLayoutAdapter;
import com.dainavahood.workoutlogger.extras.DateDialog;
import com.dainavahood.workoutlogger.model.Workout;
import com.dainavahood.workoutlogger.workouts.WorkoutsActivity;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WorkoutsHistoryListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private WorkoutsDataSource dataSource;
    private List<Workout> workouts;
    private CustomWorkoutsHistoryLayoutAdapter adapter;
    private Button all, between;
    private TextView textView;
    private int _year;
    private int _month;
    private int _day;
    private boolean showingAll = true, currentTime = true;
    private String firstDay, lastDay;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calendar = Calendar.getInstance();

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataSource = new WorkoutsDataSource(this);
        dataSource.open();

        getWeek(sdf, 0, currentTime);
        textView = (TextView) findViewById(R.id.date);
        textView.setText(sdf.format(calendar.getTime()));
        all = (Button) findViewById(R.id.all);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workouts = dataSource.findAllWorkoutHistory();
                refreshListView();
                showingAll = true;
            }
        });
        between = (Button) findViewById(R.id.between);
        between.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workouts = dataSource.findWorkoutHistoryOfThatWeek(firstDay,lastDay);
                refreshListView();
            }
        });

        workouts = dataSource.findAllWorkoutHistory();
        ListView lv = refreshListView();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Workout clickedWorkout = (Workout) parent.getItemAtPosition(position);
                Intent intent = new Intent(WorkoutsHistoryListActivity.this, WorkoutHistoryActivity.class);
                intent.putExtra(Constants.WORKOUT_PACKAGE_NAME, clickedWorkout);
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Workout deleteWorkout = (Workout) parent.getItemAtPosition(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(WorkoutsHistoryListActivity.this);
                alert.setTitle("Delete this workout history");
                alert.setMessage("Do you want to delete this workout history?");

                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataSource.removeWorkoutHistory(deleteWorkout);
                        workouts = dataSource.findAllWorkoutHistory();
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

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private ListView refreshListView() {
        adapter = new CustomWorkoutsHistoryLayoutAdapter(this, workouts);
        ListView lv = (ListView) findViewById(R.id.workoutsHistoryListView);
        lv.setAdapter(adapter);
        return lv;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pick_date_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.pick_date:
                showDateDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDateDialog() {
        DateDialog dateDialog = new DateDialog();

        if (showingAll) {
            Calendar calendar = Calendar.getInstance();
            _year = calendar.get(Calendar.YEAR);
            _month = calendar.get(Calendar.MONTH);
            _day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        Bundle bundle = new Bundle();
        bundle.putInt("year", _year);
        bundle.putInt("month", _month);
        bundle.putInt("day", _day);
        dateDialog.setArguments(bundle);

        dateDialog.setCallBack(onDateSetListener);
        dateDialog.show(getFragmentManager(), "DatePicker");
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear += 1;
            String date = year + "-" + monthOfYear + "-" + dayOfMonth;

            try {
                Date d = sdf.parse(date);
                long millisec = d.getTime();
                String selectedDate = getDate(millisec);
                textView.setText(selectedDate);
                workouts = dataSource.findWorkoutHistoryByDate(selectedDate);
                refreshListView();
                _year = year;
                _month = monthOfYear - 1;
                _day = dayOfMonth;
                showingAll = false;

                getWeek(sdf, millisec, !currentTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    private void getWeek(SimpleDateFormat sdf, long millisec, boolean currentTime) {
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        if (!currentTime) {
            calendar.setTimeInMillis(millisec);
        }
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        firstDay = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, 6);
        lastDay = sdf.format(calendar.getTime());
    }

    private String getDate(long unixSeconds) throws ParseException {
        Timestamp timestamp = new Timestamp(unixSeconds);
        String formattedDate = sdf.format(timestamp);
        return formattedDate;
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

            case R.id.nav_workouts:
                Intent intent2 = new Intent(this, WorkoutsActivity.class);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }
}
