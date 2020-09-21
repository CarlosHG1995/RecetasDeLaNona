package com.master.recetasdelanona.Firebase;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.master.recetasdelanona.Vistas.MenuActivity;
import com.master.recetasdelanona.R;


public class LoginFirebaseActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener{

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final int RC_GOOGLE_SIGN_IN = 123;
    private ViewGroup contenedor;
    private LoginButton btnFacebook;
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);
        contenedor = findViewById(R.id.contenedor);
       //contenedor.setBackgroundResource(R.color.primary_light);
        //SESION CON GOOGLE
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder
                ( GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        // SESION FIREBASE FACEBOOK
        btnFacebook = findViewById(R.id.facebook);
        //btnFacebook.setReadPermissions("email", "public_profile");
        this.callbackManager = CallbackManager.Factory.create();

        btnFacebook.registerCallback(callbackManager,new FacebookCallback <LoginResult>(){
            @Override public void onSuccess(LoginResult loginResult) {
                facebookAuth(loginResult.getAccessToken());
            }
            @Override public void onCancel() {
                mensaje(getString(R.string.facebook_cancel));
            }
            @Override public void onError(FacebookException error) { mensaje(error.getLocalizedMessage()); }
        });

        this.verificaSiUsuarioValidado();
        //permisos
        String[] PERMISOS = {
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.GET_ACCOUNTS};
        ActivityCompat.requestPermissions(this, PERMISOS,1);
    }

    private void mensaje(String mensaje) {
        Snackbar.make(contenedor,mensaje,Snackbar.LENGTH_LONG).show();
    }

    public void autentificarGoogle(View v) {
        if(!this.hayRed()){
            mensaje(this.getString(R.string.sin_conexion));//String.valueOf(R.string.sin_conexion));
        } else {
        Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(i, RC_GOOGLE_SIGN_IN);}
    }

    @Override public void onConnectionFailed( ConnectionResult connectionResult) {
        mensaje(this.getString(R.string.error_conneccion));//(String.valueOf(R.string.error_conneccion));
    }

    @Override protected void onActivityResult(int requestCode ,int resultCode , Intent data) {
        super.onActivityResult(requestCode ,resultCode ,data);
        if(requestCode == RC_GOOGLE_SIGN_IN){
            if(resultCode == RESULT_OK){
                GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if(googleSignInResult.isSuccess()){
                    googleAuth(googleSignInResult.getSignInAccount());
                } else {
                    mensaje(this.getString(R.string.error_conneccion));//(String.valueOf(R.string.error_conneccion));
                }
            }
        }
        else if(requestCode == btnFacebook.getRequestCode()){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // AUTENTICACION FACEBOOK
    private void facebookAuth(AccessToken accessToken) {
        final AuthCredential credential = FacebookAuthProvider.getCredential(
                accessToken.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override public void onComplete(@NonNull Task<AuthResult> task)
                    { if (!task.isSuccessful())
                    { if (task.getException() instanceof FirebaseAuthUserCollisionException)
                    { LoginManager.getInstance().logOut(); }
                        mensaje(task.getException().getLocalizedMessage()); }
                    else { verificaSiUsuarioValidado(); } } });

    }

    // AUTENTICACION GOOGLE
    private void googleAuth(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        auth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener <AuthResult>()
                    { @Override
                    public void onComplete(
                            @NonNull Task <AuthResult> task) {
                        if (!task.isSuccessful()) {
                            mensaje(task.getException().getLocalizedMessage()); }
                        else{ verificaSiUsuarioValidado();
                        }
                    } });
    }

    private void verificaSiUsuarioValidado() {
        AccessToken accessToken = this.obtenerAccessToken();
        if( auth.getCurrentUser() != null){
            UsuariosFirebase.guardarUsuario(auth.getCurrentUser());
            Intent i = new Intent(this, MenuActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                       Intent.FLAG_ACTIVITY_NEW_TASK|
                       Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            Toast.makeText(this ,this.getString(R.string.bienvenido) +" " +auth.getCurrentUser().getDisplayName() ,Toast.LENGTH_SHORT).show();
            Log.d("verifica user validado","--"+ auth.getCurrentUser());
         } else if(accessToken == null){
            mensaje(this.getString(R.string.login));
        }
    }

    private boolean hayRed ( ) {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService
                (Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo ( );
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected ( );
    }

    private AccessToken obtenerAccessToken(){
        return AccessToken.getCurrentAccessToken();
    }

    @Override public void onRequestPermissionsResult(int requestCode ,String[] permissions , int[] grantResults) {
    switch(requestCode){
        case 1:{
        if(!(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)){
            mensaje("Denegación de algún permiso");
        }    return;
        }
        }
    }

    //mostrar en un alert dialog las notificaciones desde firebsae
    @Override protected void onResume() {
        super.onResume();
    }

    @Override protected void onStart() {
        super.onStart();
        current = this;
    }

    private static LoginFirebaseActivity current;
    public static  LoginFirebaseActivity getCurrentContext(){
        return current;
    }

    public static Context getAppContext(){
        return LoginFirebaseActivity.getCurrentContext();
    }
}
