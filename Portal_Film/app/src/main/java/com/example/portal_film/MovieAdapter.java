package com.example.portal_film;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.portal_film.model.ResultsItem;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

//Extend class ke RecycleView Adapter
//option+enter hingga merah'nya hilang
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private Context context;
    //private List<MovieModel> data = new ArrayList<>(); //unt dummy
    private List<ResultsItem> data = new ArrayList<>();//unt API

    //variabel unt parcelable
    public static final String DATA_MOVIE = "datamovie";
    public static final String DATA_EXTRA = "dataextra";

    //constructor
    //Klik kanan -> generate -> constructor

    public MovieAdapter(Context context, List<ResultsItem> data) {
        this.context = context;
        this.data = data;
    }


    //Menyambungkan layout item
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from( context ).inflate( R.layout.item_movie,parent,false );
        return new MyViewHolder(itemview);
    }


    //Set Data
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //untuk teks / judul
        //holder.tvjudul.setText(data.get( position ).getJudulFilm());//unt dummy
        holder.tvjudul.setText( data.get(position).getTitle());//unt API

        //Untuk gambar / poster pakai Glide, sumber :
        //gradle : https://github.com/bumptech/glide
        //penggunaan : https://bumptech.github.io/glide/
        //Glide digunakan unt load data dari url (jgn lupa permission internet di manifest)
        //Glide.with(context).load( data.get(position).getPosterfilm()).into(holder.ivPoster);//unt dummy
        Glide.with( context ).load("https://image.tmdb.org/t/p/w500"+data.get( position ).getPosterPath()).into( holder.ivPoster );//unt API

        //Memberi event saat di-klik pindah ke detailMovieActivity
        holder.itemView.setOnClickListener( new View.OnClickListener() {


            @Override
            public void onClick(View v) {
               Intent pindah = new Intent( context,DetailMovieActivity.class );

               //Kirim Data antar Activity
               //Buat bundle unt parcelable :
                Bundle bundle = new Bundle();
                bundle.putParcelable( DATA_MOVIE, Parcels.wrap(data.get( position )));

                //kirimkan data pada bundle melalui Intent
                pindah.putExtra( DATA_EXTRA,bundle );

               context.startActivity( pindah );
            }
        } );

    }

    //Menghitung jumlah data
    @Override
    public int getItemCount() {
        return data.size();
    }

    //Mengenalkan komponen dalam item
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvjudul;
        ImageView ivPoster;

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );
            tvjudul = itemView.findViewById(R.id.tv_item_judul);
            ivPoster = itemView.findViewById(R.id.iv_item_gambar);
        }
    }

}
