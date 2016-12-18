package com.example.flaforgue.geophone.managers.clicklisteners;

import android.content.Intent;
import android.location.Location;
import android.view.View;

import com.example.flaforgue.geophone.activities.ArchiveActivity;
import com.example.flaforgue.geophone.activities.HomeActivity;
import com.example.flaforgue.geophone.managers.database.SQLManager;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Baptite_Portable on 18/12/2016.
 */

public class MapBtnClickListenerManager implements View.OnClickListener {
    private HomeActivity parentActivity;
    private SQLManager manager;
    private HashMap<String, List<Location>> locationsMap;

    public MapBtnClickListenerManager(HomeActivity parentActivity) {
        this.parentActivity = parentActivity;
        this.manager = new SQLManager(parentActivity);
        this.locationsMap = this.manager.getData();
    }

    /**
     * Affichage de l'historique des recherches
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent archiveIntent = new Intent(this.parentActivity, ArchiveActivity.class);
        archiveIntent.putExtra("locations", locationsMap);
        this.parentActivity.startActivity(archiveIntent);
    }

}
