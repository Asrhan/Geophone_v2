package com.example.flaforgue.geophone.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.flaforgue.geophone.managers.clicklisteners.HomeBtnClickListenerManager;
import com.example.flaforgue.geophone.R;

/**
 * Sert à afficher une activity en attendant la fin de la recherche
 */
public class RadarActivity extends AppCompatActivity {

    private FloatingActionButton homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        this.homeBtn = (FloatingActionButton) this.findViewById(R.id.homeBtn);
        this.homeBtn.setOnClickListener(new HomeBtnClickListenerManager(this));
    }
}
