package com.master.recetasdelanona;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.master.recetasdelanona.Firebase.CategoriasRecetasFMService;

public class ReceptorFCM extends BroadcastReceiver {
    @Override public void onReceive(Context context ,Intent intent) {
    context.startService(new Intent(context, CategoriasRecetasFMService.class));
    }
}
