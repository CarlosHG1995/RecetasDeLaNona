package com.master.recetasdelanona;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Dialogo extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(getIntent().hasExtra("mensaje")){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(this.getString(R.string.mensaje));
            alertDialog.setIcon(R.mipmap.libro_receta_round);
            //alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.setMessage(extras.getString("mensaje"));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL ,this.getString(R.string.CERRAR)
                    ,new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog ,int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
            extras.remove("mensaje");
        }
    }
}
