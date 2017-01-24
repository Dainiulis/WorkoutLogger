package com.dainavahood.workoutlogger.exercises;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dainavahood.workoutlogger.MainActivity;
import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.db.ExercisesDataSource;
import com.dainavahood.workoutlogger.history.WorkoutsHistoryListActivity;
import com.dainavahood.workoutlogger.model.Exercise;
import com.dainavahood.workoutlogger.workouts.CreateSetGroupActivity;
import com.dainavahood.workoutlogger.workouts.SetDetailsActivity;
import com.dainavahood.workoutlogger.workouts.WorkoutLOGActivity;
import com.dainavahood.workoutlogger.workouts.WorkoutsActivity;

import java.util.ArrayList;
import java.util.List;

public class ExercisesActivity extends AppCompatActivity {

    public static final String MUSCLE_GROUP = "MUSCLE_GROUP";

    ExercisesDataSource exercisesDataSource;
    private ArrayAdapter<Exercise> adapter;
    private String filteredText;
    private ListView lv;
    private List<Exercise> exercisesToRemove = new ArrayList<>();
    private List<Exercise> exercises = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Helper inicijavimas ir duombazes sukurimas/paleidimas
        exercisesDataSource = new ExercisesDataSource(this);
        exercisesDataSource.open();
        lv = (ListView) findViewById(R.id.exercisesListView);

        //Koki muscleGroup pasirinko
        final String muscleGroup = getIntent().getStringExtra(ExerciseGroupActivity.MUSCLE_GROUP);
        //pakeiciu title
        setTitle(muscleGroup);

        //Muscle group is kurio gauti duomenis is duomenubazes

        exercises = exercisesDataSource.findByMuscleGroup(muscleGroup);
        lv = refreshListView(exercises);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY ||
                        getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0 ) == Constants.WORKOUT_LOG_ACTIVITY) {
                    Exercise exercise = (Exercise) parent.getItemAtPosition(position);
                    Intent intent = new Intent(ExercisesActivity.this, SetDetailsActivity.class);
                    if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY) {
                        intent.putExtra(CreateSetGroupActivity.CALLING_ACTIVITY, Constants.CREATE_SET_GROUP_ACTIVITY);
                    } else {
                        intent.putExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, Constants.WORKOUT_LOG_ACTIVITY);
                    }
                    intent.putExtra(Constants.EXERCISE_PACKAGE_NAME, exercise);
                    intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);
                    finish();
                }
            }
        });

        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = lv.getCheckedItemCount();
                switch (checkedCount) {
                    case 1:
                        mode.setTitle(checkedCount + " exercise selected");
                        break;
                    default:
                        mode.setTitle(checkedCount + " exercises selected");
                        break;
                }
                Exercise exercise = (Exercise) lv.getItemAtPosition(position);
                if (!checked) {
                    exercisesToRemove.remove(exercise);
                } else {
                    exercisesToRemove.add(exercise);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if (lv.getAdapter().getItem(0).getClass().equals(String.class)) {
                    return false;
                }
                mode.getMenuInflater().inflate(R.menu.delete_multiple, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete:
                        for (Exercise exercise : exercisesToRemove) {
                            exercisesDataSource.removeExercise(exercise);
                            adapter.remove(exercise);
                        }
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                exercises = exercisesDataSource.findAll();
                exercisesToRemove.clear();
                adapter.notifyDataSetChanged();
                adapter.getFilter().filter(filteredText);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExercisesActivity.this, CreateExerciseActivity.class);
                intent.putExtra(MUSCLE_GROUP, muscleGroup);
                if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY){
                    intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    intent.putExtra(CreateSetGroupActivity.CALLING_ACTIVITY, Constants.CREATE_SET_GROUP_ACTIVITY);
                } else if (getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0) == Constants.WORKOUT_LOG_ACTIVITY) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    intent.putExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, Constants.WORKOUT_LOG_ACTIVITY);
                }
                startActivity(intent);
                finish();
            }
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @NonNull
    private ListView refreshListView(List<Exercise> exercises) {
        adapter = new ArrayAdapter<Exercise>(this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1, exercises);
        lv.setAdapter(adapter);

        android.widget.SearchView sv = (android.widget.SearchView) findViewById(R.id.searchView1);
        sv.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredText = newText;
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return lv;
    }

    @Override
    protected void onResume() {
        super.onResume();
        exercisesDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        exercisesDataSource.close();
    }

}
