package com.dainavahood.workoutlogger.extras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.model.Workout;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomWorkoutsHistoryLayoutAdapter extends ArrayAdapter implements Filterable {

    private final Context context;
    private List<Workout> workouts;
    private List<Workout> originalWorkouts;
//    private static int layout = R.layout.custom_workout_history_list;

    public CustomWorkoutsHistoryLayoutAdapter(Context context, List<Workout> workouts) {
        super(context, R.layout.custom_workout_history_list, workouts);
        this.context = context;
        this.workouts = workouts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_workout_history_list, parent, false);

        TextView workoutName = (TextView) rowView.findViewById(R.id.workoutName);
        TextView workoutDate = (TextView) rowView.findViewById(R.id.workoutDate);


        workoutName.setText(workouts.get(position).getName());

        long unixSeconds = workouts.get(position).getDate();
        String formattedDate = getDate(unixSeconds);
        workoutDate.setText(formattedDate);

        return rowView;
    }

    private String getDate(long unixSeconds) {
        Timestamp timestamp = new Timestamp(unixSeconds * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd EE");
        String formattedDate = simpleDateFormat.format(timestamp);
        return formattedDate;
    }
}
