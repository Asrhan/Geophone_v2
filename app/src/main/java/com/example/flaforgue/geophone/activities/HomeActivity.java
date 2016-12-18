package com.example.flaforgue.geophone.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flaforgue.geophone.managers.clicklisteners.FindBtnClickListenerManager;
import com.example.flaforgue.geophone.managers.clicklisteners.MapBtnClickListenerManager;
import com.example.flaforgue.geophone.managers.clicklisteners.OptionBtnClickListenerManager;
import com.example.flaforgue.geophone.managers.database.SQLManager;
import com.example.flaforgue.geophone.managers.message.MessagesManager;
import com.example.flaforgue.geophone.managers.clicklisteners.QuickFindBtnClickListenerManager;
import com.example.flaforgue.geophone.R;

import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

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
    protected FloatingActionButton optionsBtn;
    protected FloatingActionButton mapBtn;

    private boolean binded;
    private MessagesManager messagesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.i("INFO", "Démarrage de l'application");

        //Demande des permissions
        askPermissions();

        //get parameters stored in settings
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Service d'écoute en arriere plan
        Intent mIntent = new Intent(this, MessagesManager.class);
        bindService(mIntent, messagesManagerConnection, BIND_AUTO_CREATE);

        //Récupération des inputs de l'activité
        this.numberInput = (EditText) findViewById(R.id.numberInput);
        this.txtFavourite = (TextView) findViewById(R.id.txtAddContact);
        this.firstContactBtn = (Button) findViewById(R.id.firstBtn);
        this.secondContactBtn = (Button) findViewById(R.id.secondBtn);
        this.thirdContactBtn = (Button) findViewById(R.id.thirdBtn);
        this.fourthContactBtn = (Button) findViewById(R.id.fourthBtn);
        this.optionsBtn = (FloatingActionButton) this.findViewById(R.id.optionsBtn);
        this.mapBtn = (FloatingActionButton) this.findViewById(R.id.mapBtn);
        this.findBtn = (Button) findViewById(R.id.findBtn);

        //Afficher les boutons de recherche rapide si les contacts sont paramétrés
        //Si le premier bouton est afficher
        String contactName = prefs.getString("pref_contact_name_1", "");
        String contactNumber = prefs.getString("pref_contact_number_1","");
        if(contactName != null && !contactName.isEmpty() && contactNumber != null && !contactNumber.isEmpty()) {
            this.firstContactBtn.setVisibility(View.VISIBLE);
            this.firstContactBtn.setText("Localiser " + contactName);
            this.firstContactBtn.setOnClickListener(new QuickFindBtnClickListenerManager(this, contactNumber));
            this.txtFavourite.setVisibility(View.GONE);
        }

        contactName = prefs.getString("pref_contact_name_2", "");
        contactNumber = prefs.getString("pref_contact_number_2", "");
        if(contactName != null && !contactName.isEmpty() && contactNumber != null && !contactNumber.isEmpty()) {
            this.secondContactBtn.setVisibility(View.VISIBLE);
            this.secondContactBtn.setText("Localiser " + contactName);
            this.secondContactBtn.setOnClickListener(new QuickFindBtnClickListenerManager(this, contactNumber));
            this.txtFavourite.setVisibility(View.GONE);
        }

        contactName = prefs.getString("pref_contact_name_3", "");
        contactNumber = prefs.getString("pref_contact_number_3", "");
        if(contactName != null && !contactName.isEmpty() && contactNumber != null && !contactNumber.isEmpty()) {
            this.thirdContactBtn.setVisibility(View.VISIBLE);
            this.thirdContactBtn.setText("Localiser " + contactName);
            this.thirdContactBtn.setOnClickListener(new QuickFindBtnClickListenerManager(this, contactNumber));
            this.txtFavourite.setVisibility(View.GONE);
        }

        contactName = prefs.getString("pref_contact_name_4", "");
        contactNumber = prefs.getString("pref_contact_number_4","");
        if(contactName != null && !contactName.isEmpty() && contactNumber != null && !contactNumber.isEmpty()) {
            this.fourthContactBtn.setVisibility(View.VISIBLE);
            this.fourthContactBtn.setText("Localiser " + contactName);
            this.fourthContactBtn.setOnClickListener(new QuickFindBtnClickListenerManager(this, contactNumber));
            this.txtFavourite.setVisibility(View.GONE);
        }

        //On set les listener pour les boutons
        this.findBtn.setOnClickListener(new FindBtnClickListenerManager(this));
        this.optionsBtn.setOnClickListener(new OptionBtnClickListenerManager(this));
        this.mapBtn.setOnClickListener(new MapBtnClickListenerManager(this));
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

    /**
     * Lancement de la recherche du téléphone en fonction du numéro
     * @param destination
     */
    public void localize(String destination) {
        if (checkPhoneFormat(destination)) {
            askPin(destination);
        } else {
            Toast.makeText(this, R.string.wrong_format, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Vérifie le format du numéro de téléphone
     * @param number
     * @return Retourne vrai si le format est correct
     */
    private boolean checkPhoneFormat(String number) {
        String regexStr = "^[0-9]{10}$";
        return number.matches(regexStr);
    }

    /**
     * Demande des autorisations
     */
    private void askPermissions() {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.INTERNET,Manifest.permission.VIBRATE,Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA}, PERMISSIONS_REQUEST);
    }

    /**
     * Gestion des réponses aux autorisations, si une permission est refusée, on affiche une pop-up
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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

    /**
     * On indique à l'utilisateur que les autorisations sont obligatoires pour le bon fonctionnement de l'application
     * Fermeture de l'application
     */
    private void displayAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Permissions obligatoires");

        alertDialogBuilder
                .setMessage("Toutes les permissions sont nécessaires pour le bon fonctionnement de l'application. " +
                        "Elles vous seront demandées à la prochaine ouverture de l'application. \n\n" +
                        "Vous pouvez également accéder aux permissions via : \n Paramètres -> Gestionnaires d'applications -> Geophone -> Autorisations")
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        finishAffinity();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    /**
     * Affiche une pop-up pour demander le PIN puis envoyer la recherche du téléphone
     * @param destination
     */
    public void askPin(String destination) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Code PIN");

        final HomeActivity context = this;
        final EditText pin = new EditText(this);
        pin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pin.setWidth(1);
        pin.setHint("PIN");
        pin.setGravity(Gravity.CENTER_HORIZONTAL);
        final String dest = destination;

        alertDialogBuilder
                .setMessage("Entrez votre code PIN")
                .setView(pin)
                .setCancelable(false)
                .setPositiveButton("Localiser",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        context.messagesManager.sendLocationRequest(dest, pin.getText().toString());
                        context.numberInput.setText("");

                        Intent intentSettingsActivity = new Intent(context, RadarActivity.class);
                        context.startActivity(intentSettingsActivity);
                        overridePendingTransition(R.anim.slide_from_down, R.anim.slide_to_up);
                        context.finish();
                    }
                })
                .setNegativeButton("Annuler",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}
