package com.example.bencinskecrpalke;

import android.widget.LinearLayout;

public class MenuCardBencinskaCrpalka {

    private String naziv;
    private String naslov;
    private LinearLayout gorivoLayout;
    private double x;
    private double y;
    private int logoImageID;


    public MenuCardBencinskaCrpalka ()
    {
        this.naziv = "Naziv";
        this.naslov = "Naslov";
        this.gorivoLayout = null;
        this.x = 0.0;
        this.y = 0.0;
        logoImageID = R.drawable.no_logo;
    }

    public MenuCardBencinskaCrpalka (String title, String address, double x, double y, int logo, LinearLayout fuelLayout)
    {
        this.naziv = title;
        this.naslov = address;
        this.x = x;
        this.y = y;
        this.logoImageID = logo;
        this.gorivoLayout = fuelLayout;
    }

    public String getNaslov()
    {
        return this.naslov;
    }
    public String getNaziv() { return this.naziv; }
    public LinearLayout getGorivoLayout() { return this.gorivoLayout; }
    public double getX()
    {
        return this.x;
    }
    public double getY()
    {
        return this.y;
    }
    public int getLogoImageID() { return this.logoImageID; }

    public void setNaslov(String naslov) { this.naslov = naslov; }
    public void setNaziv(String naziv) { this.naziv = naziv; }
    public void setGorivoLayout(LinearLayout linearLayout) { this.gorivoLayout = linearLayout; }
    public void setX(double x)
    {
        this.x = x;
    }
    public void setY(double y)
    {
        this.y = y;
    }
    public void setLogo(int drawableLogoID) { this.logoImageID = drawableLogoID; }

}
