package com.example.flaforgue.geophone.activities;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.flaforgue.geophone.R;
import com.example.flaforgue.geophone.managers.clicklisteners.ListNumberItemClickListenerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity {

    private ListView listNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        //Récupération de l'historique des recherches
        Intent intentList = getIntent();
        HashMap<String,List<Location>> locationsMap = (HashMap<String,List<Location>>)intentList.getSerializableExtra("locations");

        this.listNumber = (ListView) findViewById(R.id.listNumber);

        //Affichage de la liste des numéros recherchés
        List<String> numberList = new ArrayList<>(locationsMap.keySet());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,numberList);
        listNumber.setAdapter(arrayAdapter);

        listNumber.setOnItemClickListener(new ListNumberItemClickListenerManager(this, locationsMap));
    }
}
