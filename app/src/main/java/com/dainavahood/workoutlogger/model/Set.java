package com.dainavahood.workoutlogger.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Set implements Parcelable, Cloneable {
    private long id;
    private String exerciseName;
    private long setGroupId;
    private int orderNr;
    private int rest;
    private int reps;
    private double weight;
    private String notes;
    private boolean time;
    private long date;

    private long workoutHistoryId;

    public Set() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public long getSetGroupId() {
        return setGroupId;
    }

    public void setSetGroupId(long setGroupId) {
        this.setGroupId = setGroupId;
    }

    public int getOrderNr() {
        return orderNr;
    }

    public void setOrderNr(int orderNr) {
        this.orderNr = orderNr;
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isTime() {
        return time;
    }

    public void setTime(boolean time) {
        this.time = time;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getWorkoutHistoryId() {
        return workoutHistoryId;
    }

    public void setWorkoutHistoryId(long workoutHistoryId) {
        this.workoutHistoryId = workoutHistoryId;
    }


    @Override
    public String toString() {
        return  getOrderNr()+1 + " | " + getExerciseName() + " | " + getReps() + "reps | " + getWeight() + "wg | " + getRest() + "s rest | " + getNotes() + "\n";
    }

    public Set(Parcel in) {
        id = in.readLong();
        exerciseName = in.readString();
        setGroupId = in.readLong();
        orderNr = in.readInt();
        rest = in.readInt();
        reps = in.readInt();
        weight = in.readDouble();
        notes = in.readString();
        time = in.readByte() != 0;
        date = in.readLong();
        workoutHistoryId = in.readLong();
    }

    public static final Creator<Set> CREATOR = new Creator<Set>() {
        @Override
        public Set createFromParcel(Parcel in) {
            return new Set(in);
        }

        @Override
        public Set[] newArray(int size) {
            return new Set[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(exerciseName);
        dest.writeLong(setGroupId);
        dest.writeInt(orderNr);
        dest.writeInt(rest);
        dest.writeInt(reps);
        dest.writeDouble(weight);
        dest.writeString(notes);
        dest.writeByte((byte) (time ? 1 : 0));
        dest.writeLong(date);
        dest.writeLong(workoutHistoryId);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
