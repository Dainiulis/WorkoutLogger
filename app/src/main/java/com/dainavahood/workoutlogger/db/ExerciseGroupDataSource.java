package com.dainavahood.workoutlogger.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ExerciseGroupDataSource {

    SQLiteOpenHelper dbhelper;
    SQLiteDatabase database;

    //Eilutes kurias naudosiu
    private static final String[] exercise_group_columns = {
            DatabaseContract.ExerciseGroupTable._ID,
            DatabaseContract.ExerciseGroupTable.EX_GROUP_NAME
    };

    public ExerciseGroupDataSource(Context context){
        dbhelper = new DatabaseHelper(context);
    }

    //Atidaro duombaze
    public void open(){
        database = dbhelper.getWritableDatabase();
    }

    //Uzdaro duombaze (reikia uzdaryti iseinant is activity, nes gali buti SQL nutekejimu)
    public void close(){
        dbhelper.close();
    }

    public void create(String groupName){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ExerciseGroupTable.EX_GROUP_NAME, groupName);
        database.insert(DatabaseContract.ExerciseGroupTable.TABLE_NAME, null, values);
    }

    public List<String> findAll(){
        List<String> exercisesGroup = new ArrayList<>();

        Cursor cursor = database.query(
                DatabaseContract.ExerciseGroupTable.TABLE_NAME,
                exercise_group_columns,
                null,
                null,
                null,
                null,
                null);

        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()){
                exercisesGroup.add(cursor.getString(cursor.getColumnIndex(DatabaseContract.ExerciseGroupTable.EX_GROUP_NAME)));
            }
        }

        cursor.close();
        return exercisesGroup;
    }

    public void removeExerciseGroup (String exerciseGroup) {

        String where = DatabaseContract.ExerciseGroupTable.EX_GROUP_NAME + "=?";
        String[] whereargs = { exerciseGroup };
        database.delete(DatabaseContract.ExerciseGroupTable.TABLE_NAME, where, whereargs);

    }

}
