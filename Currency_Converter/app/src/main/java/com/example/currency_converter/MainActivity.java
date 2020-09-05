package com.example.currency_converter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RadioGroup rg;
    RadioButton rb;
    ImageView imgcurrency;
    EditText txtinput,txtoutput;
    Button btnconvert;

    int[] imgmoney = {
            R.drawable.usd,R.drawable.euro,R.drawable.pound
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        //----------------------------------------------------------------------------------
        //Perubahan Gambar
        //----------------------------------------------------------------------------------
        imgcurrency = findViewById( R.id.iv_currency );
        rg = findViewById( R.id.radio_group );

        rg.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rb = rg.findViewById(checkedId); //Ambil elemen radio button yg di-checked
                int i = rg.indexOfChild(rb); //Ambil index radio button yg di-checked
                imgcurrency.setImageResource(imgmoney[i]); //set image berdasarkan index radio button

                //Buat Toast sekedar unt tester :
                //Toast.makeText( MainActivity.this, "Selected Id : " + rb + "Child Index : " + i, Toast.LENGTH_LONG ).show();
            }
        } );

        //----------------------------------------------------------------------------------
        //Konversi mata uang'nya
        //----------------------------------------------------------------------------------
        txtinput = findViewById( R.id.txt_input );
        txtoutput = findViewById( R.id.txt_output );
        btnconvert = findViewById( R.id.btn_convert );

        btnconvert.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double angkainput;
                String teksinput;
                double result;

                teksinput = txtinput.getText().toString();

                //Cek yg di-input kosong/ga
                if (teksinput.isEmpty()){ angkainput=0; }
                else { angkainput = Double.parseDouble( teksinput );}

                result = angkainput*15000;
                txtoutput.setText( String.valueOf( result ) );
                Toast.makeText( MainActivity.this, "Input Text : " + angkainput, Toast.LENGTH_LONG ).show();

                //PR :
                //1.Ubah dalam berbagai mata uang -> menggunakan API
                //2.Format ribuan setiap kali input number di teks
                //3.Ketika tombol convert di-klik -> cek jenis mata uang yg dipilih
            }
        } );
    }
}
