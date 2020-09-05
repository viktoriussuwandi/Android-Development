package com.example.myfruit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomListAdapter extends ArrayAdapter {
    private Context context;
    private String [] namaBuah;
    private int [] gambarBuah;


    //Constructor
    public CustomListAdapter(Context context1, String[] namaBuah, int[] gambarBuah) {
        super( context1, R.layout.item_buah, namaBuah );
        this.context = context1; this.namaBuah = namaBuah; this.gambarBuah = gambarBuah;
    }

    //GetView

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //1.Atur layout item
        View view = LayoutInflater.from( context ).inflate( R.layout.item_buah,parent,false );

        //2.Ambil setiap komponen unt penempatan item
        TextView TextNamaBuah = view.findViewById( R.id.TextViewDetailNama );
        ImageView ImageGambarBuah = view.findViewById( R.id.ImageViewDetailGambar );

        //3.Tampilkan data tiap item
        TextNamaBuah.setText( namaBuah[position] );
        ImageGambarBuah.setImageResource( gambarBuah[position] );

        return view;
    }
}
