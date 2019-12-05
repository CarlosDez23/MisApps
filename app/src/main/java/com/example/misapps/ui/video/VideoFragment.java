package com.example.misapps.ui.video;


import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.misapps.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;

public class VideoFragment extends Fragment {

    //ArrayList en el que rescataremos los vídeos
    private ArrayList<Video> listVideos = new ArrayList<>();
    private FloatingActionButton fabYoutube;

    public static VideoFragment newInstance() {
        return new VideoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.video_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.fabYoutube = getView().findViewById(R.id.fabYoutube);
        rescatarVideos();
        initRecyclerView();
        gestionFab();

    }

    /**
     * Método para sacar los vídeos que tiene nuestro dispositivo. Como siempre
     * utilizamos un content resolver filtrando por el video y con el cursor vamos recorriendo
     * uno a uno y añadiéndolo a nuestra lista de vídeos
     */
    private void rescatarVideos() {

        Uri uri;
        Cursor cursor;
        int data;
        int thum;
        String path = null;
        Video aux;

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        //Contenido que queremos sacar
        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Media.TITLE};
        //Filtro de ordenamiento
        String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        //Cursor para recorrer los ficheros
        cursor = getContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
        data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);


        /**
         * Recorremos el cursor construyendo nuestros objetos Video y
         * añadiéndolos a la lista
         */
        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));

            /**
             * Para pillar la preview del video, utilizamos este método
             * Fuente --> StackOverFlow
             */
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize = 1;
            ContentResolver img= getContext().getContentResolver();
            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(img, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);
            //------------------------------------------------------------------------------------//

            path = cursor.getString(data);
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            aux = new Video(path, bitmap, title,false);
            listVideos.add(aux);
        }


    }

    /**
     * Iniciamos nuestro recyclerView
     */
    private void initRecyclerView() {
        VideoAdapter adapter = new VideoAdapter(listVideos, getContext(), getFragmentManager());
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerVideos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
    }

    /**
     * Hacemos una transacción con el fragment que mostrará el vídeo de YouTube
     * AVISO IMPORTANTE --> Actualmente, Google aún no ha implementado la API de
     * YouTube en AndroidX. Por lo que, aunque tengamos nuestra APIKey, YouTube
     * bloqueará nuestra aplicación. No se puede sacar contenido ni en un videoview
     * ni en un webview
     */
    private void gestionFab(){
        fabYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoFromWebFragment youtube = new VideoFromWebFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transacion = fm.beginTransaction();
                transacion.replace(R.id.nav_host_fragment,youtube);
                transacion.addToBackStack(null);
                transacion.commit();

            }
        });
    }

}


