package com.dainavahood.workoutlogger.extras;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.model.Set;

import java.util.List;

public class CustomSetsLayoutAdapter extends ArrayAdapter {

    private static final long INVALID_ID = -1;
    private final Context context;
    private final List<Set> sets;
    private View rowView;

    public CustomSetsLayoutAdapter(Context context, List<Set> sets) {
        super(context, R.layout.custom_set_group_list_layout, sets);
        this.context = context;
        this.sets = sets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.custom_set_group_list_layout, parent, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rowView.setBackground(context.getResources().getDrawable(R.drawable.selected_items));
        } else {
            rowView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.selected_items));
        }

        TextView orderNr = (TextView) rowView.findViewById(R.id.orderNr);
        TextView exerciseName = (TextView) rowView.findViewById(R.id.exerciseName);
        TextView reps = (TextView) rowView.findViewById(R.id.reps);
        TextView weight = (TextView) rowView.findViewById(R.id.weight);
        TextView rest = (TextView) rowView.findViewById(R.id.rest);

        orderNr.setText(String.valueOf(sets.get(position).getOrderNr() + 1));
        exerciseName.setText(sets.get(position).getExerciseName());
        reps.setText(String.valueOf(sets.get(position).getReps()));
        weight.setText(String.valueOf(sets.get(position).getWeight()));
        rest.setText(String.valueOf(sets.get(position).getRest()));

        return rowView;

    }

}
