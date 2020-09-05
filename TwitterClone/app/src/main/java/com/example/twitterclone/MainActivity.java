package com.example.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText usernameTxt,passwordTxt;

    //Function unt pindah ke UsersActivity
    public void redirectUser(){
        if(ParseUser.getCurrentUser() != null){
            Intent intent = new Intent( getApplicationContext(),UsersActivity.class );
            startActivity( intent );
        }else {}
    }

    //Function unt tombol SigUp / Login
    public void SignupLogin(View v){
        //Klo user terdaftar -> Login
        ParseUser.logInInBackground( usernameTxt.getText().toString(), passwordTxt.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e == null ){
                    Toast.makeText( MainActivity.this,"Login Successfull",Toast.LENGTH_SHORT ).show();
                    redirectUser();
                }else {
                    //Klo user tdk terdaftar -> SignUp
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername( usernameTxt.getText().toString() );newUser.setPassword( passwordTxt.getText().toString() );
                    newUser.signUpInBackground( new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                Toast.makeText( MainActivity.this,"SignUp Successfull",Toast.LENGTH_SHORT ).show();
                                redirectUser();
                            }else{ Toast.makeText( MainActivity.this,e.getMessage().substring( e.getMessage().indexOf(" ") ),Toast.LENGTH_SHORT ).show(); }
                        }
                    } );
                    Toast.makeText( MainActivity.this,e.getMessage().substring( e.getMessage().indexOf(" ") ),Toast.LENGTH_SHORT ).show();
                }
            }
        } );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        setTitle( "Twitter : Login" );
        usernameTxt = findViewById( R.id.UserNameEditText );
        passwordTxt = findViewById( R.id.PasswordEditText );
        redirectUser();
        ParseAnalytics.trackAppOpenedInBackground( getIntent() );
    }
}