package com.example.myfruit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash_screen );

        ImageView imageView = (ImageView) findViewById( R.id.imageView ) ;
        imageView.setImageResource( R.drawable.logomengenalbuah );

        //Membuat efek delay
        Handler handler = new Handler(  );
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent( SplashScreen.this,MainActivity.class );
                startActivity( intent );
                finish(); //Ketika user sudah di MainActivity, user tdk bisa kembali ke splash screen
            }
        }, 1500 );
    }
}