package com.dorm.muro.dormitory;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private DialogResultListener resultListener;
    public String dialogTag;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialogTag = getArguments().getString("DIALOG_TAG");
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        return new DatePickerDialog(getActivity(), this, year, month, 1);
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        resultListener.onDateSelected(year, month, dialogTag);
    }


    public interface DialogResultListener {
        void onDateSelected(int year, int month, String dialogTag);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            resultListener = (DialogResultListener) context;
        } catch (ClassCastException ignored) {
        }

    }
}
