package com.master.recetasdelanona.Adaptadores;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.master.recetasdelanona.Datos.CategoriaRecetas;
import com.master.recetasdelanona.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;

public class AdaptadorCategorias extends FirestoreRecyclerAdapter<CategoriaRecetas,AdaptadorCategorias.ViewHolder > {

    protected View.OnClickListener onClickListener;

    public AdaptadorCategorias(@NonNull FirestoreRecyclerOptions<CategoriaRecetas> options) {
        super(options);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {  View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoria_receta,parent,false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtCategoria;
        public ImageView imagen;
        public ViewGroup vista;
        public ViewHolder(View itemView)
        {   super(itemView);
            txtCategoria=   itemView.findViewById(R.id.text_);
            imagen =   itemView.findViewById(R.id.imageview);
            vista = itemView.findViewById(R.id.constrain);
        }
    }

    @Override protected void onBindViewHolder(@NonNull final ViewHolder holder ,int i ,@NonNull CategoriaRecetas item) {
        holder.txtCategoria.setText(item.getCategoria());
        Picasso.get().load(item.getImagen()).error(R.drawable.libro_receta).placeholder(R.drawable.plato).into(new Target() {
            @Override public void onBitmapLoaded(
                    final Bitmap bitmap ,
                    Picasso.LoadedFrom loadedFrom) {
                //holder.imagen;
                assert holder.imagen != null;
                holder.imagen.setImageBitmap(bitmap);
                Palette.from(bitmap)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette palette1 = palette.from(bitmap).generate();
                                if (palette1 == null) {
                                    return;
                                }
                                holder.txtCategoria.setTextColor(palette1.getDominantColor(1));
                                holder.vista.setBackgroundColor(palette1.getMutedColor(1));
                                holder.imagen.setBackgroundColor(palette1.getDarkVibrantColor(0));
                                holder.imagen.invalidate();
                            }
                        });
            }
            @Override public void onBitmapFailed(Exception e ,Drawable drawable) {}
            @Override public void onPrepareLoad(Drawable drawable) {}
        });//
        holder.itemView.setOnClickListener(onClickListener);
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }

}
