package com.example.catatan_hutang;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.arlib.floatingsearchview.FloatingSearchView.*;

public class MainActivity extends AppCompatActivity {
    List<CatatanModel> dataCatatan = new ArrayList<>();
    RecyclerView recycler;
    RealmHelper realm;
    FloatingSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( MainActivity.this,Tambah.class ) );
            }
        } );

        //1.Membuat layout per item -> item_catatan.xml

        //2.Membuat data model -> CatatanModel.java
            //Data dummy
//            CatatanModel catatan1 = new CatatanModel();
//            catatan1.setId( 1 );
//            catatan1.setJudul( "Hutang ke A" );
//            catatan1.setJumlahutang( "20000" );
//            catatan1.setTanggal( "12-04-2020" );
//            for (int i=0;i<20;i++){
//                dataCatatan.add( catatan1 );
//            }

            //Data Realm (selain pakai Data Dummy)
            //Realm adalah DBMS internal dengan basis MongoDb, sumber : https://realm.io/docs/java/latest
            //Cata pembuatan :
            //1.Persiapan -> lengkapi dependencies & implementation -> gradle
            //2.Ubah Model menjadi object/entity (spt konsep MVC) -> CatatanModel.java
            //3.Buat Helper untuk query ke Realm'nya -> RealmHelper.java
            //  A.Insert data :
            //    1.Get data dari user -> Tambah.java & CatatanModel.java
            //    2.Generate ID dengan cara check existing Id & generate -> RealmHelper.java
            //    3.Simpan ke dalam Realm

            //  B.Tampil Data :
            //    1.Get data dari Realm -> RealmHelper.java
            //    2.Ubah format menjadi Arraylist -> RealmHelper.java
            //    3.Tampilkan ke dalam RecycleView


        realm = new RealmHelper( MainActivity.this ); //Inisiasi RealmHelper.java
        dataCatatan = realm.showData(); //Panggil function showdata dari RealmHelper.java & tampung ke arraylist dataCatatan

        //----------------------------------------------------------------------------

        //3.Adapter-> catatanAdapter.java
            recycler = findViewById( R.id.recyclerView );
            recycler.setAdapter( new CatatanAdapter( MainActivity.this,dataCatatan ) );

        //4.Layoutmanager
        recycler.setLayoutManager(new LinearLayoutManager( MainActivity.this ) );
        recycler.setHasFixedSize( true );
        recycler.addItemDecoration(new DividerItemDecoration( MainActivity.this,1 ) );


        //------------------------------------------------------------------------------------------
        //SEARCH VIEW
        //------------------------------------------------------------------------------------------
        searchView = findViewById( R.id.floating_search_view );
        searchView.setOnQueryChangeListener( new FloatingSearchView.OnQueryChangeListener(){
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                Toast.makeText( MainActivity.this,""+newQuery,Toast.LENGTH_SHORT ).show();

                //menjalankan filter data dengan mengirim list datacatatan & query(saran pencarian/search)
                List<CatatanModel> filtercatatan = filterdata(dataCatatan,newQuery);

                //Update recycleView -> menampilkan list yg sesuai dengan pencarian
                recycler.setAdapter( new CatatanAdapter( MainActivity.this,filtercatatan ));
            }
        } );
    }

    private List<CatatanModel> filterdata(List<CatatanModel> dataCatatan, String newQuery) {
        String lowercasequery = newQuery.toLowerCase();//merubah semua saran pencarian menjadi huruf kecil
        List<CatatanModel> filterdata = new ArrayList<>();//membuat arraylist baru unt menampung data yg ditulis

        //Membuat pengulangan & pengkondisian, fungsi :
        //1.Ambil semua judul pada list datacatatan
        //2.Mencocokan teks yg ditulis user dengan setiap data pada list datacatatan
        for (int i=0 ; i<dataCatatan.size() ; i++){
            String text = dataCatatan.get(i).getJudul().toLowerCase();
            if(text.contains( lowercasequery )){
                filterdata.add(dataCatatan.get(i));
            }
        }
        return filterdata;
    }


    //------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu_main,menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected( item );
    }


    //Membuat function onResume, fungsi :
    // agar data dapat tampil ketika selesai dari activity lainya (Tambah data)
    @Override
    protected void onResume() {
        //1.Panggil function showdata pada RealmHelper.java
        super.onResume();
        dataCatatan = realm.showData();

        //2.update adapter'nya
        recycler.setAdapter( new CatatanAdapter( MainActivity.this,dataCatatan ) );
    }


}
