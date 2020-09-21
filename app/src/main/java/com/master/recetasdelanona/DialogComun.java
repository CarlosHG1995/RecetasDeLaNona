package com.master.recetasdelanona;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class DialogComun {

    public static FirebaseRemoteConfig mFirebaseRemoteConfig;
    public static String fondo_login;
    public static Double ultima_version;
    public static Boolean cambioColor;

    public static void mostrarDialog(final Context context ,final String msn){
        Intent intent = new Intent(context,Dialogo.class) ;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mensaje",msn);
        context.startActivity(intent);
    }

}