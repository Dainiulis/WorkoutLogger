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

        GroupViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_set_group_history_list, parent, false);
            viewHolder = new GroupViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }

        Set set = (Set) getGroup(groupPosition);

        viewHolder.exerciseName.setText(set.getExerciseName());
        viewHolder.orderNr.setText(String.valueOf(set.getOrderNr() + 1));
        viewHolder.reps.setText(String.valueOf(set.getReps()));
        viewHolder.weight.setText(String.valueOf(set.getWeight()));
        viewHolder.rest.setText(String.valueOf(set.getRest()));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_set_group_history_list_child, parent, false);
            viewHolder = new ChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        String noteText = (String) getChild(groupPosition, childPosition);

        viewHolder.tvNote.setText(noteText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private static class GroupViewHolder {
        private View view;
        private TextView
                exerciseName,
                orderNr,
                reps,
                weight,
                rest;
        private GroupViewHolder(View view) {
            this.view = view;
            exerciseName = (TextView) view.findViewById(R.id.exerciseName1);
            orderNr = (TextView) view.findViewById(R.id.orderNr1);
            reps = (TextView) view.findViewById(R.id.reps1);
            weight = (TextView) view.findViewById(R.id.weight1);
            rest = (TextView) view.findViewById(R.id.rest1);
        }
    }

    private static class ChildViewHolder {
        private View view;
        private TextView tvNote;

        private ChildViewHolder(View view) {
            this.view = view;
            tvNote = (TextView) view.findViewById(R.id.notes);
        }
    }
}
