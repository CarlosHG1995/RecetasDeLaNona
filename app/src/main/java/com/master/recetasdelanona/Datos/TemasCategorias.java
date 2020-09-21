package com.master.recetasdelanona.Datos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.messaging.FirebaseMessaging;
import com.master.recetasdelanona.R;

public class TemasCategorias extends AppCompatActivity {

    CheckBox checkBox_Arepas,checkBox_Carnes,checkBox_Tortas,
            checkBox_sopas,checkBox_granos;
    Switch switch_Cancelar;
    ActionBar actionBar;
    Toolbar toolbar;
    //mostrarDialog
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temas_mensajes_firebase);
        toolbar=findViewById(R.id.toolbar_temas);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        checkBox_Arepas = findViewById(R.id.checkBox);
        checkBox_Carnes = findViewById(R.id.checkBox2);
        checkBox_sopas = findViewById(R.id.checkBox3);
        checkBox_granos = findViewById(R.id.checkBox4);
        checkBox_Tortas = findViewById(R.id.checkBox5);
        switch_Cancelar= findViewById(R.id.switch_cancel);

        checkBox_Arepas.setChecked(Preferencias_TemasFirebase_Suscribirse(
                getApplicationContext(),"Arepas"));
        checkBox_Carnes.setChecked(Preferencias_TemasFirebase_Suscribirse(
                getApplicationContext(),"Carnes"));
        checkBox_sopas.setChecked(Preferencias_TemasFirebase_Suscribirse(
                getApplicationContext(),"Sopas"));
        checkBox_granos.setChecked(Preferencias_TemasFirebase_Suscribirse(
                getApplicationContext(),"Granos"));
        checkBox_Tortas.setChecked(Preferencias_TemasFirebase_Suscribirse(
                getApplicationContext(),"Tortas"));

        checkBox_Arepas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView ,boolean isChecked) {
                ManageATemasFirebase("Arepas",isChecked);
            }
        });
        checkBox_Carnes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView ,boolean isChecked) {
                ManageATemasFirebase("Carnes",isChecked);
            }
        });
        checkBox_sopas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView ,boolean isChecked) {
                ManageATemasFirebase("Sopas",isChecked);
            }
        });
        checkBox_granos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView ,boolean isChecked) {
                ManageATemasFirebase("Granos",isChecked);
            }
        });
        checkBox_Tortas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView ,boolean isChecked) {
                ManageATemasFirebase("Tortas",isChecked);
            }
        });

        Boolean cancelar_notificaciones = Preferencias_TemasFirebase_Suscribirse(getApplicationContext(),"Todos");
        switch_Cancelar.setChecked(cancelar_notificaciones);
        checkBox_Arepas.setEnabled(!cancelar_notificaciones);
        checkBox_Carnes.setEnabled(!cancelar_notificaciones);
        checkBox_sopas.setEnabled(!cancelar_notificaciones);
        checkBox_granos.setEnabled(!cancelar_notificaciones);
        checkBox_Tortas.setEnabled(!cancelar_notificaciones);

        switch_Cancelar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView ,boolean isChecked) {
                ManageATemasFirebase("Todos",isChecked);
            }
        });
    }

    private void ManageATemasFirebase(String categoria_tema,Boolean suscripcion)
    { if(categoria_tema.equals("Todos"))
       {
        if(suscripcion){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(categoria_tema);
        guardar_Suscripcion_TemasFirebase_pref(getApplicationContext(),
                                               categoria_tema,true);
        checkBox_Arepas.setChecked(false);
        checkBox_Carnes.setChecked(false);
        checkBox_sopas.setChecked(false);
        checkBox_granos.setChecked(false);
        checkBox_Tortas.setChecked(false);
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic(categoria_tema);
            guardar_Suscripcion_TemasFirebase_pref(getApplicationContext(),categoria_tema,false);
            /*try {
                //guardarIdRegistro(getApplicationContext(),FirebaseInstanceId.getInstance().getToken(FirebaseInstanceId.getInstance().getId().toString(),"FCM" ));
                guardar_Suscripcion_TemasFirebase_pref(getApplicationContext(),categoria_tema,false);
            }catch(IOException e){
            e.printStackTrace();
            }*/
        }
        checkBox_Arepas.setEnabled(!suscripcion);
        checkBox_Carnes.setEnabled(!suscripcion);
        checkBox_sopas.setEnabled(!suscripcion);
        checkBox_granos.setEnabled(!suscripcion);
        checkBox_Tortas.setEnabled(!suscripcion);
       }
        else {
            if(suscripcion){
            //mostrarDialog(getApplicationContext(),"Suscripción correctamente a las recetas\n" + categoria_tema);
            FirebaseMessaging.getInstance().subscribeToTopic(categoria_tema);
            guardar_Suscripcion_TemasFirebase_pref(getApplicationContext(),categoria_tema,true);
            }
            else
            { //mostrarDialog(getApplicationContext(),"Suscripción cancelada exitosamente\n" + categoria_tema);
                //eliminarIdRegistro(getApplicationContext());//SI FALLA COMENTAR ESTA LINEA
            FirebaseMessaging.getInstance().unsubscribeFromTopic(categoria_tema);
            guardar_Suscripcion_TemasFirebase_pref(getApplicationContext(),categoria_tema,false);
            }
        }
    }

    public static void guardar_Suscripcion_TemasFirebase_pref(
            Context context, String categoria_tema, Boolean suscripcion)
    { final SharedPreferences sharedPreferences = context.getSharedPreferences(
      "Categoria", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean(categoria_tema,suscripcion);
    editor.commit();
    }

    public static Boolean Preferencias_TemasFirebase_Suscribirse(
            Context context, String categoria_tema)
    { final SharedPreferences preferences = context.getSharedPreferences("Categoria"
    , Context.MODE_PRIVATE);
        return preferences.getBoolean(categoria_tema,false);
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
         if(id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
