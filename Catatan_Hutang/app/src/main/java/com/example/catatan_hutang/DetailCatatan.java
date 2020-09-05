package com.example.catatan_hutang;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DetailCatatan extends AppCompatActivity {

    public static final String KEY_ID = "key_id";
    EditText edjudul,edjumlah,edtanggal;
    Button btnupdate,btndelete;
    RealmHelper realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.detail_catatan );

        //------------------------------------------------------------------------------------
        //VIEW DETAIL
        //------------------------------------------------------------------------------------
        //Menampilkan detail dari sebuah data :

        //1.Terima id dari adapter
        final int dataID = getIntent().getIntExtra( KEY_ID,0 );

        //2.Kirim id ke function showdetail pada RealmHelper.java, fungsi :
        //  ambil data dari Realm berdasarkan id'nya
        realm = new RealmHelper( DetailCatatan.this );
        CatatanModel data = realm.showdetail(dataID);

        //3.Tampilkan data ke dalam komponen XML
        edjudul = findViewById( R.id.ed_judul );
        edjumlah = findViewById( R.id.ed_jumlah );
        edtanggal = findViewById( R.id.ed_tanggal );

        edjudul.setText( data.getJudul() );
        edjumlah.setText( data.getJumlahutang() );
        edtanggal.setText( data.getTanggal() );

        //Memunculkan kalender sebagai pilihan tanggal
        edtanggal.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.Ambil Calender dari library & tampung ke variabel
                Calendar kalender = Calendar.getInstance();
                int nowYear = kalender.get( Calendar.YEAR );
                int nowMonth = kalender.get( Calendar.MONTH );
                int nowDay = kalender.get( Calendar.DAY_OF_MONTH );

                //2.Tentukan tanggal default'nya
                DatePickerDialog dialog = new DatePickerDialog( DetailCatatan.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar kal = Calendar.getInstance();
                                kal.set( year,month,dayOfMonth );

                                SimpleDateFormat formatdate = new SimpleDateFormat( "dd MMMM yyyy", Locale.getDefault() );
                                edtanggal.setText( formatdate.format( kal.getTime() ) );
                            }
                        }, nowYear, nowMonth, nowDay );

                //3.Tampilkan Calendar'nya
                dialog.show();
            }
        } );

        //------------------------------------------------------------------------------------
        //UPDATE
        //------------------------------------------------------------------------------------

        btnupdate = findViewById( R.id.btn_update );
        btnupdate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatatanModel catatan = new CatatanModel();
                catatan.setId(dataID);
                catatan.setJudul( edjudul.getText().toString() );
                catatan.setJumlahutang( edjumlah.getText().toString() );
                catatan.setTanggal( edtanggal.getText().toString() );
                realm.updatedata(catatan);
                finish();
            }
        } );

        //------------------------------------------------------------------------------------
        //DELETE
        //------------------------------------------------------------------------------------
        btndelete = findViewById( R.id.btn_delete );
        btndelete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.deletedata(dataID);
                finish();
            }
        } );

    }
}
