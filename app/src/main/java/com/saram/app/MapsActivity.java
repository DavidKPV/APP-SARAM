package com.saram.app;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

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

        // VERIFICA QUE TENGA LA APP LOS PERMISOS NECESARIOS PARA LA UTILIZACIÓN DE LA UBICACIÓN
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        // SE VERIFICA CON LA CONDICIONAL QUE SE TENGAN LOS PERMISOS INSTALADOS
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // AQUI SE COLOCA MENSAJES EXTRAS QUE SE QUIERAN COLOCAR SOBRE LA EXPLICACIÓN DE LOS PERMISOS
            } else {
                // PEDIRÁ LA ACTIVACIÓN DEL SERVICIO
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        /*
        LocationListener ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                 final LatLng MiUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };*/
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

        // ESTA ES UNA POSICIÓN ESTÁTICA QUE SE ACABA DE AGREGAR PARA VER FUNCIONALIDAD DE LA API
        LatLng SARAM = new LatLng(19.42847, -99.12766);
        mMap.addMarker(new MarkerOptions().position(SARAM).title("Mi Moto :3"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SARAM, 18f));
    }
}