package com.example.flaforgue.geophone.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.flaforgue.geophone.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double longitude;
    private double latitude;
    private boolean isArchive;
    private String number;
    private HashMap<String,List<Location>> locationsMap;
    private List<Location> locations;
    private final Geocoder geocoder = new Geocoder(this, Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Récupération des paramètres (Unique localisation ou historique)
        Intent intentLocation = getIntent();
        this.isArchive = intentLocation.getBooleanExtra("isArchive", false);
        this.number = intentLocation.getStringExtra("number");
        this.locations = new ArrayList<>();
        if(isArchive) {
            this.locationsMap = (HashMap<String,List<Location>>)intentLocation.getSerializableExtra("locations");
            this.locations = locationsMap.get(number);
        } else {
            this.longitude = intentLocation.getDoubleExtra("longitude", 0);
            this.latitude = intentLocation.getDoubleExtra("latitude", 0);
            Location location = new Location("Unique location");
            location.setLongitude(this.longitude);
            location.setLatitude(this.latitude);
            this.locations.add(location);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng location = null;
        List<Address> addresses;

        //Affichage de tous les points fournis par la HashMap
        for(Location l : this.locations) {
            location = new LatLng(l.getLatitude(), l.getLongitude());
            try {

                //Affichage des infos des markers lorsque l'on clique dessus
                addresses = geocoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
                String cityName = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);
                String snippet = "Pays : " + countryName + "; Ville : " + stateName.split(" ")[1] + "; Adresse : " + cityName;
                mMap.addMarker(new MarkerOptions().position(location).title(this.number).snippet(snippet));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (location != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

    }
}
