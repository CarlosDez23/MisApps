package com.example.misapps.ui.acercade;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.misapps.R;

public class AcercadeFragment extends Fragment {

    private ImageButton imbtnAcercaTwitter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_acerca, container, false);
        return root;
    }

    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        llamarVistas();
        setearListener();
    }

    /**
     * Método para acceder a las vistas de esta actividad
     */
    private void llamarVistas(){
        this.imbtnAcercaTwitter = (ImageButton)getView().findViewById(R.id.imbtnAcercaTwitter);
    }

    /**
     * Método para setear los listener
     */
    private void setearListener(){
        this.imbtnAcercaTwitter.setOnClickListener(listenerImgBtn);
    }

    /**
     * Listener del botón de twitter, que lleva a mi perfil
     */
    private View.OnClickListener listenerImgBtn = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            //Objeto que va a contener la url
            Uri uri = Uri.parse("https://twitter.com/carlos_dez");
            //Intent para que se abra el navegador, le pasamos la url del objeto Uri
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            startActivity(intent);
        }
    };

}