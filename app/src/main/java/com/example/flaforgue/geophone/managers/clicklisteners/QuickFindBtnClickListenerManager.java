package com.example.flaforgue.geophone.managers.clicklisteners;

import android.view.View;

import com.example.flaforgue.geophone.activities.HomeActivity;

/**
 * Created by Baptite_Portable on 17/12/2016.
 */

public class QuickFindBtnClickListenerManager implements View.OnClickListener {
    private HomeActivity parentActivity;
    private String number;

    public QuickFindBtnClickListenerManager(HomeActivity parentActivity, String num) {
        this.parentActivity = parentActivity;
        this.number = num;
    }

    @Override
    public void onClick(View v) {
        this.parentActivity.localize(number);
    }
}
