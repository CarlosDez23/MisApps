package com.example.misapps.ui.music;



import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.session.MediaSessionManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.IBinder;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.example.misapps.R;
import java.util.ArrayList;

public class FragmentMP3 extends Fragment {

    //Gestión de Media
    private Audio cancion;
    ArrayList<Audio> listCanciones;
    boolean serviceBound = true;
    private MediaPlayerService player;
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.example.misapps.ui.music.MediaPlayerService";
    private int posicion;
    private boolean reproduciendo = true;



    //Gestión de vistas
    private ImageButton ibPlay;
    private ImageButton ibAnterior;
    private ImageButton ibSiguiente;
    private ImageView ivCaratula;

    /**
     * En el constructor debemos pasarle el objeto canción, ,su posiciñon en la lista, el servicio
     * @param cancion
     * @param listCanciones
     * @param posicion
     * @param player
     */
    public FragmentMP3(Audio cancion, ArrayList<Audio>listCanciones, int posicion, MediaPlayerService player){
        this.cancion = cancion;
        this.listCanciones = listCanciones;
        this.posicion = posicion;
        this.player = new MediaPlayerService();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mp3_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        llamarVistas();
    }

    /**
     * Método para llamar a las vistas
     */
    private void llamarVistas(){
        ibPlay = getView().findViewById(R.id.ibPlay);
        ibPlay.setImageResource(android.R.drawable.ic_media_pause);
        ibAnterior = getView().findViewById(R.id.ibCancionAnterior);
        ibSiguiente = getView().findViewById(R.id.ibCancionSiguiente);
        ivCaratula = getView().findViewById(R.id.ivCaratula);
        gestionAlbumArt(cancion);


        //Gestión de listeners
        ibSiguiente.setOnClickListener(listenerOnClick);
        ibAnterior.setOnClickListener(listenerOnClick);
        ibPlay.setOnClickListener(listenerOnClick);
    }

    /**
     * Método para gestionar la imagen del album. Se hace un get del Bitmap que tiene
     * el objeto Audio como atributo. Si el mismo es igual a nulo, se pone una imagen
     * por defecto. En caso contrario se aprovecha la imagen para pintarla en el fragment
     * @param cancion
     */
    private void gestionAlbumArt(Audio cancion){
        Bitmap caratula = cancion.getAlbum_id();
        if (caratula == null){
            ivCaratula.setImageResource(R.mipmap.ic_reproduciendo_foreground);
        }else{
            ivCaratula.setImageBitmap(cancion.getAlbum_id());
        }
    }


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


    private void playAudio(int audioIndex) {
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
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            getActivity().sendBroadcast(broadcastIntent);
        }
    }


    /**
     * Gestión de los botones de el fragment de reproducción
     */
    private View.OnClickListener listenerOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()){
                /**
                 * Para avanzar a la canción siguiente. Hay que controlar que si llega
                 *  a la última posición vuelva al principio de la lista
                 */
                case R.id.ibCancionSiguiente:
                    if(posicion == listCanciones.size() - 1){
                        posicion = 0;
                    }else{
                        posicion++;
                    }
                    //Igualmente, se cambia la caratula de la canción
                    gestionAlbumArt(listCanciones.get(posicion));
                    playAudio(posicion);

                    break;

                /**
                 * Para retroceder a la canción anterior. Al contrario que antes, hay
                 * que controlar que si llega a la primera canción, vaya a la última
                 * posición de la lista
                 */
                case R.id.ibCancionAnterior:

                    if (posicion == 0){
                        posicion = listCanciones.size()-1;
                    }else{
                        posicion--;
                    }
                    //Igualmente, se cambia la caratula de la canción
                    gestionAlbumArt(listCanciones.get(posicion));
                    playAudio(posicion);
                    break;

                /**
                 * Para pausar/continuar. En este caso utilizo una variable bandera, sobretodo
                 * para cambiar el aspecto del botón, ya que el propio mediaplayer se encarga
                 * de asegurarse de si se está reproduciendo algo o no
                 */
                case R.id.ibPlay:

                    if (reproduciendo){
                        player.pauseMedia();
                        reproduciendo = false;
                        ibPlay.setImageResource(android.R.drawable.ic_media_play);
                    }else{
                        player.resumeMedia();
                        ibPlay.setImageResource(android.R.drawable.ic_media_pause);
                        reproduciendo = true;
                    }
                    break;

                default:
                    break;
            }
        }
    };
}
