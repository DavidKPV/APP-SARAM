package com.saram.app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.saram.app.R;
import com.saram.app.models.rutas;

public class nav_pprivacidad extends Fragment {

    // DECLARAMOS EL OBJETO DEL WEB VIEW
    WebView vistaPagina;

    public nav_pprivacidad() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // SE INSTANCIA UN VIEW PARA PODER UTILIZAR CÓDIGO JAVA (AUNQUE LAS SENTENCIAS CAMBIAN EN
        // COMPARACIÓN A COMO SE DECLARAN Y SE TRAEN UN ACTIVITY)
        final View view = inflater.inflate(R.layout.fragment_pprivacidad, container, false);

        // ENLAZAMOS EL OBJETO CON LA VISTA
        vistaPagina = (WebView) view.findViewById(R.id.vistaPoliticas);

        // PRIMERO HABILITANMOS EL JAVA SCRIPT
        WebSettings configuraciones = vistaPagina.getSettings();
        configuraciones.setJavaScriptEnabled(true);

        // SE CARGA LA DIRECCIÓN DE LA PÁGINA EN LA APP
        vistaPagina.loadUrl(rutas.privacidad);
        // SE HABILITARÁ LA CONFIGURACIÓN PARA QUE LA APP EJECUTE LA PÁGINA DE MANERA INTERNA
        // SIN NECESIDAD DE DEPENDER DE LOS NAVEGADORES WEB
        vistaPagina.setWebViewClient(new WebViewClient());

        return view;
    }
}