package com.dainavahood.workoutlogger.workouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.model.Exercise;
import com.dainavahood.workoutlogger.model.Set;

public class SetDetailsActivity extends AppCompatActivity {

    public static final String HOW_MANY_TO_ADD = "HOW_MANY_TO_ADD";
    Exercise exercise;
    Set set;
    Bundle bundle;
    EditText etReps, etWeight, etRest, etHowMany;
    Button minus1, minus2, minus3, plus1, plus2, plus3, save, plus4, minus4;
    int intValue;
    double doubleValue;
    Boolean timeForReps = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bundle = getIntent().getExtras();
        if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY ||
                getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0) == Constants.WORKOUT_LOG_ACTIVITY){
            exercise = bundle.getParcelable(Constants.EXERCISE_PACKAGE_NAME);
        } else {
            set = bundle.getParcelable(Constants.SET_PACKAGE_NAME);
        }

        TextView tv = (TextView) findViewById(R.id.exerciseName);
        if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY ||
                getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0) == Constants.WORKOUT_LOG_ACTIVITY){
            tv.setText(exercise.getName());
        } else {
            tv.setText(set.getExerciseName());
        }


//        **************Change reps****************

        TextView tvReps = (TextView) findViewById(R.id.repsName);
        if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY ||
                getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0) == Constants.WORKOUT_LOG_ACTIVITY){
            if (exercise.getExerciseType().equals("Sets/Time")) {
                tvReps.setText(R.string.time);
                timeForReps = true;
            }
        } else {
            if (set.isTime()) {
                tvReps.setText(R.string.time);
                timeForReps = true;
            }
        }

        etReps = (EditText) findViewById(R.id.editReps);
        minus1 = (Button) findViewById(R.id.minus1);
        plus1 = (Button) findViewById(R.id.plus1);

        if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY ||
                getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0) == Constants.WORKOUT_LOG_ACTIVITY){
        } else {
            etReps.setText(String.valueOf(set.getReps()));
        }

        plus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intValue = Integer.parseInt(etReps.getText().toString());
                intValue++;
                etReps.setText(String.valueOf(intValue));
            }
        });

        minus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etVal = etReps.getText().toString();
                if (etVal.equals("")){
                    etVal = "0";
                }
                intValue = Integer.parseInt(etVal);
                if (intValue > 0){
                    intValue--;
                }
                etReps.setText(String.valueOf(intValue));
            }
        });

//        *******************Change Weight*****************************

        etWeight = (EditText) findViewById(R.id.editWeight);
        minus2 = (Button) findViewById(R.id.minus2);
        plus2 = (Button) findViewById(R.id.plus2);

        if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY ||
                getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0) == Constants.WORKOUT_LOG_ACTIVITY){
        } else {
            etWeight.setText(String.valueOf(set.getWeight()));
        }

        plus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doubleValue = Double.parseDouble(etWeight.getText().toString());
                doubleValue = doubleValue + 0.5;
                etWeight.setText(String.valueOf(doubleValue));
            }
        });

        minus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etVal = etWeight.getText().toString();
                if (etVal.equals("")){
                    etVal = "0";
                }
                doubleValue = Double.parseDouble(etVal);
                if (doubleValue > 0){
                    doubleValue = doubleValue - 0.5;
                }
                etWeight.setText(String.valueOf(doubleValue));
            }
        });

//        **************Change rest********************

        etRest = (EditText) findViewById(R.id.editRest);
        minus3 = (Button) findViewById(R.id.minus3);
        plus3 = (Button) findViewById(R.id.plus3);

        if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY ||
                getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0) == Constants.WORKOUT_LOG_ACTIVITY){
        } else {
            etRest.setText(String.valueOf(set.getRest()));
        }


        plus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intValue = Integer.parseInt(etRest.getText().toString());
                intValue++;
                etRest.setText(String.valueOf(intValue));
            }
        });


        minus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etVal = etRest.getText().toString();
                if (etVal.equals("")){
                    etVal = "0";
                }
                intValue = Integer.parseInt(etVal);
                if (intValue > 0){
                    intValue--;
                }
                etRest.setText(String.valueOf(intValue));
            }
        });
//      *********************************************************

        RelativeLayout howManyLayout = (RelativeLayout) findViewById(R.id.howManyLayout);
        howManyLayout.setVisibility(View.GONE);
        if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY){
            howManyLayout.setVisibility(View.VISIBLE);
        }
        etHowMany = (EditText) findViewById(R.id.editHowMany);
        etHowMany.setKeyListener(null);
        minus4 = (Button) findViewById(R.id.minus4);
        plus4 = (Button) findViewById(R.id.plus4);

        plus4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intValue = Integer.parseInt(etHowMany.getText().toString());
                intValue++;
                etHowMany.setText(String.valueOf(intValue));
            }
        });


        minus4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String etVal = etHowMany.getText().toString();
                if (etVal.equals("") || etVal.equals("0")) {
                    etVal = "1";
                }
                intValue = Integer.parseInt(etVal);
                if (intValue > 1) {
                    intValue--;
                }
                etHowMany.setText(String.valueOf(intValue));
            }
        });


        save = (Button) findViewById(R.id.saveSet);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY ||
                        getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0) == Constants.WORKOUT_LOG_ACTIVITY){
                    set = new Set();
                }

                Intent data = new Intent();

                if (getIntent().getIntExtra(CreateSetGroupActivity.CALLING_ACTIVITY, 0) == Constants.CREATE_SET_GROUP_ACTIVITY){
                    set.setExerciseName(exercise.getName());
                    int howManyToAdd = getInt(etHowMany.getText().toString());
                    data.putExtra(HOW_MANY_TO_ADD, howManyToAdd);
                } else if (getIntent().getIntExtra(WorkoutLOGActivity.WORKOUT_LOG_CALLING_ACTIVITY, 0) == Constants.WORKOUT_LOG_ACTIVITY) {
                    set.setExerciseName(exercise.getName());
                }
                else {
                    set.setExerciseName(set.getExerciseName());
                }

                set.setReps(getInt(etReps.getText().toString()));
                set.setWeight(getDouble(etWeight.getText().toString()));
                set.setRest(getInt(etRest.getText().toString()));
                if (timeForReps) {
                    set.setTime(true);
                } else {
                    set.setTime(false);
                }

                data.putExtra(Constants.SET_PACKAGE_NAME, set);
                setResult(RESULT_OK, data);
                finish();
            }
        });

    }

    private int getInt(String value) {
        if (value.equals("")){
            value = "0";
        }
        return Integer.parseInt(value);
    }

    private double getDouble(String value) {
        if (value.equals("")){
            value = "0";
        }
        return Double.parseDouble(value);
    }

}
