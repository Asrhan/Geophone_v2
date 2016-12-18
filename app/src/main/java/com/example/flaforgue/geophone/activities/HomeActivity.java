package com.example.flaforgue.geophone.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flaforgue.geophone.activities.superactivities.ActivityWithMenu;
import com.example.flaforgue.geophone.managers.clicklisteners.FindBtnClickListenerManager;
import com.example.flaforgue.geophone.managers.database.SQLManager;
import com.example.flaforgue.geophone.managers.message.MessagesManager;
import com.example.flaforgue.geophone.managers.clicklisteners.QuickFindBtnClickListenerManager;
import com.example.flaforgue.geophone.R;

import java.util.HashMap;
import java.util.List;

public class HomeActivity extends ActivityWithMenu {

    private SQLManager manager;
    private HashMap<String, List<Location>> locationMap;

    private static final int PERMISSIONS_REQUEST = 1;

    private Button firstContactBtn;
    private Button secondContactBtn;
    private Button thirdContactBtn;
    private Button fourthContactBtn;
    private Button findBtn;
    private EditText numberInput;
    private TextView txtFavourite;

    private boolean binded;
    private MessagesManager messagesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("INFO", "Démarrage de l'application");

        //ask for permissions
        askPermissions();

        //get parameters stored in settings
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Get locations from local database
        this.manager = new SQLManager(this);
        this.locationMap = this.manager.getData();

        // background service listening to messages of others Geophone applications
        Intent mIntent = new Intent(this, MessagesManager.class);
        bindService(mIntent, messagesManagerConnection, BIND_AUTO_CREATE);

        this.numberInput = (EditText) findViewById(R.id.numberInput);
        this.txtFavourite = (TextView) findViewById(R.id.txtAddContact);
        this.firstContactBtn = (Button) findViewById(R.id.firstBtn);
        this.secondContactBtn = (Button) findViewById(R.id.secondBtn);
        this.thirdContactBtn = (Button) findViewById(R.id.thirdBtn);
        this.fourthContactBtn = (Button) findViewById(R.id.fourthBtn);

        //Display Btn if contact filled
        String contactName = prefs.getString("pref_contact_name_1", "");
        String contactNumber = prefs.getString("pref_contact_number_1","");
        if(contactName != null && !contactName.isEmpty() && contactNumber != null && !contactNumber.isEmpty()) {
            this.firstContactBtn.setVisibility(View.VISIBLE);
            this.firstContactBtn.setText("Localiser " + contactName);
            this.firstContactBtn.setOnClickListener(new QuickFindBtnClickListenerManager(this, contactNumber));
            this.txtFavourite.setVisibility(View.GONE);
        }
        contactName = prefs.getString("pref_contact_name_2", "");
        contactNumber = prefs.getString("pref_contact_number_2","");
        if(contactName != null && !contactName.isEmpty() && contactNumber != null && !contactNumber.isEmpty()) {
            this.secondContactBtn.setVisibility(View.VISIBLE);
            this.secondContactBtn.setText("Localiser " + contactName);
            this.secondContactBtn.setOnClickListener(new QuickFindBtnClickListenerManager(this, contactNumber));
        }
        contactName = prefs.getString("pref_contact_name_3", "");
        contactNumber = prefs.getString("pref_contact_number_3","");
        if(contactName != null && !contactName.isEmpty() && contactNumber != null && !contactNumber.isEmpty()) {
            this.thirdContactBtn.setVisibility(View.VISIBLE);
            this.thirdContactBtn.setText("Localiser " + contactName);
            this.thirdContactBtn.setOnClickListener(new QuickFindBtnClickListenerManager(this, contactNumber));
        }
        contactName = prefs.getString("pref_contact_name_4", "");
        contactNumber = prefs.getString("pref_contact_number_4","");
        if(contactName != null && !contactName.isEmpty() && contactNumber != null && !contactNumber.isEmpty()) {
            this.fourthContactBtn.setVisibility(View.VISIBLE);
            this.fourthContactBtn.setText("Localiser " + contactName);
            this.fourthContactBtn.setOnClickListener(new QuickFindBtnClickListenerManager(this, contactNumber));
        }

        this.findBtn = (Button) findViewById(R.id.findBtn);
        this.findBtn.setOnClickListener(new FindBtnClickListenerManager(this));
    }

    ServiceConnection messagesManagerConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            binded = false;
            messagesManager = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            binded = true;
            MessagesManager.LocalBinder mLocalBinder = (MessagesManager.LocalBinder)service;
            messagesManager = mLocalBinder.getMessagesManagerInstance();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(binded) {
            unbindService(messagesManagerConnection);
            binded = false;
        }
    };

    public String getNumberInputValue() {
        return this.numberInput.getText().toString();
    }

    public void localize(String destination) {
        if (checkPhoneFormat(destination)) {
            this.messagesManager.sendLocationRequest(destination);
            this.numberInput.setText("");

            Intent intentSettingsActivity = new Intent(this, RadarActivity.class);
            this.startActivity(intentSettingsActivity);
            overridePendingTransition(R.anim.slide_from_down, R.anim.slide_to_up);
            this.finish();
        } else {
            Toast.makeText(this, R.string.wrong_format, Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPhoneFormat(String number) {
        String regexStr = "^[0-9]{10}$";
        return number.matches(regexStr);
    }

    private void askPermissions() {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_CONTACTS,Manifest.permission.INTERNET,Manifest.permission.VIBRATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (!(grantResults.length > 0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED && grantResults[6] == PackageManager.PERMISSION_GRANTED && grantResults[7] == PackageManager.PERMISSION_GRANTED)) {
                    displayAlert();
                }
            }
        }
    }

    private void displayAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Permissions obligatoires");

        alertDialogBuilder
                .setMessage("Veuillez accepter toutes les permissions!")
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        askPermissions();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}
