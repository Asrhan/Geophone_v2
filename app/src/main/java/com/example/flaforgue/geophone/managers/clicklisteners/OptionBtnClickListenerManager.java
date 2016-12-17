package com.example.flaforgue.geophone.managers.clicklisteners;

import android.content.Intent;
import android.view.View;

import com.example.flaforgue.geophone.activities.superactivities.ActivityWithMenu;
import com.example.flaforgue.geophone.activities.SettingsActivity;

/**
 * Created by Baptite_Portable on 16/12/2016.
 */

public class OptionBtnClickListenerManager implements View.OnClickListener {

    private ActivityWithMenu parentActivity;

    public OptionBtnClickListenerManager(ActivityWithMenu parentActivity) {
        this.parentActivity = parentActivity;
    }

    @Override
    public void onClick(View v) {
        Intent settingsIntent = new Intent(this.parentActivity, SettingsActivity.class);
        this.parentActivity.startActivity(settingsIntent);
    }
}
