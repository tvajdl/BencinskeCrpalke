package com.example.bencinskecrpalke;

//razred Seja, vanj se hranijo podatki trenutnega stanja aplikacije-activityja, da se lahko ob ponovnem kreiranju activityja njegovo stanje obnovi. Pogost primer tega je menjava orientacije zaslona, kjer se
//activity uniči oz da v 'background state' in na novo naloži preko metode onCreate. Ta razred se ne uporablja pri hladnih zagonih (cold start, cold boot) aplikacije


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Seja {

    protected static MainActivity.StanjeOkvirja stanjeOkvirja; //stanje okvirja z nastavitvami; odprt, zaprt, se animira
    protected static String buttonOdpriZapriOkvirText = ""; // načeloma je ▼ ali ▲
    protected static boolean SPLETNE_ZAHTEVE_NAPAKA = false; //zastavica, preverja, ali se je zgodila napaka pri branju podatkov iz spletnih zahtev
    protected static String textBoxLokacija = "";
    protected static ArrayList<String> arrayListDistributer = new ArrayList<>(); //spinnerDistributer shranjeni nazivi distributerjev
    protected static ArrayList<String> arrayListDistributerID = new ArrayList<>(); //shranjeni IDji od zgornjih distributerjev, indexi se ujemajo naziv=ID
    protected static ArrayList<String> arrayListVrstaGoriva = new ArrayList<>(); //podatki iz spinnerja spinnerVrstaGoriva
    protected static ArrayList<String> arrayListVrstaGorivaID = new ArrayList<>(); //shranjeni IDji od zgornjih vrsta goriv, indexi se ujemajo ime goriva=ID
    protected static int spinnerDistributerIzbraniItem = 0; //izbrana možnost na spinnerDistributer
    protected static int spinnerVrstaGorivaIzbraniItem = 0; //izbrana možnost na spinnerVrstaGoriva
}
