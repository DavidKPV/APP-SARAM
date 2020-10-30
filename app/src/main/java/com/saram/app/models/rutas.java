package com.saram.app.models;

public class rutas {
    private static final String SERVIDOR = "http://192.168.43.200:8080/";
    private static final String UBICACION = "SARAM-API/public/api/";
    private static final String UBICACION_PAG = "SARAM-API/public/";

    public static final String addMotos = SERVIDOR + UBICACION + "addmoto";
    public static final String getContactos = SERVIDOR + UBICACION + "getContactos";
    public static final String delContactos = SERVIDOR + UBICACION + "delContactos";
    public static final String setContactos = SERVIDOR + UBICACION + "setContactos";
    public static final String updateContactos = SERVIDOR + UBICACION + "updateContactos";
    public static final String updateMoto = SERVIDOR + UBICACION + "updatemoto";
    public static final String getMotos = SERVIDOR + UBICACION + "getmotos";
    public static final String delMoto = SERVIDOR + UBICACION + "deleteMoto";
    public static final String login = SERVIDOR + UBICACION + "login";
    public static final String registerUser = SERVIDOR + UBICACION + "registerUser";
    public static final String updateUser = SERVIDOR + UBICACION + "updateUser";
    public static final String getUser = SERVIDOR + UBICACION + "getuser";
    public static final String getEstado = SERVIDOR + UBICACION + "getEstado";
    public static final String getUbicacion = SERVIDOR + UBICACION + "getUbicacion";
    public static final String privacidad = SERVIDOR + UBICACION_PAG + "privacidad";
    public static final String servicio = SERVIDOR + UBICACION_PAG + "#Servicio";
}
