package com.example.gps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    static String BASE_URL = "https://api.weatherapi.com/v1/current.json?key=ea0dac08eab74b65b5b10838231901&q=";
    private TextView locationText;
    private TextView temperatureText;
    private TextView windText;
    private TextView cloudText;
    private double latitude;
    private double longitude;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void composeInformation(View v) {
        locationText = findViewById(R.id.localValue);
        temperatureText = findViewById(R.id.temperatureValue);
        windText = findViewById(R.id.windValue);
        cloudText = findViewById(R.id.cloudValue);

        getGPSInformation();
        getAPIData();
    }

    private void getGPSInformation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.INTERNET}, 1);
            ActivityCompat. requestPermissions (MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_NETWORK_STATE}, 1);

            return;
        }

        LocationManager mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener mLocListener = new MyLocationListener();

        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener);

        if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            latitude = MyLocationListener.Latitude;
            longitude = MyLocationListener.Longitude;

            String toast = "Latitude: " + latitude + "\n" +
                           "Longitude: " + longitude + "\n";
            Toast.makeText(MainActivity.this, toast, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Disabled GPS.", Toast.LENGTH_LONG).show();
        }
    }

    private void getAPIData() {
        queue = Volley.newRequestQueue(this);

        String url = BASE_URL + latitude + "," + longitude;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

            try {
                JSONObject location = response.getJSONObject("location");

                String name = location.getString("name");
                String region = location.getString("region");

                String location_info = name + " (" + region + ")";
                locationText.setText(location_info);

                JSONObject current = response.getJSONObject("current");

                double temp = current.getDouble("temp_c");
                double feels_like = current.getDouble("feelslike_c");

                String temp_info = temp + " ºC (feels like " + feels_like + " ºC)";
                temperatureText.setText(temp_info);

                double wind_speed = current.getDouble("wind_kph");
                double wind_degree = current.getDouble("wind_degree");
                String wind_dir = current.getString("wind_dir");

                String wind_info = wind_speed + " km/h (" + wind_degree + "º " + wind_dir + ")";
                windText.setText(wind_info);

                double cloud = current.getDouble("cloud");

                String cloud_info = cloud + " %";
                cloudText.setText(cloud_info);

            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, "Error parsing JSON.", Toast.LENGTH_LONG).show();
                return;
            }

        }, error -> Toast.makeText(MainActivity.this, "Error fetching API.", Toast.LENGTH_LONG).show());

        queue.add(request);
    }
}