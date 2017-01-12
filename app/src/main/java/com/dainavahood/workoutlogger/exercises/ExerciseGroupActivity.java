package com.dainavahood.workoutlogger.exercises;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.db.ExerciseGroupDataSource;
import com.dainavahood.workoutlogger.db.ExercisesDataSource;
import com.dainavahood.workoutlogger.extras.ExerciseGroupXMLPullParser;
import com.dainavahood.workoutlogger.extras.ExercisesXMLPullParser;
import com.dainavahood.workoutlogger.model.Exercise;
import com.dainavahood.workoutlogger.workouts.CreateSetGroupActivity;
import com.dainavahood.workoutlogger.workouts.SetDetailsActivity;
import com.dainavahood.workoutlogger.workouts.WorkoutLOGActivity;

import java.util.ArrayList;
import java.util.List;

public class ExerciseGroupActivity extends AppCompatActivity {

    public static final String MUSCLE_GROUP = "MUSCLE_GROUP";

    private static final int DETAIL_REQUEST = 3001;
    public static final String RETURN_NAME = "RETURN_NAME";

    private ExerciseGroupDataSource exerciseGroupDataSource;
    private ExercisesDataSource exercisesDataSource;
    private ListView lv;
    private List<Exercise> exercises;
    private List<Exercise> exercisesToRemove = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<Exercise> adapter1;
    private android.widget.SearchView sv;
    private List<String> exercisesGroup;
    private String filteredText;
    private List<String> selectedExercisesGroups = new ArrayList<>();
    private ActionMode actionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        exerciseGroupDataSource = new ExerciseGroupDataSource(this);
        exerciseGroupDataSource.open();

        exercisesDataSource = new ExercisesDataSource(this);
        exercisesDataSource.open();

        exercisesGroup = exerciseGroupDataSource.findAll();
        exercises = exercisesDataSource.findAll();
        if (exercisesGroup.size() == 0) {
            createData();
            exercisesGroup = exerciseGroupDataSource.findAll();
        }
        lv = (ListView) findViewById(R.id.exerciseGroupListView);
        sv = (android.widget.SearchView) findViewById(R.id.searchView1);
        refreshListView();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//              Patikrinu is kurio activity ateinu ir kokio tipo arrayadapter objektai ir pagal tai atlieku atitinkamus veiksmus!!!!!!!!!
                if (
                        parent.getItemAtPosition(position).getClass().equals(Exercise.class) &&
                                getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY ||
                        parent.getItemAtPosition(position).getClass().equals(Exercise.class) &&
                                getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0) == Constants.WORKOUT_LOG_ACTIVITY
                        ) {
                    Exercise exercise = (Exercise) parent.getItemAtPosition(position);
                    Intent intent = new Intent(ExerciseGroupActivity.this, SetDetailsActivity.class);
                    if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY) {
                        intent.putExtra(CreateSetGroupActivity.CALLING_ACTIVITY, Constants.CREATE_SET_GROUP_ACTIVITY);
                    } else {
                        intent.putExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, Constants.WORKOUT_LOG_ACTIVITY);
                    }
                    intent.putExtra(Constants.EXERCISE_PACKAGE_NAME, exercise);
                    intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);
                    finish();

                } else if (
                        parent.getItemAtPosition(position).getClass().equals(String.class) &&
                                getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY ||
                        parent.getItemAtPosition(position).getClass().equals(String.class) &&
                                getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0) == Constants.WORKOUT_LOG_ACTIVITY
                        ) {
                    String muscleGroup = (String) parent.getItemAtPosition(position);

                    Intent intent = new Intent(ExerciseGroupActivity.this, ExercisesActivity.class);
                    if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY) {
                        intent.putExtra(CreateSetGroupActivity.CALLING_ACTIVITY, Constants.CREATE_SET_GROUP_ACTIVITY);
                    } else {
                        intent.putExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, Constants.WORKOUT_LOG_ACTIVITY);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    intent.putExtra(MUSCLE_GROUP, muscleGroup);
                    startActivity(intent);
                    finish();

                } else if (parent.getItemAtPosition(position).getClass().equals(String.class)) {

                    Intent intent = new Intent(ExerciseGroupActivity.this, ExercisesActivity.class);
                    String muscleGroup = (String) parent.getItemAtPosition(position);
                    intent.putExtra(MUSCLE_GROUP, muscleGroup);
                    startActivity(intent);

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
                        mode.setTitle(checkedCount + " selected");
                        break;
                    default:
                        mode.setTitle(checkedCount + " selected");
                        break;
                }
                if (lv.getAdapter().getItem(0).getClass().equals(String.class)) {
                    String exerciseGr = (String) lv.getItemAtPosition(position);
                    if (!checked) {
                        selectedExercisesGroups.remove(exerciseGr);
                    } else {
                        selectedExercisesGroups.add(exerciseGr);
                    }
                } else {
                    Exercise exercise = (Exercise) lv.getItemAtPosition(position);
                    if (!checked) {
                        exercisesToRemove.remove(exercise);
                    } else {
                        exercisesToRemove.add(exercise);
                    }
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                actionMode = mode;
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
                        if (lv.getAdapter().getItem(0).getClass().equals(String.class)) {
                            for (String exerciseGroup : selectedExercisesGroups) {
                                exerciseGroupDataSource.removeExerciseGroup(exerciseGroup);
                                exercisesDataSource.removeExercisesWithGroup(exerciseGroup);
                            }
                        } else {
                            for (Exercise exercise : exercisesToRemove) {
                                Toast.makeText(ExerciseGroupActivity.this, String.valueOf(exercise.getId()), Toast.LENGTH_SHORT).show();
                                exercisesDataSource.removeExercise(exercise);
                                adapter1.remove(exercise);
                            }
                        }
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                exercises = exercisesDataSource.findAll();
                selectedExercisesGroups.clear();
                exercisesToRemove.clear();
                if (lv.getAdapter().getItem(0).getClass().equals(String.class)) {
                    exercisesGroup = exerciseGroupDataSource.findAll();
                    refreshListView();
                }
                adapter1.notifyDataSetChanged();
                adapter1.getFilter().filter(filteredText);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExerciseGroupActivity.this, CreateExerciseGroupActivity.class);
                startActivityForResult(intent, DETAIL_REQUEST);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @NonNull
    private void refreshListView() {
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1, exercisesGroup);
        lv.setAdapter(adapter);
        adapter1 = new ArrayAdapter<Exercise>(ExerciseGroupActivity.this,
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1, exercises);
        sv.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (selectedExercisesGroups.size() > 0 || exercisesToRemove.size() > 0) {
                    actionMode.finish();
                    selectedExercisesGroups.clear();
                    exercisesToRemove.clear();
                }
                if (newText.isEmpty()) {
                    lv.setAdapter(adapter);
                } else {
                    lv.setAdapter(adapter1);
                    filteredText = newText;
                    adapter1.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == DETAIL_REQUEST) {
            if (resultCode == RESULT_OK) {
                String groupName = data.getStringExtra(RETURN_NAME);
                exercisesDataSource.open();
                exerciseGroupDataSource.open();
                exerciseGroupDataSource.create(groupName);
                exercisesGroup = exerciseGroupDataSource.findAll();
                adapter.notifyDataSetChanged();
                refreshListView();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        exerciseGroupDataSource.open();
        exercisesDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        exerciseGroupDataSource.close();
        exercisesDataSource.close();
    }

    public void createData() {
        ExerciseGroupXMLPullParser parser = new ExerciseGroupXMLPullParser();
        List<String> exerciseGroups = parser.parseXML(this);
        ExercisesXMLPullParser parser1 = new ExercisesXMLPullParser();
        List<Exercise> exercises = parser1.parseXML(this);

        for (Exercise exercise : exercises) {
            exercisesDataSource.create(exercise);
        }
        for (String groupName : exerciseGroups) {
            exerciseGroupDataSource.create(groupName);
        }
    }

}
