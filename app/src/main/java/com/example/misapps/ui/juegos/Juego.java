package com.example.misapps.ui.juegos;

public class Juego {

    private int idJuego;
    private String Nombre;
    private String plataforma;
    private float precio;
    private String img;
    private String fechaLanzamiento;

    public Juego(int idJuego) {
        this.idJuego = idJuego;
    }

    public Juego(int idJuego, String nombre, String plataforma, float precio, String img, String fechaLanzamiento) {
        this.idJuego = idJuego;
        Nombre = nombre;
        this.plataforma = plataforma;
        this.precio = precio;
        this.img = img;
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public int getIdJuego() {
        return idJuego;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(String fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    @Override
    public String toString() {
        return "Juego{" +
                "idJuego=" + idJuego +
                ", Nombre='" + Nombre + '\'' +
                ", plataforma='" + plataforma + '\'' +
                ", precio=" + precio +
                ", img='" + img + '\'' +
                ", fechaLanzamiento='" + fechaLanzamiento + '\'' +
                '}';
    }
}
