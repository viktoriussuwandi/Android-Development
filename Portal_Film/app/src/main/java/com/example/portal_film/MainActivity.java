package com.example.portal_film;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.example.portal_film.model.ResponseMovie;
import com.example.portal_film.model.ResultsItem;
import com.example.portal_film.retrofit.RetrofitConfig;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //List<MovieModel> dataMovie = new ArrayList<>(); //unt dummy
    List<ResultsItem> dataMovie = new ArrayList<>(); //unt API

    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        //1.Layout per item
        //  item_movie.xml

        //2.Model Data
        //  MovieModel.java
        getDataOnline();

        //contoh dummy data
        //MovieModel movie1 = new MovieModel();
        //movie1.setJudulFilm("Sonic Hedgehog");
        //movie1.setPosterfilm("https://image.tmdb.org/t/p/w1280/bljXY2zZSD6poD4dOPwIxQkFTUB.jpg");
        //for (int i = 0; i <20 ; i++) { dataMovie.add(movie1); }

        //3.Adapter
        // MovieAdapter.java
        recycler = findViewById( R.id.recyclerView );
        recycler.setAdapter( new MovieAdapter( MainActivity.this,dataMovie ) );

        //4.Layout Manager
        recycler.setLayoutManager( new GridLayoutManager(MainActivity.this,2 ));

    }

    //function unt ambil data API
    private void getDataOnline () {
        //menambahkan loading
        ProgressDialog progress = new ProgressDialog( MainActivity.this );
        progress.setMessage( "Waiting..." );
        progress.show();

        Call<ResponseMovie> request = RetrofitConfig.getApiService().ambildataMovie( "43f74160b22cece803a5937d1909912f" );
        request.enqueue( new Callback<ResponseMovie>() {

            @Override
            public void onResponse(Call<ResponseMovie> call, Response<ResponseMovie> response) {
                progress.dismiss();
                if (response.isSuccessful()){
                    dataMovie = response.body().getResults();
                    recycler.setAdapter( new MovieAdapter( MainActivity.this,dataMovie ) );

                }else {
                    Toast.makeText( MainActivity.this,"Request failed",Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseMovie> call, Throwable t) {
                progress.dismiss();
                Toast.makeText( MainActivity.this,"Request failed"+t.getMessage(),Toast.LENGTH_SHORT ).show();
            }
        } );
    }
}
