package com.example.uberclone;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

public class StarterApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore( this );
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("myappID")
                .clientKey("UUEdlbokzMJ4")
                .server("http://54.255.219.49/parse/")
                .build()
        );

        //Method ParseUser.enableAutomaticUser(); berfungsi unt mengatur Authentikasi user
        // Ada beberapa alasan melakukan disable terhadap yaitu :
        //1.Akan menggunakan Anonymous user
        //2.Authentikasi akan diatur tersendiri (tidak mengikuti aturan default ParseServer)
        //ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL(  );
        defaultACL.setPublicReadAccess( true );
        defaultACL.setPublicWriteAccess( true );
        ParseACL.setDefaultACL( defaultACL,true );

    }
}
