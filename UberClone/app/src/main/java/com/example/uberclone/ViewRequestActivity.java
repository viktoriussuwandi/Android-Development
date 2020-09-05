package com.example.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestActivity extends AppCompatActivity {

    ListView requestListView; ArrayAdapter arrayAdapter;
    ArrayList<String> request = new ArrayList<String>(  );
    LocationManager locationManager; LocationListener locationListener;
    ArrayList<Double> requestLatitudes = new ArrayList<Double>(  ); ArrayList<Double> requestLongtitudes = new ArrayList<Double>(  );
    ArrayList<String> requestUsernames = new ArrayList<String>(  );



    //----------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Function unt update ListView -> Tampilkan pilihan order (berdasarkan jarak dari lokasi driver)
    public void updateListView(Location location){
        //Check ada location/tidak
        if (location != null){
            //a.Ambil self-Location
            final ParseGeoPoint geoPointLocation = new ParseGeoPoint( location.getLatitude(),location.getLongitude() );

            //b.Ambil lokasi pada data Request dari ParseServer :
            //  Driver yg belum dpt order -> Username : undefined
            //  Lokasi dekat self-location driver
            //  Limit hanya 10 lokasi saja
            ParseQuery<ParseObject> query = ParseQuery.getQuery( "Request" ); query.whereDoesNotExist( "driverUsername" ); query.whereNear( "location",geoPointLocation ); query.setLimit( 10 );
            query.findInBackground( new FindCallback<ParseObject>() {
                @Override public void done(List<ParseObject> objects, ParseException e) {
                 if(e == null){ request.clear();requestLatitudes.clear();requestLongtitudes.clear();requestUsernames.clear();
                     if(objects.size() > 0){
                         //Ambil semua posisi request :
                         //a.hitung jarak request ke self-location -> update adapter'nya
                         //b.Ambil longlat'nya -> simpan ke Array -> kirim ke DriverLocationActivity
                         for(ParseObject object : objects) {
                             ParseGeoPoint requestLocation = (ParseGeoPoint) object.get( "location" );
                             double distanceInKm = geoPointLocation.distanceInKilometersTo((ParseGeoPoint) object.get( "location" )); Double distanceOneDp = (double) Math.round( distanceInKm * 10 ) / 10; request.add( distanceOneDp.toString() + " KM" );
                             requestLatitudes.add( requestLocation.getLatitude() );requestLongtitudes.add( requestLocation.getLongitude() );requestUsernames.add( object.getString( "username" ) );
                         }
                     }
                     else {request.add( "No active request nearby" );}
                     arrayAdapter.notifyDataSetChanged();
                 }
                 else {Toast.makeText( ViewRequestActivity.this,e.getMessage(),Toast.LENGTH_SHORT ).show();}
                }} );
        }
        else{ Toast.makeText( ViewRequestActivity.this,"You have NO location",Toast.LENGTH_SHORT ).show(); }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Function unt check Permission
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission( this,Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,0,0, locationListener );
                }
                locationManager.requestLocationUpdates( locationManager.GPS_PROVIDER,0,0,locationListener );
                Location lastKnownLocation = locationManager.getLastKnownLocation( locationManager.GPS_PROVIDER );
                updateListView( lastKnownLocation );
            }
        } else{}
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_view_request );
        setTitle( "Nearby Request" );

        //ListView
        request.clear();
        requestListView = findViewById( R.id.ListView ); arrayAdapter = new ArrayAdapter( this,android.R.layout.simple_list_item_1,request ); requestListView.setAdapter(arrayAdapter);
        request.add( "Getting nearby request" );
        requestListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              if (ContextCompat.checkSelfPermission( ViewRequestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
                  Location lastKnownLocation = locationManager.getLastKnownLocation( locationManager.GPS_PROVIDER );
                  //Cek
                  if (requestUsernames.size() > position && requestLatitudes.size() > position && requestLongtitudes.size() > position && lastKnownLocation != null ) {
                      //Pindah ke DriverLocationActivity -> Kirim Latlong request & Latlong driver
                      Intent intent = new Intent( getApplicationContext(),DriverLocationActivity.class );
                      intent.putExtra( "requestLatitude",requestLatitudes.get( position ) );intent.putExtra( "requestLongtitude",requestLongtitudes.get( position ) );
                      intent.putExtra( "driverLatitude",lastKnownLocation.getLatitude() );intent.putExtra( "driverLongtitude",lastKnownLocation.getLongitude());
                      intent.putExtra( "requestUsername",requestUsernames.get( position ) );
                      startActivity( intent );
                  } else {Toast.makeText( ViewRequestActivity.this,"No Longlat Found",Toast.LENGTH_SHORT ).show(); }
              } else {Toast.makeText( ViewRequestActivity.this,"Permission Denied",Toast.LENGTH_SHORT ).show(); }
            }
        });

        //----------------------------------------------------------------------------------------------------------------------------------------------------------------
        locationManager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );
        locationListener = new LocationListener() {
            @Override public void onLocationChanged(Location location) {
                updateListView(location);
                //Setelah accept order, Saat driver menghampiri rider, lokasi akan berubah
                //Maka perlu dilakukan update lokasi (jarak) pada ParseServer
                //Pada class User :
                //untuk user dgn role : driver -> tambahkan kolom location
                ParseUser.getCurrentUser().put( "location",new ParseGeoPoint( location.getLatitude(),location.getLongitude() ) ); ParseUser.getCurrentUser().saveInBackground();
            }
            @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override public void onProviderEnabled(String provider) { }
            @Override public void onProviderDisabled(String provider) { }
        };

        //Function unt minta Permission
        if(ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){ locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,0,0, locationListener ); }
        else {
            //We don't get permission
            if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) { ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1 ); }
            //We got permission
            else { Location lastKnownLocation = locationManager.getLastKnownLocation( locationManager.GPS_PROVIDER );updateListView( lastKnownLocation ); }
        }
        //----------------------------------------------------------------------------------------------------------------------------------------------------------------


    }
}