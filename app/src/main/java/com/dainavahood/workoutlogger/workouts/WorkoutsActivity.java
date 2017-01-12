package com.dainavahood.workoutlogger.workouts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.db.WorkoutsDataSource;
import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.model.Workout;

import java.util.List;

public class WorkoutsActivity extends AppCompatActivity {

    public static final String WORKOUT_ACTIVITY = "WORKOUT_ACTIVITY";

    private Workout workout;
    private List<Workout> workouts;
    private ArrayAdapter<Workout> adapter;

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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkoutsActivity.this, CreateWorkoutActivity.class);
                startActivity(intent);
            }
        });



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
