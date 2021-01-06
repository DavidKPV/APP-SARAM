package com.saram.app.controllers.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.Result;
import com.saram.app.R;
import com.saram.app.models.general.AlertsDiaog;
import com.saram.app.models.general.Permissions;
import com.saram.app.models.services.LoginData;

import java.util.regex.Pattern;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private AlertsDiaog alertsDiaog = new AlertsDiaog();
    private Permissions permissions;
    private LoginData loginData;

    // SE DECLARAN LOS OBJETOS UTILIZADOS
    private ImageView ivlogo, ivChatbot;
    private LinearLayout llQR;
    private TextView tvOlvido, tvRegistro;
    private Button btnIngreso;
    private EditText etNombre, etPass;
    private TextInputLayout tilNombre, tilPass;

    // PARA EL SCANNER DEL CÓDIGOS QR
    private ZXingScannerView myScannerview;
    int permissionCheckCamera;

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ELIMINA EL TOOL BAR DE LA ACTIVITY
        getSupportActionBar().hide();
        // SE ENLANZAN LOS CONTROLADORES CON LA VISTA
        ivlogo = (ImageView) findViewById(R.id.ivLogo);
        ivChatbot = (ImageView) findViewById(R.id.ivChatBot);
        tvOlvido = (TextView) findViewById(R.id.tvOlvido);
        tvRegistro = (TextView) findViewById(R.id.tvRegistro);
        btnIngreso = (Button) findViewById(R.id.btnIngreso);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etPass = (EditText) findViewById(R.id.etPass);
        tilNombre = (TextInputLayout) findViewById(R.id.tilNombre);
        tilPass = (TextInputLayout) findViewById(R.id.tilPass);
        llQR = (LinearLayout) findViewById(R.id.llQR);
        // CLASES
        permissions = new Permissions(this);
        loginData = new LoginData(MainActivity.this, ivlogo);

        Animation animacionChat = AnimationUtils.loadAnimation(this, R.anim.chat_bot_escala);
        animacionChat.setFillAfter(false);
        ivChatbot.setAnimation(animacionChat);

        // SE CREAN LAS VALIDACIONES EN TIEMPO REAL
        // VALIDACIÓN DENTRO DEL CAMPO NOMBRE
        etNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilNombre.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // VALIDACION DENTRO DEL CAMPO CONTRASEÑA
        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // PARA LIMPIAR LOS ERRORES DENTRO DEL PROCESO DE LLENADO DEL CAMPO
                tilNombre.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // OYENTE DEL TEXTO DE RECUPERAR LA CONTRASEÑA
        tvOlvido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevaContrasena();
            }
        });

        // OYENTE PARA UN NUEVO REGISTRO
        tvRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nuevoRegistro();
            }
        });

        // SE CREA EL OYENTE PARA EL BOTÓN DE INGRESO
        btnIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ingresar();
            }
        });

        // SE CREAL EL OYECTE PARA EL LINEAR LAYOUT DE LECTOR DE QR
        llQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });

        // OYENTE DEL CHATBOT
        ivChatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent irChat = new Intent(getApplicationContext(), ChatBotActivity.class);
                startActivity(irChat);
            }
        });
    }

    private void checkCameraPermission(){
        // VERIFICA QUE TENGA LA APP LOS PERMISOS NECESARIOS PARA LA UTILIZACIÓN DE LA CAMARA
        permissionCheckCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheckCamera == PackageManager.PERMISSION_DENIED) {
            alertsDiaog.alert_OK(MainActivity.this, "DEBES DE ACTIVAR LOS PERMISOS DE ACCESO A LA CÁMARA PARA PODER UTILIZAR ESTA OPCIÓN", "ENTENDIDO", 1, permissions).show();
        }
        else{
            activarLector();
        }
    }

    // MÉTODO DE INGRESO AL INICIO DE LA APP
    private void ingresar(){
        // SE OBTIENEN LOS VALORES DE LOS CAMPOS
        final String email = tilNombre.getEditText().getText().toString();
        final String pass = tilPass.getEditText().getText().toString();
        // SE VALIDAN QUE LOS CAMPOS NO ESTEN VACÍOS
        if(email.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "LLENA TODOS LOS CAMPOS", Toast.LENGTH_LONG).show();
        }
        else{
            // SE OBTIENEN LOS RESULTADOS DE LA VALIDACIÓN DE AMBOS CAMPOS
            Boolean vemail = validaemail(email);
            Boolean vpass = validapassword(pass);
            // SE VALIDA QUE LOS CAMPOS HAYAN PASADO SUS RESPECTIVAS VALIDACIONES
            if(vemail && vpass) {
                loginData.serviceIngresarData(email, pass);
            }
        }
    }

    // MÉTODO DE LA ACCIÓN DEL OYENTE PARA LA RECUPERACIÓN DE LA CONTRASEÑA
    private void nuevaContrasena(){
        Intent intentcontra = new Intent(this, RecuperarContra.class);
        startActivity(intentcontra);
    }

    // MÉTODO DE LA ACCIÓN DEL OYENTE PARA UN NUEVO REGISTRO
    private void nuevoRegistro(){
        Intent intentregistro = new Intent(this, RegistroActivity.class);
        startActivity(intentregistro);
    }

    private boolean validaemail(String noe){
        // O SE ASEGURA QUE TENGA DATOS ESPECÍFICOS DE UN EMAIL
        Pattern patronEmail = Patterns.EMAIL_ADDRESS;
        // Y NO PASE DEL LÍMTE PERMITIDO
        if(!patronEmail.matcher(noe).matches() || noe.length() > 50 ) {
            tilNombre.setError("Correo no válido, Intentalo de nuevo");
            return false;
        }else{
            tilNombre.setError(null);
        }
        return true;
    }

    private boolean validapassword(String password){
        // VALIDA QUE NO PASE DEL LÍMTE PERMITIDO
        if (password.length() > 16) {
            tilPass.setError("Contraseña no válida, Intentalo de nuevo");
            return false;
        }else{
            tilPass.setError(null);
        }
        return true;
    }

    private void activarLector(){
        myScannerview = new ZXingScannerView(this);
        setContentView(myScannerview);
        myScannerview.setResultHandler(this);
        myScannerview.startCamera();
    }

    // ESTE MÉTODO SE ENCARGA DE LEER EL CÓDIFO Y TRADUCIRLO
    @Override
    public void handleResult(final Result result) {
        // PARA QUE CONTINUE ESCANEANDO SI ES QUE YA SE ESCANEÓ UNA VEZ
        myScannerview.resumeCameraPreview(this);
        loginData.serviceIngresarQr(result.getText());
    }
}