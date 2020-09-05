package com.example.myfruit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    String [] namaBuah = { "Alpukat","Apel","Ceri","Durian","Jambu Air","Manggis","Strawberry" };
    int [] gambarBuah = { R.drawable.alpukat, R.drawable.apel, R.drawable.ceri, R.drawable.durian, R.drawable.jambuair, R.drawable.manggis,R.drawable.strawberry};
    int [] suaraBuah = { R.raw.alpukat, R.raw.apel, R.raw.ceri, R.raw.durian, R.raw.jambuair, R.raw.manggis,R.raw.strawberry };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        listView = findViewById( R.id.ListView );

        //Menjadikan file CustomListAdapter sebagai adapter pada listView
        //ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,namaBuah); //adapter yg lama
        CustomListAdapter adapter = new CustomListAdapter( MainActivity.this,namaBuah,gambarBuah );
        listView.setAdapter(adapter);

        //Ketika item ListView di klik -> muncul suara nama buah
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaPlayer.create( MainActivity.this, suaraBuah[position] ).start();

                //Pindah ke DetailActivity.java
                Intent intent = new Intent( MainActivity.this, DetailActivity.class );
                intent.putExtra( konstanta.DATANAMA,namaBuah[position] );
                intent.putExtra( konstanta.DATAGAMBAR,gambarBuah[position] );
                startActivity( intent );
            }
        } );

    }
}