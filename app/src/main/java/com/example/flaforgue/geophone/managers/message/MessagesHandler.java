package com.example.flaforgue.geophone.managers.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.flaforgue.geophone.activities.CloseActivity;
import com.example.flaforgue.geophone.activities.CloseSearcherActivity;
import com.example.flaforgue.geophone.activities.HomeActivity;
import com.example.flaforgue.geophone.activities.MapsActivity;
import com.example.flaforgue.geophone.managers.DeviceComponentManager;

import java.util.Calendar;
import java.util.Date;

public class MessagesHandler extends BroadcastReceiver {

    private final SmsManager smsManager = SmsManager.getDefault();
    private final String   ACTION_RECEIVE_SMS  = "android.provider.Telephony.SMS_RECEIVED";

    private DeviceComponentManager deviceComponentManager;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(ACTION_RECEIVE_SMS)) {
            this.deviceComponentManager = new DeviceComponentManager(context);
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");

                final SmsMessage[] messages = new SmsMessage[pdus.length];

                for (int i = 0; i < pdus.length; i++)  {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], bundle.getString("format"));
                    } else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                }

                if (messages.length >= 0) {
                    final String messageBody = messages[0].getMessageBody();
                    final String phoneNumber = messages[0].getDisplayOriginatingAddress();

                    this.handleRequest(context, phoneNumber, messageBody);
                }
            }
        }

    }

    private void handleRequest(Context context, String phoneNumber, String messageBody) {
        Location remoteLocation;
        Location currentLocation;
        Intent intentLocation;

        switch (messageBody.split(";")[0]) {

            case MessageCode.LOCATION_REQUEST:
                remoteLocation = buildLocationFromMessage(messageBody);
                currentLocation = deviceComponentManager.getLocation();
                if (!checkCoordinates(currentLocation)) {
                    this.smsManager.sendTextMessage(phoneNumber, null, MessageCode.UNKNOWN_LOCATION + ";", null, null);
                } else {
                    float distance = calculateDistance(remoteLocation, currentLocation);
                    if (distance <= 20) {
                        this.enableComponentDependingOnParam(context);
                        this.smsManager.sendTextMessage(phoneNumber, null, MessageCode.SEND_LOCATION_CLOSE + ";", null, null);
                        Intent intentFound = new Intent(context, CloseActivity.class);
                        intentFound.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentFound);
                    } else if (distance > 20 && distance <= 50) {
                        this.smsManager.sendTextMessage(phoneNumber, null, MessageCode.SEND_LOCATION_MIDDLE + ";" + calculateDirection(remoteLocation,currentLocation), null, null);
                    } else {
                        this.smsManager.sendTextMessage(phoneNumber, null, MessageCode.SEND_LOCATION_FAR + ";" + currentLocation.getLongitude() + ";" + currentLocation.getLatitude(), null, null);
                    }
                }
                break;

            case MessageCode.SEND_LOCATION_CLOSE:
                intentLocation = new Intent(context, CloseSearcherActivity.class);
                intentLocation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentLocation);
                break;

            case MessageCode.SEND_LOCATION_MIDDLE:
                //TODO
                break;

            case MessageCode.SEND_LOCATION_FAR:
                remoteLocation = buildLocationFromMessage(messageBody);
                intentLocation = new Intent(context, MapsActivity.class);
                intentLocation.putExtra("longitude", remoteLocation.getLongitude());
                intentLocation.putExtra("latitude", remoteLocation.getLatitude());
                intentLocation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentLocation);
                break;

            case MessageCode.UNKNOWN_LOCATION:
                intentLocation = new Intent(context, HomeActivity.class);
                intentLocation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentLocation);
                break;

            default:
                Log.i("info", "" + messageBody.length());
                break;
        }
    }

    private boolean checkCoordinates (Location loc) {
        return (loc != null);
    }

    private Location buildLocationFromMessage (String message) {
        Location loc = new Location("Build Location");
        loc.setLongitude(Double.parseDouble(message.split(";")[1]));
        loc.setLatitude(Double.parseDouble(message.split(";")[2]));
        return loc;
    }

    private float calculateDistance(Location firstLoc, Location secondLoc) {
        return firstLoc.distanceTo(secondLoc);
    }

    private float calculateDirection(Location firstLoc, Location secondLoc) {
        return firstLoc.bearingTo(secondLoc);
    }

    private void enableComponentDependingOnParam(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean nightTimeEnabled = prefs.getBoolean("pref_switch_time", true);
        boolean flashEnabled = prefs.getBoolean("pref_switch_flash", false);
        String time = prefs.getString("pref_time","22:00");
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        this.deviceComponentManager.doVibrate();

        if (!nightTimeEnabled || !isTimePassed(time,hour,minute)) {
            this.deviceComponentManager.playSound();
            if(flashEnabled)
                this.deviceComponentManager.turnOnFlash();
        }
        else if (nightTimeEnabled && isTimePassed(time,hour,minute))
            this.deviceComponentManager.turnOnFlash();
    }

    private boolean isTimePassed(String time, int hour, int minute) {
        int paramHour = Integer.parseInt(time.split(":")[0]);
        int paramMinute = Integer.parseInt(time.split(":")[1]);
        if(hour >= 7) {
            if (hour < paramHour) {
                return false;
            } else if (hour == paramHour) {
                return (minute > paramMinute);
            }
        }
        return true;

    }
}
