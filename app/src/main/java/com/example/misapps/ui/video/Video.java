package com.example.misapps.ui.video;

import android.graphics.Bitmap;

/**
 * Clase vídeo, nos servirá para poder rescatar los vídeos del almacenamiento
 * del dispositivo y poder trabajar con ellos en un ArrayList de manera que
 * ya tendremos el componente principal para pintar nuestro RecyclerView
 */
public class Video {
    private String ruta;
    private Bitmap thumbnail;
    private String titulo;
    private boolean selected;

    public Video() {
    }

    public Video(String ruta, Bitmap thumbnail,String titulo, boolean selected) {
        this.ruta = ruta;
        this.thumbnail = thumbnail;
        this.titulo = titulo;
        this.selected = selected;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
