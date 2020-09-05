package com.example.portal_film;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.portal_film.model.ResultsItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

public class DetailMovieActivity extends AppCompatActivity {

    //Terima data yg dikirimkan parcelable dengan ResultsItem
    ResultsItem dataMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_movie );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        //Terima data dari MovieAdapter.java (dlm bentuk Bundle yg dikirim lewat Intent)
        Bundle bundle = getIntent().getBundleExtra(MovieAdapter.DATA_EXTRA);

        //buka bundle'nya agar bisa diberikan sesuai film'nya
        dataMovie = Parcels.unwrap(bundle.getParcelable( MovieAdapter.DATA_MOVIE ));


        //Ubah Text pada ActionBar dengan Judul Film
        getSupportActionBar().setTitle( dataMovie.getTitle() );

        //Deklarasi variabel unt menampilkan poster & deskripsi pada activity_detail_movie.xml
        ImageView ivBackDrop = findViewById( R.id.iv_detail_backdrop );
        TextView tvDeskripsi = findViewById( R.id.tv_detail_description );

        //Tampilkan Poster film ke komponen ImageView pada activity_detail_movie.xml
        Glide.with( DetailMovieActivity.this).load("https://image.tmdb.org/t/p/w500"+dataMovie.getBackdropPath()).into(ivBackDrop);

        //Tampilkan deskripsi ke komponen TextView pada activity_detail_movie.xml
        tvDeskripsi.setText( dataMovie.getOverview() );

        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make( view, "Replace with your own action", Snackbar.LENGTH_LONG )
                        .setAction( "Action", null ).show();
            }
        } );
    }
}
