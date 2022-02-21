package com.example.bencinskecrpalke;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GorivoCell extends LinearLayout
{
    /*
    private String gorivoNaziv;
    private String gorivoCena;
    private Color gorivoNazivTextColor;
    private Color gorivoCenaTextColor;
    private Float gorivoNazivTextSize;
    private Float gorivoCenaTextSize;
    private Color gorivoNazivBackgroundColor;
    private Color gorivoCenaBackgroundColor;
*/
    private View cell;

    //se uporablja kadar se kreira ven iz xml
    public GorivoCell(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        cell = View.inflate(context, R.layout.gorivo_cell_layout, this);



        ((TextView)cell.findViewById(R.id.GorivoNaziv)).setTextSize((float)getResources().getDimensionPixelSize(R.dimen._3ssp));
        ((TextView)cell.findViewById(R.id.GorivoCena)).setTextSize((float)getResources().getDimensionPixelSize(R.dimen._4ssp));

        /*
        * android:layout_width="wrap_content"
                    android:layout_height="match_parent"
        * */



        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GorivoCell);

        try
        {
             //bere iz attrs.xml (custom attributi, ki se uporabljajo, kot app:nazivGoriva) in zapise v inflejtan view
            ((TextView)cell.findViewById(R.id.GorivoNaziv)).setText(ta.getString(R.styleable.GorivoCell_nazivGoriva));
            ((TextView)cell.findViewById(R.id.GorivoCena)).setText(ta.getString(R.styleable.GorivoCell_cenaGoriva));
            ((TextView) cell.findViewById(R.id.GorivoNaziv)).setTextColor(ta.getColor(R.styleable.GorivoCell_nazivGorivaTextColor, Color.BLACK));
            ((TextView) cell.findViewById(R.id.GorivoCena)).setTextColor(ta.getColor(R.styleable.GorivoCell_cenaGorivaTextColor, Color.BLACK));
            ((TextView)cell.findViewById(R.id.GorivoNaziv)).setTextSize(ta.getFloat(R.styleable.GorivoCell_nazivGorivaTextSize, (float)getResources().getDimensionPixelSize(R.dimen._3ssp)));
            ((TextView)cell.findViewById(R.id.GorivoCena)).setTextSize(ta.getFloat(R.styleable.GorivoCell_cenaGorivaTextSize, (float)getResources().getDimensionPixelSize(R.dimen._4ssp)));

            //button.setTextSize();
            //TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen._12ssp)

            Drawable background = cell.findViewById(R.id.rowGorivoNaziv).getBackground();
            background.mutate();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable)background).getPaint().setColor(ta.getColor(R.styleable.GorivoCell_nazivGorivaBackgroundColor, Color.WHITE));
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable)background).setColor(ta.getColor(R.styleable.GorivoCell_nazivGorivaBackgroundColor, Color.WHITE));
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable)background).setColor(ta.getColor(R.styleable.GorivoCell_nazivGorivaBackgroundColor, Color.WHITE));
            }

            background = cell.findViewById(R.id.rowGorivoCena).getBackground();
            background.mutate();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable)background).getPaint().setColor(ta.getColor(R.styleable.GorivoCell_cenaGorivaBackgroundColor, Color.WHITE));
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable)background).setColor(ta.getColor(R.styleable.GorivoCell_cenaGorivaBackgroundColor, Color.WHITE));
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable)background).setColor(ta.getColor(R.styleable.GorivoCell_cenaGorivaBackgroundColor, Color.WHITE));
            }
        }
        finally
        {
            ta.recycle();
        }
    }
/*
    //se uporablja kadar se kreira dinamično v kodi
    public GorivoCell(Context context)
    {
        super(context);
    }

    public GorivoCell(Context context, String naziv, String cena, Color nazivTextColor, Color cenaTextColor, Color nazivBackgroundColor, Color cenaBackgroundColor)
    {
        super(context);
        this.gorivoNaziv = naziv;
        this.gorivoCena = cena;
        this.gorivoNazivTextColor = nazivTextColor;
        this.gorivoCenaTextColor = cenaTextColor;
        this.gorivoNazivTextSize = (float)3.0;
        this.gorivoCenaTextSize = (float)4.0;
        this.gorivoNazivBackgroundColor = nazivBackgroundColor;
        this.gorivoCenaBackgroundColor = cenaBackgroundColor;
    }

    public GorivoCell(Context context, String naziv, String cena, Color nazivTextColor, Color cenaTextColor, Float nazivTextSize, Float cenaTextSize, Color nazivBackgroundColor, Color cenaBackgroundColor)
    {
        super(context);
        this.gorivoNaziv = naziv;
        this.gorivoCena = cena;
        this.gorivoNazivTextColor = nazivTextColor;
        this.gorivoCenaTextColor = cenaTextColor;
        this.gorivoNazivTextSize = nazivTextSize;
        this.gorivoCenaTextSize = cenaTextSize;
        this.gorivoNazivBackgroundColor = nazivBackgroundColor;
        this.gorivoCenaBackgroundColor = cenaBackgroundColor;
    }
*/

    public GorivoCell(Context context)
    {
        super(context);
        //cell = View.inflate(context, R.layout.gorivo_cell_layout, this);

        cell = LayoutInflater.from(context).inflate(R.layout.gorivo_cell_layout, this, true);


        ViewGroup.LayoutParams params = cell.getLayoutParams();

        //params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        //params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        //cell.setLayoutParams(params);

        ((TextView)cell.findViewById(R.id.GorivoNaziv)).setTextSize((float)getResources().getDimensionPixelSize(R.dimen._3ssp));
        ((TextView)cell.findViewById(R.id.GorivoCena)).setTextSize((float)getResources().getDimensionPixelSize(R.dimen._4ssp));
    }

    public void setNazivText(String naziv)
    {
        ((TextView)cell.findViewById(R.id.GorivoNaziv)).setText(naziv);
    }

    public void setCenaText(String cena)
    {
        ((TextView)cell.findViewById(R.id.GorivoCena)).setText(cena);
    }

    public void setNazivTextColor(int color)
    {
        ((TextView) cell.findViewById(R.id.GorivoNaziv)).setTextColor(color);
    }

    public void setCenaTextColor(int color)
    {
        ((TextView) cell.findViewById(R.id.GorivoCena)).setTextColor(color);
    }

    public void setNazivTextSize(float size)
    {
        ((TextView)cell.findViewById(R.id.GorivoNaziv)).setTextSize(size);
    }

    public void setCenaTextSize(float size)
    {
        ((TextView)cell.findViewById(R.id.GorivoCena)).setTextSize(size);
    }

    public void setNazivBackgroundColor(int color)
    {
        Drawable background = cell.findViewById(R.id.rowGorivoNaziv).getBackground();
        background.mutate();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable)background).getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable)background).setColor(color);
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable)background).setColor(color);
        }
    }

    public void setCenaBackgroundColor(int color)
    {
        Drawable background = cell.findViewById(R.id.rowGorivoCena).getBackground();
        background.mutate();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable)background).getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable)background).setColor(color);
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable)background).setColor(color);
        }
    }
}
