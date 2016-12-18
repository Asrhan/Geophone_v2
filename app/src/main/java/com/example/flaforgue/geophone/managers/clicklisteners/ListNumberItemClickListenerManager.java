package com.example.flaforgue.geophone.managers.clicklisteners;

import android.content.Intent;
import android.location.Location;
import android.view.View;
import android.widget.AdapterView;

import com.example.flaforgue.geophone.activities.ArchiveActivity;
import com.example.flaforgue.geophone.activities.MapsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Baptite_Portable on 18/12/2016.
 */

public class ListNumberItemClickListenerManager implements AdapterView.OnItemClickListener {

    private HashMap<String,List<Location>> locationsMap;
    private ArchiveActivity parentActivity;

    public ListNumberItemClickListenerManager(ArchiveActivity parentActivity, HashMap<String,List<Location>> loc) {
        this.parentActivity = parentActivity;
        this.locationsMap = loc;
    }

    /**
     * Affichage de la map avec tout les marqueurs appartenant au numéro recherché
     * sur lequel on a cliqué
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<String> numberList = new ArrayList<>(locationsMap.keySet());
        String number = numberList.get(position);
        Intent archiveIntent = new Intent(parentActivity, MapsActivity.class);
        archiveIntent.putExtra("number",number);
        archiveIntent.putExtra("locations",locationsMap);
        archiveIntent.putExtra("isArchive",true);
        this.parentActivity.startActivity(archiveIntent);
    }
}
