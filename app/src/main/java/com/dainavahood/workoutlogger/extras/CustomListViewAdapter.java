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

    private final Activity context;
    private final List<SetGroup> setGroups;
    private List<Set> sets;
    private int count;

    public CustomListViewAdapter(Activity context, List<SetGroup> setGroups) {
        super(context, R.layout.custom_list_view, setGroups);

        this.context = context;
        this.setGroups = setGroups;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_list_view, null, true);

        TableLayout table = (TableLayout) rowView.findViewById(R.id.table);

        int orderNr = setGroups.get(position).getOrderNr();

        TextView tv = (TextView) rowView.findViewById(R.id.setGroupName);
        tv.setText(orderNr+1 + ") " + setGroups.get(position).getName());

        sets = setGroups.get(position).getSets();
        count = 0;
        for (Set set: sets) {
            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));
            if (count%2!=0) {
                row.setBackgroundColor(context.getResources().getColor(R.color.divider));
            }

            row.setId(100 + count);
            row = addColumn(String.valueOf(set.getOrderNr()+1), row, 200+count);
            row = addColumn(set.getExerciseName(), row, 201+count);
            row = addColumn(String.valueOf(set.getReps()), row, 202+count);
            row = addColumn(String.valueOf(set.getWeight()), row, 203+count);
            row = addColumn(String.valueOf(set.getRest()), row, 204+count);

            table.addView(row, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            count++;

        }

        return rowView;
    }

    private TableRow addColumn(String columnValue, TableRow row, int idPrefix){
        TextView tv = new TextView(context);
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
