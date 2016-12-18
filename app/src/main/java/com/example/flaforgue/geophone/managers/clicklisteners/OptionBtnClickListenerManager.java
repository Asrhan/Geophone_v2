package com.example.flaforgue.geophone.managers.clicklisteners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.flaforgue.geophone.activities.HomeActivity;
import com.example.flaforgue.geophone.activities.SettingsActivity;

/**
 * Created by Baptite_Portable on 16/12/2016.
 */

public class OptionBtnClickListenerManager implements View.OnClickListener {

    private HomeActivity parentActivity;

    public OptionBtnClickListenerManager(HomeActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    /**
     * Affichage des options
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent settingsIntent = new Intent(this.parentActivity, SettingsActivity.class);
        this.parentActivity.startActivity(settingsIntent);
    }
}
