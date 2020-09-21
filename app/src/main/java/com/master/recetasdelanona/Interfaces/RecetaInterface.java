package com.master.recetasdelanona.Interfaces;

import com.master.recetasdelanona.Datos.Receta;

public interface RecetaInterface {

    Receta elemento(int id);
    void añade_receta(Receta receta);
    int tamaño();

}
