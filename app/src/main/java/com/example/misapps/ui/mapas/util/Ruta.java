package com.example.misapps.ui.mapas.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Ruta {

    private String nombre;
    private ArrayList<LatLng>listaPosiciones;

    public Ruta() {
    }

    public Ruta(String nombre, ArrayList<LatLng> listaPosiciones) {
        this.nombre = nombre;
        this.listaPosiciones = listaPosiciones;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<LatLng> getListaPosiciones() {
        return listaPosiciones;
    }

    public void setListaPosiciones(ArrayList<LatLng> listaPosiciones) {
        this.listaPosiciones = listaPosiciones;
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "nombre='" + nombre + '\'' +
                ", listaPosiciones=" + listaPosiciones +
                '}';
    }
}
