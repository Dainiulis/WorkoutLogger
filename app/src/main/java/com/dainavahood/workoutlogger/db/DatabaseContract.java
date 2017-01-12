package com.dainavahood.workoutlogger.db;


import android.provider.BaseColumns;

public class DatabaseContract {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "workouts.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String CREATE_TABLE_STRING = "CREATE TABLE ";

    private DatabaseContract(){}

    public static abstract class ExercisesTable implements BaseColumns {
        public static final String TABLE_NAME = "exercises";
        public static final String EXERCISE_ID = "exerciseId";
        public static final String EX_NAME = "name";
        public static final String EX_TYPE = "type";
        public static final String EX_GROUP = "exercise_group";


        public static final String CREATE_TABLE =
                CREATE_TABLE_STRING + TABLE_NAME + " (" +
                        EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        EX_NAME + TEXT_TYPE + COMMA_SEP +
                        EX_TYPE + TEXT_TYPE + COMMA_SEP +
                        EX_GROUP + TEXT_TYPE +
                        ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class ExerciseGroupTable implements BaseColumns {
        public static final String TABLE_NAME = "exercise_group";
        public static final String EX_GROUP_NAME = "exercise_name";

        public static final String CREATE_TABLE =
                CREATE_TABLE_STRING + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        EX_GROUP_NAME + TEXT_TYPE +
                        ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static abstract class WorkoutsTable implements BaseColumns {
        public static final String TABLE_NAME = "workouts";
        public static final String WORKOUT_NAME = "workout_name";
        public static final String DATE = "date_timestamp";

        public static final String CREATE_TABLE =
                CREATE_TABLE_STRING + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORKOUT_NAME + TEXT_TYPE + COMMA_SEP +
                        DATE + " TIMESTAMP NOT NULL DEFAULT current_timestamp" +
                        ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class SetGroupTable implements BaseColumns {
        public static final String TABLE_NAME = "set_group";
        public static final String NAME = "name";
        public static final String WORKOUT_ID = "workoutId";
        public static final String ORDER_NR = "orderNr";
        public static final String DATE = "date_timestamp";

        public static final String CREATE_TABLE =
                CREATE_TABLE_STRING + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NAME + TEXT_TYPE + COMMA_SEP +
                        ORDER_NR + " INTEGER NOT NULL," +
                        DATE + " TIMESTAMP NOT NULL DEFAULT current_timestamp," +
                        WORKOUT_ID + " INTEGER," +
                        " FOREIGN KEY(" + WORKOUT_ID + ") REFERENCES " + WorkoutsTable.TABLE_NAME + "(" + WorkoutsTable._ID + ") ON DELETE CASCADE" +
                        ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class SetsTable implements BaseColumns {
        public static final String TABLE_NAME = "sets";
        public static final String SET_GROUP_ID = "set_groupId";
        public static final String ORDER_NR = "orderNr";
        public static final String REST = "rest";
        public static final String EXERCISE_NAME = "exercise_name";
        public static final String REPS_TIME = "reps_time";
        public static final String WEIGHT = "weight";
        public static final String NOTES = "notes";
        public static final String IS_TIME = "is_time";
        public static final String DATE = "date_timestamp";

        public static final String CREATE_TABLE =
                CREATE_TABLE_STRING + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ORDER_NR + " INTEGER NOT NULL, " +
                        REST + " INTEGER, " +
                        EXERCISE_NAME + TEXT_TYPE + COMMA_SEP +
                        REPS_TIME + " INTEGER, " +
                        WEIGHT + " DOUBLE, " +
                        IS_TIME + " INTEGER, " +
                        NOTES + TEXT_TYPE + COMMA_SEP +
                        DATE + " TIMESTAMP NOT NULL DEFAULT current_timestamp," +
                        SET_GROUP_ID + " INTEGER, " +
                        "FOREIGN KEY(" + SET_GROUP_ID + ") REFERENCES " + SetGroupTable.TABLE_NAME + "(" + SetGroupTable._ID + ") ON DELETE CASCADE" +
                        ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

//--------------------------WORKOUTS HISTORY-----------------------------------------------------

    public static abstract class SetsHistoryTable implements BaseColumns {
        public static final String TABLE_NAME = "sets_history";
        public static final String SET_GROUP_ID = "set_groupId";
        public static final String ORDER_NR = "orderNr";
        public static final String REST = "rest";
        public static final String EXERCISE_NAME = "exercise_name";
        public static final String REPS_TIME = "reps_time";
        public static final String WEIGHT = "weight";
        public static final String NOTES = "notes";
        public static final String IS_TIME = "is_time";
        public static final String WORKOUT_HISTORY_ID = "workout_historyId";
        public static final String DATE = "date_timestamp";

        public static final String CREATE_TABLE =
                CREATE_TABLE_STRING + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ORDER_NR + " INTEGER NOT NULL, " +
                        REST + " INTEGER, " +
                        EXERCISE_NAME + TEXT_TYPE + COMMA_SEP +
                        REPS_TIME + " INTEGER, " +
                        WEIGHT + " DOUBLE, " +
                        IS_TIME + " INTEGER, " +
                        NOTES + TEXT_TYPE + COMMA_SEP +
                        DATE + " TIMESTAMP NOT NULL DEFAULT current_timestamp," +
                        SET_GROUP_ID + " INTEGER, " +
                        WORKOUT_HISTORY_ID + " INTEGER, " +
                        "FOREIGN KEY(" + WORKOUT_HISTORY_ID + ") REFERENCES " + WorkoutsHistoryTable.TABLE_NAME + "(" + WorkoutsHistoryTable._ID + ") ON DELETE CASCADE" +
                        ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class WorkoutsHistoryTable implements BaseColumns {
        public static final String TABLE_NAME = "workouts_history";
        public static final String WORKOUT_NAME = "workout_name";
        public static final String WORKOUT_ID = "wokroutId";
        public static final String DATE = "date_timestamp";

        public static final String CREATE_TABLE =
                CREATE_TABLE_STRING + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WORKOUT_NAME + TEXT_TYPE + COMMA_SEP +
                        WORKOUT_ID + " INTEGER, " +
                        DATE + " TIMESTAMP NOT NULL DEFAULT current_timestamp" +
                        ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
