package com.dainavahood.workoutlogger.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dainavahood.workoutlogger.extras.Constants;
import com.dainavahood.workoutlogger.model.Set;
import com.dainavahood.workoutlogger.model.SetGroup;
import com.dainavahood.workoutlogger.model.Workout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkoutsDataSource {


    //DB Startavimiui
    SQLiteOpenHelper dbhelper;
    SQLiteDatabase database;


    //Naudojamu eiluciu kintamieji seto exercise NETINKAMAI PAVADINAU KLASES IR KINTAMUOSIUS!!!!
    private static String[] set_columns = {
            DatabaseContract.SetsTable._ID,
            DatabaseContract.SetsTable.SET_GROUP_ID,
            DatabaseContract.SetsTable.ORDER_NR,
            DatabaseContract.SetsTable.REST,
            DatabaseContract.SetsTable.EXERCISE_NAME,
            DatabaseContract.SetsTable.REPS_TIME,
            DatabaseContract.SetsTable.WEIGHT,
            DatabaseContract.SetsTable.NOTES,
            DatabaseContract.SetsTable.IS_TIME,
            DatabaseContract.SetsTable.DATE
    };

    private static String[] set_history_columns = {
            DatabaseContract.SetsHistoryTable._ID,
            DatabaseContract.SetsHistoryTable.SET_GROUP_ID,
            DatabaseContract.SetsHistoryTable.ORDER_NR,
            DatabaseContract.SetsHistoryTable.REST,
            DatabaseContract.SetsHistoryTable.EXERCISE_NAME,
            DatabaseContract.SetsHistoryTable.REPS_TIME,
            DatabaseContract.SetsHistoryTable.WEIGHT,
            DatabaseContract.SetsHistoryTable.NOTES,
            DatabaseContract.SetsHistoryTable.IS_TIME,
            DatabaseContract.SetsHistoryTable.WORKOUT_HISTORY_ID,
            DatabaseContract.SetsHistoryTable.DATE
    };

    //setu grupes eilutes
    private static String[] set_group_columns = {
            DatabaseContract.SetGroupTable._ID,
            DatabaseContract.SetGroupTable.WORKOUT_ID,
            DatabaseContract.SetGroupTable.NAME,
            DatabaseContract.SetGroupTable.ORDER_NR,
            DatabaseContract.SetGroupTable.DATE
    };

//    private static String[] set_group_history_columns = {
//            DatabaseContract.SetGroupHistoryTable._ID,
//            DatabaseContract.SetGroupHistoryTable.WORKOUT_HISTORY_ID,
//            DatabaseContract.SetGroupHistoryTable.NAME,
//            DatabaseContract.SetGroupHistoryTable.ORDER_NR,
//            DatabaseContract.SetGroupHistoryTable.DATE
//    };

    //workout eilutes
    private static String[] workout_columns = {
            DatabaseContract.WorkoutsTable._ID,
            DatabaseContract.WorkoutsTable.WORKOUT_NAME,
            DatabaseContract.WorkoutsTable.DATE
    };

    private static String[] workout_history_columns = {
            DatabaseContract.WorkoutsHistoryTable._ID,
            DatabaseContract.WorkoutsHistoryTable.WORKOUT_NAME,
            DatabaseContract.WorkoutsHistoryTable.WORKOUT_ID,
            DatabaseContract.WorkoutsHistoryTable.DATE
    };


    //Inicializavimas
    public WorkoutsDataSource(Context context) {
        dbhelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbhelper.getWritableDatabase();
    }

    public void close() {
        database.close();
    }


//*************************************************************************************
//*********************Įrašų kūrimas, paieska******************************************
//*************************************************************************************
    //~~~~~~~~~~~~Set sekcija~~~~~~~~~~~~~~
    public Set createSet(Set set){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.SetsTable.SET_GROUP_ID, set.getSetGroupId());
        values.put(DatabaseContract.SetsTable.ORDER_NR, set.getOrderNr());
        values.put(DatabaseContract.SetsTable.REST, set.getRest());
        values.put(DatabaseContract.SetsTable.EXERCISE_NAME, set.getExerciseName());
        values.put(DatabaseContract.SetsTable.REPS_TIME, set.getReps());
        values.put(DatabaseContract.SetsTable.WEIGHT, set.getWeight());
        values.put(DatabaseContract.SetsTable.NOTES, set.getNotes());
        values.put(DatabaseContract.SetsTable.IS_TIME, set.isTime());

        long newRowId = database.insert(DatabaseContract.SetsTable.TABLE_NAME, null, values);
        set.setId(newRowId);
        return set;
    }

    public void updateSet(Set set){

        String where = DatabaseContract.SetsTable._ID + "=?";
        String[] whereArgs = {String.valueOf(set.getId())};

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.SetsTable.SET_GROUP_ID, set.getSetGroupId());
        values.put(DatabaseContract.SetsTable.ORDER_NR, set.getOrderNr());
        values.put(DatabaseContract.SetsTable.REST, set.getRest());
        values.put(DatabaseContract.SetsTable.EXERCISE_NAME, set.getExerciseName());
        values.put(DatabaseContract.SetsTable.REPS_TIME, set.getReps());
        values.put(DatabaseContract.SetsTable.WEIGHT, set.getWeight());
        values.put(DatabaseContract.SetsTable.NOTES, set.getNotes());
        values.put(DatabaseContract.SetsTable.IS_TIME, set.isTime());
        database.update(DatabaseContract.SetsTable.TABLE_NAME, values, where, whereArgs);
    }

    public List<Set> findAllSets(long setGroupId){

        List<Set> sets = new ArrayList<>();

        String sortOrder = DatabaseContract.SetsTable.ORDER_NR + " ASC";

        String where = DatabaseContract.SetsTable.SET_GROUP_ID + "=?";
        String[] whereArgs = {String.valueOf(setGroupId)};

        Cursor cursor = database.query(
                DatabaseContract.SetsTable.TABLE_NAME,
                set_columns,
                where,
                whereArgs,
                null,
                null,
                sortOrder);

        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                Set set = new Set();

                Boolean isTime = false;
                if (cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetsTable.IS_TIME)) == 1){
                    isTime = true;
                }

                set.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.SetsTable._ID)));
                set.setSetGroupId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetsTable.SET_GROUP_ID)));
                set.setOrderNr(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetsTable.ORDER_NR)));
                set.setRest(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetsTable.REST)));
                set.setExerciseName(cursor.getString(cursor.getColumnIndex(DatabaseContract.SetsTable.EXERCISE_NAME)));
                set.setReps(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetsTable.REPS_TIME)));
                set.setWeight(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetsTable.WEIGHT)));
                set.setNotes(cursor.getString(cursor.getColumnIndex(DatabaseContract.SetsTable.NOTES)));
                set.setTime(isTime);
                set.setDate(cursor.getLong(cursor.getColumnIndex(DatabaseContract.SetsTable.DATE)));
                sets.add(set);
            }
        }
        cursor.close();
        return sets;
    }

    public void removeSet(Set set){
        String where = DatabaseContract.SetsTable._ID + "=?";
        String[] whereArgs = {String.valueOf(set.getId())};
        database.delete(DatabaseContract.SetsTable.TABLE_NAME, where, whereArgs);
    }

    ///////////////////////////////////////////////////////////
    //~~~~~~~~~~~~~~~~~~SetGroup sekcija~~~~~~~~~~~~~~~~~~~~~~
    public SetGroup createSetGroup(SetGroup setGroup){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.SetGroupTable.NAME, setGroup.getName());
        values.put(DatabaseContract.SetGroupTable.WORKOUT_ID, setGroup.getWorkoutId());
        values.put(DatabaseContract.SetGroupTable.ORDER_NR, setGroup.getOrderNr());

        long newRowId = database.insert(DatabaseContract.SetGroupTable.TABLE_NAME, null, values);
        setGroup.setId(newRowId);
        return setGroup;
    }

    public void updateSetGroup(SetGroup setGroup){

        String where = DatabaseContract.SetGroupTable._ID + "=?";
        String[] whereArgs = {String.valueOf(setGroup.getId())};

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.SetGroupTable.NAME, setGroup.getName());
        values.put(DatabaseContract.SetGroupTable.WORKOUT_ID, setGroup.getWorkoutId());
        values.put(DatabaseContract.SetGroupTable.ORDER_NR, setGroup.getOrderNr());

        database.update(DatabaseContract.SetGroupTable.TABLE_NAME, values, where, whereArgs);
    }

    public List<SetGroup> findAllSetGroups(long workoutId) {
        List<SetGroup> setGroups = new ArrayList<>();

        String where = DatabaseContract.SetGroupTable.WORKOUT_ID + "=?";
        String[] whereArgs = {String.valueOf(workoutId)};

        Cursor cursor = database.query(
                DatabaseContract.SetGroupTable.TABLE_NAME,
                set_group_columns,
                where,
                whereArgs,
                null,
                null,
                null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                SetGroup setGroup = new SetGroup();

                setGroup.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.SetGroupTable._ID)));
                setGroup.setWorkoutId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetGroupTable.WORKOUT_ID)));
                setGroup.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.SetGroupTable.NAME)));
                setGroup.setOrderNr(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetGroupTable.ORDER_NR)));
                setGroup.setDate(cursor.getLong(cursor.getColumnIndex(DatabaseContract.SetGroupTable.DATE)));
                setGroup.setSets(findAllSets(cursor.getLong(cursor.getColumnIndex(DatabaseContract.SetGroupTable._ID))));
                setGroups.add(setGroup);
            } // while (cursor.moveToNext()) end
        } // if (cursor.getCount() > 0) end
        cursor.close();
        return setGroups;
    } // findAllSetGroup end

    public void removeSetGroup(SetGroup setGroup){
        String where = DatabaseContract.SetGroupTable._ID + "=?";
        String[] whereArgs = {String.valueOf(setGroup.getId())};
        database.delete(DatabaseContract.SetGroupTable.TABLE_NAME, where, whereArgs);
    }

    /////////////////////////////////////////////////////////////////////
    //~~~~~~~~~~~~~~~~~~~~~~~~Workout sekcija ~~~~~~~~~~~~~~~~~~~~~~~~~~
    public Workout createWorkout(Workout workout) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WorkoutsTable.WORKOUT_NAME, workout.getName());
        long newRowId = database.insert(DatabaseContract.WorkoutsTable.TABLE_NAME, null, values);
        workout.setId(newRowId);

        return workout;
    }

    public void updateWorkout(Workout workout){

        String where = DatabaseContract.WorkoutsTable._ID + "=?";
        String[] whereArgs = {String.valueOf(workout.getId())};

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WorkoutsTable.WORKOUT_NAME, workout.getName());
        database.update(DatabaseContract.WorkoutsTable.TABLE_NAME, values, where, whereArgs);

    }

    public List<Workout> findAllWorkouts(){
        List<Workout> workouts = new ArrayList<>();

        Cursor cursor = database.query(
                DatabaseContract.WorkoutsTable.TABLE_NAME,
                workout_columns,
                null,
                null,
                null,
                null,
                null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()){
                Workout workout = new Workout();
                workout.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsTable._ID)));
                workout.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.WorkoutsTable.WORKOUT_NAME)));
                workout.setDate(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsTable.DATE)));
                workouts.add(workout);
            }
        }
        cursor.close();
        return workouts;

    }

    public void removeWorkout(Workout workout){
        String where = DatabaseContract.WorkoutsTable._ID + "=?";
        String[] whereArgs = {String.valueOf(workout.getId())};

        database.delete(DatabaseContract.WorkoutsTable.TABLE_NAME, where, whereArgs);
    }
    //HISTORY

    public long createWorkoutHistory(Workout workout) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.WorkoutsHistoryTable.WORKOUT_NAME, workout.getName());
        values.put(DatabaseContract.WorkoutsHistoryTable.WORKOUT_ID, workout.getId());
        long newRowId = database.insert(DatabaseContract.WorkoutsHistoryTable.TABLE_NAME, null, values);
        return newRowId;
    }

    public List<Workout> findAllWorkoutHistory() {
        String sortOrder = DatabaseContract.WorkoutsHistoryTable.DATE + " DESC";
        List<Workout> workouts = new ArrayList<>();

        String query = "SELECT " + DatabaseContract.WorkoutsHistoryTable._ID + ", " +
                DatabaseContract.WorkoutsHistoryTable.WORKOUT_NAME + ", " +
                DatabaseContract.WorkoutsHistoryTable.WORKOUT_ID + ", " +
                "strftime('%s', " + DatabaseContract.WorkoutsHistoryTable.DATE + ") " + "AS " + DatabaseContract.WorkoutsHistoryTable.DATE +
                " FROM " + DatabaseContract.WorkoutsHistoryTable.TABLE_NAME +
                " ORDER BY " + DatabaseContract.WorkoutsHistoryTable.DATE + " DESC";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Workout workout = new Workout();
                workout.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable._ID)));
                workout.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable.WORKOUT_NAME)));
                workout.setWorkoutId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable.WORKOUT_ID)));
                workout.setDate(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable.DATE)));
                workouts.add(workout);
            }
        }
        cursor.close();
        return workouts;
    }

    public List<Workout> findWorkoutHistoryByDate(String date) {
        String sortOrder = DatabaseContract.WorkoutsHistoryTable.DATE + " DESC";
        List<Workout> workouts = new ArrayList<>();
        String[] whereArgs = {date};

        String query = "SELECT " + DatabaseContract.WorkoutsHistoryTable._ID + ", " +
                DatabaseContract.WorkoutsHistoryTable.WORKOUT_NAME + ", " +
                DatabaseContract.WorkoutsHistoryTable.WORKOUT_ID + ", " +
                "strftime('%s', " + DatabaseContract.WorkoutsHistoryTable.DATE + ") " + "AS " + DatabaseContract.WorkoutsHistoryTable.DATE +
                " FROM " + DatabaseContract.WorkoutsHistoryTable.TABLE_NAME +
                " WHERE " + "strftime('%Y-%m-%d', " + DatabaseContract.WorkoutsHistoryTable.DATE + ") =?" +
                " ORDER BY " + DatabaseContract.WorkoutsHistoryTable.DATE + " DESC";

        Cursor cursor = database.rawQuery(query, whereArgs);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Workout workout = new Workout();
                workout.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable._ID)));
                workout.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable.WORKOUT_NAME)));
                workout.setWorkoutId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable.WORKOUT_ID)));
                workout.setDate(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable.DATE)));
                workouts.add(workout);
            }
        }
        cursor.close();
        return workouts;
    }

    public List<Workout> findWorkoutHistoryOfThatWeek(String date1, String date2) {
        String sortOrder = DatabaseContract.WorkoutsHistoryTable.DATE + " DESC";
        List<Workout> workouts = new ArrayList<>();
        String[] whereArgs = {date1,date2};

        String query = "SELECT " + DatabaseContract.WorkoutsHistoryTable._ID + ", " +
                DatabaseContract.WorkoutsHistoryTable.WORKOUT_NAME + ", " +
                DatabaseContract.WorkoutsHistoryTable.WORKOUT_ID + ", " +
                "strftime('%s', " + DatabaseContract.WorkoutsHistoryTable.DATE + ") " + "AS " + DatabaseContract.WorkoutsHistoryTable.DATE +
                " FROM " + DatabaseContract.WorkoutsHistoryTable.TABLE_NAME +
                " WHERE " + "strftime('%Y-%m-%d', " + DatabaseContract.WorkoutsHistoryTable.DATE + ") BETWEEN ? AND ?"+
                " ORDER BY " + DatabaseContract.WorkoutsHistoryTable.DATE + " DESC";

        Cursor cursor = database.rawQuery(query, whereArgs);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Workout workout = new Workout();
                workout.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable._ID)));
                workout.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable.WORKOUT_NAME)));
                workout.setWorkoutId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable.WORKOUT_ID)));
                workout.setDate(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable.DATE)));
                workouts.add(workout);
            }
        }
        cursor.close();
        return workouts;
    }

    public void removeWorkoutHistory(Workout workout){
        String where = DatabaseContract.WorkoutsHistoryTable._ID + "=?";
        String[] whereArgs = {String.valueOf(workout.getId())};

        database.delete(DatabaseContract.WorkoutsHistoryTable.TABLE_NAME, where, whereArgs);
    }

    public Workout getPreviousWorkout(Workout workout) {
        Workout returnWorkout = new Workout();

        String query = "SELECT * FROM " + DatabaseContract.WorkoutsHistoryTable.TABLE_NAME +
                " WHERE " + DatabaseContract.WorkoutsHistoryTable.WORKOUT_ID + "=?" +
                " ORDER BY " + DatabaseContract.WorkoutsHistoryTable.DATE + " DESC LIMIT 1";
        String[] whereArgs = { String.valueOf(workout.getId()) };

        Cursor cursor = database.rawQuery(query, whereArgs);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            returnWorkout.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable._ID)));
            returnWorkout.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable.WORKOUT_NAME)));
            returnWorkout.setWorkoutId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable.WORKOUT_ID)));
            returnWorkout.setDate(cursor.getLong(cursor.getColumnIndex(DatabaseContract.WorkoutsHistoryTable.DATE)));
        }
        return returnWorkout;
    }

    public Set createSetHistory(Set set){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.SetsHistoryTable.SET_GROUP_ID, set.getSetGroupId());
        values.put(DatabaseContract.SetsHistoryTable.ORDER_NR, set.getOrderNr());
        values.put(DatabaseContract.SetsHistoryTable.REST, set.getRest());
        values.put(DatabaseContract.SetsHistoryTable.EXERCISE_NAME, set.getExerciseName());
        values.put(DatabaseContract.SetsHistoryTable.REPS_TIME, set.getReps());
        values.put(DatabaseContract.SetsHistoryTable.WEIGHT, set.getWeight());
        values.put(DatabaseContract.SetsHistoryTable.NOTES, set.getNotes());
        values.put(DatabaseContract.SetsHistoryTable.IS_TIME, set.isTime());
        values.put(DatabaseContract.SetsHistoryTable.WORKOUT_HISTORY_ID, set.getWorkoutHistoryId());

        long newRowId = database.insert(DatabaseContract.SetsHistoryTable.TABLE_NAME, null, values);
        set.setId(newRowId);
        return set;
    }

    public List<Set> findAllSetsHistory(Workout workout){
        List<Set> sets = new ArrayList<>();

        String where = DatabaseContract.SetsHistoryTable.WORKOUT_HISTORY_ID + "=?";
        String[] whereArgs = { String.valueOf(workout.getId()) };

        Cursor cursor = database.query(
                DatabaseContract.SetsHistoryTable.TABLE_NAME,
                set_history_columns,
                where,
                whereArgs,
                null,
                null,
                null
        );

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()){
                Set set = new Set();
                Boolean isTime = false;
                if (cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetsHistoryTable.IS_TIME)) == 1){
                    isTime = true;
                }
                set.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.SetsHistoryTable._ID)));
                set.setSetGroupId(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetsHistoryTable.SET_GROUP_ID)));
                set.setOrderNr(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetsHistoryTable.ORDER_NR)));
                set.setRest(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetsHistoryTable.REST)));
                set.setExerciseName(cursor.getString(cursor.getColumnIndex(DatabaseContract.SetsHistoryTable.EXERCISE_NAME)));
                set.setReps(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetsHistoryTable.REPS_TIME)));
                set.setWeight(cursor.getInt(cursor.getColumnIndex(DatabaseContract.SetsHistoryTable.WEIGHT)));
                set.setNotes(cursor.getString(cursor.getColumnIndex(DatabaseContract.SetsHistoryTable.NOTES)));
                set.setTime(isTime);
                set.setDate(cursor.getLong(cursor.getColumnIndex(DatabaseContract.SetsHistoryTable.DATE)));
                set.setWorkoutHistoryId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.SetsHistoryTable.WORKOUT_HISTORY_ID)));
                sets.add(set);
            }
        }

        cursor.close();
        return sets;
    }
    ///////////////////////////////////////////////////////////////
}
