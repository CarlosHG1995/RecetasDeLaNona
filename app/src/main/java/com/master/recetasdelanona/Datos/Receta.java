package com.master.recetasdelanona.Datos;

import java.util.Arrays;

public class Receta {

    private String ingredientes;
    private String nombre_receta;
    private String imagen;
    private String preparacion;

    public Receta() {}

    public Receta(String ingredientes ,String nombre_receta ,String imagen ,String preparacion) {
        this.ingredientes=ingredientes;
        this.nombre_receta=nombre_receta;
        this.imagen=imagen;
        this.preparacion=preparacion;
    }

    public String getIngredientes() { return ingredientes; }

    public void setIngredientes(String ingredientes) { this.ingredientes=ingredientes; }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen=imagen;
    }

    public String getPreparacion() {
        return preparacion;
    }

    public void setPreparacion(String preparacion) {
        this.preparacion=preparacion;
    }

    public String getNombre_receta() { return nombre_receta; }

    public void setNombre_receta(String nombre_receta) { this.nombre_receta=nombre_receta; }
}
