package com.master.recetasdelanona.Firebase;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.master.recetasdelanona.Datos.Usuario;

public class UsuariosFirebase {

    public static void  guardarUsuario(final FirebaseUser user){
        Usuario usuario = new Usuario(user.getDisplayName(),user.getEmail());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(user.getUid()).set(usuario);
        //db.getReference("usuarios/"+user.getUid()).setValue(usuario);
    }
}
