package com.example.misapps.ui.music;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.misapps.Manifest;
import com.example.misapps.R;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Build.VERSION.SDK_INT;

/**
 * El fragment donde se muestran las canciones. Se presentan en un recycler, de forma que cuando
 * se dispare el evento del listener del item del recycler, se llama al servicio para que repro-
 * duzca la canción
 */
public class MusicFragment extends Fragment {


    private MediaPlayerService player;
    boolean serviceBound = false;
    ArrayList<Audio> listCanciones;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.misapps.ui.music.MediaPlayerService";


    public static MusicFragment newInstance() {
        return new MusicFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.music_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Llamada a pintar el recycler con sus respectivas canciones
        loadAudioList();
    }

    /**
     * Al guardar la instancia, guardamos el estado del servicio (boolean)
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Para bindear el servicio en caso de que no esté conectado
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    /**
     * Para empezar a reproducir el audio. Le pasamos también al storage la lista y el indice
     * Comprobamos primero que el servicio esté esté bindeado
     * @param audioIndex
     */
    public void playAudio(int audioIndex) {
        if (!serviceBound) {
            StorageUtil storage = new StorageUtil(getContext());
            storage.storeAudio(listCanciones);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(getActivity(), MediaPlayerService.class);
            getActivity().startService(playerIntent);
            getActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        } else {

            StorageUtil storage = new StorageUtil(getContext());
            storage.storeAudioIndex(audioIndex);
            /**
             * Le indicamos al broadcast que queremos reproducir una canción nueva
             * para que no se reproduzcan dos canciones a la vez
             */
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            getActivity().sendBroadcast(broadcastIntent);
        }
    }


    /**
     * Este método recupera todas las pistas de música de nuestro teléfono y guarda sus
     * respectivos paths en un ArrayList
     */
    private void loadAudio() {
        ContentResolver contentResolver = getContext().getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            listCanciones = new ArrayList<>();
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                Bitmap albumArt = obtenerImagenAlbum(Long.parseLong(id));

                listCanciones.add(new Audio(data, title, album, artist, albumArt));
            }
        }
        cursor.close();
    }
    /**
     * Método para convertir el id que nos devuelve el cursor con el albumart de la canción
     * en Bitmap
     * @param album_id
     * @return
     */
    private Bitmap obtenerImagenAlbum(Long album_id){
        Bitmap bitmap = null;
        try{
            final Uri imagenAlbum = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(imagenAlbum,album_id);
            ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(uri,"r");
            if(pfd != null){
                FileDescriptor fd = pfd.getFileDescriptor();
                bitmap = BitmapFactory.decodeFileDescriptor(fd);
            }

        }catch (Exception e){

        }
        return bitmap;
    }



    /**
     * Llamamos al recyclerView y a cargar las canciones
     */
    private void loadAudioList() {
        loadAudio();
        initRecyclerView();
    }

    /**
     * Inicializamos el recyclerView y le añadimos su Listener para reproducir cada canción.Obvia-
     * mente se controla que la lista de canciones no esté vacía
     */

    private void initRecyclerView() {
        if (listCanciones != null && listCanciones.size() > 0) {
            RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerMusica);
            AdaptadorMusica adapter = new AdaptadorMusica(listCanciones, getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addOnItemTouchListener(new CustomTouchListener(getContext(), new onItemClickListener() {
                @Override
                public void onClick(View view, int index) {
                    /**
                     * Hacemos una transacción al fragment del mp3, para una vista más detallada
                     * de la reproducción de la canción
                     */
                    playAudio(index);
                    FragmentMP3 reproductor = new FragmentMP3(listCanciones.get(index), listCanciones, index, player);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, reproductor);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }));
        }
    }


}
