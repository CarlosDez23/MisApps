package com.example.misapps.ui.mapas;


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

public class MapaFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private FusedLocationProviderClient posicion;
    private Location localizacion;
    private LatLng pos;

    //Para pintar el fragment mapa
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;

    public MapaFragment() {
    }

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

    }

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

        //Zoom
        //map.setMinZoomPreference(12.0f);

        //Activamos la localizacion
        map.setMyLocationEnabled(true);


    }
}
