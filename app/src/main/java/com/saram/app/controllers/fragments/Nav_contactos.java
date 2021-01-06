package com.saram.app.controllers.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.saram.app.R;
import com.saram.app.controllers.activitys.Contactos_saramActivity;
import com.saram.app.controllers.activitys.InicioActivity;

public class Nav_contactos extends Fragment {

    public Nav_contactos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // SE INFLA EL FRAGMENTO PARA QUE APAREZCA UNA VEZ SELECCIONADA LA OPCÍON EN EL NAVIGATION DRAWER
        // SE COLOCA COMO VISTA PARA ACEPTAR CÓDIGO PROPIO DE UNA ACTIVITY
        View view = inflater.inflate(R.layout.fragment_contactos, container, false);

        // VERIFICA QUE TENGA LA APP LOS PERMISOS NECESARIOS PARA LA UTILIZACIÓN DE LOS CONTACTOS
        int permissionCheckContactos = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);

        if(permissionCheckContactos == PackageManager.PERMISSION_GRANTED){
            // SIMPLEMENTE SE CAMBIA A LA ACTIVITY PARA EVITAR TRABAJAR CON SINTAXIS DE FRAGMENTOS
            // Y CONTINUAR CON EL CÓDIGO DE ACTIVITYS
            Intent contactos = new Intent(getActivity(), Contactos_saramActivity.class);
            startActivity(contactos);
        }
        else{
            Toast.makeText(getContext(), "DEBES ACTIVAR LOS PERMISOS DE LECTURA DE CONTACTOS PARA UTILIZAR ESTA OPCIÓN", Toast.LENGTH_LONG).show();

            Intent inicio = new Intent(getContext(), InicioActivity.class);
            startActivity(inicio);
        }

        return view;
    }
}