package com.example.misapps.ui.video;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.misapps.MainActivity;
import com.example.misapps.R;

public class VisualizarVideoFragment extends Fragment {


    String pathVideo;
    private VideoView reproductor;


    public VisualizarVideoFragment(String pathVideo) {
        this.pathVideo = pathVideo;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.visualizar_video_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().hide();
        /**
         * Para poner en pantalla completa la reproducción del vídeo
         */
        ((MainActivity) getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        llamarVistas();
        reproducirVideo();

    }

    /**
     * Para llamar a las vistas del layout
     */
    private void llamarVistas() {
        reproductor = getView().findViewById(R.id.videoView);
    }

    /**
     * Método para reproducir el vídeo. Cojemos el path del vídeo, lo parseamos a uri y
     * al videoview se lo pasamos para que lo reproduzca. Igualmente llamamos al MediaController
     * para que se nos muestren unos controles de pausar,reproducir y avanzar. Igualmente
     * le decimos start() para que nada más que se entre al fragment se reproduzca el vídeo
     */
    private void reproducirVideo() {
        Uri uri = Uri.parse(pathVideo);
        reproductor.setVideoURI(uri);
        MediaController mediaController = new MediaController(getContext());
        reproductor.setMediaController(mediaController);
        mediaController.setAnchorView(reproductor);
        reproductor.start();
    }

    /**
     * Volvemos a mostrar la barra de notificación y la Toolbar al salir
     * del fragment. Igualmente desactivamos el modo pantalla completa, ya
     * que queremos que nuestra aplicación se muestra con normalidad
     */

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).getSupportActionBar().show();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }
}
