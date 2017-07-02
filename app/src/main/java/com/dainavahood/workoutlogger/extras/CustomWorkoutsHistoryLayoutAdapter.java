package com.dainavahood.workoutlogger.extras;

import android.app.Activity;
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

import static com.dainavahood.workoutlogger.R.id.workoutDate;
import static com.dainavahood.workoutlogger.R.id.workoutName;

public class CustomWorkoutsHistoryLayoutAdapter extends ArrayAdapter implements Filterable {

    private List<Workout> workouts;

    public CustomWorkoutsHistoryLayoutAdapter(Context context, List<Workout> workouts) {
        super(context, R.layout.custom_workout_history_list, workouts);
        this.workouts = workouts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_workout_history_list, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.workoutName.setText(workouts.get(position).getName());

        long unixSeconds = workouts.get(position).getDate();
        String formattedDate = getDate(unixSeconds);
        viewHolder.workoutDate.setText(formattedDate);

        return convertView;
    }

    private static class ViewHolder {
        private View view;
        private TextView workoutName, workoutDate;

        private ViewHolder(View view) {
            this.view = view;
            this.workoutName = (TextView) view.findViewById(R.id.workoutName);
            this.workoutDate = (TextView) view.findViewById(R.id.workoutDate);
        }
    }

    private String getDate(long unixSeconds) {
        Timestamp timestamp = new Timestamp(unixSeconds * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd EE");
        String formattedDate = simpleDateFormat.format(timestamp);
        return formattedDate;
    }
}
