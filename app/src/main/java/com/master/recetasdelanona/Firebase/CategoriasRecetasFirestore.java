package com.master.recetasdelanona.Firebase;

import com.google.firebase.firestore.FirebaseFirestore;
import com.master.recetasdelanona.Datos.CategoriaRecetas;

public class CategoriasRecetasFirestore {

    public static String CATEGORIAS= "categorias";
    static String URL = "https://firebasestorage.googleapis.com/v0/b/recetasdelanona-6c6a3.appspot.com/o/";

    public static void crearCategorias(){
        CategoriaRecetas categoriaRecetas;
        FirebaseFirestore recetasFirebaseFirestore = FirebaseFirestore.getInstance();
        categoriaRecetas= new CategoriaRecetas("Carnes",URL+"carne.png?alt=media&token=6f2fc4b4-dabe-4ec5-ae2e-9bddfcea5498");
        recetasFirebaseFirestore.collection(CATEGORIAS).document("Carnes").set(categoriaRecetas);

        categoriaRecetas= new CategoriaRecetas("Granos",URL+"granos.png?alt=media&token=3c6f50d8-564d-432a-be12-95dec30c6c88");
        recetasFirebaseFirestore.collection(CATEGORIAS).document("Granos").set(categoriaRecetas);

        categoriaRecetas= new CategoriaRecetas("Jugos",URL+"jugo.png?alt=media&token=a287853b-ed1a-4f8e-a69c-d765a7196ff0");
        recetasFirebaseFirestore.collection(CATEGORIAS).document("Jugos").set(categoriaRecetas);

        categoriaRecetas= new CategoriaRecetas("Sopa",URL+"sopa.png?alt=media&token=ff343038-c602-48b9-9ad3-19dd58d70a1d");
        recetasFirebaseFirestore.collection(CATEGORIAS).document("Sopa").set(categoriaRecetas);
    }
}
