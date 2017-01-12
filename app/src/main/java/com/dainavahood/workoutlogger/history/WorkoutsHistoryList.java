package com.dainavahood.workoutlogger.history;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.db.WorkoutsDataSource;
import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.extras.CustomWorkoutsHistoryLayoutAdapter;
import com.dainavahood.workoutlogger.extras.DateDialog;
import com.dainavahood.workoutlogger.model.Workout;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WorkoutsHistoryList extends AppCompatActivity {

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
                Intent intent = new Intent(WorkoutsHistoryList.this, WorkoutHistory.class);
                intent.putExtra(Constants.WORKOUT_PACKAGE_NAME, clickedWorkout);
                startActivity(intent);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Workout deleteWorkout = (Workout) parent.getItemAtPosition(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(WorkoutsHistoryList.this);
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
