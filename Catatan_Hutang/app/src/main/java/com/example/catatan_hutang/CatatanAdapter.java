package com.example.catatan_hutang;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

public class CatatanAdapter extends RecyclerView.Adapter<CatatanAdapter.MyViewHolder> {

    private Context context;
    private List<CatatanModel> data = new ArrayList<>();

    public CatatanAdapter(Context context, List<CatatanModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public CatatanAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_catatan,parent,false);

        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull CatatanAdapter.MyViewHolder holder, final int position) {
        holder.tvjudul.setText( data.get( position ).getJudul() );
        holder.tvjumlah.setText("Rp."+ data.get( position ).getJumlahutang() );
        holder.tvtanggal.setText( data.get( position ).getTanggal() );

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pindah = new Intent( context,DetailCatatan.class );
                pindah.putExtra( DetailCatatan.KEY_ID, data.get(position).getId());//kirimkan hanya id'nya saja
                context.startActivity( pindah );
            }
        } );
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvjudul,tvjumlah,tvtanggal;

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );
            tvjudul = itemView.findViewById( R.id.tvjudul);
            tvjumlah = itemView.findViewById( R.id.tvjumlah);
            tvtanggal = itemView.findViewById( R.id.tvtanggal);

        }
    }
}
