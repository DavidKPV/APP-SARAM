package com.saram.app.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import com.saram.app.bd.SQLiteHelper;
import com.saram.app.bd.User;

public class Userbd {
    private byte[] IMAGEN;
    private SQLiteHelper conexion;
    private SQLiteDatabase sql1, sql2, sql3;
    private Context contexto;

    public  Userbd(Context contexto){
        this.contexto = contexto;
        conexion = new SQLiteHelper(contexto,"saram.db", null, 1);
    }

    public byte[] getImagen() {
        return IMAGEN;
    }

    public void setData(
            int id, String nombre, byte[] imagen
    ){
        sql1 = conexion.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(User.FIELD_ID, id);
        valores.put(User.FIELD_NAME, nombre);
        valores.put(User.FIELD_IMG, imagen);

        long idre = sql1.insert(User.NAME_TABLE, null,valores);

        //Toast.makeText(contexto, "REGISTRO: "+idre+"\nREGISTRADO CON ÉXITO", Toast.LENGTH_LONG).show();
        sql1.close();
    }

    public void updateData(
            String clave, byte[] imagen
    ){
        sql2 = conexion.getWritableDatabase();
        String[] vclave = new String[]{clave};

        ContentValues valores = new ContentValues();
        valores.put(User.FIELD_IMG, imagen);

        long idre = sql2.update(User.NAME_TABLE, valores, User.FIELD_ID+"=?", vclave);

        //Toast.makeText(contexto, "ACTUALIZADO CON ÉXITO", Toast.LENGTH_LONG).show();
        sql2.close();
    }

    public String[] getData(String clave){
        String[] resultados = new String[2];
        String[] vclave = new String[]{clave};
        try {
            sql3 = conexion.getReadableDatabase();
            Cursor obtener = sql3.query(
                    User.NAME_TABLE,
                    null,
                    User.FIELD_ID+"=?",
                    vclave,
                    null,
                    null,
                    null,
                    null
            );
            obtener.moveToFirst();
            resultados[0] = obtener.getString(0);
            resultados[1] = obtener.getString(1);
            IMAGEN = obtener.getBlob(2);
            sql3.close();
            return  resultados;
        }catch (Exception e){
            //Toast.makeText(contexto, "NO SE ENCONTRÓ NINGUNA CITA\nCON ESTE IDENTIFICADOR", Toast.LENGTH_LONG).show();
            return resultados;
        }
    }

}
