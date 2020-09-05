package com.example.uberclone;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DriverLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button acceptBtn;
    Intent intent;
    RelativeLayout mapLayout;

    //Function saat driver terima request
    //Pairing driver-rider
    //Jalankan fitur direction
    public void acceptRequest (View view){
        //Pairing driver-rider
        ParseQuery <ParseObject> query = ParseQuery.getQuery( "Request" );
        query.whereEqualTo( "username",intent.getStringExtra( "requestUsername" ) );
        query.findInBackground( new FindCallback<ParseObject>() {
            @Override public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                  if (objects.size() > 0){
                      for(ParseObject object : objects){
                          object.put( "driverUsername", ParseUser.getCurrentUser().getUsername() );
                          object.saveInBackground( new SaveCallback() {
                              @Override
                              public void done(ParseException e) {
                                  if(e == null){
                                      //Jalankan fitur Direction
                                      //Ada 2 pilihan cara unt parsing http ke google map
                                      //silahkan pilih salah satu
                                      //http://maps.google.com/maps?saddr=-6.1857016666666675,106.81085333333334&daddr=-6.187963333333332,106.80960666666667
                                      //https://www.google.com/maps/dir/?api=1&origin=-6.1857016666666675,106.81085333333334&destination=-6.187963333333332,106.80960666666667
                                      String direction = "http://maps.google.com/maps?saddr="+
                                              intent.getDoubleExtra( "driverLatitude",0 ) + "," + intent.getDoubleExtra( "driverLongtitude",0 )
                                              +"&daddr="+
                                              intent.getDoubleExtra( "requestLatitude",0 ) + "," + intent.getDoubleExtra( "requestLongtitude",0 );
                                      String direction1 = "https://www.google.com/maps/dir/?api=1&origin="+
                                              intent.getDoubleExtra( "driverLatitude",0 ) + "," + intent.getDoubleExtra( "driverLongtitude",0 )
                                              +"&destination="+
                                              intent.getDoubleExtra( "requestLatitude",0 ) + "," + intent.getDoubleExtra( "requestLongtitude",0 );
                                      Intent directionIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( direction));
                                      startActivity( directionIntent );
                                  }else {Toast.makeText( DriverLocationActivity.this,"Pairing unsuccessful " + e.getMessage(),Toast.LENGTH_SHORT ).show();}
                              }
                          } );
                      }
                  }else {Toast.makeText( DriverLocationActivity.this,"No Request Username",Toast.LENGTH_SHORT ).show();}
                }else {Toast.makeText( DriverLocationActivity.this,e.getMessage(),Toast.LENGTH_SHORT ).show();}
            }
        } );
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_driver_location );
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map ); mapFragment.getMapAsync( this );
        mapLayout = findViewById( R.id.MapLayout );
    }

    @Override public void onMapReady(GoogleMap googleMap) { mMap = googleMap;
        //1.Ambil data Request yg dikirim dari ViewRequestActivity
        //-semua posisi request
        //-self-posisi driver

        //2.Ubah marker color unt driver & rider
        //

        intent = getIntent();

        ArrayList<Marker> markers = new ArrayList<Marker>(  );
        LatLng requestLocation =  new LatLng( intent.getDoubleExtra( "requestLatitude",0 ), intent.getDoubleExtra( "requestLongtitude",0 ) );
        LatLng driverLocation  =  new LatLng( intent.getDoubleExtra( "driverLatitude",0 ), intent.getDoubleExtra( "driverLongtitude",0 ) );

        markers.clear();
        markers.add( mMap.addMarker( new MarkerOptions().position( requestLocation ).title( "Request Location" )) );
        markers.add( mMap.addMarker(
                new MarkerOptions().position( driverLocation ).title( "Your (Driver) Location" ).
                icon( BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE) )) );

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Marker marker : markers){ builder.include( marker.getPosition() ); }
        LatLngBounds bounds = builder.build();
        int padding = 250;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds( bounds,padding);
        mMap.animateCamera( cu );
    }
}

// Add a marker in Sydney and move the camera
//LatLng sydney = new LatLng( -34, 151 ); mMap.addMarker( new MarkerOptions().position( sydney ).title( "Marker in Sydney" ) ); mMap.moveCamera( CameraUpdateFactory.newLatLng( sydney ) );