package com.dainavahood.workoutlogger.extras;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

public class DateDialog extends DialogFragment {

    DatePickerDialog.OnDateSetListener onDateSetListener;

    public void setCallBack(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int year = bundle.getInt("year");
        int month = bundle.getInt("month");
        int day = bundle.getInt("day");
        return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
    }

}
