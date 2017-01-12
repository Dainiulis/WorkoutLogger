package com.dainavahood.workoutlogger.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


//Naudojant SQLiteAssetHelper sql upgrade statementuose nerasyt BEGIN ir COMMIT TRANSACTION
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.ExercisesTable.CREATE_TABLE);
        db.execSQL(DatabaseContract.ExerciseGroupTable.CREATE_TABLE);
        db.execSQL(DatabaseContract.WorkoutsTable.CREATE_TABLE);
        db.execSQL(DatabaseContract.SetGroupTable.CREATE_TABLE);
        db.execSQL(DatabaseContract.SetsTable.CREATE_TABLE);
        db.execSQL(DatabaseContract.SetsHistoryTable.CREATE_TABLE);
        db.execSQL(DatabaseContract.WorkoutsHistoryTable.CREATE_TABLE);
//        db.execSQL(DatabaseContract.SetGroupHistoryTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.ExercisesTable.DELETE_TABLE);
        db.execSQL(DatabaseContract.ExerciseGroupTable.DELETE_TABLE);
        db.execSQL(DatabaseContract.WorkoutsTable.DELETE_TABLE);
        db.execSQL(DatabaseContract.SetGroupTable.DELETE_TABLE);
        db.execSQL(DatabaseContract.SetsTable.DELETE_TABLE);
        db.execSQL(DatabaseContract.SetsHistoryTable.DELETE_TABLE);
        db.execSQL(DatabaseContract.WorkoutsHistoryTable.DELETE_TABLE);
//        db.execSQL(DatabaseContract.SetGroupHistoryTable.DELETE_TABLE);
        onCreate(db);
    }
}
