package com.example.flaforgue.geophone.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.flaforgue.geophone.activities.superactivities.ActivityWithMenu;
import com.example.flaforgue.geophone.managers.clicklisteners.FindBtnClickListenerManager;
import com.example.flaforgue.geophone.managers.message.MessagesManager;
import com.example.flaforgue.geophone.managers.clicklisteners.QuickFindBtnClickListenerManager;
import com.example.flaforgue.geophone.R;

public class HomeActivity extends ActivityWithMenu {

    private Button firstContactBtn;
    private Button secondContactBtn;
    private Button thirdContactBtn;
    private Button fourthContactBtn;

    private Button findBtn;
    private EditText numberInput;

    private boolean binded;
    private MessagesManager messagesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get parameters stored in settings
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // background service listening to messages of others Geophone applications
        Intent mIntent = new Intent(this, MessagesManager.class);
        bindService(mIntent, messagesManagerConnection, BIND_AUTO_CREATE);

        this.numberInput = (EditText) findViewById(R.id.numberInput);

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
}
