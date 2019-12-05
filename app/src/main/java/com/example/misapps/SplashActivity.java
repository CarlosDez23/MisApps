package com.example.misapps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Clase para gestionar el splash
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Ocultamos la barra superior
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_splash);

        /**
         * Creamos un hilo que se encarga de ocultar el splash pasados 3 segundos y hace un
         * intent dirigido al main activity
         */
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                SplashActivity.this.finish();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, 3000);
    }

    /**
     * Este método, al igual que Paquirrín, no hace nada.
     */
    private void nada() {}
}
