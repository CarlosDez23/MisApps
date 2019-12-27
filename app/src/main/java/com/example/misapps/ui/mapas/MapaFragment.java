package com.example.misapps.ui.mapas;


import android.icu.text.Transliterator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.location.Location;

import com.example.misapps.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MapaFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private FusedLocationProviderClient posicion;
    private Location localizacion;
    private LatLng pos;

    //Para pintar el fragment mapa
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;

    //Lista para gestionar el histórico de posiciones en una ruta
    private ArrayList<Location> listaRuta = new ArrayList<>();


    //Elementos de la interfaz
    private FloatingActionButton fabEmpezarRuta;



    public static MapaFragment newInstance() {
        return new MapaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mapa_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniciarMapa();
        llamarVistas();

    }

    private void llamarVistas(){
        this.fabEmpezarRuta = getView().findViewById(R.id.fabEmpezarRuta);
        this.fabEmpezarRuta.setOnClickListener(listenerOnClick);



    }

    private View.OnClickListener listenerOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.fabEmpezarRuta:
                    Snackbar.make(getView(),"Ruta empezada", Snackbar.LENGTH_LONG).show();
                    iniciarRuta();
                    break;
                default:
                    break;

            }

        }
    };

    private void iniciarRuta(){
        listaRuta.clear();
    }

    /**
     * PARTE DE GESTIÓN DEL MAPA Y SUS EVENTOS
     */

    private void iniciarMapa(){
        posicion = LocationServices.getFusedLocationProviderClient(getActivity());
        FragmentManager fm = getChildFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.contenedorMapa);
        if (supportMapFragment == null){
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.contenedorMapa,supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //Para gestionar los gestos
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(false);

        //Zoom
        //map.setMinZoomPreference(12.0f);

        //Activamos la localizacion
        map.setMyLocationEnabled(true);


    }
}
