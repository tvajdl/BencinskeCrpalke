package com.example.bencinskecrpalke;

import org.apmem.tools.layouts.FlowLayout;

public class MenuCardBencinskaCrpalka {

    private String naziv;
    private String naslov;
    private String odpiralniCas;
    private String oddaljenost;
    private FlowLayout gorivoLayout;
    private double lng;
    private double lat;
    private int logoImageID;
    private int markerImageID;
    private boolean odprtoZdaj;

    public MenuCardBencinskaCrpalka ()
    {
        this.naziv = "Naziv";
        this.naslov = "Naslov";
        this.odpiralniCas = "Odpiralni čas";
        this.oddaljenost = "Oddaljenost";
        this.gorivoLayout = null;
        this.lng = 0.0;
        this.lat = 0.0;
        this.logoImageID = R.drawable.no_logo;
        this.markerImageID = R.drawable.no_logo_marker;
        this.odprtoZdaj = false;
    }

    public MenuCardBencinskaCrpalka (String title, String address, String open_hours, String oddaljenost, double lng, double lat, int logo, int marker, boolean odprtoZdaj, FlowLayout fuelLayout)
    {
        this.naziv = title;
        this.naslov = address;
        this.odpiralniCas = "Delovni čas:\r\n" + open_hours;
        this.oddaljenost = "Oddaljenost: " + oddaljenost + "km";
        this.lng = lng;
        this.lat = lat;
        this.logoImageID = logo;
        this.markerImageID = marker;
        this.gorivoLayout = fuelLayout;
        this.odprtoZdaj = odprtoZdaj;
    }

    public String getNaslov()
    {
        return this.naslov;
    }
    public String getNaziv() { return this.naziv; }
    public String getOdpiralniCas() { return this.odpiralniCas; }
    public String getOddaljenost() { return this.oddaljenost; }
    public FlowLayout getGorivoLayout() { return this.gorivoLayout; }
    public double getLng()
    {
        return this.lng;
    }
    public double getLat()
    {
        return this.lat;
    }
    public int getLogoImageID() { return this.logoImageID; }
    public int getMarkerImageID() { return this.markerImageID; }
    public Boolean getOdprtoZdaj() { return this.odprtoZdaj; }

    public void setNaslov(String naslov) { this.naslov = naslov; }
    public void setNaziv(String naziv) { this.naziv = naziv; }
    public void setOdpiralniCas(String odpiralniCas) { this.odpiralniCas = odpiralniCas; }
    public void setOddaljenost(String oddaljenost) { this.oddaljenost = oddaljenost; }
    public void setGorivoLayout(FlowLayout linearLayout) { this.gorivoLayout = linearLayout; }
    public void setLng(double lng)
    {
        this.lng = lng;
    }
    public void setLat(double lat)
    {
        this.lat = lat;
    }
    public void setLogoImageID(int drawableLogoID) { this.logoImageID = drawableLogoID; }
    public void setMarkerImageID(int drawableMarkerID) { this.markerImageID = drawableMarkerID; }
    public void setOdprtoZdaj(boolean odprtoZdaj) { this.odprtoZdaj = odprtoZdaj; }

    public void skrij()
    {

    }
}
