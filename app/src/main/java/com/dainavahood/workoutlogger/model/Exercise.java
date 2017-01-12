package com.dainavahood.workoutlogger.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {
    private long id;
    private String name;
    private String exerciseGroup;
    private String exerciseType;

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

    public String getExerciseGroup() {
        return exerciseGroup;
    }

    public void setExerciseGroup(String exerciseGroup) {
        this.exerciseGroup = exerciseGroup;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    @Override
    public String toString() {
        return name + " - [" + exerciseType + "]";
    }

    public Exercise(){}

    public Exercise(Parcel in) {
        id = in.readLong();
        name = in.readString();
        exerciseGroup = in.readString();
        exerciseType = in.readString();
    }

    public static final Parcelable.Creator<Exercise> CREATOR = new Parcelable.Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
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
        dest.writeString(exerciseGroup);
        dest.writeString(exerciseType);
    }
}
