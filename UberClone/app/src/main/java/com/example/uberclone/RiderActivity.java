package com.example.uberclone;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


//Saran : Sederhanakan orderRequest dengan cara membuat 1 Function terpisah
//Bugs :
//1.Pengaturan request active masih kacau
//  Saat rider login pertama kali, belum bisa di-identifikasi apakah sedang request order / tidak
//  karena belum tersimpan di database
//2.Saat order diterima, Location terus menerus di-update (device bekerja lebih berat)

public class RiderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Button callUberBtn,logOutBtn;
    Boolean requestActive = false;
    Boolean driverActive = false;

    //Komponen unt update posisi driver setelah order di-accept
    TextView driverPositionTextView;
    Handler handler = new Handler( );

    //Function saat order sudah diterima oleh driver
    public void driverPositionUpdates(){
        //1.Check Order sudah di accept / belum -> ambil driverUsername dari class Request
        ParseQuery<ParseObject> query = ParseQuery.getQuery( "Request" ); query.whereEqualTo( "username",ParseUser.getCurrentUser().getUsername() ); query.whereExists( "driverUsername" );
        query.findInBackground( new FindCallback<ParseObject>() {
            @Override public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0){
                    driverActive = true;
                    //a.Ambil jarak driver ke rider
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo( "username",objects.get( 0 ).getString( "driverUsername" ) );
                    query.findInBackground( new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null && objects.size() > 0){
                                //1.Ambil lokasi driver dari class User
                                ParseGeoPoint driverLocation = objects.get( 0 ).getParseGeoPoint( "location" );
                                //2.Ambil lokasi rider
                                if(ContextCompat.checkSelfPermission( RiderActivity.this,Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
                                    Location lastKnownLocation = locationManager.getLastKnownLocation( locationManager.GPS_PROVIDER );
                                    if(lastKnownLocation != null) {
                                        ParseGeoPoint userLocation = new ParseGeoPoint( lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude() );
                                        double distanceInKm = driverLocation.distanceInKilometersTo(userLocation);
                                        Double distanceOneDp = (double) Math.round( distanceInKm * 10 ) / 10;
                                        //Update element
                                        driverPositionTextView.setText( "Your driver is " + distanceOneDp.toString() + " KM away" );

                                        //3.Tunjukan lokasi driver
                                        LatLng requestLocationLatLng = new LatLng( userLocation.getLatitude(), userLocation.getLongitude() );
                                        LatLng driverLocationLatLng = new LatLng( driverLocation.getLatitude(), userLocation.getLongitude() );

                                        ArrayList<Marker> markers = new ArrayList<Marker>();
                                        markers.clear();
                                        markers.add( mMap.addMarker( new MarkerOptions().position( requestLocationLatLng ).title( "Request (Your) Location" ) ) );
                                        markers.add( mMap.addMarker( new MarkerOptions().position( driverLocationLatLng ).title( "Driver Location" ).icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_BLUE ) ) ) );

                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                            for (Marker marker : markers) {
                                                builder.include( marker.getPosition() );
                                            }
                                            LatLngBounds bounds = builder.build();
                                            int padding = 250;
                                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds( bounds, padding );
                                            mMap.animateCamera( cu );
                                    }
                                }

                            }else {}
                        }
                    } );
                    callUberBtn.setVisibility( View.INVISIBLE );
                }else {}
                handler.postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        driverPositionUpdates();
                    }
                },20000 );
            }
        } );
    }

    //------------------------------------------------------------------------------
    //Function unt logOut
    public void Logout(View view){
        Intent intent = new Intent( getApplicationContext(),MainActivity.class );
        startActivity( intent );
        ParseUser.logOut();
    }

    //Function saat rider melakukan request order (tombol CallUber di klik)
    public void callUber(View view){
        if (requestActive){
            Log.i("Call Button Info ","de-Activated Call Uber");
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");
            query.whereEqualTo( "username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground( new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e == null){
                        if (objects.size() > 0){
                            for(ParseObject object : objects){ object.deleteInBackground(); }//Delete object'nya, agar data ga terbaca order terus-menerus
                            requestActive = false; callUberBtn.setText( "Call An Uber" );//Ubah elemen yg dibutuhkan
                        }
                        else {Toast.makeText( getApplicationContext(),"No Taxi found",Toast.LENGTH_SHORT ).show();}
                    }
                    else {Toast.makeText( getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT ).show();}
                }
            } );
        }
        else {
            Log.i("Call Button Info ","Activated Call Uber");
            if(ContextCompat.checkSelfPermission( this,Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,0,0, locationListener );
                Location lastKnownLocation = locationManager.getLastKnownLocation( locationManager.GPS_PROVIDER );

                if (lastKnownLocation != null){
                    ParseObject request = new ParseObject( "Request" );
                    request.put( "username", ParseUser.getCurrentUser().getUsername() );
                    ParseGeoPoint parseGeoPoint = new ParseGeoPoint( lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude() );
                    request.put( "location",parseGeoPoint );
                    request.saveInBackground( new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                //1.Saat rider melakukan request order -> update text-button & status order
                                requestActive = true; callUberBtn.setText( "Cancel Uber" );
                                //2.Saat order di-accept oleh driver -> check posisi driver
                                driverPositionUpdates();
                            }
                            else {Toast.makeText( getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT ).show();}
                        }
                    } );
                }else {Toast.makeText( this,"Could not find location. Please try again later",Toast.LENGTH_SHORT ).show();}
            }else {}
        }
        //
    }

    //Function unt update Map
    public void updateMap (Location location){
        if(driverActive == false){
            LatLng userLocation = new LatLng( location.getLatitude(),location.getLongitude() );
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( userLocation,15 ) );
            mMap.addMarker( new MarkerOptions().position(userLocation).title( "Your Location" ));
        }else { driverPositionUpdates(); }
    }

    //Unt check Permission
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission( this,Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,0,0, locationListener );
                }
                locationManager.requestLocationUpdates( locationManager.GPS_PROVIDER,0,0,locationListener );
                Location lastKnownLocation = locationManager.getLastKnownLocation( locationManager.GPS_PROVIDER );
                updateMap( lastKnownLocation );
            }
        }
        else{}
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_rider );
        callUberBtn = findViewById( R.id.CallUberButton );
        logOutBtn = findViewById( R.id.LogoutButton );
        driverPositionTextView = findViewById( R.id.DriverPositionTextView );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );

        //Check sedang order Uber/tidak
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");
        query.whereEqualTo( "username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground( new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if (objects.size() > 0){
                        for(ParseObject object : objects){ object.deleteInBackground(); }//Delete object'nya, agar data ga terbaca order terus-menerus
                        requestActive = true; callUberBtn.setText( "Cancel An Uber" );//Ubah elemen yg dibutuhkan

                        //Check order sudah diterima atau belum
                        handler.postDelayed( new Runnable() {
                            @Override
                            public void run() {
                                driverPositionUpdates();
                            }
                        },2000 );
                        //driverPositionUpdates();
                    }
                    else {Toast.makeText( getApplicationContext(),"No Taxi found",Toast.LENGTH_SHORT ).show();}
                }
                else {Toast.makeText( getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT ).show();}
            }
        } );
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );
        locationListener = new LocationListener() {
            @Override public void onLocationChanged(Location location) { updateMap(location); }
            @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override public void onProviderEnabled(String provider) { }
            @Override public void onProviderDisabled(String provider) { }
        };

        //Minta Permission
        if(ContextCompat.checkSelfPermission( this,Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,0,0, locationListener );
        }
        else {
            //We don't get permission
            if(ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1 );
            }
            else{
                //We got permission
                Location lastKnownLocation = locationManager.getLastKnownLocation( locationManager.GPS_PROVIDER );
                if(lastKnownLocation != null) { updateMap( lastKnownLocation ); }
                else {}
            }
        }


    }
}

// Add a marker in Sydney and move the camera
//LatLng sydney = new LatLng( -34, 151 );
//mMap.addMarker( new MarkerOptions().position( sydney ).title( "Marker in Sydney" ) );
//mMap.moveCamera( CameraUpdateFactory.newLatLng( sydney ) );