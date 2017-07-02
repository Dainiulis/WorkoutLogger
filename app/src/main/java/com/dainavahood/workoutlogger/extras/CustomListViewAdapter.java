package com.dainavahood.workoutlogger.extras;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dainavahood.workoutlogger.R;
import com.dainavahood.workoutlogger.model.Set;
import com.dainavahood.workoutlogger.model.SetGroup;

import java.util.List;

public class CustomListViewAdapter extends ArrayAdapter{

    private final List<SetGroup> setGroups;
    private List<Set> sets;
    private int count;

    public CustomListViewAdapter(Activity context, List<SetGroup> setGroups) {
        super(context, R.layout.custom_list_view, setGroups);

        this.setGroups = setGroups;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view, null, true);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int orderNr = setGroups.get(position).getOrderNr();

        viewHolder.tv.setText(orderNr+1 + ") " + setGroups.get(position).getName());

        sets = setGroups.get(position).getSets();
        count = 0;
        for (Set set: sets) {
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));
            if (count%2!=0) {
                row.setBackgroundColor(getContext().getResources().getColor(R.color.divider));
            }

            row.setId(100 + count);
            row = addColumn(String.valueOf(set.getOrderNr()+1), row, 200+count);
            row = addColumn(set.getExerciseName(), row, 201+count);
            row = addColumn(String.valueOf(set.getReps()), row, 202+count);
            row = addColumn(String.valueOf(set.getWeight()), row, 203+count);
            row = addColumn(String.valueOf(set.getRest()), row, 204+count);

            viewHolder.table.addView(row, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            count++;

        }

        return convertView;
    }

    private static class ViewHolder {
        private View view;
        private TextView tv;
        private TableLayout table;
        private ViewHolder(View view) {
            this.view = view;
            tv = (TextView) view.findViewById(R.id.setGroupName);
            table = (TableLayout) view.findViewById(R.id.table);
        }
    }

    private TableRow addColumn(String columnValue, TableRow row, int idPrefix){
        TextView tv = new TextView(getContext());
        tv.setId(idPrefix);
        tv.setText(columnValue);
        tv.setPadding(2, 2, 2, 2);
        if (idPrefix == 201+count){
            tv.setEms(5);
        }
        row.addView(tv);

        return row;
    }
}
