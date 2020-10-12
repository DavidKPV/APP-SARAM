package com.saram.app.fragments;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.saram.app.R;
import com.saram.app.activitys.inicioActivity;
import com.saram.app.models.rutas;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    // OBJETOS PARA LA CONEXIÓN AL SERVIDOR UTILIZANDO VOLLEY
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    // CREAMOS UNA CADENA LA CUAL CONTENDRÁ LA CADENA DE NUESTRO WEB SERVICE
    String HttpUriCheck = rutas.getUbicacion;
    String vtoken, idmoto, modelo;
    double latitudb, longitudb;

    // ESTE MÉTODO EVITA QUE SE REGRESE CON LA FLECHA DE RETORNO QUE TODOS TENEMOS
    @Override
    public void onBackPressed() {
        // CUANDO REGRESE CON LA FLECHA DE REGRESO LO QUE HARÁ SERÁ REGRESAR AL INICIO
        Intent inicio = new Intent(this, inicioActivity.class);
        startActivity(inicio);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // SE ACTIVAN LOS OBJETOS DE REQUEST QUEUE Y PROGRESS DIALOG
        requestQueue = Volley.newRequestQueue(MapsActivity.this);
        progressDialog = new ProgressDialog(MapsActivity.this);

        // OBTENEMOS EL TOKEN DEL SHARED
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        vtoken = sp1.getString("token", "");
        idmoto = sp1.getString("moto","");
        modelo = sp1.getString("modelo","");

        // LLAMAMOS A LA BASE DE DATOS
        //-------------------------INICIO DE LA LLAMADA AL WEB SERVICE--------------------
        // SE ACTIVA TODO PARA TRAER INFORMACIÓN DEL ESTADO DE LA MOTOCICLETA
        // MOSTRAMOS EL PROGRESS DIALOG ---- AQUÍ SE COMIENZA EL ARMADO Y LA EJECUCIÓN DEL WEB SERVICE
        progressDialog.setMessage("CARGANDO...");
        progressDialog.show();
        // CREACIÓN DE LA CADENA A EJECUTAR EN EL WEB SERVICE MEDIANTE VOLLEY
        // Objeto de volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUriCheck,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String serverresponse) {
                        // UNA VEZ QUE SE MANDAN TODOS LOS VALORES AL WEB SERVICE
                        // QUITAMOS EL PROGRESS DIALOG PARA QUE UNA VEZ QUE SE MANDEN LOS DATOS
                        // YA SE PUEDA TRABAJAR
                        progressDialog.dismiss();
                        // MANEJO DE ERRORES CON RESPECTO A LA RESPUESTA
                        try {
                            // CREAR UN OBJETO DE TIPO JSON PARA OBTENER EL ARCHIVO QUE MANDARÁ EL WEB SERVICE
                            JSONObject obj = new JSONObject(serverresponse);
                            // INTERPRETAR EL VALOR DEL JSON OBTENIDO DEL WEB SERVICE
                            String status = obj.getString("status");

                            if(status.contentEquals("true")){
                                // SE OBTIENEN LOS VALORES DE LATITUD Y LONGITUD
                                String latitud = obj.getString("latitud");
                                String longitud = obj.getString("longitud");

                                // PASAMOS LOS VALORES A DOUBLE YA QUE SINO GOOGLE MAPS NO COMPRENDERÁ LA DIRECCIÓN
                                latitudb = Double.parseDouble(latitud);
                                longitudb = Double.parseDouble(longitud);

                                Toast.makeText(getApplicationContext(), "PREPARANDO LOCALIZACIÓN DE MOTOCICLETA "+modelo+"...", Toast.LENGTH_LONG).show();

                                // ESTA SERÁ LA POSICIÓN DE LA MOTOCICLETA QUE SE ESTÉ GEOLOCALIZANDO
                                LatLng SARAM = new LatLng(latitudb, longitudb);
                                mMap.addMarker(new MarkerOptions().position(SARAM).title("Mi Moto "+modelo));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SARAM, 18f));
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "NO HAY REGISTROS DE UBICACIÓN DE LA MOTOCICLETA "+modelo+" AEGÚRATE DE QUE EL DISPOSITIVO SARAM ASOCIADO A ESTA MOTO ESTÉ EN FUNCIONAMIENTO", Toast.LENGTH_LONG).show();
                                //Intent regresa = new Intent(getApplicationContext(), inicioActivity.class);
                                //startActivity(regresa);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    // ESTE SE EJECUTA SI HAY UN ERROR EN LA RESPUESTA
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // SE OCULTA EL PROGRESS DIALOG
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            // MAPEO DE LOS VALORES QUE MANDAMOS AL WEB SERVICE
            protected Map<String, String> getParams(){
                Map<String, String> parametros = new HashMap<>();
                parametros.put("ID_motocicleta", idmoto);
                return parametros;
            }
            // SOLO SE MANDA EL TOKEN
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Authorization", vtoken);
                return params;
            }
        };

        // SE MANDA A EJECUTAR EL STRING PARA LA LIBRERÍA DE VOLLEY
        requestQueue.add(stringRequest);
        //------------------FIN DE LA LLAMADA DEL WEB SERVICE-----------------------------
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // COLOCA EL TIPO DE MAPA QUE SE MOSTRARÁ
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // PARA ACTIVAR LA POSICÓN DEL DISPOSITIVO MÓVIL PRIMERO SE ASEGURA DE QUE SE TENGAN LOS PERMISOS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // SE ACTIVA EL BOTÓN PARA DIRIGIRSE A LA UBICACIÓN
        mMap.setMyLocationEnabled(true);
        // PARA QUE AUTOMÁTICAMENTE SE ACTIVE LA UBICACIÓN DEL MÓVIL
        // mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }
}