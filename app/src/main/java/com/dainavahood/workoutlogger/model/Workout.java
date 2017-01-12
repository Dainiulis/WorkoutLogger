package com.dainavahood.workoutlogger.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Workout implements Parcelable{

    private long id;
    private String name;
    private long date;
    private long workoutId;

    public Workout(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(long workoutId) {
        this.workoutId = workoutId;
    }

    @Override
    public String toString() {
        return name;
    }

    protected Workout(Parcel in) {
        id = in.readLong();
        name = in.readString();
        date = in.readLong();
        workoutId = in.readLong();
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(date);
        dest.writeLong(workoutId);
    }
}
