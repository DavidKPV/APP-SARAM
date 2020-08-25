package com.saram.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

        // OBTENEMOS EL TOKEN DEL SHARED
        SharedPreferences sp1 = getActivity().getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        String modelo = sp1.getString("modelo","");

        if(permissionCheckUbicacion == PackageManager.PERMISSION_GRANTED){
            if(modelo.equals("Nada que monitorear")){
                Toast.makeText(getContext(), "DEBES ELEGIR UNA MOTOCICLETA A MONITOREAR EN LA SECCIÓN DE INFORMACIÓN", Toast.LENGTH_LONG).show();
                Intent inicio = new Intent(getContext(), inicioActivity.class);
                startActivity(inicio);
            }
            else{
                // SIMPLEMENTE SE CAMBIA A LA ACTIVITY PARA EVITAR TRABAJAR CON SINTAXIS DE FRAGMENTOS
                // Y CONTINUAR CON EL CÓDIGO DE ACTIVITYS
                Intent ubicacion = new Intent(getContext(), MapsActivity.class);
                startActivity(ubicacion);
            }
        }
        else{
            if(modelo.equals("Nada que monitorear")){
                Toast.makeText(getContext(), "DEBES ELEGIR UNA MOTOCICLETA A MONITOREAR EN LA SECCIÓN DE INFORMACIÓN", Toast.LENGTH_LONG).show();
                Intent inicio = new Intent(getContext(), inicioActivity.class);
                startActivity(inicio);
            }
            else{
                Toast.makeText(getContext(), "DEBES ACTIVAR LOS PERMISOS DE UBICACIÓN PARA UTILIZAR ESTA OPCIÓN", Toast.LENGTH_LONG).show();
                Intent inicio = new Intent(getContext(), inicioActivity.class);
                startActivity(inicio);
            }
        }


        return view;
    }
}