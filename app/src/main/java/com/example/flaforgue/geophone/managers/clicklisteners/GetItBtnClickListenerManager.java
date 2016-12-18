package com.example.flaforgue.geophone.managers.clicklisteners;

import android.content.Intent;
import android.location.Location;
import android.view.View;

import com.example.flaforgue.geophone.activities.ArchiveActivity;
import com.example.flaforgue.geophone.activities.CloseActivity;
import com.example.flaforgue.geophone.activities.HomeActivity;
import com.example.flaforgue.geophone.managers.DeviceComponentManager;
import com.example.flaforgue.geophone.managers.database.SQLManager;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Baptite_Portable on 18/12/2016.
 */

public class GetItBtnClickListenerManager implements View.OnClickListener {
    private CloseActivity parentActivity;

    public GetItBtnClickListenerManager(CloseActivity parentActivity) {
        this.parentActivity = parentActivity;
    }

    /**
     * Fermeture de l'application une fois le telephone trouvé
     * @param v
     */
    @Override
    public void onClick(View v) {
        DeviceComponentManager.turnOffFlash();
        DeviceComponentManager.stopVibrate();
        DeviceComponentManager.stopSound();
        this.parentActivity.finishAffinity();
    }
}

