package com.example.myfruit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    ImageView imgDetailImage;
    TextView txtDetailNama;

    //Bikin variabel supaya bisa ditampilin di log
    private static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail );

        ImageView imgDetailImage = findViewById( R.id.ImageViewDetailGambar );
        TextView txtDetailNama = findViewById( R.id.TextViewDetailNama );

        //Terima data dari ListView pada MainActivity.java
        String namaBuah = getIntent().getStringExtra( konstanta.DATANAMA);
        Integer gambarBuah = getIntent().getIntExtra( konstanta.DATAGAMBAR,0 );

        //Coba dulu (nge-test) : ditampilkan di log
        Log.d(TAG,"Nama : "+ namaBuah);
        Log.d(TAG,"Gambar : "+ gambarBuah);

        //Tampilin di komponen activity_detail.xml
        imgDetailImage.setImageResource( gambarBuah );
        txtDetailNama.setText( namaBuah );
    }
}