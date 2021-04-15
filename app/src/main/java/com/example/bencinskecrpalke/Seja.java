package com.example.bencinskecrpalke;

//razred Seja, vanj se hranijo podatki trenutnega stanja aplikacije-activityja, da se lahko ob ponovnem kreiranju activityja njegovo stanje obnovi. Pogost primer tega je menjava orientacije zaslona, kjer se
//activity uniči oz da v 'background state' in na novo naloži preko metode onCreate. Ta razred se ne uporablja pri hladnih zagonih (cold start, cold boot) aplikacije


import java.util.ArrayList;

public class Seja {

    protected static MainActivity.StanjeOkvirja stanjeOkvirja; //stanje okvirja z nastavitvami; odprt, zaprt, se animira
    protected static String buttonOdpriZapriOkvirText = ""; // načeloma je ▼ ali ▲
    protected static boolean SPLETNE_ZAHTEVE_NAPAKA = false; //zastavica, preverja, ali se je zgodila napaka pri branju podatkov iz spletnih zahtev
    protected static String textBoxLokacija = "";
    protected static ArrayList<String> arrayListDistributer = new ArrayList<>(); //spinnerDistributer
    protected static ArrayList<String> arrayListVrstaGoriva = new ArrayList<>(); //podatki iz spinnerja spinnerVrstaGoriva
    protected static int spinnerDistributerIzbraniItem = 0; //izbrana možnost na spinnerDistributer
    protected static int spinnerVrstaGorivaIzbraniItem = 0; //izbrana možnost na spinnerVrstaGoriva

}
