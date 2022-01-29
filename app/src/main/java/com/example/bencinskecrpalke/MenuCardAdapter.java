package com.example.bencinskecrpalke;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        this.context = context;
        this.menuCardsArrayList = menuCardsArrayList;
    }

    @NonNull
    @Override
    public MenuCardAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_card_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuCardAdapter.Viewholder holder, int position) {
        //se klice, ko se card dinamicno ustvari
        // to set data to textview of each card layout
        MenuCardBencinskaCrpalka model = menuCardsArrayList.get(position);
        //holder je sestavljen glede menu_card_layout.xml
        //model je objekt razreda MenuCardBencinskaCrpalka
        holder.logo.setImageResource(model.getLogoImageID());
        holder.naslov.setText(model.getNaslov());
        holder.naziv.setText(model.getNaziv());
//        holder.gorivoLayout = model.getGorivoLayout();

        //Toast.makeText(holder.logo.getContext(), holder.naziv.getText(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return menuCardsArrayList.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {

        public View view; //trenutni CardView
        public ImageView logo;
        public TextView naziv, naslov;
        public LinearLayout gorivoLayout;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            logo = itemView.findViewById(R.id.logo);
            naziv = itemView.findViewById(R.id.naziv);
            naslov = itemView.findViewById(R.id.naslov);
            //gorivoLayout = itemView.findViewById(R.id.gorivoLayout);

            //Toast.makeText(itemView.getContext(), "test", Toast.LENGTH_SHORT).show();

            view = itemView;

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
    }
}
