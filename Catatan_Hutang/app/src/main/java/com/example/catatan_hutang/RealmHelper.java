package com.example.catatan_hutang;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class RealmHelper {
    private Context context;
    private Realm realm;


    public RealmHelper(Context context) {

        this.context = context;
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    //1.Insert
    public void insertData(CatatanModel catatan){
        //1.a.Buka koneksi Realm
        realm.beginTransaction();

        //1.b.Insert data'nya ke Realm
        //notes : jgn lupa extend class CatatanModel.java ke RealmObject->menjadikan CatatanModel.java sebagai object Realm
        realm.copyToRealm(catatan);
        realm.commitTransaction();

        //1.c.Beri pesan insert data berhasil ditambahkan
        realm.addChangeListener( new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                Toast.makeText( context,"Data Successfuly Added",Toast.LENGTH_SHORT ).show();
            }
        } );
        //1.d.tutup koneksi Realm
        realm.close();
    }

    //Function unt increment ID
    public long generateId(){

        //3.a.Check jumlah existing data
        if (realm.where( CatatanModel.class ).count() != 0){
            //Klo existing data >0, Ambil id maksimal'nya dan tambahkan 1
            long id = realm.where( CatatanModel.class ).max( "id" ).longValue();
            return id+1;
        }else{
            //Klo existing data 0, set id'nya menjadi 1
            return 1;
        }
    }

    //---------------------------------------------------------------------------------------

    //2.Menampilkan data
    public List<CatatanModel> showData() {

        //2.a.Inisiasi pengiriman data (transaksi) realm


        //2.b.Buat arraylist unt existing data di realm
        RealmResults<CatatanModel> hasil = realm.where( CatatanModel.class ).findAll();//Query ke Realm (klo di sql sama kyk select *)

        //2.c.Copy data dari realm ke dalam list CatatanModel.java
        List<CatatanModel> datalist = new ArrayList<>();
        datalist.addAll( realm.copyFromRealm( hasil ) );

        //2.d.Jalankan realm & kembalikan data'nya
        realm.beginTransaction();
        realm.commitTransaction();

        return datalist;

    }

    //3.Menampilkan detail data
    public CatatanModel showdetail(int id){
        //Ambil data dari realm berdasarkan id yg dikirimkan dari CatatanModel.java
        CatatanModel data = realm.where( CatatanModel.class ).equalTo( "id",id ).findFirst();
        return data;
    }

    //---------------------------------------------------------------------------------------
    //UPDATE
    //---------------------------------------------------------------------------------------
    public void updatedata(CatatanModel catatan){
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(catatan);
        realm.commitTransaction();
        realm.addChangeListener( new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                Toast.makeText( context,"Data Updated",Toast.LENGTH_SHORT ).show();
            }
        } );
        realm.close();
    }

    //---------------------------------------------------------------------------------------
    //DELETE
    //---------------------------------------------------------------------------------------
    public void deletedata(Integer id){
        realm.beginTransaction();
        //Ambil data dari realm berdasarkan id yg dikirimkan dari CatatanModel.java
        //Sama seperti saat menampilkan detail data
        CatatanModel catatan = realm.where( CatatanModel.class ).equalTo( "id",id ).findFirst();
        catatan.deleteFromRealm();
        realm.commitTransaction();
        realm.addChangeListener( new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                Toast.makeText( context,"Data Deleted",Toast.LENGTH_SHORT ).show();
            }
        } );
        realm.close();
    }

}




