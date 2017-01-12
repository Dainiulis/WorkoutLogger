package com.dainavahood.workoutlogger.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dainavahood.workoutlogger.model.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExercisesDataSource {

    SQLiteOpenHelper dbhelper;
    SQLiteDatabase database;

    //Eilutes kurias naudosiu
    private static final String[] exercises_columns = {
            DatabaseContract.ExercisesTable.EXERCISE_ID,
            DatabaseContract.ExercisesTable.EX_NAME,
            DatabaseContract.ExercisesTable.EX_GROUP,
            DatabaseContract.ExercisesTable.EX_TYPE
    };
    //Rikiavimas
    private static final String sortOrder = DatabaseContract.ExercisesTable.EX_NAME + " ASC";

    public ExercisesDataSource(Context context) {
        dbhelper = new DatabaseHelper(context);
    }

    //Patikrina ar duombaze atidaryta
    public boolean databaseOpenState(){
        return database.isOpen();
    }

    //Atidaro duombaze
    public void open(){
        database = dbhelper.getWritableDatabase();
    }

    //Uzdaro duombaze (reikia uzdaryti iseinant is activity, nes gali buti SQL nutekejimu)
    public void close(){
        dbhelper.close();
    }

    public Exercise create(Exercise exercise) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ExercisesTable.EX_NAME, exercise.getName());
        values.put(DatabaseContract.ExercisesTable.EX_GROUP, exercise.getExerciseGroup());
        values.put(DatabaseContract.ExercisesTable.EX_TYPE, exercise.getExerciseType());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = database.insert(DatabaseContract.ExercisesTable.TABLE_NAME, null, values);
        exercise.setId(newRowId);
        return exercise;
    }

    public List<Exercise> findAll() {
        List<Exercise> exercises = new ArrayList<>();

        Cursor cursor = database.query(
                DatabaseContract.ExercisesTable.TABLE_NAME,
                exercises_columns,
                null,
                null,
                null,
                null,
                sortOrder);

        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()){
                Exercise exercise = new Exercise();
                exercise.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EXERCISE_ID)));
                exercise.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EX_NAME)));
                exercise.setExerciseGroup(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EX_GROUP)));
                exercise.setExerciseType(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EX_TYPE)));
                exercises.add(exercise);
            }
        }
        cursor.close();
        return exercises;
    }

    public List<Exercise> findByMuscleGroup(String muscleGroup) {
        List<Exercise> exercises = new ArrayList<Exercise>();

        String whereMuscleGroup = DatabaseContract.ExercisesTable.EX_GROUP + " = ? COLLATE NOCASE";
        String[] whereArgs = { muscleGroup };

        //Gauti visus exercises iš pasirinkto muscle_group is duomenu bazes
        Cursor cursor = database.query(
                DatabaseContract.ExercisesTable.TABLE_NAME,
                exercises_columns,
                whereMuscleGroup,
                whereArgs,
                null,
                null,
                sortOrder);

        //prideda kiekviena exercise name i sarasa List<String> exercises
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()){
                Exercise exercise = new Exercise();
                exercise.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EXERCISE_ID)));
                exercise.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EX_NAME)));
                exercise.setExerciseGroup(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EX_GROUP)));
                exercise.setExerciseType(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EX_TYPE)));
                exercises.add(exercise);
            }
        }
        cursor.close();
        return exercises;
    }


    //paieskos query kai ieskoma pagal muscle group
    public List<Exercise> findByMuscleGroupSeachQuerry(String query, String exerciseGroup) {
        List<Exercise> exercises = new ArrayList<>();

        String where = DatabaseContract.ExercisesTable.EX_NAME + " LIKE ? COLLATE NOCASE AND " + DatabaseContract.ExercisesTable.EX_GROUP + " = ?";
        String[] whereArgs = {"%" + query + "%", exerciseGroup};

        //Gauti visus exercises iš pasirinkto muscle_group is duomenu bazes
        Cursor cursor = database.query(
                DatabaseContract.ExercisesTable.TABLE_NAME,
                exercises_columns,
                where,
                whereArgs,
                null,
                null,
                sortOrder);

        //prideda kiekviena exercise name i sarasa List<String> exercises
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()){
                Exercise exercise = new Exercise();
                exercise.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EXERCISE_ID)));
                exercise.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EX_NAME)));
                exercise.setExerciseGroup(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EX_GROUP)));
                exercise.setExerciseType(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EX_TYPE)));
                exercises.add(exercise);
            }
        }
        cursor.close();
        return exercises;
    }


    //paieskos query, kai ieskoma visu exercise
    public List<Exercise> findAllBySeachQuerry(String query) {
        List<Exercise> exercises = new ArrayList<>();

        String where = DatabaseContract.ExercisesTable.EX_NAME + " LIKE ? COLLATE NOCASE";
        String[] whereArgs = {"%" + query + "%"};

        //Gauti visus exercises iš pasirinkto muscle_group is duomenu bazes
        Cursor cursor = database.query(
                DatabaseContract.ExercisesTable.TABLE_NAME,
                exercises_columns,
                where,
                whereArgs,
                null,
                null,
                sortOrder);

        //prideda kiekviena exercise name i sarasa List<String> exercises
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()){
                Exercise exercise = new Exercise();
                exercise.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EXERCISE_ID)));
                exercise.setName(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EX_NAME)));
                exercise.setExerciseGroup(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EX_GROUP)));
                exercise.setExerciseType(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExercisesTable.EX_TYPE)));
                exercises.add(exercise);
            }
        }
        cursor.close();
        return exercises;
    }

    public void removeExercise(Exercise exercise){
        database = dbhelper.getWritableDatabase();
        String where = DatabaseContract.ExercisesTable.EXERCISE_ID + "=?";
        String[] whereArgs = { String.valueOf(exercise.getId()) };
        database.delete(DatabaseContract.ExercisesTable.TABLE_NAME, where, whereArgs);
    }

    public void removeExercisesWithGroup (String exerciseGroup) {
        String where = DatabaseContract.ExercisesTable.EX_GROUP + "=?";
        String[] whereArgs = { exerciseGroup };
        database.delete(DatabaseContract.ExercisesTable.TABLE_NAME, where, whereArgs);
    }

}
