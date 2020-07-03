package com.saram.app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class  inicioActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    TextView tvNombreMenu;
    ImageView ivUser;

    // ESTE MÉTODO EVITA QUE SE REGRESE CON LA FLECHA DE RETORNO QUE TODOS TENEMOS
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // VERIFICA QUE HAYA UNA SESION ACTIVA
        activa_inicio();
        verifica();

        // SE ENLAZA DRAWER LAYOUT Y EL NAVIGATION VIEW PARA EL MENÚ DESPLEGABLE
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // ESTE ES EL QUE CAMBIA EL COLOR DE TEXTO DEL ITEM
        navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.blanco)));

        //PARA CAMBIAR EL COLOR DEL ITEM DEL MENÚ
        navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.blanco)));

        // Pasando cada ID del menú como una posición de Ids porque cada
        // menú debe ser considerado como destinos de nivel top.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_contactos, R.id.nav_info, R.id.nav_pprivacidad, R.id.nav_ubicacion, R.id.nav_home, R.id.nav_cerrarS)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inicio, menu);

        tvNombreMenu = (TextView) findViewById(R.id.tvNombreMenu);
        ivUser = (ImageView) findViewById(R.id.ivUser);

        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);

        tvNombreMenu.setText(sp1.getString("nombre", ""));

        ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // REDIRIGIRÁ A LA OPCIÓN DE EDITAR INFORMACIÓN DE USUARIO
                Intent infoUser = new Intent(getApplicationContext(), userinfoActivity.class);
                startActivity(infoUser);
            }
        });

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void activa_inicio(){
        SharedPreferences sp1 = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);
        // SE MODIFICA EL VALOR DEL SHARED INICIALIZANDO EL EDITOR DEL SHARED
        SharedPreferences.Editor editor = sp1.edit();
        editor.putBoolean("sesion", true);
        editor.commit();
    }

    private void verifica(){
        // ACTIVAMOS EL EDITOR DEL SHARED
        SharedPreferences shared = getSharedPreferences("MisDatos", Context.MODE_PRIVATE);

        // TRAEMOS EL VALOR DE LA SESION
        boolean val = shared.getBoolean("sesion", false);

        // SE INSTANCIA EL ACTIVITY DEL LOGIN DE LA APP    contexto, nombre de la clase compilada
        Intent intent1 = new Intent(this, MainActivity.class);

        if(val == false) {
            // SE MANDA A EJECUTAR EL ACTIVITY DE INICIO DE LA APP
            startActivity(intent1);
        }

        // VERIFICA QUE TENGA LA APP LOS PERMISOS NECESARIOS PARA LA UTILIZACIÓN DE LOS CONTACTOS
        int permissionCheckContactos = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        // SE VERIFICA CON LA CONDICIONAL QUE SE TENGAN LOS PERMISOS INSTALADOS
        if (permissionCheckContactos == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                // AQUI SE COLOCA MENSAJES EXTRAS QUE SE QUIERAN COLOCAR SOBRE LA EXPLICACIÓN DE LOS PERMISOS
            } else {
                // PEDIRÁ LA ACTIVACIÓN DEL SERVICIO
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            }
        }

        // VERIFICA QUE TENGA LA APP LOS PERMISOS NECESARIOS PARA LA UTILIZACIÓN DE LA UBICACIÓN
        int permissionCheckUbicacion = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        // SE VERIFICA CON LA CONDICIONAL QUE SE TENGAN LOS PERMISOS INSTALADOS
        if (permissionCheckUbicacion == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // AQUI SE COLOCA MENSAJES EXTRAS QUE SE QUIERAN COLOCAR SOBRE LA EXPLICACIÓN DE LOS PERMISOS
            } else {
                // PEDIRÁ LA ACTIVACIÓN DEL SERVICIO
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    // PARA DARLE EL COMPORTAMIENTO A CADA ITEM QUE SE TIENE EN EL MENÚ SUPERIOR IZQUIERDO
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // HAY QUE HACER UN SWITCH PARA LOS ITEMS
        switch(item.getItemId()){
            case R.id.accion_salir:
                // ESTA FUNCIÓN HACE QUE SALGAS DE LA APP (SE FINALICE SIN CERRAR SESION);
                finishAffinity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}