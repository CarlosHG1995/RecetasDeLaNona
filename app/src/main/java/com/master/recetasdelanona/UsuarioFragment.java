package com.master.recetasdelanona;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UsuarioFragment extends Fragment {
    @Override public View onCreateView( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState)
    {   View vista = inflater.inflate(R.layout.fragment_usuario,container,false);
        FirebaseUser usuario =FirebaseAuth.getInstance().getCurrentUser();
        TextView correo = vista.findViewById(R.id.correo);
        correo.setText(usuario.getEmail());
        TextView nombres = vista.findViewById(R.id.nombres);
        nombres.setText(usuario.getDisplayName());
        Log.d("user fragment"," nombre "+ usuario.getDisplayName() );
        //imagen de usuario logeado con volley
        RequestQueue peticiones =Volley.newRequestQueue(getActivity().getApplicationContext());
        ImageLoader lector_imagenes = new ImageLoader(peticiones ,new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache <>(10);
            public void putBitmap(String url ,Bitmap bitmap) {
            cache.put(url,bitmap);
            }
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }
        });
        //foto del usuario
        Uri urlImagen = usuario.getPhotoUrl();
        if(urlImagen != null)
        {   NetworkImageView fotousuario = vista.findViewById(R.id.imagen);
            fotousuario.setImageUrl(urlImagen.toString(),lector_imagenes);
        }

        return vista;//super.onCreateView(inflater ,container ,savedInstanceState);
    }
}
