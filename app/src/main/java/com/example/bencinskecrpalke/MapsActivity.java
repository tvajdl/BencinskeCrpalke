package com.example.bencinskecrpalke;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/*
 *
 * TODO
 *
 * https://material.io/components/navigation-drawer#anatomy
 * da nameče bencinske tu notri
 *
 * */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //https://developer.android.com/training/location/retrieve-current
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest; //parametri, ki se uporabijo, kadar se requesta location
    private LocationCallback locationCallback; //callback

    //meni za izbiranje bencinskih crpalk
    private DrawerLayout drawerLayout; //layout za navigation drawer
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView; //za dodajanje entrijev v meni

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        //create location services client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000); //5 sekund
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult)
            //@NonNull v originalni funkciji, katera se overrida (ne v tej),
            // pomeni, da parameter ne more nikoli biti null, ker so original developerji to tako naštimali
            //zato tukaj ni potrebno imeti locationResult == null preverjanja
            {
                Location location = locationResult.getLastLocation();
                LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(myLoc).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 10.0f));
                //ker smo rabli samo en location lahko odstranimo request
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        };

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.drawerLayout_najdene_bencinske_crpalke);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        RecyclerView rv = findViewById(R.id.recyclerView_najdene_bencinske_crpalke);
        ArrayList<MenuCardBencinskaCrpalka> menuCardBencinskaCrpalkaArrayList = new ArrayList<>();
        // here we have created new array list and added data to it.

        //layout kateri vsebuje cene goriv za posamezno bencinsko
        FlowLayout flow = new FlowLayout(getBaseContext());
        GorivoCell hofer = new GorivoCell(getBaseContext());
        hofer.setNazivText("95");
        hofer.setCenaText("1.43");
        hofer.setNazivTextColor(Color.BLACK);
        hofer.setCenaTextColor(Color.WHITE);
        hofer.setNazivBackgroundColor(Color.WHITE);
        hofer.setCenaBackgroundColor(Color.GREEN);

        GorivoCell hofer2 = new GorivoCell(getBaseContext());
        hofer2.setNazivText("100");
        hofer2.setCenaText("1.50");
        hofer2.setNazivTextColor(Color.BLACK);
        hofer2.setCenaTextColor(Color.WHITE);
        hofer2.setNazivBackgroundColor(Color.WHITE);
        hofer2.setCenaBackgroundColor(Color.GREEN);

        flow.addView(hofer);
        flow.addView(hofer2);

        menuCardBencinskaCrpalkaArrayList.add(new MenuCardBencinskaCrpalka("crpalka1", "naslov", 1.0, 1.0, R.drawable._1, flow));
        /*
        menuCardBencinskaCrpalkaArrayList.add(new MenuCardBencinskaCrpalka("crpalka2", "naslov", 2.0, 2.0, R.drawable._13, new FlowLayout(getBaseContext())));
        menuCardBencinskaCrpalkaArrayList.add(new MenuCardBencinskaCrpalka("crpalka3", "naslov", 3.0, 3.0, R.drawable._3, new FlowLayout(getBaseContext())));
        menuCardBencinskaCrpalkaArrayList.add(new MenuCardBencinskaCrpalka("crpalka4", "naslov", 4.0, 4.0, R.drawable._4, new FlowLayout(getBaseContext())));
        menuCardBencinskaCrpalkaArrayList.add(new MenuCardBencinskaCrpalka("crpalka5", "naslov", 4.0, 4.0, R.drawable._21, new FlowLayout(getBaseContext())));
        menuCardBencinskaCrpalkaArrayList.add(new MenuCardBencinskaCrpalka("crpalka6", "naslov", 4.0, 4.0, R.drawable._27, new FlowLayout(getBaseContext())));
        menuCardBencinskaCrpalkaArrayList.add(new MenuCardBencinskaCrpalka("crpalka7", "naslov", 4.0, 4.0, R.drawable._9, new FlowLayout(getBaseContext())));
        menuCardBencinskaCrpalkaArrayList.add(new MenuCardBencinskaCrpalka("crpalka8", "naslov", 4.0, 4.0, R.drawable._29, new FlowLayout(getBaseContext())));
        menuCardBencinskaCrpalkaArrayList.add(new MenuCardBencinskaCrpalka("crpalka9", "naslov", 4.0, 4.0, R.drawable._2, new FlowLayout(getBaseContext())));
        */

        // we are initializing our adapter class and passing our arraylist to it.
        MenuCardAdapter menuCardAdapter = new MenuCardAdapter(this, menuCardBencinskaCrpalkaArrayList);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(menuCardAdapter);

        // to make the Navigation drawer icon always appear on the action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("response");

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

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


        //zahtevaj pravice za fino lokacijo
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);

    }

    //se kliče ob rezultatu poizvedbe o pravici. v našem primeru želimo potem prikazati trenutno lokacijo
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);



        //odda zahtevo, da se začne izvajati location lookup
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper()); //Looper klasa izvaja operacije zadaj
    }

    //navigacijski meni za zbiranje bencinskih crpalk na voljo
    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}