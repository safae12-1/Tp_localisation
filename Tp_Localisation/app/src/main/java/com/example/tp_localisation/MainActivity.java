package com.example.tp_localisation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class MainActivity extends AppCompatActivity {
    private double latitude;
    private double longitude;
    private double altitude;
    private float accuracy;

    RequestQueue requestQueue;

    String insertUrl = "http://192.168.1.5/localisation/createPosition.php";// L'URL du serveur distant où les données de localisation seront envoyées.
    private TextView locationTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Button addButton = findViewById(R.id.button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity2.class);
                startActivity(intent);
            }
        });
        requestQueue = Volley.newRequestQueue(getApplicationContext());//Initialise la file d'attente de requêtes Volley

        //locationTextView = findViewById(R.id.locationTextView);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        altitude = location.getAltitude();
                        accuracy = location.getAccuracy();
                        String msg = String.format(getResources().getString(R.string.new_location), latitude, longitude, altitude, accuracy);

                        Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);

                        addPosition(latitude, longitude); //on fait appel a cette fonction pour ajouter les coordon"es dans la BD
                       // locationTextView.setText("Latitude: " + latitude + "\nLongitude: " + longitude);

                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        String newStatus = "";
                        switch (status) {
                            case LocationProvider.OUT_OF_SERVICE:
                                newStatus = "OUT_OF_SERVICE";
                                break;
                            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                                newStatus = "TEMPORARILY_UNAVAILABLE";
                                break;
                            case LocationProvider.AVAILABLE:
                                newStatus = "AVAILABLE";
                                break;
                        }
                        String msg = String.format(getResources().getString(R.string.provider_new_status),
                                provider, newStatus);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                    }


                    @Override
                    public void onProviderEnabled(String provider) {
                        String msg = String.format(getResources().getString(R.string.provider_enabled),provider);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        String msg = String.format(getResources().getString(R.string.provider_disabled),provider);
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }



    void addPosition(final double lat, final double lon) {
        StringRequest request = new StringRequest(Request.Method.POST,insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("msg", "success");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Volley Error: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                HashMap<String, String> params = new HashMap<String, String>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                params.put("latitude", lat + "");
                params.put("longitude", lon + "");
                params.put("date", sdf.format(new Date()) + "");
                params.put("imei", "37963673129");
                return params;
            }
        };
        requestQueue.add(request);
    }
}