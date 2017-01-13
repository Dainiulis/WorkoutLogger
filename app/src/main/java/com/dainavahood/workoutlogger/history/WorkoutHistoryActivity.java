package com.dainavahood.workoutlogger.history;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.db.WorkoutsDataSource;
import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.extras.CustomExpandableListView;
import com.dainavahood.workoutlogger.extras.CustomSetsLayoutAdapter;
import com.dainavahood.workoutlogger.model.Set;
import com.dainavahood.workoutlogger.model.Workout;

import java.util.List;

public class WorkoutHistoryActivity extends AppCompatActivity {

    private WorkoutsDataSource dataSource;
    private Bundle bundle;
    private Workout workout;
    private List<Set> sets;
    private CustomExpandableListView adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataSource = new WorkoutsDataSource(this);
        dataSource.open();

        bundle = getIntent().getExtras();
        workout = bundle.getParcelable(Constants.WORKOUT_PACKAGE_NAME);
        if (workout != null) {
            setTitle(workout.getName());
        }
        ExpandableListView lv = refreshListView();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private ExpandableListView refreshListView() {
        sets = dataSource.findAllSetsHistory(workout);
        adapter = new CustomExpandableListView(this, sets);
        ExpandableListView lv = (ExpandableListView) findViewById(R.id.expandableListView);
        lv.setIndicatorBounds(0, 20);
        lv.setAdapter(adapter);
        return lv;
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
