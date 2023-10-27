package com.example.tp_localisation;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.tp_localisation.databinding.ActivityMaps2Binding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    String showUrl = "http://192.168.1.5/localisation/showPositions.php";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        setUpMapIfNeeded();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void setUpMapIfNeeded() {
        JSONObject requestData = new JSONObject(); //un objet JSON vide  qui sera utilisé pour envoyer des données avec la requête

        try {
            requestData.put("key", "value"); // Ajoutez des données à votre objet JSON
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //pour envoyer une requête JSON à l'URL showUrl avec les données contenues dans requestData
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, showUrl, null, response -> {
            try {
                JSONArray positions = response.getJSONArray("positions");
                for (int i = 0; i < positions.length(); i++) {
                    JSONObject position = positions.getJSONObject(i);
                    mMap.addMarker(new MarkerOptions().position(new
                            LatLng(position.getDouble("latitude"),
                            position.getDouble("longitude"))).title("Marker")); //ajouter des marqueurs sur la carte
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Gestion des erreurs
                Log.e("info",error.toString());
            }
        });

        requestQueue.add(jsonObjectRequest);//Ajoute la requête à la file d'attente pour son exécution.
    }

}