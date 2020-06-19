package com.saram.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

public class motoInfoActivity extends AppCompatActivity {
    FloatingActionButton fabMas;
    ImageView imgEdita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moto_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fabMas = (FloatingActionButton) findViewById(R.id.fabMas);
        imgEdita = (ImageView) findViewById(R.id.imgEditar);

        imgEdita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //en esta se debe mandar a llamar para la sig pantalla
                Intent agregar =  new Intent(getApplicationContext(), agregarMotoActivity.class);
                startActivity(agregar);
            }
        });
        fabMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent agregar =  new Intent(getApplicationContext(), agregarMotoActivity.class);
                startActivity(agregar);
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}