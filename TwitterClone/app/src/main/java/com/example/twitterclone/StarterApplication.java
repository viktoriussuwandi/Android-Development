package com.example.twitterclone;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL(  );
        defaultACL.setPublicReadAccess( true );
        defaultACL.setPublicWriteAccess( true );
        ParseACL.setDefaultACL( defaultACL,true );

    }
}

//Tester for
//create Class & object on Parse Server
//    ParseObject object = new ParseObject( "ExampleObject" );
//        object.put( "MyNumber","123" );
//                object.put( "MyString","rob" );
//
//                object.saveInBackground( new SaveCallback() {
//@Override
//public void done(ParseException e) {
//        if (e == null){
//        Log.i("Parse Result","Successfull!!");
//        }else {
//        Log.i( "Parse Result","Failed " + e.toString() );
//        }
//        }
//        } );