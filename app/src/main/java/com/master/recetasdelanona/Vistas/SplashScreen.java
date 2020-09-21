package com.master.recetasdelanona.Vistas;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.master.recetasdelanona.Firebase.LoginFirebaseActivity;
import com.master.recetasdelanona.R;

import static com.master.recetasdelanona.DialogComun.mostrarDialog;

public class SplashScreen extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        TextView txt = findViewById(R.id.textViewSplash);
        ImageView imge = findViewById(R.id.imageViewSplash);

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                Intent intent = new Intent(SplashScreen.this, LoginFirebaseActivity.class);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(SplashScreen.this,R.anim.fui_slide_in_right,R.anim.transicion_vista_splash);
                startActivity(intent, activityOptions.toBundle());
                //overridePendingTransition(R.anim.fui_slide_in_right,R.anim.transicion_vista_splash);
                finish();
            }
        },5000);
    }

    @Override protected void onResume() {
        super.onResume();
        NotificacionFCM_();
    }

    //mostrar en un alert dialog las notificaciones desde firebsae
    private void NotificacionFCM_(){
        /**/Bundle extras = getIntent().getExtras();
        if (extras!=null && extras.keySet().size()>4) {//
            String categoria="";
            categoria ="Categoria recetas: "+extras.getString("cat_recetas")+ "\n";
            categoria = categoria +" "+ extras.getString("sugerencia");
            categoria = categoria +" "+ extras.getString("sugerencia2");
            categoria = categoria +" "+ extras.getString("sugerencia3");
            mostrarDialog(getApplicationContext(), categoria);
            for (String key : extras.keySet())
            { Log.d("TAG","key extras "+key);
              getIntent().removeExtra(key);
            } extras = null;
        }
        /* else if(extras == null){
            Log.d("TAG","extras vacios ");
            Toast.makeText(this ,"extras vacios splash " + extras.toString() ,Toast.LENGTH_SHORT).show();
        }*/
        if (getIntent().hasExtra("body"))
        {
            mostrarDialog(this,extras.getString("title")
                               +":\n" + extras.getString("body"));
            extras.remove("body");
        }
    }


}
