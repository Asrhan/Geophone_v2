package com.example.flaforgue.geophone.managers.message;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.flaforgue.geophone.R;
import com.example.flaforgue.geophone.managers.DeviceComponentManager;
import com.example.flaforgue.geophone.managers.database.SQLManager;

import java.util.HashMap;

public class MessagesManager extends Service {

    // managers
    private final SmsManager messagesSender = SmsManager.getDefault();
    private MessagesHandler messagesHandler = null;
    private DeviceComponentManager deviceComponentManager = null;

    IBinder mBinder = (IBinder) new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        if(messagesHandler == null) {
            messagesHandler = new MessagesHandler();
        }

        if(deviceComponentManager == null) {
            deviceComponentManager = new DeviceComponentManager(this);
        }

        return mBinder;
    }

    public class LocalBinder extends Binder {
        public MessagesManager getMessagesManagerInstance() {
            return MessagesManager.this;
        }
    }

    /**
     * Envoi de la demande de localisation par SMS
     * @param destination Numéro du téléphone recherché
     * @param pin
     */
    public void sendLocationRequest(String destination, String pin) {
        Location currentLocation = deviceComponentManager.getLocation();
        if (currentLocation != null) {
            this.messagesSender.sendTextMessage(destination, null, MessageCode.LOCATION_REQUEST + ";" + currentLocation.getLongitude() + ";" + currentLocation.getLatitude() + ";" + pin, null, null);
            Toast.makeText(this, getString(R.string.request_sent), Toast.LENGTH_SHORT).show();
        }
    }


}
