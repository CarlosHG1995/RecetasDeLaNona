package com.master.recetasdelanona.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.master.recetasdelanona.R;
import com.master.recetasdelanona.Vistas.VistaCategoriaRecetas;

import static com.master.recetasdelanona.DialogComun.mostrarDialog;

public class CategoriasRecetasFMService extends FirebaseMessagingService {

    static final String URL_SERVIDOR = "https://eventos-carlos.000webhostapp.com/dispositivos/index.html";
    static String ID_PROYECTO="recetasdelanona-6c6a3";
    String idRegistro ="";
    String categoria="";
    @Override public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d("","from "+remoteMessage.getFrom()+"\n to" +remoteMessage.getTo());
        Log.d("","CLICK ACTION "+ remoteMessage.getNotification().getClickAction());

        if (remoteMessage.getData().size() > 0) {

            categoria ="Categoria de receta: "+remoteMessage.getData().get("cat_recetas")+ "\n";
            categoria = categoria + "\n"+ remoteMessage.getData().get("sugerencia");
            categoria = categoria +"\n"+ remoteMessage.getData().get("sugerencia2");
            categoria = categoria +"\n"+ remoteMessage.getData().get("sugerencia3");
            mostrarDialog(getApplicationContext(),categoria);
        }
        //else { }
            if(remoteMessage.getNotification() != null){
                //verNotificacion(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
                mostrarDialog(getApplicationContext(),
                              remoteMessage.getNotification().getTitle()+":\n"+
                              remoteMessage.getNotification().getBody() + "\nCategoria de receta: " +
                              remoteMessage.getData().get("cat_recetas")+"\n"+
                              remoteMessage.getData().get("sugerencia")+"\n"+
                              remoteMessage.getData().get("sugerencia2")+"\n"+
                              remoteMessage.getData().get("sugerencia3"));
            }


        /*if(remoteMessage.getData().containsKey("click_action")){
            Log.d("TAG","data tiene action " );
            categoria = remoteMessage.getData().get("cat_recetas")+ "\n";
            Intent intent = new Intent(this,VistaCategoriaRecetas.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("categoria",categoria);
            startActivity(intent);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("categoria",id_Categoria);
            *
            //finish();*
        }*/


        //super.onMessageReceived(remoteMessage);
    }

    private void verNotificacion(String titulo, String  body){
        Intent intent = new Intent(this, LoginFirebaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.libro_receta)
                .setContentTitle(titulo)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());
    }



    @Override public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        String idPush = token;
        Log.d("TAG","token " + token +" idpush " + idPush);
    }

}
