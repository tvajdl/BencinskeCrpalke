package com.example.bencinskecrpalke;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;

/*
 * TODO
 *
 * class user ali neki, v katerega se shranijo vsi podatki, kadar se orientacija screena spremeni, da se shranjeni podatki nalo&#x17E;ijo nazaj
 *
 * lastnosti seje prek savedInstanceState.put shraniš in zloadaš nazaj
 * + https://stackoverflow.com/questions/1337424/android-spinner-get-the-selected-item-change-event
 * */

//posebej class samo za GPS???

//public class MainActivity extends AppCompatActivity {
public class MainActivity extends Activity {

    public enum StanjeOkvirja //stanje okvirja na začetnem naslonu, kjer lahko zbiraš med distributerjem, vrsto goriva, krajem, itd..
    {
        ODPRT,
        ZAPRT,
        SE_ANIMIRA
    }

    private final int SPLASH_DOLZINA = 2500; //kako dolgo prikazuje splash screen preden ga zacne skrivat
    private int SPLETNE_ZAHTEVE_STEVEC = 0; //števec, ki šteje koliko json requestov se trenutno izvaja
    private RequestQueue queue; //objekt čakalne vrste za izvajanje spletnih poizvedb
    private boolean SPLETNE_ZAHTEVE_NAPAKA = false; //zastavica, preverja, ali se je zgodila napaka pri branju podatkov iz spletnih zahtev

    private boolean APLIKACIJA_ZE_AKTIVNA = false; //zastavica, ki preverja, ali je bila aplikacija ze zagnana in so podatki ze bili nalozeni


    private static final String URL_GET_DISTRIBUTERJI = "https://goriva.si/api/v1/franchise/?format=json"; //API od katerega preko GET zahteve dobi imena vseh franšiz/bencinskih črpalk
    private static final String URL_GET_GORIVA = "https://goriva.si/api/v1/fuel/?format=json"; // dobi imena vseh vrst goriv

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //preden se activity unici, se shranijo podatki, da se ob novem ustvarjanju activityja lahko podatki obnovijo
        //ta funkcija se pogosto klice ob menjavi orientacije zaslona (iz portret v landscape)
        //ob zapiranju programa (puščica nazaj) se ne kliče

        savedInstanceState.putBoolean("APLIKACIJA_ZE_AKTIVNA", true);
        savedInstanceState.putSerializable("OKVIR_ANIMACIJA", Seja.stanjeOkvirja);
        savedInstanceState.putString("textBoxLokacija", Seja.textBoxLokacija);
/*
        //shrani stanje spinnerjev
        ArrayList<String> l = pridobiPodatkeIzSpinnerja(findViewById(R.id.spinnerVrstaGoriva));
        String s = "";
        for (int i = 0; i < l.size(); i++)
            s = s + l.get(i) + " ";

        //Toast.makeText(this, "size=" + l.size() + s, Toast.LENGTH_LONG).show();
        savedInstanceState.putStringArrayList("spinnerVrstaGoriva", (ArrayList<String>)l);

        l = pridobiPodatkeIzSpinnerja(findViewById(R.id.spinnerDistributer));
        s = "";
        for (int i = 0; i < l.size(); i++)
            s = s + l.get(i) + " ";

        //Toast.makeText(this, "size=" + l.size() + s, Toast.LENGTH_LONG).show();
        savedInstanceState.putStringArrayList("spinnerDistributer", (ArrayList<String>)l);
*/
        //shrani stanje spinnerjev
        savedInstanceState.putStringArrayList("spinnerVrstaGoriva", Seja.arrayListVrstaGoriva);
        savedInstanceState.putInt("spinnerVrstaGorivaIzbraniItem", Seja.spinnerVrstaGorivaIzbraniItem);
        savedInstanceState.putStringArrayList("spinnerDistributer",Seja.arrayListDistributer);
        savedInstanceState.putInt("spinnerDistributerIzbraniItem", Seja.spinnerDistributerIzbraniItem);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queue = Volley.newRequestQueue(MainActivity.this); //inicializacija objekta čakalne vrste za izvajanje spletnih poizvedb

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_main);

        //preveri, ali je aplikacije ze bila aktivna (ali se je npr samo orientacija zaslona spremenila, da ni potrebno ponovno nalagati podatkov in kazati splash screena)
        if (savedInstanceState != null)
        {
            APLIKACIJA_ZE_AKTIVNA = savedInstanceState.getBoolean("APLIKACIJA_ZE_AKTIVNA");
            Seja.stanjeOkvirja = (StanjeOkvirja) savedInstanceState.getSerializable("OKVIR_ANIMACIJA");
            Seja.spinnerDistributerIzbraniItem = savedInstanceState.getInt("spinnerDistributerIzbraniItem");
            Seja.spinnerVrstaGorivaIzbraniItem = savedInstanceState.getInt("spinnerVrstaGorivaIzbraniItem");
            Seja.textBoxLokacija = savedInstanceState.getString("textBoxLokacija");
            ((TextView)findViewById(R.id.textBoxLokacija)).setText(Seja.textBoxLokacija);

            ArrayList<String> al = savedInstanceState.getStringArrayList("spinnerVrstaGoriva");
            //int idx;
            if (al.size() == 0) pridobiPodatkeZaSpinner(R.id.spinnerVrstaGoriva, URL_GET_GORIVA); //če je spinner prazen, se ga napolni preko spletne poizvedbe
            else //if (al.size() >= 1) //preveri prvi vnos ali je default 'prazen spinner' vnos, ali so dejansko podatki not
            {
                if (al.get(0).contains(getResources().getStringArray(R.array.spinnerVrstaGoriva)[0])) //primerja, ali je prvi item v listi item <string-array name="spinnerVrstaGoriva"><item>Izberi vrsto goriva</item></string-array>
                    pridobiPodatkeZaSpinner(R.id.spinnerVrstaGoriva, URL_GET_GORIVA); //če je default 'Izberi x', potem še spinner ni bil napolnjen in naredi spletno poizvedbo
                else {
                    napolniSpinnerSPodatki(R.id.spinnerVrstaGoriva, al); //če vsebuje več vnosov, potem se vnosi samo prekopirajo, brez spletne poizvedbe
                   // idx = savedInstanceState.getInt("spinnerVrstaGorivaIzbraniItem");
                   // ((Spinner)findViewById(R.id.spinnerVrstaGoriva)).setSelection(idx, true);
                }
            }

            al = savedInstanceState.getStringArrayList("spinnerDistributer");
            if (al.size() == 0) pridobiPodatkeZaSpinner(R.id.spinnerDistributer, URL_GET_DISTRIBUTERJI);
            else //if (al.size() >= 1)
            {
                if (al.get(0).contains(getResources().getStringArray(R.array.spinnerDistributer)[0]))
                    pridobiPodatkeZaSpinner(R.id.spinnerDistributer, URL_GET_DISTRIBUTERJI);
                else {
                    napolniSpinnerSPodatki(R.id.spinnerDistributer, al);
                 //   idx = savedInstanceState.getInt("spinnerDistributerIzbraniItem");
                //    ((Spinner)findViewById(R.id.spinnerDistributer)).setSelection(idx, true);
                }
            }

        }
        else //hladen zagon, nalozi vse od zacetka
        {
            //zaklene UI, dokler se podatki ne naložijo
            omogociUI(false);
            //postavi splash screen v ospredje
            (findViewById(R.id.imageViewSplash)).bringToFront();
            Seja.stanjeOkvirja = StanjeOkvirja.ZAPRT;



            //napolne spinnerje (dropdowne) s podatki iz spleta
            pridobiPodatkeZaSpinner(R.id.spinnerDistributer, URL_GET_DISTRIBUTERJI);
            pridobiPodatkeZaSpinner(R.id.spinnerVrstaGoriva, URL_GET_GORIVA);

            //nalozi okvir, kjer uporabnik izbira dodatne moznosti za iskanje bencinskih crpalk
            naloziOkvirZMonznostmi();
        }

        //Listener, ki posluša, ko uporabnik izbere možnost na spinnerju
        AdapterView.OnItemSelectedListener spinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //shrani nazadnje izbrano možnost
                if (parent.getId() == R.id.spinnerDistributer) {
                    Seja.spinnerDistributerIzbraniItem = position;
                 //   Toast.makeText(MainActivity.this, "pos="+position, Toast.LENGTH_SHORT).show();
                } else if (parent.getId() == R.id.spinnerVrstaGoriva) {
                    Seja.spinnerVrstaGorivaIzbraniItem = position;
                    //Toast.makeText(MainActivity.this, "Gorivo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        //doda Listener objekt obema spinnerjema
        ((Spinner)findViewById(R.id.spinnerDistributer)).setOnItemSelectedListener(spinnerOnItemSelectedListener);
        ((Spinner)findViewById(R.id.spinnerVrstaGoriva)).setOnItemSelectedListener(spinnerOnItemSelectedListener);

        //doda listener za odpiranje MapsActivity
        (findViewById(R.id.buttonIsci)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });


    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //prekličemo vse obstoječe spletne Volley requeste
        queue.cancelAll(R.id.spinnerDistributer);
        queue.cancelAll(R.id.spinnerVrstaGoriva);
    }

    private void omogociUI(boolean flag)
    {
        Button isci = findViewById(R.id.buttonIsci);
        Button uporabiGPS = findViewById(R.id.buttonGPS);
        Button odpriZapriOkvir = findViewById(R.id.buttonOdpriZapriOkvir);
        EditText textBoxLokacija = findViewById(R.id.textBoxLokacija);
        Spinner spinnerIzberiDistributerja = findViewById(R.id.spinnerDistributer);
        Spinner spinnerVrstaGoriv = findViewById(R.id.spinnerVrstaGoriva);
        ConstraintLayout okvir = findViewById(R.id.constraintLayoutOkvir);

        isci.setEnabled(flag);
        uporabiGPS.setEnabled(flag);
        odpriZapriOkvir.setEnabled(flag);
        textBoxLokacija.setEnabled(flag);
        spinnerIzberiDistributerja.setEnabled(flag);
        spinnerVrstaGoriv.setEnabled(flag);
        okvir.setEnabled(flag);

        if (flag) //omogoči, prikaže
        {
            isci.setVisibility(View.VISIBLE);
            uporabiGPS.setVisibility(View.VISIBLE);
            odpriZapriOkvir.setVisibility(View.VISIBLE);
            textBoxLokacija.setVisibility(View.VISIBLE);
            spinnerIzberiDistributerja.setVisibility(View.VISIBLE);
            spinnerVrstaGoriv.setVisibility(View.VISIBLE);
            okvir.setVisibility(View.VISIBLE);
        }
        else //onemogoči, skrije
        {
            isci.setVisibility(View.INVISIBLE);
            uporabiGPS.setVisibility(View.INVISIBLE);
            odpriZapriOkvir.setVisibility(View.INVISIBLE);
            textBoxLokacija.setVisibility(View.INVISIBLE);
            spinnerIzberiDistributerja.setVisibility(View.INVISIBLE);
            spinnerVrstaGoriv.setVisibility(View.INVISIBLE);
            okvir.setVisibility(View.INVISIBLE);
        }
    }

    //napolni spinnerje s podatki preko GET zahteve s klicem na goriva.si API
    //po uspešno opravljeni zahtevi se UI samodejno omogoči, drugače ostane onemogočen
    private void pridobiPodatkeZaSpinner(final int spinnerViewID, final String URL_GET)
    {
        omogociUI(false);
        JsonArrayRequest array_request = new JsonArrayRequest(Request.Method.GET, URL_GET, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("Volley onResponse", "URL="+URL_GET);
                //Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG);
                SPLETNE_ZAHTEVE_STEVEC--; //zahteva zaključena, števec se dekrementira

                ArrayList<String> list = new ArrayList<>(JSONArrayNamesToArrayList(response));
                //list.addAll(JSONArrayNamesToArrayList(response));

                if (spinnerViewID == R.id.spinnerDistributer)
                {
                    list.add(0, "Vsi distributerji"); //doda možnost izbrati vse na prvo mesto
                    //Seja.arrayListDistributer = list; //doda seznam distributerjev v 'sejo', da se ob ponovni vrnitvi k aplikaciji ni potrebno se enkrat klicat GET funkcije s spleta
                }
                else if (spinnerViewID == R.id.spinnerVrstaGoriva)
                {
                    list.add(0, "Vsa vrsta goriv");
                    //Seja.arrayListVrstaGoriva = list;
                }




                napolniSpinnerSPodatki(spinnerViewID, list);

                //če se še izvajajo spletne zahteve ostane UI zaklenjen
                //če pride do napake pri pridobivanju spletne zahteve ostane UI zaklenjen
                if (!seIzvajajoSpletneZahteve() && !SPLETNE_ZAHTEVE_NAPAKA)
                {
                    //če je aplikacija že prej bila aktivna se samo UI po polnjenju s podatki nazaj omogoči, drugače se še prej splash screen skrije
                    if (APLIKACIJA_ZE_AKTIVNA)
                        omogociUI(true);
                    else
                        skrijSplashScreen();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley onErrorResponse", "NAPAKA: " + error.getMessage());
                Toast.makeText(MainActivity.this, "Napaka pri povezavi s strežnikom. Poskusite pozneje", Toast.LENGTH_LONG).show();
                omogociUI(false); //napaka, UI se zaklene
                SPLETNE_ZAHTEVE_STEVEC--; //zahteva zaključena, števec se dekrementira
                SPLETNE_ZAHTEVE_NAPAKA = true;
                //Toast.makeText(MainActivity.this, R.string.VolleyErrorResponse, Toast.LENGTH_LONG);
                //Toast.makeText(MainActivity.this, "se izvaja="+SPLETNE_ZAHTEVE_STEVEC, Toast.LENGTH_SHORT).show();
            }
        });

        //At a high level, you use Volley by creating a RequestQueue and passing it Request objects.
        //The RequestQueue manages worker threads for running the network operations, reading from and writing to the cache,
        //and parsing responses. Requests do the parsing of raw responses and Volley takes care of dispatching the
        // parsed response back to the main thread for delivery.

        array_request.setTag(spinnerViewID); //označimo request, da se lahko kasneje prekliče, če je potrebno
        //request, ki se doda v queue
        queue.add(array_request); //dodajanje requesta v queue
        SPLETNE_ZAHTEVE_STEVEC++;
    }

    //ustvari ArrayList iz imen ("name") hranjenih v JSONArrayu
    private ArrayList<String> JSONArrayNamesToArrayList(JSONArray arr)
    {
        ArrayList<String> arrList = new ArrayList<>();
        if (arr != null)
        {
            for (int i = 0; i < arr.length(); i++)
            {
                try
                {
                    arrList.add(arr.getJSONObject(i).getString("name"));
                }
                catch (Exception e)
                {
                    Log.e("JSONtoArrayList", "Napaka pri polnjenju array liste=" + e.getMessage());
                }
            }
        }

        return arrList;
    }

    private void napolniSpinnerSPodatki(int spinnerViewID, ArrayList<String> spinnerItemsList)
    {
        Spinner spinner = findViewById(spinnerViewID); //izbere spinner za napolnit
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerItemsList); //ustvari adapter, tip dropdowna in ga napolni s podatki

        //Če je arrayListDistributer prazen pomeni, da se še ni izvajala poizvedba za distributerji
        // in je v spinnerju samo možnost "Izberi distributerja"
        //Če je arrayListDistributer prazen je index 0 = Izberi distributerja
        //Če arrayListDistributer ni prazen je index 0 = Vsi distributerji
        if ((spinnerViewID == R.id.spinnerDistributer) && (Seja.spinnerDistributerIzbraniItem >= 0) && (Seja.arrayListDistributer.size() > 0))
        {
            spinner.setAdapter(adapter);
            spinner.setSelection(Seja.spinnerDistributerIzbraniItem);
        }
        else if ((spinnerViewID == R.id.spinnerVrstaGoriva) && (Seja.spinnerVrstaGorivaIzbraniItem >= 0) && (Seja.arrayListVrstaGoriva.size() > 0))
        {
            spinner.setAdapter(adapter);
            spinner.setSelection(Seja.spinnerVrstaGorivaIzbraniItem);
        }
        else
        {
            //ob kliku na spinner ga šele napolni s podatki, tako da preden klikneš na spinner je še vedno izpisano "izberi X"
            spinner.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    //želimo, da se spinner samo enkrat napolne s podatki, drugače je problemi z UI
                    if (spinner.getAdapter().getCount() <= 1)
                    // če je več, kot en item v spinnerju (prvi je defualt item 'izberi x') potem to pomeni, da je spinner že napolnjen.
                    {
                        if (spinnerViewID == R.id.spinnerDistributer)
                        {
                            //doda seznam distributerjev v 'sejo', da se ob ponovni vrnitvi k aplikaciji ni potrebno se enkrat klicat GET funkcije s spleta
                            //V sejo se dodajajo tukaj, ker tukaj se dejansko adapter šele doda v spinner
                            //se je dogajalo, da si obrnil zaslon preden je uporabnik kliknal spinner, in je prvi item se samodejno spremenil iz "Izberi distributerja" v "Vsi distributerji"
                            Seja.arrayListDistributer = spinnerItemsList;
                        }
                        else if (spinnerViewID == R.id.spinnerVrstaGoriva)
                        {
                            Seja.arrayListVrstaGoriva = spinnerItemsList;
                        }

                        spinner.setAdapter(adapter);
                        spinner.performClick(); //ga odpre, drugače ga ne bi, ker ga je polnjenje s podatki zaprlo, user je pa kliknil nanj
                        return true; //ontouch je odigral svojo vlogo in se ga ne bo naprej podajalo po ostalih viewih
                    }

                    return false;
                }
            });
        }
    }

    private void skrijSplashScreen()
    {
        //po pretečenem času skrije splash screen in odlkene UI
        //kreira handler, kateri požene kodo po pretečenem času
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable(){
            @Override
            public void run() {


                //skrije splash screen
                //https://stackoverflow.com/questions/20782260/making-a-smooth-fade-out-for-imageview-in-android
                ImageView splash = findViewById(R.id.imageViewSplash);
                Animation fadeOut = new AlphaAnimation(1, 0); // the 1, 0 here notifies that we want the opacity to go from opaque (1) to transparent (0)
                fadeOut.setInterpolator(new AccelerateInterpolator()); //An interpolator defines the rate of change of an animation.
                //// This allows the basic animation effects (alpha, scale, translate, rotate) to be accelerated, decelerated, repeated, etc.
                fadeOut.setDuration(1000); // Fadeout duration should be 1000 milli seconds
                //po končani animaciji se slika skrije in UI omogoci nazaj
                fadeOut.setAnimationListener(new Animation.AnimationListener()
                {
                    public void onAnimationEnd(Animation animation)
                    {
                        splash.setVisibility(View.INVISIBLE);
                        omogociUI(true);
                    }
                    public void onAnimationRepeat(Animation animation) {}
                    public void onAnimationStart(Animation animation) {}
                });

                splash.startAnimation(fadeOut);

            }
        }, SPLASH_DOLZINA);
    }

    //preveri, ali se izvajajo kaksne (get) zahteve preko Volleya (JsonArrayRequest)
    private boolean seIzvajajoSpletneZahteve()
    {
        return SPLETNE_ZAHTEVE_STEVEC > 0;
    }

    private void naloziOkvirZMonznostmi()
    {
        ConstraintLayout okvir = findViewById(R.id.constraintLayoutOkvir);
        okvir.setVisibility(View.INVISIBLE); //skrijemo in na novo prikažemo, da se ob zagonu ne vidijo sledi animacije
        //kreiramo animacijo za okvir,
        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                1, (float) 0.001, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling - v katero smer se začne podirati
                Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling - 0f da gre od zgoraj navzdol, črta ostane zgoraj; pri 1 bi črta bila spodaj
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(10);
        okvir.startAnimation(anim); //zažene se animacija zapiranja, da je ob zagonu aplikacije okvir zaprt/skrit
        okvir.setVisibility(View.VISIBLE);

        Button okvirPuscica = findViewById(R.id.buttonOdpriZapriOkvir); //puscica gumb, na katerega stisnes, da se okvir odpira/zapira

        okvirPuscica.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //ConstraintLayout okvir = findViewById(R.id.constraintLayoutOkvir);
                switch (Seja.stanjeOkvirja) //stanje animacije
                {
                    case ODPRT:
                    {
                        Animation okvir_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.okvir_close);
                        okvir_close.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                Seja.stanjeOkvirja = StanjeOkvirja.SE_ANIMIRA;

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                Seja.stanjeOkvirja = StanjeOkvirja.ZAPRT;
                                ((Button)findViewById(R.id.buttonOdpriZapriOkvir)).setText("▼"); //puscica gumb, na katerega stisnes, da se okvir odpira/zapira
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        okvir_close.setFillAfter(true); //da ostanejo rezultati animacije
                        okvir.startAnimation(okvir_close);
                    }
                    break;
                    case ZAPRT:
                    {
                        Animation okvir_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.okvir_open);
                        okvir_open.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                                Seja.stanjeOkvirja = StanjeOkvirja.SE_ANIMIRA;
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                Seja.stanjeOkvirja = StanjeOkvirja.ODPRT;
                                ((Button)findViewById(R.id.buttonOdpriZapriOkvir)).setText("▲"); //puscica gumb, na katerega stisnes, da se okvir odpira/zapira
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        okvir_open.setFillAfter(true); //da ostanejo rezultati animacije
                        okvir.startAnimation(okvir_open);

                    }
                    break;
                    default:{}
                }
            }
        });
    }

    //pridobi vse vnose iz spinnerja
    public ArrayList<String> pridobiPodatkeIzSpinnerja(Spinner s)
    {
        SpinnerAdapter adapter = s.getAdapter();
        int n = adapter.getCount();
        ArrayList<String> list = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
        {
            list.add((String) adapter.getItem(i));
        }
        return list;
    }

}
