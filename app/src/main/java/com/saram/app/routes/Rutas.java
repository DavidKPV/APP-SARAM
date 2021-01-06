package com.saram.app.routes;

public class Rutas {
    private static final String SERVIDOR = "http://192.168.43.200:8080/";
    private static final String UBICACION = "api/";

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
    public static final String privacidad = SERVIDOR + "privacidad";
    public static final String servicio = SERVIDOR + "#Servicio";
    public static final String servicioBot = SERVIDOR + "PRUEBA_BOT";
}
