package com.example.flaforgue.geophone.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.flaforgue.geophone.managers.DeviceComponentManager;
import com.example.flaforgue.geophone.R;
import com.example.flaforgue.geophone.managers.clicklisteners.GetItBtnClickListenerManager;

public class CloseActivity extends AppCompatActivity {

    private Button getItBtn;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        this.getItBtn = (Button) findViewById(R.id.getItBtn);
        this.message = (TextView) findViewById(R.id.txtMessage);

        //Affichage du message personnalisé si configuré
        this.message.setText(prefs.getString("pref_message","Je suis là"));

        //Fermeture de l'application
        this.getItBtn.setOnClickListener(new GetItBtnClickListenerManager(this));
    }
}
