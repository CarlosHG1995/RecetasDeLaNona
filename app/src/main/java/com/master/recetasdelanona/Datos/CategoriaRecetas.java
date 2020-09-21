package com.master.recetasdelanona.Datos;

public class CategoriaRecetas {

    private String categoria;
    private String imagen;

    public CategoriaRecetas() { }

    public CategoriaRecetas(String categoria ,String imagen) {
        this.categoria=categoria;
        this.imagen=imagen;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria=categoria;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen=imagen;
    }
}
