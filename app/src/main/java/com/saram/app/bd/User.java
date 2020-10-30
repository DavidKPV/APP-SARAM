package com.saram.app.bd;

public class User {
    // CONSTANTES PARA LOS CAMPOS DE LA TABLA USUARIO
    public static final String NAME_TABLE = "usuario";
    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "nombre";
    public static final String FIELD_IMG = "imagen";

    // SE CREA LA TABLA CITAS CON SUS VALORES
    public static final String CREAR_TABLA_USER = "CREATE TABLE "+NAME_TABLE+" ("+
            FIELD_ID +" INTEGER PRIMARY KEY," +
            FIELD_NAME+" TEXT,"+
            FIELD_IMG+" BLOB);";
}
