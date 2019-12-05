package com.example.misapps.ui.music;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.misapps.MainActivity;
import com.example.misapps.R;

import java.io.IOException;
import java.util.ArrayList;


public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {


    private final IBinder iBinder = new LocalBinder();
    private static MediaPlayer mediaPlayer;
    //Ruta a las canciones
    private String mediaFile;
    //Para pausar y reanudar el MediaPlayer
    private int resumePosition;
    private AudioManager audioManager;

    //Para gestionar las llamadas por teléfono
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    //Las pistas que recogemos de nuestro dispositivo
    private ArrayList<Audio> audioList;
    private int audioIndex = -1;
    //Representa a la canción en reproducción
    private Audio activeAudio;


    public static final String ACTION_PLAY = "com.misapps.ui.music.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.misapps.ui.music..ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.misapps.ui.music.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.misapps.ui.music.ACTION_NEXT";
    public static final String ACTION_STOP = "com.misapps.ui.music.ACTION_STOP";

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    //ID para la notificación
    private static final int NOTIFICATION_ID = 101;

    public MediaSessionManager getMediaSessionManager() {
        return mediaSessionManager;
    }

    public MediaSessionCompat getMediaSession() {
        return mediaSession;
    }

    public MediaControllerCompat.TransportControls getTransportControls() {
        return transportControls;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        callStateListener();
        registerBecomingNoisyReceiver();
        register_playNewAudio();
    }

    /**
     * Para gestionar si nuestro servicio recibe el foco, lo pierde etc
     *
     * @param focusState
     */
    @Override
    public void onAudioFocusChange(int focusState) {
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // Gana el foco por lo que reproduce la musica
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lo pierde, por lo que hace un release del media player
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                /*
                Lo pierde por un momento
                 */
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                /*
                Lo pierde por un momento, como cuando llega una notificación
                por lo que baja el volumen por um momento
                 */
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }

    }

    /**
     * Para saber si tiene el foco la aplicación
     *
     * @return
     */
    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(getApplicationContext().AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Pasa por aquí si ha ganado el foco
            return true;
        }

        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    /**
     * Cuando completa la reproducción de una canción. Pasamos a la siguiente
     *
     * @param mp
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        //Para que pase a la siguiente canción
        skipToNext();
    }

    /**
     * Para gestionar errores durante la reproducción del audio. Se produce un log
     * para realizar las pruebas. No influye en la ejecución de la aplicación.
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        //Cuando ha ocurrido un error
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    /**
     * Como el servicio se encuentra en una caja de arena distinta a nuestra aplicación, se bindea
     * mediante esta clase, de forma que se agencia o se "llama" a nuestra aplicación
     */

    public class LocalBinder extends Binder {

        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    /**
     * Para iniciar el servicio
     */
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(activeAudio.getData());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    /**
     * Para gestionar los distintos eventos en el reproductor --> reproducir, parar, pausar, volver
     * a reproducir
     */
    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    public void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    /**
     * Cuando nuestra actividad le pide al servicio que comience se lanza este método
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();

            if (audioIndex != -1 && audioIndex < audioList.size()) {
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }


        if (requestAudioFocus() == false) {

            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }

        handleIncomingActions(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * En el on destroy, cerramos la notificación y también hacemos el release del
     * mediaPlayer
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();

        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        removeNotification();


        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);

        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
    }

    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseMedia();
            buildNotification(PlaybackStatus.PAUSED);
        }
    };


    /**
     * Este fragmento sirve para gestionar cuando se conectan unos auriculares al
     * dispositivo móvil
     */
    private void registerBecomingNoisyReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    /**
     * Gestión de llamadas telefónicas.Obviamente el servicio de música se debe interrumpir
     */

    private void callStateListener() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * Para que el propio servicio reproducta otra canción cuando otra está iniciada
     * y no se produzca el fallo de que se reproduzcan varias canciones a la vez
     */
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            audioIndex = new StorageUtil(getApplicationContext()).loadAudioIndex();
            if (audioIndex != -1 && audioIndex < audioList.size()) {
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }

            stopMedia();
            mediaPlayer.reset();
            initMediaPlayer();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
        }
    };


    private void register_playNewAudio() {

        IntentFilter filter = new IntentFilter(MusicFragment.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    /**
     * Sirve para gestionar el mediaSessionManager. Esto simplemente son los controles que
     * se pintan en la notificación para poder gestionar el servicio desde la misma (reproducir,
     * pausar,avanzar a la siguiente...etc)
     *
     * @throws RemoteException
     */
    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //Ya está iniciado

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        updateMetaData();


        /**
         * Para gestionar los controles del mediaplayer que se encuentran en la notificación
         * Lo que se hace es llamar a los métodos del servicio que pausan, reproducen...etc
         * las canciones
         */
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    /**
     * Para que la notificación saque los datos de la canción
     */
    private void updateMetaData() {
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.fondo);
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
                .build());
    }

    /**
     * Avanzar a la siguiente cacnción
     */
    private void skipToNext() {

        if (audioIndex == audioList.size() - 1) {

            audioIndex = 0;
            activeAudio = audioList.get(audioIndex);
        } else {
            activeAudio = audioList.get(++audioIndex);
        }

        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);

        stopMedia();
        mediaPlayer.reset();
        initMediaPlayer();
    }

    /**
     * Retroceder a la anterior canción
     */
    private void skipToPrevious() {

        if (audioIndex == 0) {
            audioIndex = audioList.size() - 1;
            activeAudio = audioList.get(audioIndex);
        } else {
            activeAudio = audioList.get(--audioIndex);
        }


        new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);
        stopMedia();
        mediaPlayer.reset();
        initMediaPlayer();
    }

    /**
     * Para construir una notificación.
     *
     * @param playbackStatus
     */

    private void buildNotification(PlaybackStatus playbackStatus) {

        int notificationAction = android.R.drawable.ic_media_pause;
        PendingIntent play_pauseAction = null;
        initChannels(getApplicationContext());


        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;

            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;

            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.otrofondo);

        // Creamos una notificación
        NotificationCompat.Builder notificationBuilder;

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext(), "default")
                .setShowWhen(false)

                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()

                        .setMediaSession(mediaSession.getSessionToken())

                        .setShowActionsInCompactView(0, 1, 2))

                .setColor(getResources().getColor(R.color.colorPrimary))

                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)

                .setContentText(activeAudio.getArtist())
                .setContentTitle(activeAudio.getAlbum())
                .setContentInfo(activeAudio.getTitle())
                .setContentIntent(resultPendingIntent)
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2));

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    /**
     * Para eliminar la notificación. Cuando se quita, se para el servicio
     */
    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        stopMedia();
    }

    /**
     * Identificica el control pulsado y se lo pasa al método de handleIncomingActions
     * para poder gestionar correctamente el mediaplayer en función del botón pulsado
     *
     * @param actionNumber
     * @return
     */
    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MediaPlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pausa
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                // Pasar a la siguiente canción
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                // Ir a la canción anterior
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    /**
     * Eventos sobre los controles de la notificación
     *
     * @param playbackAction
     */
    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }

    /**
     * Método para iniciar el canal de la notificación
     *
     * @param context
     */
    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }
}