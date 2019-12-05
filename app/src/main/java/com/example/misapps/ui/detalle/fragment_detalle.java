package com.example.misapps.ui.detalle;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.misapps.MainActivity;
import com.example.misapps.R;
import com.example.misapps.ui.noticias.Noticia;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

public class fragment_detalle extends Fragment {

    private Noticia noticia;
    private TextView tvDetalleTitulo;
    private WebView wvDetalleContenido;
    private ImageView ivDetalleImagen;
    private FloatingActionButton fabDetallesIr;
    public static Noticia noticiaMain;


    /**
     * A este constructor le pasamos la noticia que queremos mostrar
     * @param noticia
     */
    public fragment_detalle(Noticia noticia){
        this.noticia = noticia;
        noticiaMain = noticia;
    }

    public fragment_detalle() {

    }


    public static fragment_detalle newInstance() {
        return new fragment_detalle();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detalle_fragment, container, false);

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        llamarVistas();
        procesarNoticia();
        //Para mostrar el item de compartir
        MainActivity.menuArriba.findItem(R.id.itemCompartir).setVisible(true);
        MainActivity.menuArriba.findItem(R.id.itemCompartirJuego).setVisible(false);
        //Para ocultar el acceso al menú lateral
        //Se hace un getActivity haciendole un casting a MainActivity
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

    }

    /**
     * Método para llamar a las vistas
     */
    private void llamarVistas(){
        tvDetalleTitulo = (TextView)getView().findViewById(R.id.tvDetallesTitular);
        wvDetalleContenido=(WebView)getView().findViewById(R.id.wvDetallesContenido);
        ivDetalleImagen=(ImageView) getView().findViewById(R.id.ivDetallesImagen);
        fabDetallesIr =(FloatingActionButton)getView().findViewById(R.id.fabDetallesIr);
        fabDetallesIr.setOnClickListener(fabListener);
    }

    /**
     * Método para mostrar la noticia
     */
    private void procesarNoticia(){
        tvDetalleTitulo.setText(this.noticia.getTitulo());
        wvDetalleContenido.loadData(this.noticia.getContenido(),"text/html",null);
        Picasso.get().load(this.noticia.getImagen()).into(this.ivDetalleImagen);

    }

    /**
     * Listener para el fab
     */
    private View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            abrirEnNavegador();
        }
    };

    /**
     * Intent para abrir la url de la noticia en el navegador
     */
    private void abrirEnNavegador(){
        Uri uri = Uri.parse(this.noticia.getLink());
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }

}
