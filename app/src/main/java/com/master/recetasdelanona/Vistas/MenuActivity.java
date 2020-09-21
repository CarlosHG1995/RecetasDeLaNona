package com.master.recetasdelanona.Vistas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.firebase.inappmessaging.FirebaseInAppMessagingClickListener;
import com.google.firebase.inappmessaging.model.Action;
import com.google.firebase.inappmessaging.model.InAppMessage;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.master.recetasdelanona.Adaptadores.AdaptadorCategorias;
import com.master.recetasdelanona.Datos.CategoriaRecetas;
import com.master.recetasdelanona.Datos.TemasCategorias;
import com.master.recetasdelanona.Firebase.LoginFirebaseActivity;
import com.master.recetasdelanona.R;

import static com.master.recetasdelanona.DialogComun.cambioColor;
import static com.master.recetasdelanona.DialogComun.fondo_login;
import static com.master.recetasdelanona.DialogComun.mostrarDialog;
import static com.master.recetasdelanona.DialogComun.mFirebaseRemoteConfig;
import static com.master.recetasdelanona.DialogComun.ultima_version;
import static com.master.recetasdelanona.Firebase.CategoriasRecetasFirestore.CATEGORIAS;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toogle;
    NavigationView navigationView;
    static FirebaseAnalytics mFirebaseAnalytics;
    private AdaptadorCategorias adaptadorCategorias;
    private FirebaseFirestore instance = FirebaseFirestore.getInstance();
    private CollectionReference categorias_referencia = instance.collection(CATEGORIAS);
    private ProgressDialog progresoCarga;
    private String linkInvitacion = "https://recetasdelanona.page.link/jTpt",
            url_actualizar_app = "https://drive.google.com/file/d/1c_HRVzgCQCshxHjYSVDk5sHxWFKkNQZe/view?usp=sharing";
    private boolean activar = true;
    private ViewGroup vista;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        vista= findViewById(R.id.bar_layout);
        //Barra de acciones
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //navigation drawer
        drawer = findViewById(R.id.drawer_layout);
        toogle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.drawer_open,R.string.drawer_close);
        toogle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toogle);
        toogle.syncState();
        toogle.setToolbarNavigationClickListener (new View.OnClickListener ( ) {
            @Override public void onClick(View view){
                onBackPressed();
            }
        });
        progresoCarga = new ProgressDialog(MenuActivity.this);
        progresoCarga.setIndeterminate(false);
        progresoCarga.setMax(100);
        progresoCarga.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progresoCarga.setCancelable(true);
        progresoCarga.setCanceledOnTouchOutside(false);
        progresoCarga.setTitle(getString(R.string.progress_carga3));
        progresoCarga.setMessage(getString(R.string.progress_wait));
        progresoCarga.show();
        navigationView= findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        //configuracion remota
        Inicializar_remot_config();
        //base de Categorias en firebase storage
        //crearCategorias();
        //NotificacionFCM();
        cargarInfoFromfirebase();
        final SharedPreferences preferences_cancelar= getApplicationContext().getSharedPreferences("Categoria",
                                                                                                   Context.MODE_PRIVATE);
        if(preferences_cancelar.getBoolean("Cancelar",false)==false){
            final SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                    "Categoria",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("Cancelar",true);
            editor.commit();
            FirebaseMessaging.getInstance().subscribeToTopic("Todos");
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance (this);
        Log.d("TAG","analitics " + mFirebaseAnalytics);
        inAppClickListener inAppClickListener = new inAppClickListener();
        FirebaseInAppMessaging.getInstance().addClickListener(inAppClickListener);
        FirebaseInAppMessaging.getInstance().setAutomaticDataCollectionEnabled(true);

    }

    public void cargarInfoFromfirebase(){
        /**/
        Query query = categorias_referencia.limit(40);

        FirestoreRecyclerOptions<CategoriaRecetas> opciones = new FirestoreRecyclerOptions.Builder<CategoriaRecetas>()
                .setQuery(query, CategoriaRecetas.class).build();
        adaptadorCategorias = new AdaptadorCategorias(opciones);

        Log.d("TAG","infoquery " +query + "\n opciones del recycler->" + opciones);
        final RecyclerView recyclerView = findViewById(R.id.reciclerViewCategorias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptadorCategorias);
        progresoCarga.dismiss();

        adaptadorCategorias.setOnItemClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                int posicion = recyclerView.getChildAdapterPosition(view);
                CategoriaRecetas item = adaptadorCategorias.getItem(posicion);
                String id_Categoria = adaptadorCategorias.getSnapshots().getSnapshot(posicion).getId();
                Log.d("categoria","id-- " + id_Categoria);
                Context context = getAppContext();
                Intent intent = new Intent(context,VistaCategoriaRecetas.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("categoria",id_Categoria);
                context.startActivity(intent);
                overridePendingTransition(R.anim.entrada,R.anim.salida);
            }
        });
    }

    @Override protected void onStart() {
        Log.d("TAG","on start adaptador cat activitymenu ");
        super.onStart();
        //cargarInfoFromfirebase();
        adaptadorCategorias.startListening();
        currentcontext=this;
        //Inicializar_remot_config();
    }

    @Override protected void onStop() {
        Log.d("TAG","on stop adaptador cat activitymenu " );
        super.onStop();
        //Inicializar_remot_config();
        adaptadorCategorias.stopListening();
    }

    //mostrar en un alert dialog las notificaciones desde firebsae
    @Override protected void onResume() {
        Log.d("TAG","on resume adaptador cat activitymenu " );
        super.onResume();
    }

    private static MenuActivity currentcontext;
    public static MenuActivity getCurrentContext()
    { return currentcontext; }

    public static Context getAppContext(){
        return MenuActivity.getCurrentContext();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.acercaDe){
            mensaje_acercade();
            return true;
        }
        else if(id == R.id.compartir){
            compartir();
        }
        return super.onOptionsItemSelected(item);
    }

    private void compartir(){
        Intent enviarIntent= new Intent();
        enviarIntent.setAction(Intent.ACTION_SEND);
        enviarIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.invitacion)+"\n\n" + linkInvitacion);
        enviarIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.invitacion1));
        enviarIntent.setType("text/plain");
        startActivity(Intent.createChooser(enviarIntent,getString(R.string.compartir_app)));
    }


    @SuppressWarnings("Declaración_con_Cuerpo_Vacío")
    @Override public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.info_user){
        usuario();
        }
        else if(id == R.id.suscripcion){
            Intent intent = new Intent(getBaseContext(),TemasCategorias.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fab_in,R.anim.fab_out);
            return true;
        }
        else if( id == R.id.cerrar_sesion){
            btn_logout();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer (GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void mensaje_acercade(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.info_app));
        builder.setPositiveButton(android.R.string.ok,null);
        builder.setIcon(R.mipmap.libro_receta_round);
        builder.create().show();
    }

    public void btn_logout(){
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener <Void>(){
            @Override public void onComplete(@NonNull Task <Void> task) {
                Intent intent = new Intent(MenuActivity.this,LoginFirebaseActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fui_slide_out_left,R.anim.trasicion_arriba);
            }});
    }

    public void usuario(){
        Intent intent= new Intent(this, UsuarioActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fui_slide_out_left,R.anim.fui_slide_in_right);
    }
    //inapp messaging firebase
    /**/
    public class inAppClickListener implements FirebaseInAppMessagingClickListener {
        @Override public void messageClicked (InAppMessage inAppMessage , Action action) {
            String categoria_Receta = "";
            categoria_Receta = "ID Campaña: " + inAppMessage.getCampaignMetadata ( ).getCampaignId ( );
            mostrarDialog ( getApplicationContext ( ) , categoria_Receta );
            Log.d("TAG","cat inapp " + categoria_Receta);
            // Conseguir datos personalizado
            // Map dataBundle = inAppMessage.getData();
        }
    }

    // remote config
    public void Inicializar_remot_config(){
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings
                .Builder()
                .setFetchTimeoutInSeconds(4)//tiempo
                .setMinimumFetchIntervalInSeconds(4)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(remoteConfigSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean> (){
            @Override public void onComplete(Task<Boolean>task){
                if(task.isSuccessful()){
                    mFirebaseRemoteConfig.activateFetched();
                    fondo_login= mFirebaseRemoteConfig.getString("fondo_login");
                    cambioColor = mFirebaseRemoteConfig.getBoolean("cambio_color");
                    ultima_version = mFirebaseRemoteConfig.getDouble("ultima_version_app");
                    revisar_actualizacion();
                    if(!cambioColor){
                        vista.setBackgroundColor(Color.parseColor( fondo_login));
                    }
                    Log.d("TAG","config " + task.getResult() + "\n" + ultima_version + "\n" + fondo_login + "\n" + cambioColor);
                }   else {
                    Toast.makeText(MenuActivity.this,"Remote config error",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**/
    private int getCurrentVersionCode(){
        try{
            return getPackageManager().getPackageInfo(getPackageName() ,0).versionCode;
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return -1;
    }

    private void revisar_actualizacion(){
        int fetcheVersionCode = (int) mFirebaseRemoteConfig.getDouble("ultima_version_app");
        Log.d("TAG","getcurrent " + getCurrentVersionCode() + " " +fetcheVersionCode);
        if(getCurrentVersionCode() <fetcheVersionCode){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
                    getString(R.string.cancelar1) ,new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog ,int which) {
                        Toast toast = Toast.makeText(MenuActivity.this,getString(R.string.actualizar1),Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,20,20);
                        toast.show();
                    }
                });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                    getString(R.string.actualizar), new DialogInterface.OnClickListener(){
                @Override public void onClick(DialogInterface dialog ,int which) {
                    Uri uri = Uri.parse(url_actualizar_app);
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
                }
            });
            alertDialog.setTitle(getString(R.string.mensaje_actualizar) +" "+ getString(R.string.app_name));
            alertDialog.setMessage(getString(R.string.mensaje_alert_update));
            alertDialog.setCancelable(false);
            alertDialog.setIcon(R.drawable.libro_receta);
            //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(R.color.primary_light));
            alertDialog.show();
        }
        else if(activar){ //si vuelve a parecer varias veces el toast cambiar a else vacío
            /*
            activar = false;
            Toast toast = Toast.makeText(MenuActivity.this,getString(R.string.mensaje_actualizado),Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,20,20);
            toast.show();*/

        }
    }

}