package com.dainavahood.workoutlogger.workouts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.db.WorkoutsDataSource;
import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.extras.CustomListViewAdapter;
import com.dainavahood.workoutlogger.model.Set;
import com.dainavahood.workoutlogger.model.SetGroup;
import com.dainavahood.workoutlogger.model.Workout;

import java.util.ArrayList;
import java.util.List;

public class CreateWorkoutActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CODE = 1222;
    private static final int EDIT_REQUEST_CODE = 1223;
    public static final String CREATE_WORKOUT_CALLING_ACTIVITY = "CREATE_WORKOUT_CALLING_ACTIVITY";

    //LAIKINAI TEST
    private List<SetGroup> setGroups = new ArrayList<>();
    private List<Set> setsToRemove;
    private List<SetGroup> setGroupsToRemove;
    private SetGroup setGroup;
    private CustomListViewAdapter adapter;
    private int setGroupPosition;
    private WorkoutsDataSource dataSource;
    private Workout workout;
    private Bundle bundle;
    private EditText et;
    private CoordinatorLayout coordinatorLayout;
    private Boolean areModifications = false;
    private Drawable etBackGround;
    private KeyListener keyListener;
    private Button editName, buttonSave, buttonAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataSource = new WorkoutsDataSource(this);
        dataSource.open();

        setsToRemove = new ArrayList<>();
        setGroupsToRemove = new ArrayList<>();

        et = (EditText) findViewById(R.id.editText);
        etBackGround = et.getBackground();
        keyListener = et.getKeyListener();
        editName = (Button) findViewById(R.id.editName);
        et.setKeyListener(null);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            et.setBackground(null);
        }

        if (getIntent().getIntExtra(WorkoutsActivity.WORKOUT_ACTIVITY, 0) == Constants.WORKOUT_ACTIVITY){
            bundle = getIntent().getExtras();
            workout = bundle.getParcelable(Constants.WORKOUT_PACKAGE_NAME);
            et.setText(workout.getName());
            setGroups = dataSource.findAllSetGroups(workout.getId());
            setTitle(workout.getName());
        }

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editName.getText().toString().equals(getResources().getString(R.string.edit))) {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                        et.setBackground(etBackGround);
                    }
                    editName.setText(getResources().getString(R.string.save));
                    et.setKeyListener(keyListener);
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    et.setKeyListener(null);
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                        et.setBackground(null);
                    }
                    editName.setText(getResources().getString(R.string.edit));
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(et.getWindowToken(), 0);
                }
            }
        });

        adapter = new CustomListViewAdapter(this, setGroups);
        ListView lv = (ListView) findViewById(R.id.setsGroupsList);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setGroup = (SetGroup) parent.getItemAtPosition(position);
                setGroupPosition = setGroups.indexOf(setGroup);

                Intent intent = new Intent(CreateWorkoutActivity.this, CreateSetGroupActivity.class);
                intent.putExtra(Constants.SET_GROUP_PACKAGE_NAME, setGroup);
                intent.putExtra(CREATE_WORKOUT_CALLING_ACTIVITY, Constants.CREATE_WORKOUT_ACTIVITY);
                if (getIntent().getIntExtra(WorkoutsActivity.WORKOUT_ACTIVITY, 0) == Constants.WORKOUT_ACTIVITY) {
                    intent.putExtra(WorkoutsActivity.WORKOUT_ACTIVITY, Constants.WORKOUT_ACTIVITY);
                }
                startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                setGroup = (SetGroup) parent.getItemAtPosition(position);
                setGroupPosition = setGroups.indexOf(setGroup);

                AlertDialog.Builder alert = new AlertDialog.Builder(CreateWorkoutActivity.this);
                alert.setTitle("Confirm delete");
                alert.setMessage("Delete selected set group?");

                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setGroups.remove(setGroupPosition);
                        if (getIntent().getIntExtra(WorkoutsActivity.WORKOUT_ACTIVITY, 0) == Constants.WORKOUT_ACTIVITY && setGroup.getId() != 0) {
                            setGroupsToRemove.add(setGroup);
                        }
                        getNewOrderNr();
                        adapter.notifyDataSetChanged();

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

        buttonAdd = (Button) findViewById(R.id.addSetGroup);
        buttonAdd.setOnClickListener(this);

        buttonSave = (Button) findViewById(R.id.saveWorkout);
        if (getIntent().getIntExtra(WorkoutsActivity.WORKOUT_ACTIVITY, 0) == Constants.WORKOUT_ACTIVITY) {
//            buttonSave.setText(R.string.update);
//            buttonSave.getBackground().setColorFilter(getResources().getColor(R.color.update_button), PorterDuff.Mode.SRC_ATOP);
            buttonSave.setText(R.string.start);
            buttonSave.getBackground().setColorFilter(getResources().getColor(R.color.start), PorterDuff.Mode.SRC_ATOP);
        }

        if (getIntent().getIntExtra(WorkoutsActivity.WORKOUT_ACTIVITY, 0) == Constants.WORKOUT_ACTIVITY) {
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (workout.getName().equals(et.getText().toString()) && !areModifications) {
                        buttonSave.setText(R.string.start);
                        buttonSave.getBackground().setColorFilter(getResources().getColor(R.color.start), PorterDuff.Mode.SRC_ATOP);
                    } else {
                        buttonSave.setText(R.string.update);
                        buttonSave.getBackground().setColorFilter(getResources().getColor(R.color.update_button), PorterDuff.Mode.SRC_ATOP);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }


        buttonSave.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                setGroup = bundle.getParcelable(Constants.SET_GROUP_PACKAGE_NAME);
                setGroups.add(setGroup);
                getNewOrderNr();
                adapter.notifyDataSetChanged();
            }
        }

        if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                setGroup = bundle.getParcelable(Constants.SET_GROUP_PACKAGE_NAME);
                setGroups.remove(setGroupPosition);
                setGroups.add(setGroupPosition, setGroup);
                getNewOrderNr();
                adapter.notifyDataSetChanged();
                List<Set> remove = data.getParcelableArrayListExtra(CreateSetGroupActivity.SETS_TO_REMOVE);
                if (remove != null) {
                    setsToRemove.addAll(remove);
                }
            }
        }
    }

    private void getNewOrderNr() {
        for (SetGroup setGroup : setGroups) {
            int setGroupIndex = setGroups.indexOf(setGroup);
            setGroup.setOrderNr(setGroupIndex);
        }
        areModifications = true;
        if (getIntent().getIntExtra(WorkoutsActivity.WORKOUT_ACTIVITY, 0) == Constants.WORKOUT_ACTIVITY) {
            buttonSave.setText(R.string.update);
            buttonSave.getBackground().setColorFilter(getResources().getColor(R.color.update_button), PorterDuff.Mode.SRC_ATOP);
        }
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

    private boolean checkIfWorkoutExist(String workoutName, List<Workout> allWorkouts){
        for (Workout workout : allWorkouts){
            if (workout.getName().toLowerCase().equals(workoutName.toLowerCase()) && allWorkouts.size() > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_workout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id = item.getItemId();

            switch (id){
                case R.id.action_start_workout:
                    if (getIntent().getIntExtra(WorkoutsActivity.WORKOUT_ACTIVITY, 0) == Constants.WORKOUT_ACTIVITY && areModifications || buttonSave.getText().equals(getResources().getString(R.string.update))){
                        Snackbar.make(coordinatorLayout,
                                "You have modified your workout", Snackbar.LENGTH_LONG)
                                .setAction("Update\nand start.", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveOrUpdateWorkout(true);
                                    }
                                }).show();
                        return true;
                    } else if (getIntent().getIntExtra(WorkoutsActivity.WORKOUT_ACTIVITY, 0) == Constants.WORKOUT_ACTIVITY) {
                        startWorkout();
                        return true;
                    } else {
                        Snackbar.make(coordinatorLayout,
                                "You haven't saved your workout", Snackbar.LENGTH_LONG)
                                .setAction("Save\nand start", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveOrUpdateWorkout(true);
                                    }
                                }).show();
                        return true;
                    }
            }



        return super.onOptionsItemSelected(item);
    }

    private void startWorkout() {
        Intent intent = new Intent(CreateWorkoutActivity.this, WorkoutLOGActivity.class);
        intent.putExtra(Constants.WORKOUT_PACKAGE_NAME, workout);
        intent.putExtra(CREATE_WORKOUT_CALLING_ACTIVITY, Constants.CREATE_WORKOUT_ACTIVITY);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.saveWorkout:
                if (buttonSave.getText().equals(getResources().getString(R.string.update)) || buttonSave.getText().equals(getResources().getString(R.string.save))) {
                    saveOrUpdateWorkout(false);
                } else if (buttonSave.getText().equals(getResources().getString(R.string.start))) {
                    startWorkout();
                }
                break;

            case R.id.addSetGroup:
                Intent intent = new Intent(CreateWorkoutActivity.this, CreateSetGroupActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }


    //Issaugo arba atnaujina workouta duomenu bazej
    private void saveOrUpdateWorkout(Boolean startWorkout) {
        String workoutName = et.getText().toString().trim();
        List<Workout> allWorkouts = dataSource.findAllWorkouts();
        if (getIntent().getIntExtra(WorkoutsActivity.WORKOUT_ACTIVITY, 0) == Constants.WORKOUT_ACTIVITY){
        } else {
            workout = new Workout();
        }
        workout.setName(workoutName);

        if (workoutName.isEmpty()){
            Toast.makeText(CreateWorkoutActivity.this, "Please enter workout name", Toast.LENGTH_SHORT).show();
        } else if (setGroups.size() == 0){
            Toast.makeText(CreateWorkoutActivity.this, "Add at least one set", Toast.LENGTH_SHORT).show();
        } else if (checkIfWorkoutExist(workoutName, allWorkouts) && getIntent().getIntExtra(WorkoutsActivity.WORKOUT_ACTIVITY, 0) != Constants.WORKOUT_ACTIVITY){
            Toast.makeText(CreateWorkoutActivity.this, "Workout " + workoutName + " already exist", Toast.LENGTH_SHORT).show();
        } else {
            if(workout.getId() == 0) {
                workout = dataSource.createWorkout(workout);
            } else {
                dataSource.updateWorkout(workout);
            }
            for (SetGroup setGroup : setGroups) {
                setGroup.setWorkoutId(workout.getId());
                if (setGroup.getId() == 0) {
                    setGroup = dataSource.createSetGroup(setGroup);
                } else {
                    dataSource.updateSetGroup(setGroup);
                }
                for (Set set : setGroup.getSets()) {
                    set.setSetGroupId(setGroup.getId());
                    if(set.getId() == 0) {
                        dataSource.createSet(set);
                    } else {
                        dataSource.updateSet(set);
                    }
                }
            }
            if (setsToRemove != null){
                if (setsToRemove.size() > 0 ) {
                    for (Set setToRemove : setsToRemove) {
                        dataSource.removeSet(setToRemove);
                    }
                }
            }
            if (setGroupsToRemove.size() > 0) {
                for (SetGroup setGroupToRemove : setGroupsToRemove) {
                    dataSource.removeSetGroup(setGroupToRemove);
                }
            }
            if (startWorkout){
                startWorkout();
            } else {
                Intent intent = new Intent(CreateWorkoutActivity.this, CreateWorkoutActivity.class);
                intent.putExtra(Constants.WORKOUT_PACKAGE_NAME, workout);
                intent.putExtra(WorkoutsActivity.WORKOUT_ACTIVITY, Constants.WORKOUT_ACTIVITY);
                startActivity(intent);
                if (getIntent().getIntExtra(WorkoutsActivity.WORKOUT_ACTIVITY, 0) == Constants.WORKOUT_ACTIVITY) {
                    Toast.makeText(CreateWorkoutActivity.this, workout.getName() + " workout updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreateWorkoutActivity.this, workout.getName() + " workout saved successfully", Toast.LENGTH_SHORT).show();
                }
                finish();
                overridePendingTransition(0, 0);
            }
        }
    }

}
