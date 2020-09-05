package com.example.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.CaseMap;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    ListView feedListView;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_feed );
        setTitle( "Your Feed" );

        feedListView = findViewById( R.id.FeedListView );
        final List<Map<String,String>> tweetData = new ArrayList<Map<String,String>>( ); //Map berisi key & value

        //Ambil data di class Tweet pada ParseServer,
        // kriteria :
        // - tweet dari semua user yg sudah di-follow (bukan yg belum di-follow)
        // - Urutkan dari tanggal yg paling muda
        // - Batasi hanya 20 tweet
        ParseQuery<ParseObject> query = ParseQuery.getQuery( "Tweet" );
        query.whereContainedIn( "username", ParseUser.getCurrentUser().getList( "isFollowing" ) );
        query.orderByDescending( "createdAt" );
        query.setLimit( 20 );

        query.findInBackground( new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for(ParseObject tweet : objects){
                        Map<String,String> tweetInfo = new HashMap<>( );
                        tweetInfo.put( "content",tweet.getString( "tweet" )); //unt key content pada Map tweetInfo
                        tweetInfo.put( "username",tweet.getString( "username" ) ); //unt keu username pada Map tweetInfo
                        tweetData.add( tweetInfo );
                    }


                    simpleAdapter = new SimpleAdapter( FeedActivity.this,tweetData,android.R.layout.simple_list_item_2,
                            new String[]{"content","username"}, //ambil data sesuai key & value dari Map tweetInfo
                            new int[]{android.R.id.text1,android.R.id.text2} ); //buat adapter menjadi 2 baris
                    feedListView.setAdapter( simpleAdapter );
                }else { Toast.makeText( FeedActivity.this,e.getMessage(),Toast.LENGTH_SHORT ).show(); }
            }
        } );
        //
    }
}

//Contoh Fake Data untuk FeedListView
//        for (int i=1; i<=5; i++){
//            Map<String,String> tweetInfo = new HashMap<>( );
//            tweetInfo.put( "content","Tweet Content of " + Integer.toString( i ) );
//            tweetInfo.put( "username","User " + Integer.toString( i ) );
//            tweetData.add( tweetInfo );
//        }
//
