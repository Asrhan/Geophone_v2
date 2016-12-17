package com.example.flaforgue.geophone.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.flaforgue.geophone.managers.DeviceComponentManager;
import com.example.flaforgue.geophone.R;

public class CloseActivity extends AppCompatActivity {

    private Button getItBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near);

        this.getItBtn = (Button) findViewById(R.id.getItBtn);
        this.getItBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceComponentManager.turnOffFlash();
                DeviceComponentManager.stopVibrate();
                DeviceComponentManager.stopSound();
                //TODO fermer appli
            }
        });
    }
}
