package com.saram.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class nav_ubicacion extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // SE INFLA EL FRAGMENTO PARA QUE APAREZCA UNA VEZ SELECCIONADA LA OPCÍON EN EL NAVIGATION DRAWER
        // SE COLOCA COMO VISTA PARA ACEPTAR CÓDIGO PROPIO DE UNA ACTIVITY
        View view = inflater.inflate(R.layout.fragment_ubicacion, container, false);

        // VERIFICA QUE TENGA LA APP LOS PERMISOS NECESARIOS PARA LA UTILIZACIÓN DE LA UBICACIÓN
        int permissionCheckUbicacion = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheckUbicacion == PackageManager.PERMISSION_GRANTED){
            // SIMPLEMENTE SE CAMBIA A LA ACTIVITY PARA EVITAR TRABAJAR CON SINTAXIS DE FRAGMENTOS
            // Y CONTINUAR CON EL CÓDIGO DE ACTIVITYS
            Intent ubicacion = new Intent(getContext(), MapsActivity.class);
            startActivity(ubicacion);
        }
        else{
            Toast.makeText(getContext(), "DEBES ACTIVAR LOS PERMISOS DE UBICACIÓN PARA UTILIZAR ESTA OPCIÓN", Toast.LENGTH_LONG).show();
            Intent inicio = new Intent(getContext(), inicioActivity.class);
            startActivity(inicio);
        }


        return view;
    }
}