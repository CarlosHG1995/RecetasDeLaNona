package com.master.recetasdelanona.Adaptadores;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.master.recetasdelanona.Datos.Receta;
import com.master.recetasdelanona.R;
import com.master.recetasdelanona.Vistas.MenuActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class AdaptadorRecetas extends FirestoreRecyclerAdapter<Receta, AdaptadorRecetas.ViewHolder > {

    protected View.OnClickListener onClickListener;
    public ProgressDialog progresoCarga;
    public AdaptadorRecetas(@NonNull FirestoreRecyclerOptions<Receta> options) {
        super(options);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {  View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.lista_recetas,parent,false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

     public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtNombreReceta;
        public ImageView imagen;
        public ViewGroup layout;

        public ViewHolder(View itemView)
        {   super(itemView);
            txtNombreReceta=   itemView.findViewById(R.id.text_nomb_receta);
            imagen =   itemView.findViewById(R.id.imagen_Receta);
            layout = itemView.findViewById(R.id.layout_lista);

            progresoCarga = new ProgressDialog(itemView.getContext());
            progresoCarga.setIndeterminate(false);
            progresoCarga.setMax(100);
            progresoCarga.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progresoCarga.setCancelable(true);
            progresoCarga.setCanceledOnTouchOutside(false);
            progresoCarga.show();
        }

    }

    @Override protected void onBindViewHolder(@NonNull final ViewHolder holder ,int i ,@NonNull Receta item) {

        holder.txtNombreReceta.setText(item.getNombre_receta());
        progresoCarga.dismiss();
        Log.d("TAG","adaptador nombre receta " + item.getNombre_receta());
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
                             holder.txtNombreReceta.setTextColor(palette1.getDarkVibrantColor(2));
                             holder.layout.setBackgroundColor(palette1.getLightMutedColor(0));
                             holder.imagen.setBackgroundColor(palette1.getLightVibrantColor(3));
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
