package com.dainavahood.workoutlogger.workouts;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.db.WorkoutsDataSource;
import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.exercises.ExerciseGroupActivity;
import com.dainavahood.workoutlogger.extras.CustomSetsLayoutAdapter;
import com.dainavahood.workoutlogger.model.Set;
import com.dainavahood.workoutlogger.model.SetGroup;

import java.util.ArrayList;
import java.util.List;

public class CreateSetGroupActivity extends AppCompatActivity {

    private static final int DETAIL_REQUEST = 1333;
    private static final int EDIT_REQUEST = 1334;
    public static final String CALLING_ACTIVITY = "CALLING_ACTIVITY";
    public static final String SETS_TO_REMOVE = "SETS_TO_REMOVE";

    private List<Set> sets = new ArrayList<>();
    private CustomSetsLayoutAdapter adapter;
    private Set set;
    private int setPosition, selectedPosition;
    private EditText et;
    private Bundle bundle;
    private ArrayList<Set> setsToRemove, multiChoiceRemove;
    private SetGroup setGroup;
    private KeyListener keyListener;
    private Button editName;
    private WorkoutsDataSource dataSource;
    private Drawable etBackGround;
    private boolean reodering = false, selected = false;
    private ListView lv;
    private Set selectedSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_set_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataSource = new WorkoutsDataSource(this);
        dataSource.open();

        et = (EditText) findViewById(R.id.setGroupName);
        etBackGround = et.getBackground();
        keyListener = et.getKeyListener();
        editName = (Button) findViewById(R.id.editName);
        et.setKeyListener(null);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            et.setBackground(null);
        }

        bundle = getIntent().getExtras();
        if (getIntent().getIntExtra(CreateWorkoutActivity.CREATE_WORKOUT_CALLING_ACTIVITY, 0) == Constants.CREATE_WORKOUT_ACTIVITY){
            setGroup = bundle.getParcelable(Constants.SET_GROUP_PACKAGE_NAME);
            if (setGroup != null) {
                et.setText(setGroup.getName());
                sets = setGroup.getSets();
                setTitle(setGroup.getName());
            }
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

        adapter = new CustomSetsLayoutAdapter(this, sets);
        lv = (ListView) findViewById(R.id.setGroupExercisesListView);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                set = (Set) parent.getItemAtPosition(position);
                setPosition = sets.indexOf(set);
                if (!reodering) {
                    Intent intent = new Intent(CreateSetGroupActivity.this, SetDetailsActivity.class);
                    intent.putExtra(Constants.SET_PACKAGE_NAME, set);
                    startActivityForResult(intent, EDIT_REQUEST);
                } else if (selected) {
                    sets.remove(selectedPosition);
                    sets.add(setPosition, selectedSet);
                    getNewOrderNr();
                    adapter.notifyDataSetChanged();
                    view.setSelected(false);
                    selected = false;
                } else {
                    selectedPosition = setPosition;
                    selectedSet = set;
                    view.setSelected(true);
                    selected = true;
                }


            }
        });

        setsToRemove = new ArrayList<>();
        multiChoiceRemove = new ArrayList<>();

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
                set = sets.get(position);
                if (!checked) {
                    multiChoiceRemove.remove(set);
                } else {
                    multiChoiceRemove.add(set);
                }

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
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
                        for (Set set : multiChoiceRemove) {
                            sets.remove(set);
                        }
                        setsToRemove.addAll(multiChoiceRemove);
                        adapter.notifyDataSetChanged();
                        getNewOrderNr();
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                multiChoiceRemove.clear();
            }
        });

        Button addExerciseButton = (Button) findViewById(R.id.addExerciseButton);
        addExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateSetGroupActivity.this, ExerciseGroupActivity.class);
                intent.putExtra(CALLING_ACTIVITY, Constants.CREATE_SET_GROUP_ACTIVITY);
                startActivityForResult(intent, DETAIL_REQUEST);
            }
        });

        Button saveSetGroup = (Button) findViewById(R.id.saveSetGroup);
        saveSetGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String setGroupName = et.getText().toString().trim();

                if (getIntent().getIntExtra(CreateWorkoutActivity.CREATE_WORKOUT_CALLING_ACTIVITY, 0) != Constants.CREATE_WORKOUT_ACTIVITY) {
                    setGroup = new SetGroup();
                }
                setGroup.setName(setGroupName);
                setGroup.setSets(sets);

                if (sets.size() == 0) {
                    Toast.makeText(CreateSetGroupActivity.this, "Add at least one exercise", Toast.LENGTH_SHORT).show();
                } else {
                    Intent data = new Intent();
                    if (setsToRemove.size() > 0) {
                        data.putParcelableArrayListExtra(SETS_TO_REMOVE, setsToRemove);
                    }
                    data.putExtra(Constants.SET_GROUP_PACKAGE_NAME, setGroup);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == DETAIL_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                int howMany = data.getIntExtra(SetDetailsActivity.HOW_MANY_TO_ADD, 1);
                set = bundle.getParcelable(Constants.SET_PACKAGE_NAME);
                for (int i = 0; i < howMany; i++) {
                    Set newSet = null;
                    try {
                        newSet = (Set) set.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    sets.add(newSet);
                }
                getNewOrderNr();
                adapter.notifyDataSetChanged();
            }
        }

        if (requestCode == EDIT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                set = bundle.getParcelable(Constants.SET_PACKAGE_NAME);
                sets.remove(setPosition);
                sets.add(setPosition, set);
                getNewOrderNr();
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void getNewOrderNr() {
        for (Set set : sets) {
            int setIndex = sets.indexOf(set);
            set.setOrderNr(setIndex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reoder_exercises, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.reoder:
                if (reodering) {
                    item.setTitle(R.string.reoder);
                    Toast.makeText(CreateSetGroupActivity.this, "Done reordering", Toast.LENGTH_SHORT).show();
                    reodering = false;
                } else if (!reodering) {
                    item.setTitle(R.string.done);
                    Toast.makeText(CreateSetGroupActivity.this, "Reordering", Toast.LENGTH_SHORT).show();
                    reodering = true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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
