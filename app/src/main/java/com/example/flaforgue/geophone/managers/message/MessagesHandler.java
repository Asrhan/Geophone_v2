package com.example.flaforgue.geophone.managers.message;

import android.app.Activity;
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
import android.widget.Toast;

import com.example.flaforgue.geophone.R;
import com.example.flaforgue.geophone.activities.CloseActivity;
import com.example.flaforgue.geophone.activities.CloseSearcherActivity;
import com.example.flaforgue.geophone.activities.HomeActivity;
import com.example.flaforgue.geophone.activities.MapsActivity;
import com.example.flaforgue.geophone.managers.DeviceComponentManager;
import com.example.flaforgue.geophone.managers.database.SQLManager;

import java.util.Calendar;
import java.util.Date;

public class MessagesHandler extends BroadcastReceiver {

    private final SmsManager smsManager = SmsManager.getDefault();
    private final String  ACTION_RECEIVE_SMS  = "android.provider.Telephony.SMS_RECEIVED";

    private SQLManager manager;
    private DeviceComponentManager deviceComponentManager;


    /**
     * Declenchement de la méthode à la réception d'un SMS
     * On gère les traitements en fonction du message
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(ACTION_RECEIVE_SMS)) {
            this.manager = new SQLManager(context);
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

    /**
     * Gestion des différents code MessageCode
     * @param context
     * @param phoneNumber
     * @param messageBody
     */
    private void handleRequest(Context context, String phoneNumber, String messageBody) {
        Location remoteLocation;
        Location currentLocation;
        Intent intentLocation;

        switch (messageBody.split(";")[0]) {

            //Demande de localisation
            case MessageCode.LOCATION_REQUEST:
                if (checkPin(context,messageBody.split(";")[3])) {
                    remoteLocation = buildLocationFromMessage(messageBody);
                    currentLocation = deviceComponentManager.getLocation();
                    if (!checkCoordinates(currentLocation)) {
                        this.smsManager.sendTextMessage(phoneNumber, null, MessageCode.UNKNOWN_LOCATION + ";", null, null);
                    } else {
                        float distance = calculateDistance(remoteLocation, currentLocation);
                        if (distance <= 20) {
                            this.enableComponentDependingOnParam(context);
                            this.smsManager.sendTextMessage(phoneNumber, null, MessageCode.SEND_LOCATION_CLOSE + ";" + currentLocation.getLongitude() + ";" + currentLocation.getLatitude(), null, null);
                            Intent intentFound = new Intent(context, CloseActivity.class);
                            intentFound.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentFound);
                        } else {
                            this.smsManager.sendTextMessage(phoneNumber, null, MessageCode.SEND_LOCATION_FAR + ";" + currentLocation.getLongitude() + ";" + currentLocation.getLatitude(), null, null);
                        }
                    }
                } else
                    this.smsManager.sendTextMessage(phoneNumber, null, MessageCode.ERROR_PIN + ";", null, null);
                break;

            //Reception d'une réponse de localisation proche
            case MessageCode.SEND_LOCATION_CLOSE:
                remoteLocation = buildLocationFromMessage(messageBody);
                archiveRequest(context, phoneNumber, remoteLocation);
                intentLocation = new Intent(context, CloseSearcherActivity.class);
                intentLocation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentLocation);
                break;

            //Réception d'une réponse de localisation lointaine
            case MessageCode.SEND_LOCATION_FAR:
                remoteLocation = buildLocationFromMessage(messageBody);
                archiveRequest(context, phoneNumber, remoteLocation);
                intentLocation = new Intent(context, MapsActivity.class);
                intentLocation.putExtra("longitude", remoteLocation.getLongitude());
                intentLocation.putExtra("latitude", remoteLocation.getLatitude());
                intentLocation.putExtra("isArchive", false);
                intentLocation.putExtra("number", phoneNumber);
                intentLocation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentLocation);
                break;

            //Le téléphone recherché n'a pas pu récupérer sa position
            case MessageCode.UNKNOWN_LOCATION:
                Toast.makeText(context, R.string.unk_loc_response, Toast.LENGTH_SHORT).show();
                break;

            //La requete a été envoyé avec un mauvais PIN
            case MessageCode.ERROR_PIN:
                Toast.makeText(context, R.string.incorrect_pin, Toast.LENGTH_SHORT).show();
                break;

            //Demande faite par un téléphone ne disposant pas de l'application
            case MessageCode.SIMPLE_LOCATION:
                if (messageBody.split(";").length > 1) {
                    if (checkPin(context,messageBody.split(";")[1])) {
                        currentLocation = deviceComponentManager.getLocation();
                        if (!checkCoordinates(currentLocation)) {
                            this.smsManager.sendTextMessage(phoneNumber, null, MessageCode.UNKNOWN_LOCATION + ";", null, null);
                        } else {
                            this.smsManager.sendTextMessage(phoneNumber, null, MessageCode.MAPS_LINK + createMapsURL(currentLocation), null, null);
                        }
                    }
                    else
                        this.smsManager.sendTextMessage(phoneNumber, null, MessageCode.ERROR_PIN + ";", null, null);
                } else
                    this.smsManager.sendTextMessage(phoneNumber, null, MessageCode.MISSING_PIN + ";", null, null);
                break;

            default:
                Log.i("INFO", "Aucune correspondance - Message reçu : " + messageBody.length());
                break;
        }
    }

    /**
     * Vérifie que l'object Location n'est pas null
     * @param loc
     * @return
     */
    private boolean checkCoordinates (Location loc) {
        return (loc != null);
    }

    /**
     * Construction d'un object Location en fonction du message entrant
     * @param message
     * @return Retourne un objet Location
     */
    private Location buildLocationFromMessage (String message) {
        Location loc = new Location("Build Location");
        loc.setLongitude(Double.parseDouble(message.split(";")[1]));
        loc.setLatitude(Double.parseDouble(message.split(";")[2]));
        return loc;
    }

    /**
     * Calcul de la distance entre deux points
     * @param firstLoc
     * @param secondLoc
     * @return Retourne la distance en mètres
     */
    private float calculateDistance(Location firstLoc, Location secondLoc) {
        return firstLoc.distanceTo(secondLoc);
    }

    /**
     * Activation des différents modules du télépone (flash, son, vibreur)
     * @param context
     */
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

    /**
     * Vérifie si l'heure de nuit est dépassée quand elle est active
     * @param time Heure stockée en paramètre
     * @param hour Heure actuelle
     * @param minute Minutes actuelles
     * @return Retourne vrai si l'heure est passée
     */
    private boolean isTimePassed(String time, int hour, int minute) {
        int paramHour = Integer.parseInt(time.split(":")[0]);
        int paramMinute = Integer.parseInt(time.split(":")[1]);
        if (paramHour == 7 && paramMinute == 0)
            return false;
        if(paramHour < 7) {
            if (hour < 7) {
                if (hour < paramHour) {
                    return false;
                } else if (hour == paramHour) {
                    return (minute >= paramMinute);
                } else
                    return true;
            } else
                return false;
        } else {
            if (hour >= 7) {
                if (hour < paramHour) {
                    return false;
                } else if (hour == paramHour) {
                    return (minute >= paramMinute);
                } else
                    return true;
            }
            else
                return true;
        }

    }

    /**
     * Archivage de la recherche lorsque l'on obtient un résultat
     * @param context
     * @param phoneNumber Numéro du téléphone recherché
     * @param remoteLocation Localisation du téléphone recherché
     */
    private void archiveRequest(Context context, String phoneNumber, Location remoteLocation) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean archiveEnabled = prefs.getBoolean("pref_switch_archive",true);
        if (archiveEnabled) {
            this.manager.insertLocation(phoneNumber, remoteLocation);
            Toast.makeText(context, "Recherche ajoutée à l\'historique", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Vérification du code PIN reçu
     * @param context Contexte actuel
     * @param pin Le code pin reçu
     * @return Retourne vrai si le code PIN correspond au code en paramètre
     */
    private boolean checkPin(Context context, String pin) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String pinPref = prefs.getString("pref_password","0000");
        return (pin.equals(pinPref));
    }

    /**
     * Renvoit d'un lien map au cas où a recherche est fait à partir d'un téléphone ne disposant pas de l'application
     * @param loc Localisation du téléphone recherché
     * @return Retourne un lien maps avec les coordonnées envoyées en paramètre
     */
    private String createMapsURL(Location loc) {
        String link = "http://maps.google.com/maps?q=loc:" + loc.getLatitude() +"," + loc.getLongitude();
        return link;
    }
}
