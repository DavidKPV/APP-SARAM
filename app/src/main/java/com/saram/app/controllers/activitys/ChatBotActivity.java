package com.saram.app.controllers.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.saram.app.R;
import com.saram.app.routes.Rutas;

public class ChatBotActivity extends AppCompatActivity {
    // OBJETOS
    WebView vistaChatBot;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ENLACES
        vistaChatBot = (WebView) findViewById(R.id.vistaChat);

        // PRIMERO HABILITANMOS EL JAVA SCRIPT
        WebSettings configuraciones = vistaChatBot.getSettings();
        configuraciones.setJavaScriptEnabled(true);
        configuraciones.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // SE CARGA LA DIRECCIÓN DE LA PÁGINA EN LA APP
        vistaChatBot.loadUrl(Rutas.servicioBot);
        // SE HABILITARÁ LA CONFIGURACIÓN PARA QUE LA APP EJECUTE LA PÁGINA DE MANERA INTERNA
        // SIN NECESIDAD DE DEPENDER DE LOS NAVEGADORES WEB
        vistaChatBot.setWebViewClient(new WebViewClient());
    }
}