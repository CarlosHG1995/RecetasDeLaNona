package com.master.recetasdelanona.Datos;

public class Usuario {

    private String nombre;
    private String correo;
    private long iniciosesion;

    public Usuario() { }

    public Usuario(String nombre, String correo, long iniciosesion) {
        this.nombre = nombre;
        this.correo = correo;
        this.iniciosesion = iniciosesion;
    }

    public Usuario(String nombre, String correo) {
        this(nombre, correo, System.currentTimeMillis());
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public long getIniciosesion() {
        return iniciosesion;
    }

    public void setIniciosesion(long iniciosesion) {
        this.iniciosesion = iniciosesion;
    }
}
