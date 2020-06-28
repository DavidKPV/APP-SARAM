package com.saram.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class infomotoActivity extends AppCompatActivity {

    FloatingActionButton fabMas;
    ImageView imgEdita;

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
        setContentView(R.layout.activity_infomoto);
        // PARA ACTIVAR LA FLECHA DE RETORNO
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabMas = (FloatingActionButton) findViewById(R.id.fabMas);
        imgEdita = (ImageView) findViewById(R.id.imgEditar);

        imgEdita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //en esta se debe mandar a llamar para la sig pantalla
                Intent agregar2 =  new Intent(infomotoActivity.this, editmotoActivity.class);
                startActivity(agregar2);
            }
        });

        fabMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent agregar =  new Intent(infomotoActivity.this, addmotoActivity.class);
                startActivity(agregar);
            }
        });
    }

    // PARA ACTIVAR LA FUNCIONALIDAD DE LA FLECHA DE RETORNO
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}