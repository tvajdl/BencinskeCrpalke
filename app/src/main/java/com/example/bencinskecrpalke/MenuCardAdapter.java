package com.example.bencinskecrpalke;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/*
* Adapter Class in RecyclerView will get the data from your Modal Class and set that data to your item of RecyclerView.
* Below is the code for the MenuCardAdapter.java file. Comments are added inside the code to understand the code in more detail.
* */

public class MenuCardAdapter extends RecyclerView.Adapter<MenuCardAdapter.Viewholder> {
    private Context context;
    private ArrayList<MenuCardBencinskaCrpalka> menuCardsArrayList;

    // Constructor
    public MenuCardAdapter(Context context, ArrayList<MenuCardBencinskaCrpalka> menuCardsArrayList) {
        try {
            this.context = context;
            this.menuCardsArrayList = menuCardsArrayList;
        }
        catch (Exception e)
        {
            Log.e("MenuCardAdapter Constructor", e.getMessage());
        }
    }

    @NonNull
    @Override
    public MenuCardAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.

        try
        {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_card_layout, parent, false);
            return new Viewholder(view, this);
        }
        catch (Exception e)
        {
            Log.e("onCreateViewHolder", "onCreateViewHolder = " + e.getMessage());
            return null;
        }
    }




    @Override
    public void onBindViewHolder(@NonNull MenuCardAdapter.Viewholder holder, int position) {
        try
        {
            //se klice, ko se card dinamicno ustvari
            // to set data to textview of each card layout
            MenuCardBencinskaCrpalka crpalka = menuCardsArrayList.get(position);
            //holder je sestavljen glede menu_card_layout.xml
            //crpalka je objekt razreda MenuCardBencinskaCrpalka
            //različne viewe na cardu/kartici (iz menija) polnimo s podatki iz objekta crpalka
            holder.logo.setImageResource(crpalka.getLogoImageID());

            holder.naslov.setText(crpalka.getNaslov());
            holder.naziv.setText(crpalka.getNaziv());
            holder.odpiralni_cas.setText(crpalka.getOdpiralniCas());
            holder.oddaljenost.setText(crpalka.getOddaljenost());

            Button prikaziNavigacijo = (Button)holder.itemView.findViewById(R.id.buttonNavigacija);
            prikaziNavigacijo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //odpre google maps in pokaže pot
                    MapsActivity.zazeniNavigacijo(crpalka.getLat(), crpalka.getLng(), v.getContext());
                }
            });

            //holder.gorivoLayout = crpalka.getGorivoLayout();

    /*
            if (holder.gorivoLayout.getParent() != null)
            {
                ((ViewGroup)holder.gorivoLayout.getParent()).removeView((View)holder.gorivoLayout);
            }
    */
            //če je to odkoentirano vrže error The specified child already has a parent. You must call removeView() on the child's parent first.
            //holder.gorivoLayout.addView(crpalka.getGorivoLayout());


            if (holder.gorivoLayout.getChildCount() > 0) {
                holder.gorivoLayout.removeAllViews();
                //Log.e("NULL", String.valueOf(holder.gorivoLayout.getChildCount()));
            }



            if (crpalka.getGorivoLayout().getParent() != null)
            {
                Log.e("TEST", "NI NULL");
                ((ViewGroup)crpalka.getGorivoLayout().getParent()).removeView(crpalka.getGorivoLayout());
            }
            else Log.e("TEST", "JE NULL");

            holder.gorivoLayout.addView(crpalka.getGorivoLayout());
            if (crpalka.getGorivoLayout() == null)
                Log.e("NULL", "LAYOUT NULL");

            //crpalka.getGorivoLayout().removeAllViews();
            //holder.gorivoLayout.addView(crpalka.getGorivoLayout());
    //        holder.gorivoLayout = crpalka.getGorivoLayout();

            //Toast.makeText(holder.logo.getContext(), holder.naziv.getText(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.e("e", "onbind " + e.getMessage());

        }
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return menuCardsArrayList.size();
    }

    public ArrayList<MenuCardBencinskaCrpalka> getArrayList()
    {
        return this.menuCardsArrayList;
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {

        public View view; //trenutni CardView
        public ImageView logo;
        public TextView naziv, naslov, odpiralni_cas, oddaljenost;
        public FlowLayout gorivoLayout;

        public Viewholder(@NonNull View itemView, MenuCardAdapter adapter) {
            super(itemView);
            try
            {


                logo = itemView.findViewById(R.id.imageViewLogo);
                naziv = itemView.findViewById(R.id.textViewNaziv);
                naslov = itemView.findViewById(R.id.textviewNaslov);
                odpiralni_cas = itemView.findViewById(R.id.textviewOdpiralniCas);
                gorivoLayout = itemView.findViewById(R.id.flowLayoutGorivo);
                oddaljenost = itemView.findViewById(R.id.textViewOddaljenost);

                //Toast.makeText(itemView.getContext(), "test", Toast.LENGTH_SHORT).show();

                view = itemView;

        //        FlowLayout flowLayout = (FlowLayout)itemView.findViewById(R.id.flowLayoutGoriva);
        //      for ( int i = 0; i < 2 /*flowLayout.getChildCount()*/;  i++ ){
        //            View view = flowLayout.getChildAt(i);
        //            view.setVisibility(View.GONE); //skrije tista goriva, ko jih bencinska ne ponuja
        //        }

        //        Toast.makeText(itemView.getContext(), Integer.toString(flowLayout.getChildCount()), Toast.LENGTH_LONG).show();

                //ViewGroup.LayoutParams par = view.getLayoutParams();
                //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();

                //params.gravity
                //Toast.makeText(itemView.getContext(), Integer.toString(params.gravity), Toast.LENGTH_SHORT).show();
                view.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        // item clicked

                        //Toast.makeText(v.getContext(), "clicked", Toast.LENGTH_SHORT).show();
                    }
                });



            }
            catch (Exception e)
            {
                Log.e("e", e.getMessage());

            }
        }
    }
}
