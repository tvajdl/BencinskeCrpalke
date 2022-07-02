package com.example.bencinskecrpalke;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static String GOOGLE_API_KEY = "";
    private final int MAX_CARDS = 10; //stevilo cards (črpalk) v stranskem meniju
    private static double lat = 0.0, lng = 0.0;

    //https://developer.android.com/training/location/retrieve-current
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest; //parametri, ki se uporabijo, kadar se requesta location
    private LocationCallback locationCallback; //callback

    private RequestQueue queue; //objekt čakalne vrste za izvajanje spletnih poizvedb

    //meni za izbiranje bencinskih crpalk
    private DrawerLayout drawerLayout; //layout za navigation drawer
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private ArrayList<MenuCardBencinskaCrpalka> menuCardBencinskaCrpalkaArrayList; //array list, kamor se shranjujejo posamezni vnosi bencinskih crpalk iz levega swipe menija

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("menuCardBencinskaCrpalka", (ArrayList) menuCardBencinskaCrpalkaArrayList);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        GOOGLE_API_KEY = getResources().getString(R.string.google_maps_key);
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
                lat = location.getLatitude();
                lng = location.getLongitude();
                LatLng myLoc = new LatLng(lat, lng);
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

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        menuCardBencinskaCrpalkaArrayList = new ArrayList<>();
        // here we have created new array list and added data to it.

        //layout kateri vsebuje cene goriv za posamezno bencinsko


        Bundle bundle = getIntent().getExtras();
        String jsonString = bundle.getString("response");

        /*
        * https://stackoverflow.com/questions/2591098/how-to-parse-json-in-java
        * [ … ] represents an array, so library will parse it to JSONArray
        * { … } represents an object, so library will parse it to JSONObject
        *
        * JSON object example
        * {
            "count": 548,
            "next": "https://goriva.si/api/v1/search/?format=json&franchise=&o=&page=2&position=velenje",
            "previous": null,
            "results": [{
                "pk": 2194,
                "franchise": 1,
                "name": "PETROL VELENJE - PARTIZANSKA",
                "address": "PARTIZANSKA CESTA 6",
                "lat": 46.35917056,
                "lng": 15.10632852,
                "prices": {
                    "95": 1.503,
                    "dizel": 1.541,
                    "98": null,
                    "100": 1.82,
                    "dizel-premium": null,
                    "avtoplin-lpg": null,
                    "KOEL": null
                },
                "distance": 303.0809492360206,
                "direction": "",
                "open_hours": "vsak delavnik 06:00-22:00\r\nsobote 06:00-21:00\r\nnedelje in prazniki 08:00-20:00",
                "zip_code": "3320"
            }, {   ...   }
        *
        * */

        try
        {
            queue = Volley.newRequestQueue(MapsActivity.this);

            //najdene črpalke
            JSONArray arr = new JSONObject(jsonString).getJSONArray("results");

            for (int i = 0; i < arr.length(); i++)
            {
                if (i >= MAX_CARDS)
                    break;

                String name = arr.getJSONObject(i).getString("name");
                String address = arr.getJSONObject(i).getString("address");
                String lat = arr.getJSONObject(i).getString("lat"); //y
                String lng = arr.getJSONObject(i).getString("lng"); //x
                String open_hours = arr.getJSONObject(i).getString("open_hours");
                String distance = arr.getJSONObject(i).getString("distance");
                float fDistance = Float.parseFloat(distance); //razdalja  metrih
                fDistance = fDistance / 1000; //pretvori v kilometre
                distance = String.format("%.2f", fDistance);

                String logo = arr.getJSONObject(i).getString("franchise");
                int logoID = this.getResources().getIdentifier("_" + logo, "drawable", this.getPackageName()); //dobi ID logotipa iz imena
                int markerID = this.getResources().getIdentifier("_" + logo + "_marker", "drawable", this.getPackageName());

                menuCardBencinskaCrpalkaArrayList.add(new MenuCardBencinskaCrpalka(name, address, open_hours, distance,
                        Float.parseFloat(lng), Float.parseFloat(lat),
                        logoID, markerID, false,
                        ustvariCenikView(arr.getJSONObject(i).getJSONObject("prices"))));

                String URL_GET = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=" + name + "&inputtype=textquery&fields=opening_hours&key=" + GOOGLE_API_KEY;

                CustomJSONObjectRequest request = new CustomJSONObjectRequest(Request.Method.GET, URL_GET,
                        new JSONObject(), i, new CustomJSONObjectRequest.VolleyResponse() {
                    @Override
                    public void onResponse(JSONObject object, int id) throws JSONException {
                        //id je zaporedna številka dodanega objekta v menuCardBencinskaCrpalkaArrayList,
                        //torej menuCardBencinskaCrpalkaArrayList.get(id)
                        /*{
                            "candidates" : [
                                                {
                                                    "opening_hours" :
                                                    {
                                                        "open_now" : true
                                                    }
                                                }
                                            ],
                            "status" : "OK"
                          }*/

                        //if (id <= 5)
                        {
                            boolean odprtoZdaj = object.getJSONArray("candidates").getJSONObject(0).getJSONObject("opening_hours").getBoolean("open_now");
                            menuCardBencinskaCrpalkaArrayList.get(id).setOdprtoZdaj(odprtoZdaj);
                        }
                    }

                    @Override
                    public void onError(VolleyError error, int id)
                    {
                        Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(request.getJsonObjectRequest());


            }

        }
        catch (Exception ex)
        {
            /* ... */

            Toast.makeText(MapsActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        // we are initializing our adapter class and passing our arraylist to it.
        MenuCardAdapter menuCardAdapter;

        if (savedInstanceState != null)
        {
            menuCardAdapter = new MenuCardAdapter(this, (ArrayList)savedInstanceState.getParcelableArrayList("menuCardBencinskaCrpalka"));
            //savedInstanceState.putParcelableArrayList("menuCardBencinskaCrpalka", (ArrayList) menuCardBencinskaCrpalkaArrayList);
            //(ArrayList)savedInstanceState.getParcelableArrayList("menuCardBencinskaCrpalka");

        }
        else
        {
            menuCardAdapter = new MenuCardAdapter(this, menuCardBencinskaCrpalkaArrayList);
        }

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(menuCardAdapter);



        // to make the Navigation drawer icon always appear on the action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        //filer ali v stranskem meniju prikaže vse črpalke ali samo odprte
        CheckBox checkBoxOdprtoZaprto = (CheckBox) findViewById(R.id.checkBoxOdprtoZaprto);
        checkBoxOdprtoZaprto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //rv.setAdapter(null);
                //rv.setLayoutManager(null);
                if (savedInstanceState != null)
                {
                    menuCardBencinskaCrpalkaArrayList = (ArrayList)savedInstanceState.getParcelableArrayList("menuCardBencinskaCrpalka");
                }

                if (isChecked) //prikaži samo odprte
                {
                   // rv.getAdapter().



                    ArrayList<MenuCardBencinskaCrpalka> tmp = new ArrayList<>();
                    for (MenuCardBencinskaCrpalka crpalka : menuCardBencinskaCrpalkaArrayList) {
                        if (crpalka.getOdprtoZdaj()) //preveri ali je odprta
                            tmp.add(crpalka);


                    }

                    MenuCardAdapter menuCardAdapter = new MenuCardAdapter(MapsActivity.this, tmp);
                    rv.setLayoutManager(linearLayoutManager);
                    rv.setAdapter(menuCardAdapter);
                    menuCardAdapter.notifyDataSetChanged();

                }
                else //prikaži vse
                {

                    MenuCardAdapter menuCardAdapter = new MenuCardAdapter(MapsActivity.this, menuCardBencinskaCrpalkaArrayList);
                    rv.setLayoutManager(linearLayoutManager);
                    rv.setAdapter(menuCardAdapter);
                    menuCardAdapter.notifyDataSetChanged();



                }

            }
        });



    }



    public FlowLayout ustvariCenikView(JSONObject prices) throws Exception
    {
        //ustvari flowlayout v katerem so celice naziva in cene goriva
        FlowLayout flow = new FlowLayout(MapsActivity.this);

        JSONArray tmp = prices.names(); //uporabimo JSONArray za lažjo iteracijo skozi JSONObject

        for (int i = 0; i < tmp.length(); i++)
        {
            GorivoCell cell = new GorivoCell(MapsActivity.this);
            cell.setNazivTextColor(Color.BLACK);
            cell.setCenaTextColor(Color.WHITE);
            cell.setNazivBackgroundColor(Color.WHITE);

            String cena = prices.getString(tmp.getString(i));

            switch (tmp.getString(i))
            {
                case "95":{
                    cell.setNazivText("Bencin 95");
                    cell.setCenaText(cena);
                    // .getColor se uporabi zato, da dobi dejansko hex vrednost in ne resource ID
                    cell.setCenaBackgroundColor(ContextCompat.getColor(this, R.color.LimeGreen));
                    if (!cena.equals("null"))
                        flow.addView(cell);
                }break;
                case "98":{
                    cell.setNazivText("Bencin 98");
                    cell.setCenaText(cena);
                    cell.setCenaBackgroundColor(ContextCompat.getColor(this, R.color.LimeGreen));
                    if (!cena.equals("null"))
                        flow.addView(cell);
                }break;
                case "100":{
                    cell.setNazivText("Bencin 100");
                    cell.setCenaText(cena);
                    cell.setCenaBackgroundColor(ContextCompat.getColor(this, R.color.DodgerBlue));
                    if (!cena.equals("null"))
                        flow.addView(cell);
                }break;
                case "dizel":{
                    cell.setNazivText("Diesel");
                    cell.setCenaText(cena);
                    cell.setCenaBackgroundColor(ContextCompat.getColor(this, R.color.black));
                    if (!cena.equals("null"))
                        flow.addView(cell);
                }break;
                case "dizel-premium":{
                    cell.setNazivText("Diesel Premium");
                    cell.setCenaText(cena);
                    cell.setCenaBackgroundColor(ContextCompat.getColor(this, R.color.Black));

                    if (!cena.equals("null"))
                        flow.addView(cell);
                }break;
                case "avtoplin-lpg":{
                    cell.setNazivText("Plin LPG");
                    cell.setCenaText(cena);
                    cell.setCenaTextColor(Color.BLACK);
                    cell.setCenaBackgroundColor(ContextCompat.getColor(this, R.color.Yellow));
                    if (!cena.equals("null"))
                        flow.addView(cell);
                }break;
                case "KOEL":{
                    cell.setNazivText("Kurilno olje");
                    cell.setCenaText(cena);
                    cell.setCenaBackgroundColor(ContextCompat.getColor(this, R.color.SaddleBrown));
                    if (!cena.equals("null"))
                        flow.addView(cell);
                }break;

                default:{}
            }
        }

        return flow;
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




        for (MenuCardBencinskaCrpalka crpalka : menuCardBencinskaCrpalkaArrayList)
        {
            MarkerOptions markerOptions = new MarkerOptions(); //creating a marker
            markerOptions.position(new LatLng(crpalka.getLat(), crpalka.getLng())); //setting the position for the marker
            markerOptions.title("marker test"); //this will be displayed on tapping the marker

            Bitmap icon = BitmapFactory.decodeResource(this.getResources(), crpalka.getMarkerImageID());
            icon = Bitmap.createScaledBitmap(icon, 90, 140, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)); //add icon for marker

            mMap.addMarker(markerOptions);
        }

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

    public static void zazeniNavigacijo(double destinationLat, double destinationLng, Context c)
    {

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + String.valueOf(lat) +  "," + String.valueOf(lng) +
                                                        "&destination=" + String.valueOf(destinationLat) + "," + String.valueOf(destinationLng) +
                                                        "&travelmode=driving"));
        c.startActivity(intent);
    }

}

// https://stackoverflow.com/questions/45212853/handle-multiple-request-in-android-volley
class CustomJSONObjectRequest implements Response.Listener<JSONObject>, Response.ErrorListener
{
    private VolleyResponse volleyResponse;
    private int id;
    private JsonObjectRequest jsonObjectRequest;

    //interface, ki se uporablja, da se lahko v httprequest pošlje ID črpalke. Potem pri responsu veš, za katero črpalko je bil specifični response
    public interface VolleyResponse {

        void onResponse(JSONObject object, int id) throws JSONException;

        void onError(VolleyError error, int id);
    }

    public CustomJSONObjectRequest(int method, String url, JSONObject jsonObject, int id, VolleyResponse volleyResponse) {
        this.volleyResponse = volleyResponse;
        this.id= id;
        jsonObjectRequest = new JsonObjectRequest(method, url, jsonObject, this, this);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            volleyResponse.onResponse(response, id);
        } catch (JSONException e) {
            Log.e("CustomJSONObjectRequest", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        volleyResponse.onError(error, id);
    }

    public JsonObjectRequest getJsonObjectRequest() {
        return jsonObjectRequest;
    }
}