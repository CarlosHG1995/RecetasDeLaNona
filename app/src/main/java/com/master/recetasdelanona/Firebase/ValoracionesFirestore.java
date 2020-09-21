package com.master.recetasdelanona.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.master.recetasdelanona.Firebase.CategoriasRecetasFirestore.CATEGORIAS;

public class ValoracionesFirestore {

    public static String VALORACIONES= "valoraciones";


    public static void guardarValoracion(String doc_categoria, String la_receta,
                                         Double valor) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map <String, Object> datos = new HashMap <>();
        datos.put("valoracion", valor);
        db.collection(CATEGORIAS).document(doc_categoria)
        .collection(VALORACIONES).document(la_receta).set(datos);
    }

    public interface EscuchadorValoracion {
        void onRespuesta(Double valor);
        void onNoExiste();
        void onError(Exception e);
    }
    public static void leerValoracion(String doc_categoria, String la_receta, final EscuchadorValoracion escuchador)
    { FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(CATEGORIAS).document(doc_categoria)
                .collection(VALORACIONES).document(la_receta).get()
                .addOnCompleteListener(new OnCompleteListener <DocumentSnapshot>() {
                    @Override public void onComplete(@NonNull Task <DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(!task.getResult().exists()){
                                escuchador.onNoExiste();
                            }
                            else {
                                double valor = task.getResult().getDouble("valoracion");
                                escuchador.onRespuesta(valor);
                            }
                        }
                        else {
                            Log.e("TAG","No se puede leer valoraciones",task.getException());
                            escuchador.onError(task.getException());
                        }
                    }
                });
    }
}
