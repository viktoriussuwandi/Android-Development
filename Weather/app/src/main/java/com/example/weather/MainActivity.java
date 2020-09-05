package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.textclassifier.ConversationActions;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    //Sumber API : https://openweathermap.org/
    LinearLayout descLayout;
    TextView txtcity, txtweather, txtdegree, txtlongtitude, txtlatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        txtcity = findViewById( R.id.TextViewCity );
        txtweather = findViewById( R.id.TextviewWeather );
        txtdegree = findViewById( R.id.TextviewDegree );
        txtlongtitude = findViewById( R.id.TextviewLogtitude );
        txtlatitude = findViewById( R.id.TextviewLatitude );
    }

    public void getWeather(View view) throws UnsupportedEncodingException {
        try {
            String web = "https://openweathermap.org/data/2.5/weather?";
            String city = "q=" + URLEncoder.encode( txtcity.getText().toString(), "UTF-8" );//Encoder unt menghilangkan elemen hacker
            String id = "&appid=439d4b804bc8187953eb36d2a8c26a02&unit=metric";
            DownloadTask task = new DownloadTask();
            task.execute( web + city + id );

            //Membuat method unt menghilangkan keyboard ketika tekan button "SHOW"
            InputMethodManager mgr = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE );
            mgr.hideSoftInputFromWindow( txtlatitude.getWindowToken(), 0 );
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText( getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT ).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL( strings[0] );
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader( in );

                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                in.close();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        //Ketika sudah selesai ambil data API
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute( s );
            try {
                JSONObject jsonObject = new JSONObject( s );//Membuat penampung unt semua object JSON

                //weather
                String weather = jsonObject.getString( "weather" );
                //Pada Weather, karena ada 2 main & 2 description dgn id yg berbeda,
                //maka data'nya harus diambil pakai array
                JSONArray weatherArray = new JSONArray( weather );
                String weatherResult = "";
                for (int i = 0 ; i < weatherArray.length() ; i++){
                    JSONObject part = weatherArray.getJSONObject( i );
                    String mainWeather = part.getString( "main" );
                    String descWeather = part.getString( "description" );
                    weatherResult += mainWeather + " : " + descWeather;
                }

                //temperature
                JSONObject main = jsonObject.getJSONObject( "main" );
                double temp = main.getDouble( "temp" );

                //long-lat
                JSONObject coord = jsonObject.getJSONObject( "coord" );
                double lon = coord.getDouble( "lon" );
                double lat = coord.getDouble( "lat" );

                //Output

                //Testing
                //Log.i( "Weather ", weatherResult);
                //Log.i( "Temperature ", String.valueOf(temp) + " celcius");
                //Log.i( "Longtitude ", String.valueOf(lon) + " degree");
                //Log.i( "Latitude ", String.valueOf(lat) + " degree");
                txtweather.setText( weatherResult );
                txtdegree.setText( String.valueOf( temp ) + " celcius");
                txtlongtitude.setText( String.valueOf( lon ) + " degree");
                txtlatitude.setText( String.valueOf( lat ) + " degree" );
            }
            catch (Exception e){
                Toast.makeText( getApplicationContext(), e.toString(), Toast.LENGTH_SHORT ).show();
                e.printStackTrace();
            }

        }
    }
}

//Challenge :
//1.Tambahkan icon pada tiap field hasil pencarian cuaca
//2.Tambahkan gambar & suara yg berbeda unt tiap weather'nya

