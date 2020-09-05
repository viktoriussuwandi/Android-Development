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

public class Tambah extends AppCompatActivity {

    EditText edjudul,edjumlah,edtanggal;
    Button btnsave;
    RealmHelper realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.tambah );

        realm = new RealmHelper( Tambah.this );
        edjudul = findViewById( R.id.ed_judul );
        edjumlah = findViewById( R.id.ed_jumlah );
        edtanggal = findViewById( R.id.ed_tanggal );
        btnsave = findViewById( R.id.btn_update );

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
                DatePickerDialog dialog = new DatePickerDialog( Tambah.this,
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

        //Simpan data ke Realm (Event ketika button di-klik)
        btnsave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //1.Set data ke dalam Object data'nya (dlm hal ini CatatanModel.java)
                CatatanModel catatan = new CatatanModel();
                catatan.setId( (int) realm.generateId() );
                catatan.setJudul( edjudul.getText().toString() );
                catatan.setJumlahutang( edjumlah.getText().toString() );
                catatan.setTanggal( edtanggal.getText().toString() );

                //2.Simpan data'nya ke dlm realm
                realm.insertData(catatan);
                finish();
            }
        } );

    }
}
