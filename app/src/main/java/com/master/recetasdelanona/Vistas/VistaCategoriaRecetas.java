package com.master.recetasdelanona.Vistas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.master.recetasdelanona.Adaptadores.AdaptadorRecetas;
import com.master.recetasdelanona.Datos.Receta;
import com.master.recetasdelanona.R;
import com.squareup.picasso.Picasso;

import static com.master.recetasdelanona.Firebase.CategoriasRecetasFirestore.CATEGORIAS;

public class VistaCategoriaRecetas extends AppCompatActivity {

    TextView txtCategoria;
    ImageView imgImagen;
    ActionBar actionBar;
    Toolbar toolbar_categoria;
    private AdaptadorRecetas adaptadorRecetas;
    public static String categoria_receta;
    public static FirebaseFirestore instancia= FirebaseFirestore.getInstance();
    public static CollectionReference  coleccion_cat_recetas= instancia.collection(CATEGORIAS),
     collection_ref_categoria_receta;
    private ProgressDialog progresoCarga;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recetas);
        toolbar_categoria=findViewById(R.id.toolbar_cat);
        setSupportActionBar(toolbar_categoria);
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        txtCategoria= findViewById(R.id.txtCategoria);
        imgImagen= findViewById(R.id.imagen_img);
        progresoCarga = new ProgressDialog(VistaCategoriaRecetas.this);
        progresoCarga.setIndeterminate(false);
        progresoCarga.setMax(100);
        progresoCarga.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progresoCarga.setCancelable(true);
        progresoCarga.setCanceledOnTouchOutside(false);
        progresoCarga.setTitle(getString(R.string.progress_carga));
        progresoCarga.setMessage(getString(R.string.progress_wait));
        progresoCarga.show();

        cargarInfo_Categoria();
        ListadoDeRecetas();
    }

    private void cargarInfo_Categoria(){
        Bundle extra = getIntent().getExtras();
        categoria_receta= extra.getString("categoria");
        if(categoria_receta==null) categoria_receta="";
        Log.d("TAG","categoria elegida " +categoria_receta);
        collection_ref_categoria_receta=coleccion_cat_recetas; //FirebaseFirestore.getInstance().collection("categorias");//.document("");
        collection_ref_categoria_receta.document(categoria_receta).get().addOnCompleteListener(new OnCompleteListener <DocumentSnapshot>() {
            @Override public void onComplete(Task <DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    txtCategoria.setText(getString(R.string.listado) +" "+ task.getResult().get("categoria").toString() );
                    //new VistaCategoriaRecetas.DownloadImageTask(imgImagen).execute(task.getResult().get("imagen").toString());
                    Picasso.get().load(task.getResult().get("imagen").toString()).error(R.drawable.libro_receta).placeholder(R.drawable.plato).into(imgImagen);
                    progresoCarga.dismiss();
                    Log.d("recetas","detalles de categoria "+task.getResult().getId());
                } else {
                    Toast.makeText(getApplicationContext(),"Error al hacer la consulta",Toast.LENGTH_LONG).show();
                }
            }
        });
        /*LAS LINEAS SIGUIENTES CREO LA SUBCOLECCION RECETAS
        RecetaLista recetaLista = new RecetaLista();
        for (int id=0; id<recetaLista.tamaño(); id++){
            coleccion_cat_recetas.document(id_categoria_colec).collection("recetas").add(recetaLista.elemento(id));
        }
        */
        Log.d("TAG","categ Recetas " +  categoria_receta + " collection " + collection_ref_categoria_receta);
    }

    private void ListadoDeRecetas() {
        Query query = coleccion_cat_recetas.document(categoria_receta).collection("recetas")
        .limit(40);
        FirestoreRecyclerOptions <Receta> opciones_de_las_recetas = new FirestoreRecyclerOptions.Builder<Receta>()
                .setQuery(query,Receta.class).build();
        adaptadorRecetas = new AdaptadorRecetas(opciones_de_las_recetas);
        final RecyclerView recyclerView = findViewById(R.id.reciclerViewRecetas);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,1));
        recyclerView.setAdapter(adaptadorRecetas);

        adaptadorRecetas.setOnItemClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                int posicion = recyclerView.getChildAdapterPosition(v);
                Receta las_recetas = adaptadorRecetas.getItem(posicion);
                String doc_categoria = categoria_receta;
                String receta_seleccionadaID = adaptadorRecetas.getSnapshots().getSnapshot(posicion).getId();
                Log.d("TAG"," sub coleccion receta -> "+receta_seleccionadaID+"\n"+las_recetas.toString()+"\n categoria selec "+doc_categoria);
                Context context = getAppContext();
                Intent intent = new Intent(context,VistaListaRecetas.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("documento_receta", receta_seleccionadaID);
                intent.putExtra("categoria",doc_categoria);
                context.startActivity(intent);
                overridePendingTransition(R.anim.alfa_entrada,R.anim.alfa_salida);
            }
        });
    }

    @Override protected void onStart() {
        super.onStart();
        //cargarInfo_Categoria();
        adaptadorRecetas.startListening();
        currentcontext=this;
    }

    @Override protected void onStop() {
        super.onStop();
        adaptadorRecetas.stopListening();
    }

    private static VistaCategoriaRecetas currentcontext;
    public static  VistaCategoriaRecetas getCurrentcontext()
    { return  currentcontext; }

    public static Context getAppContext(){ return  VistaCategoriaRecetas.getCurrentcontext();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        /**/
        getMenuInflater().inflate(R.menu.menu_categoria,menu);
        MenuItem menuItem = menu.findItem(R.id.buscar);
        SearchView searchReceta =(SearchView) MenuItemCompat.getActionView(menuItem);
        searchReceta.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                FirebaseSearchReceta(query);
                adaptadorRecetas.startListening();
                Log.d("TAG","query buscar " +query);
                return false;
            }

            @Override public boolean onQueryTextChange(String newText) {
                //FirebaseSearchReceta(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        /**/
        int id = item.getItemId();
        if(id == R.id.buscar){
            return true;
        }
            return super.onOptionsItemSelected(item);
    }

    //buscar alguna receta
    private void FirebaseSearchReceta(final String buscarTxt){
        Log.d("TAG","firebase search categoria " + categoria_receta + buscarTxt);
        coleccion_cat_recetas.document(categoria_receta).collection("recetas")
                .whereEqualTo("nombre_receta",buscarTxt)
                .get()
                .addOnCompleteListener(new OnCompleteListener <QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task <QuerySnapshot> task) {
                        if(task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                Query query = coleccion_cat_recetas.document(categoria_receta).collection("recetas").whereEqualTo("nombre_receta",buscarTxt);
                                Log.d("TAG","documento consulta search " + documentSnapshot.getId()+" vacio? "+ task.getResult().isEmpty());
                                FirestoreRecyclerOptions <Receta> opciones_de_las_recetas = new FirestoreRecyclerOptions.Builder<Receta>()
                                        .setQuery(query,Receta.class).build();
                                adaptadorRecetas = new AdaptadorRecetas(opciones_de_las_recetas);
                                final RecyclerView recyclerView = findViewById(R.id.reciclerViewRecetas);
                                recyclerView.setLayoutManager(new GridLayoutManager(VistaCategoriaRecetas.this,1));
                                //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,1));
                                recyclerView.setAdapter(adaptadorRecetas);
                                adaptadorRecetas.startListening();
                                adaptadorRecetas.setOnItemClickListener(new View.OnClickListener() {
                                    @Override public void onClick(View v) {
                                        int posicion = recyclerView.getChildAdapterPosition(v);
                                        Receta las_recetas = adaptadorRecetas.getItem(posicion);
                                        String doc_categoria = categoria_receta;
                                        String receta_seleccionadaID = adaptadorRecetas.getSnapshots().getSnapshot(posicion).getId();
                                        Log.d("TAG"," sub coleccion receta -> "+receta_seleccionadaID+"\n"+las_recetas.toString()+"\n categoria selec "+doc_categoria);
                                        Context context = getAppContext();
                                        Intent intent = new Intent(context,VistaListaRecetas.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("documento_receta", receta_seleccionadaID);
                                        intent.putExtra("categoria",doc_categoria);
                                        context.startActivity(intent);
                                        overridePendingTransition(R.anim.alfa_entrada,R.anim.alfa_salida);
                                    }
                                });
                            }
                        }  else {
                            Log.d("TAG","Error consulta " + task.getException());
                            Toast.makeText(VistaCategoriaRecetas.this,"No hubo coincidencias en la búsqueda o error en la consulta",Toast.LENGTH_LONG).show();
                            ListadoDeRecetas();
                            adaptadorRecetas.startListening();
                        }
                    }
                });
    }
}
