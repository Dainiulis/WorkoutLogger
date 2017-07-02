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
    private final List<Set> sets;

    public CustomSetsLayoutAdapter(Context context, List<Set> sets) {
        super(context, R.layout.custom_set_group_list_layout, sets);
        this.sets = sets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHoler viewHoler;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_set_group_list_layout, parent, false);
            viewHoler = new ViewHoler(convertView);
            convertView.setTag(viewHoler);
        } else {
            viewHoler = (ViewHoler) convertView.getTag();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewHoler.view.setBackground(getContext().getResources().getDrawable(R.drawable.selected_items));
        } else {
            viewHoler.view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.selected_items));
        }

        viewHoler.orderNr.setText(String.valueOf(sets.get(position).getOrderNr() + 1));
        viewHoler.exerciseName.setText(sets.get(position).getExerciseName());
        viewHoler.reps.setText(String.valueOf(sets.get(position).getReps()));
        viewHoler.weight.setText(String.valueOf(sets.get(position).getWeight()));
        viewHoler.rest.setText(String.valueOf(sets.get(position).getRest()));

        return convertView;

    }

    private static class ViewHoler {
        private View view;
        private TextView
                orderNr,
                exerciseName,
                reps,
                weight,
                rest;

        private ViewHoler(View view) {
            this.view = view;
            this.orderNr = (TextView) view.findViewById(R.id.orderNr);
            this.exerciseName = (TextView) view.findViewById(R.id.exerciseName);
            this.reps = (TextView) view.findViewById(R.id.reps);
            this.weight = (TextView) view.findViewById(R.id.weight);
            this.rest = (TextView) view.findViewById(R.id.rest);
        }
    }

}
