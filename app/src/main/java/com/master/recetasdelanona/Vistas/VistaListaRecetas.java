package com.master.recetasdelanona.Vistas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.master.recetasdelanona.Firebase.ValoracionesFirestore;
import com.master.recetasdelanona.R;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

import static com.master.recetasdelanona.Firebase.CategoriasRecetasFirestore.CATEGORIAS;
import static com.master.recetasdelanona.Firebase.ValoracionesFirestore.guardarValoracion;
import static com.master.recetasdelanona.Firebase.ValoracionesFirestore.leerValoracion;

public class VistaListaRecetas extends AppCompatActivity {

    TextView txtReceta,txtIngre,txtPrepara;
    ImageView imgdeReceta;
    String receta,categoria,IngrF2,preparacion,imageFire,nombReFi;
    Toolbar toolbar_detail;
    ActionBar actionBar;
    SharePhotoContent sharePhotoContent;
    public String RECETAS = "recetas";
    public static FirebaseFirestore instancia= FirebaseFirestore.getInstance();
    public static CollectionReference coleccion_cat_recetas= instancia.collection(CATEGORIAS),
            collectionReferenceReceta;
    private CallbackManager callbackManagerFacebook;
    private ShareDialog shareDialogFacebook;
    RatingBar valoracion;
    private final Activity THIS = this;
    private ProgressDialog progresoCarga;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_receta);
        overridePendingTransition(R.anim.alfa_salida,R.anim.alfa_entrada);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        //Barra de acciones
        toolbar_detail=findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar_detail);
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        this.callbackManagerFacebook = CallbackManager.Factory.create();
        this.shareDialogFacebook = new ShareDialog(this);
        this.shareDialogFacebook.registerCallback(callbackManagerFacebook ,new FacebookCallback <Sharer.Result>() {
            @Override public void onSuccess(Sharer.Result result) {
                Toast toast = Toast.makeText(   THIS,getString(R.string.foto),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,20,20);
                toast.show();
            }

            @Override public void onCancel() { }

            @Override public void onError(FacebookException e) {
                //Toast toast = Toast.makeText(   THIS,"Debes estar logeado con una cuenta de facebook, para compartir por esa red social.",Toast.LENGTH_LONG);
                Log.d("TAG","error share "+e.toString());
            }
        });
        txtReceta = findViewById(R.id.text_nomb_receta);
        txtIngre = findViewById(R.id.text_ingredientes);
        txtIngre.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                txtIngre.setCursorVisible(true);
                txtIngre.setTextIsSelectable(true);
                return  false;
            }
        });
        txtPrepara = findViewById(R.id.textPreparacion);
        txtPrepara.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                txtPrepara.setCursorVisible(true);
                txtPrepara.setTextIsSelectable(true);
                return  false;
            }
        });
        imgdeReceta = findViewById(R.id.imagen_DetaReceta);
        valoracion = findViewById(R.id.valoracion);
        progresoCarga = new ProgressDialog(VistaListaRecetas.this);
        progresoCarga.setIndeterminate(false);
        progresoCarga.setMax(100);
        progresoCarga.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progresoCarga.setCancelable(true);
        progresoCarga.setCanceledOnTouchOutside(false);
        progresoCarga.setTitle(getString(R.string.progress_carga2));
        progresoCarga.setMessage(getString(R.string.progress_wait));
        progresoCarga.show();
        cargarInfo_Receta_Fire();
    }

    public void cargarInfo_Receta_Fire(){
        Bundle extras = getIntent().getExtras();
        categoria = extras.getString("categoria");
        receta = extras.getString("documento_receta");
        if(receta==null || categoria == null) receta="";
        Log.d("TAG","vista lista receta-> " + receta + "\n id_cat_cole de vista categoria receta " + categoria );
        /**/collectionReferenceReceta = coleccion_cat_recetas.document(categoria).collection(RECETAS);
        collectionReferenceReceta.document(receta).get().addOnCompleteListener(new OnCompleteListener <DocumentSnapshot>() {
            @Override public void onComplete(@NonNull Task <DocumentSnapshot> task) {
                String IngrFi = task.getResult().getString("ingredientes");
                IngrF2 = IngrFi.replace(",","\n");
                nombReFi = task.getResult().getString("nombre_receta");
                imageFire = task.getResult().getString("imagen");
                String prepFire = task.getResult().getString("preparacion");
                preparacion = prepFire.replace(".", "\n");
                txtIngre.setText("Ingredientes: \n"+IngrF2);
                txtPrepara.setText("Preparación: \n"+preparacion);
                txtReceta.setText(nombReFi);
                Picasso.get().load(imageFire).error(R.drawable.libro_receta).placeholder(R.drawable.plato).into(imgdeReceta);
                progresoCarga.dismiss();
            }
        });
        leerValoracion(categoria ,receta ,new ValoracionesFirestore.EscuchadorValoracion() {
            @Override public void onRespuesta(Double valor) {
                valoracion.setOnRatingBarChangeListener(null);
                valoracion.setRating(valor.floatValue());
                valoracion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override public void onRatingChanged(RatingBar ratingBar ,float valor ,boolean fromUser)
                    { guardarValoracion(categoria,receta,((double) valor)); }
                });
            }

            @Override public void onNoExiste() {
                this.onRespuesta(0.0);
            }

            @Override public void onError(Exception e) { }
        });

    }

    @Override protected void onStart() {
        super.onStart();
        context=this;
    }

    @Override protected void onStop() {
        super.onStop();
    }

    public class DownloadImageTask extends AsyncTask <String, Void, Bitmap>
    {   ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mImagen = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mImagen = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mImagen;
        }
        public void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            SharePhoto sharePhoto = new SharePhoto.Builder().
                    setBitmap(result)
                    .build();
            if(UsarShareDialog_Foto() &&  AccessToken.isCurrentAccessTokenActive()){
                 sharePhotoContent = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialogFacebook.show(sharePhotoContent);
            }
            //Log.d("TAG", "bitmap " +result.toString());
        }
    }

    private static VistaListaRecetas context;
    public static VistaListaRecetas getContext(){ return context; }

    public static Context getAppContext(){ return VistaListaRecetas.getContext(); }

    @Override protected void onActivityResult(int requestCode ,int resultCode ,@Nullable Intent data) {
        super.onActivityResult(requestCode ,resultCode ,data);
        this.callbackManagerFacebook.onActivityResult(requestCode ,resultCode ,data);
    }

    //esto para uso del share dialog facebbok y poder compartir receta?
    private boolean UsoShareDialogPublicarMSN(){
        return UsarShareDialog_Link();
    }

    private boolean UsarShareDialog_Link(){
        return  ShareDialog.canShow(ShareLinkContent.class);
    }

    private boolean UsarShareDialog_Foto(){ return ShareDialog.canShow ( SharePhotoContent.class ); }

    private void publicarRecetaFacebook(){
        if(!UsarShareDialog_Foto()){
            //Toast.makeText(this,"Se puede publicar",Toast.LENGTH_LONG).show();
            Toast toast = Toast.makeText(   THIS,"Debes estar logeado con una cuenta de facebook, para compartir por esa red social.",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,20,20);
            toast.show();
            return;
        } else if(UsarShareDialog_Foto()){
            new VistaListaRecetas.DownloadImageTask(imgdeReceta).execute(imageFire);
            this.shareDialogFacebook.show(sharePhotoContent);
            Log.d("TAG","share " + shareDialogFacebook.toString());
            return;
        }

    }

    private void compartirReceta(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Receta: "+ nombReFi);
        intent.putExtra(Intent.EXTRA_TEXT, "Ingredientes: "+IngrF2+ "\n Preparación: " + preparacion);
        startActivity(Intent.createChooser(intent,getString(R.string.compartir_receta)));
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalles,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.shareFB){
            publicarRecetaFacebook();
            return true;
        }
        else if (id == R.id.share){
            compartirReceta();
            return true;
        }
        else if(id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
