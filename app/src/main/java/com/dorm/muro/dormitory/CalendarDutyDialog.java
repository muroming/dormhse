package com.dorm.muro.dormitory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.savvi.rangedatepicker.CalendarPickerView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarDutyDialog extends DialogFragment {

    private CalendarPickerView mCalendar;
    private Button mPositiveButton;
    private Toast invalidDatesToast;


    public String dateSelected;
    private Date dateBoundsMin, dateBoundsMax;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.calendar_duty_dialog, null);
        dateSelected = "";
        mCalendar = view.findViewById(R.id.settings_date_picker_calendar);
        invalidDatesToast = Toast.makeText(getContext(), getString(R.string.settings_date_picker_invalid_dates), Toast.LENGTH_SHORT);

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        dateBoundsMin = c.getTime();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH) + 1);
        dateBoundsMax = c.getTime();

        mCalendar.init(dateBoundsMin, dateBoundsMax)
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                .withSelectedDate(date);

        builder.setMessage(getString(R.string.settings_date_picker_title))
                .setView(view)
                .setPositiveButton(getString(R.string.select), positiveListener)
                .setCancelable(true)
                .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mPositiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            }
        });

        mCalendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                if (!checkDates(mCalendar.getSelectedDates())) {
                    mPositiveButton.setEnabled(false);
                    invalidDatesToast.show();
                } else {
                    mPositiveButton.setEnabled(true);
                }
            }

            @Override
            public void onDateUnselected(Date date) {
                if (!checkDates(mCalendar.getSelectedDates())) {
                    mPositiveButton.setEnabled(false);
                    invalidDatesToast.show();
                } else {
                    mPositiveButton.setEnabled(true);
                }
            }
        });
        return dialog;
    }

    DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            List<Date> selectedDate = mCalendar.getSelectedDates();
            dateSelected = dateFormat.format(selectedDate.get(0)) + " - " + dateFormat.format(selectedDate.get(1));
        }
    };

    //TODO: add more checks
    private boolean checkDates(List<Date> selectedDates) {
        if (selectedDates.size() != 2) {
            return false;
        }
        return true;
    }
}
