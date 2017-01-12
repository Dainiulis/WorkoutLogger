package com.dainavahood.workoutlogger.exercises;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.db.ExerciseGroupDataSource;
import com.dainavahood.workoutlogger.db.ExercisesDataSource;
import com.dainavahood.workoutlogger.model.Exercise;
import com.dainavahood.workoutlogger.workouts.CreateSetGroupActivity;
import com.dainavahood.workoutlogger.workouts.WorkoutLOGActivity;

import java.util.List;

public class CreateExerciseActivity extends AppCompatActivity {

    public static final String MUSCLE_GROUP = "MUSCLE_GROUP";

    protected String selectedTrType;
    protected String selectedExerciseGroup;

    ExercisesDataSource exercisesDataSource;
    ExerciseGroupDataSource exerciseGroupDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //inicijuoja duombazes padejeja DatabaseHelper Sukuria/gauna duombaze
        exercisesDataSource = new ExercisesDataSource(this);
        exercisesDataSource.open();

        exerciseGroupDataSource = new ExerciseGroupDataSource(this);
        exerciseGroupDataSource.open();

        //Koki muscleGroup pasirinko
        final String muscleGroup = getIntent().getStringExtra(ExercisesActivity.MUSCLE_GROUP);

        //drop down listas training type
        final String[] trType = getResources().getStringArray(R.array.training_type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                android.R.id.text1, trType);
        Spinner elv = (Spinner) findViewById(R.id.spinner);
        elv.setAdapter(adapter);

        //gaunamas pasirinktas training type is dropdown listo
        elv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTrType = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //drop down listas muscle group
//        final String[] muscleType = getResources().getStringArray(R.array.muscle_groups);
        List<String> muscleType = exerciseGroupDataSource.findAll();
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                android.R.id.text1, muscleType);
        Spinner elv2 = (Spinner) findViewById(R.id.spinner2);
        elv2.setAdapter(adapter2);
        //parenkamas pagal muscle group
        int spinnerPosition = adapter2.getPosition(muscleGroup);
        elv2.setSelection(spinnerPosition);

        //gaunamas pasirinktas training type is dropdown listo
        elv2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedExerciseGroup = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Save mygtukas ir veiksmai ji nuspaudus
        Button save = (Button) findViewById(R.id.saveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //gaunamas ivestas Exercise name
                EditText et = (EditText) findViewById(R.id.editExerciseName);
                String exName = et.getText().toString().trim();

                Exercise exercise = new Exercise();
                if(selectedTrType.equals("Sets/Time")){
                    exercise.setName(exName + " (time)");
                } else {
                    exercise.setName(exName);
                }
                exercise.setExerciseGroup(selectedExerciseGroup);
                exercise.setExerciseType(selectedTrType);

                List<Exercise> exercisesFromMuscleGroup = exercisesDataSource.findAll();

                //patikrinama ar ivestas Exercise name ir ar yra toks exercise duomenu bazej

                if (exName.isEmpty()) {
                    Toast.makeText(CreateExerciseActivity.this, "Enter exercise name", Toast.LENGTH_LONG).show();
                } else if (checkExercisesList(exercise).getResult() && exercisesFromMuscleGroup.size() > 0) {
                    Toast.makeText(CreateExerciseActivity.this, exercise.getName() + " exercise already exists in " +
                            checkExercisesList(exercise).getExercisesGroup() + " group", Toast.LENGTH_LONG).show();
                } else {

                    exercisesDataSource.create(exercise);

                    //finish activity and start previous activity again
                    Intent intent = new Intent(CreateExerciseActivity.this, ExercisesActivity.class);
                    intent.putExtra(MUSCLE_GROUP, selectedExerciseGroup);
                    if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY){
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        intent.putExtra(CreateSetGroupActivity.CALLING_ACTIVITY, Constants.CREATE_SET_GROUP_ACTIVITY);
                    }  else if (getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0) == Constants.WORKOUT_LOG_ACTIVITY) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        intent.putExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, Constants.WORKOUT_LOG_ACTIVITY);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        exercisesDataSource.open();
        exerciseGroupDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        exercisesDataSource.close();
        exerciseGroupDataSource.close();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateExerciseActivity.this, ExercisesActivity.class);
        final String muscleGroup = getIntent().getStringExtra(ExercisesActivity.MUSCLE_GROUP);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //patikrina ar yra toks Exercise duomenu bazej
    public CheckExercisesListResult checkExercisesList(Exercise exercise){

        List<Exercise> exercisesFromMuscleGroup = exercisesDataSource.findAll();


        for (Exercise exer : exercisesFromMuscleGroup) {
            if(exercise.getName().toLowerCase().equals(exer.getName().toLowerCase())) {
                return new CheckExercisesListResult(true, exer.getExerciseGroup());
            }
        }
        return new CheckExercisesListResult(false, null);
    }

    //klase, kad gauciau tiek patikrinima ar toks exercise yra, tiek jo group
    final class CheckExercisesListResult {
        private final boolean result;
        private final String exercisesGroup;

        CheckExercisesListResult(boolean result, String exercisesGroup) {
            this.result = result;
            this.exercisesGroup = exercisesGroup;
        }

        public boolean getResult() {
            return result;
        }

        public String getExercisesGroup() {
            return exercisesGroup;
        }
    }

}
