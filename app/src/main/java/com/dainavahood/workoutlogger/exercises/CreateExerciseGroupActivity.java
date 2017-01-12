package com.dainavahood.workoutlogger.exercises;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.db.ExerciseGroupDataSource;

import java.util.List;

public class CreateExerciseGroupActivity extends Activity {

    ExerciseGroupDataSource exerciseGroupDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exercise_group);

        exerciseGroupDataSource = new ExerciseGroupDataSource(this);
        exerciseGroupDataSource.open();

        Button button = (Button) findViewById(R.id.saveButton2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText et = (EditText) findViewById(R.id.setGroupName);
                String groupName = et.getText().toString().trim();

                List<String> allExerciseGroups = exerciseGroupDataSource.findAll();

                if (groupName.isEmpty()) {
                    Toast.makeText(CreateExerciseGroupActivity.this, "Please enter group name", Toast.LENGTH_SHORT).show();
                } else if (checkGroupExist(groupName, allExerciseGroups)) {
                    Toast.makeText(CreateExerciseGroupActivity.this, groupName + " already exists", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent data = new Intent();
                    data.putExtra(ExerciseGroupActivity.RETURN_NAME, groupName);
                    setResult(RESULT_OK, data);
                    finish();
                }


            }
        });
    }

    private boolean checkGroupExist(String groupName, List<String> allExerciseGroups) {
        for (String exerciseGroup : allExerciseGroups) {
            if (groupName.toLowerCase().equals(exerciseGroup.toLowerCase()) && allExerciseGroups.size() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        exerciseGroupDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        exerciseGroupDataSource.close();
    }
}
