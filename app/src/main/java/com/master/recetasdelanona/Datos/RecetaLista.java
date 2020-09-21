package com.master.recetasdelanona.Datos;

import java.util.ArrayList;
import java.util.List;

public class RecetaLista {

    protected List <Receta> listarecetas;
    static String URL_RECETAS = "https://firebasestorage.googleapis.com/v0/b/recetasdelanona-6c6a3.appspot.com/o/Recetas%2F";

    public RecetaLista() {
        listarecetas = new ArrayList <>();
        add_Receta();
    }

    public Receta elemento(int id){return listarecetas.get(id);}
    public void añade_receta(Receta receta){listarecetas.add(receta);}
    public int tamaño(){return listarecetas.size();}

    public void add_Receta(){
        //String ingredientes ,String nombre_receta ,String imagen ,String preparacion
        añade_receta(new Receta("250 gramos de Carne, \n 1 huevo \n sal y pimienta al gusto","Carne en bistec",
                                URL_RECETAS+"libro.png?alt=media&token=7dfc3297-97f4-4124-a1c1-dfc04e38f0eb",
                                "Cocinar todo y por último fritar el huevo")
        );
        añade_receta(new Receta("250 gramos de Carne, \n 1/2 cebolla cabezona, \n 1/2 tomate picado en cubos \n sal y pimienta al gusto",
                                "Carne desmechada",
                                URL_RECETAS+"plato.png?alt=media&token=2d0d24aa-3eb7-40d0-80e0-f3471f28cb9f",
                                "Cocinar todo y por último fritar el huevo")
        );


    }

}
