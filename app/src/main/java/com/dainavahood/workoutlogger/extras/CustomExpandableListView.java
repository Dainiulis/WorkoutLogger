package com.dainavahood.workoutlogger.extras;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.model.Set;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListView extends BaseExpandableListAdapter {

    private Context context;
    private List<Set> sets;

    public CustomExpandableListView(Context context, List<Set> sets) {
        this.context = context;
        this.sets = sets;
    }

    @Override
    public int getGroupCount() {
        return sets.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return sets.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return sets.get(groupPosition).getNotes();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_set_group_history_list, parent, false);
        }

        TextView exerciseName = (TextView) convertView.findViewById(R.id.exerciseName1);
        TextView orderNr = (TextView) convertView.findViewById(R.id.orderNr1);
        TextView reps = (TextView) convertView.findViewById(R.id.reps1);
        TextView weight = (TextView) convertView.findViewById(R.id.weight1);
        TextView rest = (TextView) convertView.findViewById(R.id.rest1);

        Set set = (Set) getGroup(groupPosition);

        exerciseName.setText(set.getExerciseName());
        orderNr.setText(String.valueOf(set.getOrderNr()+1));
        reps.setText(String.valueOf(set.getReps()));
        weight.setText(String.valueOf(set.getWeight()));
        rest.setText(String.valueOf(set.getRest()));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_set_group_history_list_child, parent, false);
        }

        TextView tvNote = (TextView) convertView.findViewById(R.id.notes);

        String noteText = (String) getChild(groupPosition, childPosition);

        tvNote.setText(noteText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
