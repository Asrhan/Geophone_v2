package com.example.flaforgue.geophone.preferences;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

// Based on http://stackoverflow.com/a/7484289/922168

public class TimePreference extends DialogPreference {
    private int mHour = 0;
    private int mMinute = 0;
    private TimePicker picker = null;
    private final String DEFAULT_VALUE = "22:00";

    public static int getHour(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[0]);
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[1]);
    }

    public TimePreference(Context context) {
        this(context, null);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setPositiveButtonText("Ok");
        setNegativeButtonText("Annuler");
    }

    public void setTime(int hour, int minute) {
        mHour = hour;
        mMinute = minute;
        String time = toTime(mHour, mMinute);
        persistString(time);
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    public String toTime(int hour, int minute) {
        String h,m;
        if (hour < 10)
            h = "0" + String.valueOf(hour);
        else
            h = String.valueOf(hour);
        if (minute < 10)
            m = "0" + String.valueOf(minute);
        else
            m = String.valueOf(minute);
        return h + ":" + m;
    }

    public void updateSummary() {
        setSummary(toTime(mHour,mMinute));
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        return picker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.setHour(mHour);
            picker.setMinute(mMinute);
        } else {
            picker.setCurrentHour(mHour);
            picker.setCurrentMinute(mMinute);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            int currHour = 0;
            int currMinute = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                currHour = picker.getHour();
                currMinute = picker.getMinute();
            } else {
                currHour = picker.getCurrentHour();
                currMinute = picker.getCurrentMinute();
            }

            if (!callChangeListener(toTime(currHour, currMinute))) {
                return;
            }

            // persist
            setTime(currHour, currMinute);
            updateSummary();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String time = null;

        if (restorePersistedValue) {
            if (defaultValue == null) {
                time = getPersistedString(DEFAULT_VALUE);
            }
            else {
                time = getPersistedString(DEFAULT_VALUE);
            }
        }
        else {
            time = defaultValue.toString();
        }

        int currMinute = getMinute(time);

        setTime(getHour(time), getMinute(time));
        updateSummary();
    }

    public static Date toDate(String inTime) {
        try {
            DateFormat inTimeFormat = new SimpleDateFormat("HH:mm", Locale.US);
            return inTimeFormat.parse(inTime);
        } catch(ParseException e) {
            return null;
        }
    }

    public static String time24to12(String inTime) {
        Date inDate = toDate(inTime);
        if(inDate != null) {
            DateFormat outTimeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
            return outTimeFormat.format(inDate);
        } else {
            return inTime;
        }
    }
}