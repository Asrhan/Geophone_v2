package com.example.flaforgue.geophone.managers.clicklisteners;

import android.view.View;

import com.example.flaforgue.geophone.activities.HomeActivity;

/**
 * Created by flaforgue on 21/11/2016.
 */

public class FindBtnClickListenerManager implements View.OnClickListener {

    private HomeActivity parentActivity;

    public FindBtnClickListenerManager(HomeActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    /**
     * Lancement de la localisation lorsqu'on clique sur le bouton
     * @param v
     */
    @Override
    public void onClick(View v) {
        String destination = this.parentActivity.getNumberInputValue();
        if (null != destination && ! destination.isEmpty()) {
            this.parentActivity.localize(destination);
        }
    }
}
