package com.dainavahood.workoutlogger.workouts;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dainavahood.workoutlogger.GlobalVariables;
import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.db.WorkoutsDataSource;
import com.dainavahood.workoutlogger.exercises.ExerciseGroupActivity;
import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.model.Set;
import com.dainavahood.workoutlogger.model.SetGroup;
import com.dainavahood.workoutlogger.model.Workout;

import org.apache.commons.codec.Encoder;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.language.Soundex;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class WorkoutLOGActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String ALL_SETS = "ALL_SETS";
    public static final String SAVED_SETS = "SAVED_SETS";
    public static final String EXERCISE_OBJ = "EXERCISE_OBJ";
    public static final String PREVIOUS_WORKOUT_SETS = "PREVIOUS_WORKOUT_SETS";
    public static final String WORKOUT_LOG_CALLING_ACTIVITY = "WORKOUT_LOG_CALLING_ACTIVITY";

    private static final int START_REST = 777;
    private static final int PAUSE_REST = 888;
    private static final int START_REPS_TIME = 111;
    private static final int PAUSE_REPS_TIME = 222;

    private static final int DETAIL_REQUEST = 02214;
    private static final int INSERT = 117;
    private static final int CHANGE_CURRENT = 118;
    private static final int ADD_NEXT = 119;

    private WorkoutsDataSource dataSource;
    private Set set, newSet;
    private ArrayList<Set> sets, setsToSave, previousSets;
    private List<SetGroup> setGroups;
    private Workout workout, previousWorkout;
    private Bundle bundle;
    private int exerciseOBJ, intValue, restToSave, repsTimeToSave, optionSelected;
    private double doubleValue;
    private EditText editRepsEt, editWeightEt, editNotesEt, editRestEt;
    private Button minus1, minus2, plus1, plus2, minus3, plus3, saveSet, cancel, saveAndQuit, dontSave, goBack, toggleRest, toggleRepsTime;
    private TextView exerciseNameTv, nextExerciseTv, previousExerciseTv;
    private String workoutName;
    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout workoutLogLayout;
    private FrameLayout frameLayout;
    private CountDownTimer countDownTimer, repsTimer, preCount;
    private KeyListener restKeyListener, timeKeyListener;
    private NotificationManager notificationManager;
    private Notification notification;
    private boolean countRestTime = false, stopRepsTimePressed = false, stopRepsTimeTicking = false, reloading = false;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;
    private int precountMillis = 5000;
    private long startingTime;
    private GlobalVariables appContext;

    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 10;
    private static final String TAG = "TAGAS";
    private SpeechRecognizer sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        appContext = (GlobalVariables) getApplicationContext();

        loadActivity();

    }

    private void loadActivity() {
        optionSelected = 0;
        frameLayout = (FrameLayout) findViewById(R.id.mainmenu);
        frameLayout.getForeground().setAlpha(0);

        dataSource = new WorkoutsDataSource(this);
        dataSource.open();

        bundle = getIntent().getExtras();
        workout = bundle.getParcelable(Constants.WORKOUT_PACKAGE_NAME);
        if (workout != null) {
            workoutName = workout.getName();
        }
        if (getIntent().getIntExtra(CreateWorkoutActivity.CREATE_WORKOUT_CALLING_ACTIVITY, 0) == Constants.CREATE_WORKOUT_ACTIVITY) {
            if (!reloading) {
                startingTime = System.currentTimeMillis();
                sets = new ArrayList<>();
                setGroups = dataSource.findAllSetGroups(workout.getId());
                for (SetGroup setGroup : setGroups) {
                    sets.addAll(setGroup.getSets());
                }

                appContext.setStartTime(System.currentTimeMillis());

                exerciseOBJ = 0;
                setsToSave = new ArrayList<>();
                previousWorkout = dataSource.getPreviousWorkout(workout);
                if (previousWorkout != null) {
                    previousSets = new ArrayList<>();
                    previousSets = (ArrayList<Set>) dataSource.findAllSetsHistory(previousWorkout);
                }
            }
        } else {
            exerciseOBJ = getIntent().getIntExtra(EXERCISE_OBJ, 0);
            setsToSave = getIntent().getParcelableArrayListExtra(SAVED_SETS);

            Toast.makeText(this, "Elapsed time " + getElapsedTime(), Toast.LENGTH_LONG).show();

            if (!reloading) {
                sets = getIntent().getParcelableArrayListExtra(ALL_SETS);
            }
            previousSets = getIntent().getParcelableArrayListExtra(PREVIOUS_WORKOUT_SETS);
        }

        setTitle(workoutName);
        exerciseNameTv = (TextView) findViewById(R.id.exerciseName);
        nextExerciseTv = (TextView) findViewById(R.id.nextExercise);
        previousExerciseTv = (TextView) findViewById(R.id.previousExercise);
        editRepsEt = (EditText) findViewById(R.id.editReps);
        editWeightEt = (EditText) findViewById(R.id.editWeight);
        editNotesEt = (EditText) findViewById(R.id.editNotes);
        editRestEt = (EditText) findViewById(R.id.editRest);
        minus1 = (Button) findViewById(R.id.minus1);
        minus2 = (Button) findViewById(R.id.minus2);
        minus3 = (Button) findViewById(R.id.minus3);
        plus1 = (Button) findViewById(R.id.plus1);
        plus2 = (Button) findViewById(R.id.plus2);
        plus3 = (Button) findViewById(R.id.plus3);
        saveSet = (Button) findViewById(R.id.saveSet);
        goBack = (Button) findViewById(R.id.back);
        toggleRest = (Button) findViewById(R.id.startPause);
        toggleRepsTime = (Button) findViewById(R.id.startTime);

        toggleRepsTime.setVisibility(View.GONE);

        restKeyListener = editRestEt.getKeyListener();
        timeKeyListener = editRepsEt.getKeyListener();

        if (exerciseOBJ < sets.size()) {
            restToSave = 0;
            repsTimeToSave = 0;
//            totalRestTime = sets.get(exerciseOBJ).getRest() * 1000;
            if (sets.get(exerciseOBJ).isTime()){
                TextView repsName = (TextView) findViewById(R.id.repsName);
                repsName.setText(R.string.time_noSec);
                toggleRepsTime.setVisibility(View.VISIBLE);
            }
            exerciseNameTv.setText(sets.get(exerciseOBJ).getExerciseName());
            if (exerciseOBJ != sets.size()-1) {
                nextExerciseTv.setText("Next exercise: " + sets.get(exerciseOBJ + 1).getExerciseName());
            }
            if (exerciseOBJ == sets.size()-1) {
                saveSet.setText(R.string.finish);
            } else {
                saveSet.setText(R.string.save);
            }
            if (previousSets.size() > 0) {
                try {
                    String previousReps = String.valueOf(previousSets.get(exerciseOBJ).getReps());
                    String previousWeight = String.valueOf(previousSets.get(exerciseOBJ).getWeight());
                    String previousNotes = previousSets.get(exerciseOBJ).getNotes();
                    String previousName = previousSets.get(exerciseOBJ).getExerciseName();
                    String previousRest = String.valueOf(previousSets.get(exerciseOBJ).getRest());
                    String repsOrTime = "Reps: ";
                    if (previousSets.get(exerciseOBJ).isTime()){
                        repsOrTime = "Time: ";
                    }

                    previousExerciseTv.setText("Previous workout results:\n" +
                            previousName + "\n" +
                            repsOrTime + previousReps + "\n" +
                            "Weight: " + previousWeight + "\n" +
                            "Rest: " + previousRest + "\n" +
                            "Notes: " + previousNotes);
                } catch (IndexOutOfBoundsException e) {
                }
            }
            editRepsEt.setText(String.valueOf(sets.get(exerciseOBJ).getReps()));
            editWeightEt.setText(String.valueOf(sets.get(exerciseOBJ).getWeight()));
            editRestEt.setText(String.valueOf(sets.get(exerciseOBJ).getRest()));

            plus1.setOnClickListener(this);
            plus2.setOnClickListener(this);
            plus3.setOnClickListener(this);
            minus1.setOnClickListener(this);
            minus2.setOnClickListener(this);
            minus3.setOnClickListener(this);
            saveSet.setOnClickListener(this);
            goBack.setOnClickListener(this);

            toggleRest.setTag(START_REST);
            toggleRest.setOnClickListener(this);

            toggleRepsTime.setTag(START_REPS_TIME);
            toggleRepsTime.setOnClickListener(this);

        }
    }

    private void initSnackbar() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.logCoordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout,
                "To save entered time press Reset and don't start timer",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("Reset", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleRest.callOnClick();
                        countRestTime = false;
                        editRestEt.setText(String.valueOf(sets.get(exerciseOBJ).getRest()));
                    }
                });
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.plus1:
                intValue = Integer.parseInt(editRepsEt.getText().toString());
                intValue++;
                editRepsEt.setText(String.valueOf(intValue));
                break;

            case R.id.minus1:
                String etVal1 = editRepsEt.getText().toString();
                if (etVal1.equals("")){
                    etVal1 = "0";
                }
                intValue = Integer.parseInt(etVal1);
                if (intValue > 0){
                    intValue--;
                }
                editRepsEt.setText(String.valueOf(intValue));
                break;

            case R.id.plus2:
                doubleValue = Double.parseDouble(editWeightEt.getText().toString());
                doubleValue = doubleValue + 0.5;
                editWeightEt.setText(String.valueOf(doubleValue));
                break;

            case R.id.minus2:
                String etVal = editWeightEt.getText().toString();
                if (etVal.equals("")){
                    etVal = "0";
                }
                doubleValue = Double.parseDouble(etVal);
                if (doubleValue > 0){
                    doubleValue = doubleValue - 0.5;
                }
                editWeightEt.setText(String.valueOf(doubleValue));
                break;

            case R.id.plus3:
                intValue = Integer.parseInt(editRestEt.getText().toString());
                intValue += 10;
                editRestEt.setText(String.valueOf(intValue));
                break;

            case R.id.minus3:
                String etVal2 = editRestEt.getText().toString();
                if (etVal2.equals("")){
                    etVal2 = "0";
                }
                intValue = Integer.parseInt(etVal2);
                if (intValue > 0){
                    intValue -= 10;
                }
                editRestEt.setText(String.valueOf(intValue));
                break;

            case R.id.saveSet:
                saveSet();
                break;

            case R.id.back:
                if (exerciseOBJ == 0) {
                    onBackPressed();
                } else {
                    cancelAllTimers();
                    Intent intent1 = new Intent(WorkoutLOGActivity.this, WorkoutLOGActivity.class);
                    setsToSave.remove(setsToSave.get(setsToSave.size() - 1));
                    intent1.putParcelableArrayListExtra(SAVED_SETS, setsToSave);
                    intent1.putParcelableArrayListExtra(ALL_SETS, sets);
                    intent1.putParcelableArrayListExtra(PREVIOUS_WORKOUT_SETS, previousSets);
                    intent1.putExtra(Constants.WORKOUT_PACKAGE_NAME, workout);
                    intent1.putExtra(EXERCISE_OBJ, --exerciseOBJ);
                    startActivity(intent1);
                    finish();
                }
                break;

            case R.id.startPause:
                if (toggleRest.getTag().equals(START_REST)){
                    countRestTime = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        toggleRest.setBackground(getResources().getDrawable(R.drawable.ic_action_pausetime));

                    } else {
                        toggleRest.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_pausetime));
                    }
                    toggleRest.setTag(PAUSE_REST);
                    minus3.setVisibility(View.INVISIBLE);
                    plus3.setVisibility(View.INVISIBLE);
                    editRestEt.setKeyListener(null);
                    startCountDownTimer();
                    initSnackbar();
                    snackbar.show();
                } else if (toggleRest.getTag().equals(PAUSE_REST)) {
                    snackbar.dismiss();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        toggleRest.setBackground(getResources().getDrawable(R.drawable.ic_action_starttime));
                    } else {
                        toggleRest.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_action_starttime));
                    }
                    toggleRest.setTag(START_REST);
                    minus3.setVisibility(View.VISIBLE);
                    plus3.setVisibility(View.VISIBLE);
                    editRestEt.setKeyListener(restKeyListener);
                    countDownTimer.cancel();
                }
                break;

            case R.id.startTime:
                if (toggleRepsTime.getTag().equals(START_REPS_TIME)) {
                    stopRepsTimePressed = false;
                    toggleRepsTime.getBackground().setColorFilter(getResources().getColor(R.color.stop), PorterDuff.Mode.SRC_ATOP);
                    toggleRepsTime.setTag(PAUSE_REPS_TIME);
                    minus1.setVisibility(View.INVISIBLE);
                    plus1.setVisibility(View.INVISIBLE);
                    editRepsEt.setKeyListener(null);
                    startRepsTimer();
                    toggleRepsTime.setText(R.string.stop);
                } else if (toggleRepsTime.getTag().equals(PAUSE_REPS_TIME)) {
                    stopRepsTimePressed = true;
                    resetRepsTimeButton();
                    preCount.cancel();
                    precountMillis = 5000;
                    repsTimer.onFinish();
                    repsTimer.cancel();
                }
                break;

        }
    }

    private void resetRepsTimeButton() {
        toggleRepsTime.getBackground().clearColorFilter();
        toggleRepsTime.setTag(START_REPS_TIME);
        minus1.setVisibility(View.VISIBLE);
        plus1.setVisibility(View.VISIBLE);
        editRepsEt.setKeyListener(timeKeyListener);
        toggleRepsTime.setText(R.string.start);
    }

    private void saveSet() {
        addToSetsToSave();

        Intent intent = new Intent(WorkoutLOGActivity.this, WorkoutLOGActivity.class);
        intent.putParcelableArrayListExtra(SAVED_SETS, setsToSave);
        intent.putParcelableArrayListExtra(ALL_SETS, sets);
        intent.putParcelableArrayListExtra(PREVIOUS_WORKOUT_SETS, previousSets);
        intent.putExtra(Constants.WORKOUT_PACKAGE_NAME, workout);
        cancelAllTimers();

        if (exerciseOBJ < sets.size() && exerciseOBJ != sets.size()-1) {
            intent.putExtra(EXERCISE_OBJ, ++exerciseOBJ);
            startActivity(intent);
            finish();
        } else if (exerciseOBJ == sets.size()-1){
            long workoutHistoryId = dataSource.createWorkoutHistory(workout);
            for (Set set : setsToSave) {
                set.setWorkoutHistoryId(workoutHistoryId);
                dataSource.createSetHistory(set);
            }
            finish();
        }
    }

    private String getElapsedTime() {
        long startTime = appContext.getStartTime();
        long elapsedTime = System.currentTimeMillis() - startTime;
        long sec = (elapsedTime / 1000) % 60;
        long min = (elapsedTime / 60000);
        return String.format("%02d:%02d", min, sec);
    }

    private void addToSetsToSave() {
        if (!countRestTime) {
            restToSave = getInt(editRestEt.getText().toString());
        }
        set = new Set();
        set.setExerciseName(exerciseNameTv.getText().toString());
        set.setReps(getInt(editRepsEt.getText().toString()));
        set.setWeight(getDouble(editWeightEt.getText().toString()));
        set.setRest(restToSave);
        set.setTime(sets.get(exerciseOBJ).isTime());
        set.setNotes(editNotesEt.getText().toString());
        if (exerciseOBJ == sets.size()-1) {
            String currentNotesInput = set.getNotes();
            set.setNotes(currentNotesInput + "\nFinished in " + getElapsedTime());
            Toast.makeText(this, "Finished workout in " + getElapsedTime(), Toast.LENGTH_LONG).show();
        }
        set.setSetGroupId(sets.get(exerciseOBJ).getSetGroupId());
        set.setOrderNr(sets.get(exerciseOBJ).getOrderNr());
        setsToSave.add(set);
    }

    private void skip() {
        editRestEt.setText("0");
        editRepsEt.setText("0");
        editWeightEt.setText("0.0");
        editNotesEt.getEditableText().insert(0, "Skipped ");
    }

    @Override
    public void onBackPressed() {
        if (exerciseOBJ == 0) {
            cancelAllTimers();
            super.onBackPressed();
        } else {
            frameLayout.getForeground().setAlpha(180);
            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.quit_workout, null);
            cancel = (Button) container.findViewById(R.id.cancel);
            dontSave = (Button) container.findViewById(R.id.dont_save);
            saveAndQuit = (Button) container.findViewById(R.id.save_and_quit);
            workoutLogLayout = (RelativeLayout) findViewById(R.id.workoutLogLayout);

            popupWindow = new PopupWindow(container, RelativeLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popupWindow.showAtLocation(workoutLogLayout, Gravity.BOTTOM, 0, 0);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    frameLayout.getForeground().setAlpha(0);
                    popupWindow.dismiss();
                }
            });
            dontSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelAllTimers();
                    WorkoutLOGActivity.super.onBackPressed();
                }
            });
            saveAndQuit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelAllTimers();
                    addToSetsToSave();
                    long workoutHistoryId = dataSource.createWorkoutHistory(workout);
                    for (Set set : setsToSave) {
                        set.setWorkoutHistoryId(workoutHistoryId);
                        dataSource.createSetHistory(set);
                    }
                    WorkoutLOGActivity.super.onBackPressed();
                }
            });
        }

    }

    private void startCountDownTimer() {
        if (editRestEt.getText().toString().equals("")) {
            editRestEt.setText("0");
        }
        countDownTimer = new CountDownTimer(Long.parseLong(editRestEt.getText().toString()) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                totalRestTime = millisUntilFinished;
                restToSave++;
                editRestEt.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                playSound(R.raw.beep21);
                restToSave++;
                if (exerciseOBJ != sets.size()-1) {
                    saveSet();
                }
            }
        }.start();
    }

    private void startRepsTimer() {
        repsTimer = new CountDownTimer(Integer.parseInt(editRepsEt.getText().toString()) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                repsTimeToSave++;
                editRepsEt.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                if (!stopRepsTimePressed){
                    playSound(R.raw.firealarm);
                    repsTimeToSave++;
                }
                if (!stopRepsTimeTicking) {
                    editRepsEt.setText(String.valueOf(repsTimeToSave));
                }
                repsTimeToSave = 0;
                resetRepsTimeButton();
            }
        };

        preCount = new CountDownTimer(precountMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                toggleRepsTime.setText(String.valueOf(millisUntilFinished/1000));
                stopRepsTimeTicking = true;
            }

            @Override
            public void onFinish() {
                repsTimer.start();
                toggleRepsTime.setText(R.string.stop);
                playSound(R.raw.beep_one);
                stopRepsTimeTicking = false;
                precountMillis = 5000;
            }

        }.start();
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

    private void playSound(int soundRaw) {

        Uri soundURI = Uri.parse(Constants.URI_TO_PACKAGE + soundRaw);
        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(this, soundURI);
        long soundDuration = Long.parseLong(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        nBuilder.setSmallIcon(R.drawable.ic_minus)
                .setContentTitle("Notification")
                .setContentText("Pranesimas")
                .setVibrate(new long[] {200, 700, 200, 700})
                .setSound(soundURI);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Constants.NOTIFICATION_WOUT_LOG, nBuilder.build());
        Runnable r = new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(Constants.NOTIFICATION_WOUT_LOG);
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(r, soundDuration);
    }

    private void cancelAllTimers() {
        if (preCount != null) {
            preCount.cancel();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (repsTimer != null) {
            repsTimer.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workout_log_menu, menu);
        if (!sets.get(exerciseOBJ).isTime()) {
            MenuItem item = menu.findItem(R.id.action_add_sec);
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_insert:
                optionSelected = INSERT;
                reloading = true;
                startRetrievingNewSet();
                return true;
            case R.id.action_change_current:
                optionSelected = CHANGE_CURRENT;
                reloading = true;
                startRetrievingNewSet();
                return true;
            case R.id.action_add_next:
                optionSelected = ADD_NEXT;
                reloading = true;
                startRetrievingNewSet();
                return true;
            case R.id.action_skip:
                skip();
                saveSet();
                return true;
            case R.id.action_add_sec:
                precountMillis += 5000;
                Toast.makeText(WorkoutLOGActivity.this, "Precount time = " + precountMillis/1000 + "sec", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startRetrievingNewSet(){
        cancelAllTimers();
        Intent intent = new Intent(WorkoutLOGActivity.this, ExerciseGroupActivity.class);
        intent.putExtra(WORKOUT_LOG_CALLING_ACTIVITY, Constants.WORKOUT_LOG_ACTIVITY);
        startActivityForResult(intent, DETAIL_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DETAIL_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                newSet = bundle.getParcelable(Constants.SET_PACKAGE_NAME);
                Toast.makeText(WorkoutLOGActivity.this, newSet.getExerciseName(), Toast.LENGTH_SHORT).show();
                switch (optionSelected) {
                    case INSERT:
                        sets.add(exerciseOBJ, newSet);
                        loadActivity();
                        break;

                    case CHANGE_CURRENT:
                        sets.remove(exerciseOBJ);
                        sets.add(exerciseOBJ, newSet);
                        loadActivity();
                        break;

                    case ADD_NEXT:
                        sets.add(exerciseOBJ + 1, newSet);
                        loadActivity();
                        break;
                }
            }
        }
    }

}
