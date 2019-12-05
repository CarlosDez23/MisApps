package com.example.misapps.ui.music;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Clase Storage que es utilizada por el servicio para reproducir las canciones
 * e ir siguiendo una lista de canciones. Genera un archivo XML que utiliza el servicio
 * para saber qué canción tiene que reproducir. El fichero se encuentra en el almacenamiento
 * interno en /data/data/com.example.misapps/shared_prefs/com.example.misapps.ui.music.STORAGE.xml
 */
public class StorageUtil {
    private final String STORAGE = "com.example.misapps.ui.music.STORAGE";
    //private final String STORAGE = "com.valdioveliu.valdio.audioplayer.STORAGE";
    private SharedPreferences preferences;
    private Context context;

    /**
     * Necesita el contexto, por lo que se le pasa en el productor
     * @param context
     */
    public StorageUtil(Context context) {
        this.context = context;
    }

    /**
     * Añade una canción a la "playlist"
     * @param arrayList
     */
    public void storeAudio(ArrayList<Audio> arrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("audioArrayList", json);
        editor.apply();
    }

    /**
     * Carga una canción
     * @return
     */
    public ArrayList<Audio> loadAudio() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("audioArrayList", null);
        Type type = new TypeToken<ArrayList<Audio>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Añade un índice junto a cada canción a la "playlist"
     * @param index
     */
    public void storeAudioIndex(int index) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    /**
     * Coje la "playlist" que hay en el archivo
     * @return
     */
    public int loadAudioIndex() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        //Devuelve -1 si no encuentra datos
        return preferences.getInt("audioIndex", -1);
    }

    /**
     * Para limpiar el fichero
     */
    public void clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
