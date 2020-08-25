package com.saram.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class nav_cerrarS extends Fragment {

    // SE DECLARA EL PROGRESS DIALOG
    ProgressDialog progressDialog;

    public nav_cerrarS() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nav_cerrar_s, container, false);

        // SE INICIALIZA EL OBJETO DEL PROGRESS DIALOG
        progressDialog = new ProgressDialog(getContext());

        // VERIFICA QUE LA SESIÓN SE ENCUENTRE CERRADA
        verifica();

        cerrar_sesion();

        return view;

    }

    private void cerrar_sesion(){
        // SE INICIA EL PROGRESS DIALOG
        progressDialog.setMessage("Cerrando Sesión");
        progressDialog.show();

        // SE INTANCIA EL SHARED
        SharedPreferences sp1 = this.getActivity().getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        // SE MODIFICA EL VALOR DEL SHARED INICIALIZANDO EL EDITOR DEL SHARED
        SharedPreferences.Editor editor = sp1.edit();
        editor.putBoolean("sesion", false);
        editor.commit();

        progressDialog.dismiss();

        Intent inten = new Intent(getContext(), MainActivity.class);
        startActivity(inten);
    }

    private void verifica(){
        // ACTIVAMOS EL EDITOR DEL SHARED
        SharedPreferences sp1 = this.getActivity().getSharedPreferences("MisDatos", Context.MODE_PRIVATE);

        // TRAEMOS EL VALOR DE LA SESION
        boolean val = sp1.getBoolean("sesion", false);

        // SE INSTANCIA EL ACTIVITY DE INICIO DE LA APP    contexto, nombre de la clase compilada
        Intent intent1 = new Intent(getContext(), inicioActivity.class);

        // SE INSTANCIA EL ACTIVITY DEL LOGIN DE LA APP    contexto, nombre de la clase compilada
        Intent intent2 = new Intent(getContext(), MainActivity.class);

        if(val == true) {
            // SE MANDA A EJECUTAR EL ACTIVITY DE INICIO DE LA APP
            startActivity(intent1);
        }else if(val == false) {
            // SE MNADA A EJCUTAR EL LOGIN DE LA APP
            startActivity(intent2);
        }
    }

}