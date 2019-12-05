package com.example.misapps.ui.noticias;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Clase noticia
 */
public class Noticia implements Serializable {

    private String titulo;
    private String link;
    private String descripcion;
    private String contenido;
    private String fecha;
    private String imagen;

    public Noticia() {
    }

    public Noticia(String titulo, String link, String descripcion, String contenido, String fecha,
                   String imagen) {
        this.titulo = titulo;
        this.link = link;
        this.descripcion = descripcion;
        this.contenido = contenido;
        this.fecha = fecha;
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "Noticia{" + "titulo=" + titulo + ", link=" + link + ", descripcion=" + descripcion
                + ", contenido=" + contenido + ", fecha=" + fecha + ", imagen=" + imagen + '}';
    }
}
