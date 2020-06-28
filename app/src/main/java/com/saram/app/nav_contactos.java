package com.saram.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class nav_contactos extends Fragment {

    public nav_contactos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // SE INFLA EL FRAGMENTO PARA QUE APAREZCA UNA VEZ SELECCIONADA LA OPCÍON EN EL NAVIGATION DRAWER
        // SE COLOCA COMO VISTA PARA ACEPTAR CÓDIGO PROPIO DE UNA ACTIVITY
        View view = inflater.inflate(R.layout.fragment_contactos, container, false);

        // SIMPLEMENTE SE CAMBIA A LA ACTIVITY PARA EVITAR TRABAJAR CON SINTAXIS DE FRAGMENTOS
        // Y CONTINUAR CON EL CÓDIGO DE ACTIVITYS
        Intent contactos = new Intent(getActivity(), contactosActivity.class);
        startActivity(contactos);

        return view;
    }
}