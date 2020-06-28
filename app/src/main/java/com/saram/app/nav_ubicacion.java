package com.saram.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class nav_ubicacion extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // SE INFLA EL FRAGMENTO PARA QUE APAREZCA UNA VEZ SELECCIONADA LA OPCÍON EN EL NAVIGATION DRAWER
        // SE COLOCA COMO VISTA PARA ACEPTAR CÓDIGO PROPIO DE UNA ACTIVITY
        View view = inflater.inflate(R.layout.fragment_ubicacion, container, false);

        // SIMPLEMENTE SE CAMBIA A LA ACTIVITY PARA EVITAR TRABAJAR CON SINTAXIS DE FRAGMENTOS
        // Y CONTINUAR CON EL CÓDIGO DE ACTIVITYS
        Intent ubicacion = new Intent(getContext(), MapsActivity.class);
        startActivity(ubicacion);

        return view;
    }
}