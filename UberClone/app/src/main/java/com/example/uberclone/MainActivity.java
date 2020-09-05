package com.example.uberclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

//Activities :
//A.All Role :
//  MainActivity + StarterAplication

//B.Rider :
//  RiderActivity
//

//C.Driver :
//  ViewRequestActivity
//  DriverLocationActivity

public class MainActivity extends AppCompatActivity {

    Switch userTypeSwitch;
    TextView riderTextView,driverTextView;
    Button getStartedBtn;

    public void redirectActivity(){
        if(ParseUser.getCurrentUser().get( "riderOrDriver" ).equals( "Rider" )){
            Intent intent = new Intent( getApplicationContext(),RiderActivity.class );
            startActivity( intent );
        }else {
            Intent intent = new Intent( getApplicationContext(),ViewRequestActivity.class );
            startActivity( intent );
        }
    }

    //Function unt tombol GetStarted
    public void getStarted(View view){
        Log.i( "Switch value",String.valueOf( userTypeSwitch.isChecked() ) );

        String userType = "Rider";
        if(userTypeSwitch.isChecked()){userType = "Driver";}else {userType = "Rider";}
        ParseUser.getCurrentUser().put( "riderOrDriver",userType );

        //Save ke ParseServer
        ParseUser.getCurrentUser().saveInBackground( new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){ redirectActivity(); }
                else{ Toast.makeText( getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT ).show(); }
            }
        } );

        Log.i( "Button Clicked Info","Redirecting as " + userType );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        getSupportActionBar().hide();
        userTypeSwitch = findViewById( R.id.UserTypeSwitch );
        riderTextView = findViewById( R.id.RiderTextView );
        driverTextView = findViewById( R.id.DriverTextView );
        getStartedBtn = findViewById( R.id.GetStartedButton );

        if(ParseUser.getCurrentUser() == null){
            ParseAnonymousUtils.logIn( new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null){
                        Log.i( "Login Info ","Anonymous login successful" ); }
                    else { Log.i( "Login Info ","Anonymous login failed" ); } }
            } );
        }
        else {
            if (ParseUser.getCurrentUser().get("riderOrDriver") != null){
                Log.i( "Parse User Info","Redirecting as " + ParseUser.getCurrentUser().get("riderOrDriver"));
                redirectActivity();
            }
            else { }
        }

        ParseAnalytics.trackAppOpenedInBackground( getIntent() );
    }
}