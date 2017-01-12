package com.dainavahood.workoutlogger.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SetGroup implements Parcelable{
    private long id;
    private String name;
    private long workoutId;
    private int orderNr;
    private List<Set> sets;
    private long date;

    public SetGroup(){}

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

    public long getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(long workoutId) {
        this.workoutId = workoutId;
    }

    public int getOrderNr() {
        return orderNr;
    }

    public void setOrderNr(int orderNr) {
        this.orderNr = orderNr;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }

    public SetGroup(Parcel in) {
        id = in.readLong();
        name = in.readString();
        workoutId = in.readLong();
        orderNr = in.readInt();
        sets = in.createTypedArrayList(Set.CREATOR);
        date = in.readLong();
    }

    public static final Creator<SetGroup> CREATOR = new Creator<SetGroup>() {
        @Override
        public SetGroup createFromParcel(Parcel in) {
            return new SetGroup(in);
        }

        @Override
        public SetGroup[] newArray(int size) {
            return new SetGroup[size];
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
        dest.writeLong(workoutId);
        dest.writeInt(orderNr);
        dest.writeTypedList(sets);
        dest.writeLong(date);
    }
}
