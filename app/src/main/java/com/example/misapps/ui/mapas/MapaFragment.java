package com.example.misapps.ui.mapas;


import android.graphics.Color;
import android.icu.text.Transliterator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.location.Location;

import com.example.misapps.R;
import com.example.misapps.ui.mapas.util.GpxParser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MapaFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    //Para las localizaciones
    private FusedLocationProviderClient mPosicion;
    private Location ultimaLocalizacion;
    private LatLng posicionActual;

    //Para pintar el fragment mapa
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;

    private ArrayList<LatLng> listaPosiciones = new ArrayList<>();

    /**
     * Para dibujar la ruta en tiempo real utilizamos la clase PolyLine
     * https://stackoverflow.com/questions/45158159/how-to-draw-polyline-on-google-map-in-android-passing-json-array
     */

    private Polyline lineaRecorrido;

    //Elementos de la interfaz
    private FloatingActionButton fabEmpezarRuta;
    private FloatingActionButton fabMisRutas;


    //Gestión de algunos aspectos de la ruta

    private boolean rutaIniciada = false;

    //Marcadores de inicio y final de ruta
    private Marker marcadorInicio;
    private Marker marcadorFinal;


    public static MapaFragment newInstance() {
        return new MapaFragment();
    }

    public MapaFragment getMapa(){
        return this;
    }

    public GoogleMap getMap() {
        return map;
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

    private void llamarVistas() {
        this.fabEmpezarRuta = getView().findViewById(R.id.fabEmpezarRuta);
        this.fabEmpezarRuta.setOnClickListener(listenerOnClick);
        this.fabMisRutas = getView().findViewById(R.id.misRutas);
        this.fabMisRutas.setOnClickListener(listenerOnClick);

    }

    private View.OnClickListener listenerOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fabEmpezarRuta:
                    if (!rutaIniciada) {
                        Snackbar.make(getView(), "Ruta empezada", Snackbar.LENGTH_LONG).show();
                        ajustesIniciales();
                        iniciarRuta();
                        fabEmpezarRuta.setImageResource(R.drawable.ic_stoproute);
                    } else {
                        finalizarRuta();
                        Snackbar.make(getView(), "Ruta finalizada", Snackbar.LENGTH_LONG).show();
                        fabEmpezarRuta.setImageResource(R.drawable.ic_startroute);
                    }
                    break;
                case R.id.misRutas:
                    FragmentListaRutas listaRutas = new FragmentListaRutas(getMapa());
                    getFragmentManager().beginTransaction().add(R.id.listaRutasGuardadas,listaRutas).addToBackStack(null).commit();

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Iniciamos la visualización del mapa haciendo una transacción entre fragments
     */

    private void iniciarMapa() {
        mPosicion = LocationServices.getFusedLocationProviderClient(getActivity());
        FragmentManager fm = getChildFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.contenedorMapa);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.contenedorMapa, supportMapFragment).commit();
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
        //Así se va a ver el mapa
        gestionarVisualizacionMapa();
        //Obtenemos la posición
        obtenerPosicionActualMapa();
    }

    /**
     * En este método se gestiona cómo se van a visualizar los
     * distintos elementos del mapa
     */
    private void gestionarVisualizacionMapa() {
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setMinZoomPreference(15.0f);
        map.setOnMarkerClickListener(this);
        map.setMyLocationEnabled(true);
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(false);
    }

    /**
     * Método para obtener la posición actual leyéndola desde el GPS. Se lanza una
     * tarea concurrente para obtener la posición.
     */
    private void obtenerPosicionActualMapa() {
        try {

            Task<Location> local = mPosicion.getLastLocation();
            local.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    System.out.println("DENTRO");
                    if (task.isSuccessful()) {
                        ultimaLocalizacion = task.getResult();
                        if (ultimaLocalizacion != null) {
                            posicionActual = new LatLng(ultimaLocalizacion.getLatitude(),
                                    ultimaLocalizacion.getLongitude());
                            situarCamara();
                            //Añadimos la posición actual a la lista de posiciones que conforman la ruta
                            listaPosiciones.add(posicionActual);
                        } else {
                            Snackbar.make(getView(), "No se puede establecer la posición actual",
                                    Snackbar.LENGTH_LONG).show();
                        }

                    } else {
                        Log.d("GPS", "No se encuetra la última posición.");
                        Log.e("GPS", "Exception: %s", task.getException());
                    }
                }
            });

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Para situar la cámara en concordancia con la posición actual
     */
    private void situarCamara() {
        map.moveCamera(CameraUpdateFactory.newLatLng(posicionActual));
    }

    /**
     * Para poner el marcador al inicio de la ruta
     *
     * @param pos
     */
    public void addMarcadorInicio(LatLng pos) {
        marcadorInicio = map.addMarker(new MarkerOptions()
                .position(pos)
                .title("Posición inicial")
                .snippet("Inicio de la ruta")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
    }

    /**
     * Para poner un marcador al final de la ruta
     *
     * @param pos
     */
    public void addMarcadorFinal(LatLng pos) {
        marcadorFinal = map.addMarker(new MarkerOptions()
                .position(pos)
                .title("Fin de la ruta")
                .snippet("Ruta finalizada en este punto")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        );
    }


    /**
     * ======= PARTE DE GESTIÓN DE LA RUTA =======
     */

    /**
     * Antes de comenzar la ruta se debe vaciar la lista de posiciones por si ha habido
     * rutas anteriores, se deben poner las variables bandera a verdadero...etc
     */
    private void ajustesIniciales() {
        listaPosiciones.clear();
        rutaIniciada = true;
        //Quitamos los marcadores que puede haber de rutas anteriores
        if (marcadorInicio != null && marcadorFinal != null) {
            marcadorInicio.remove();
            marcadorFinal.remove();
            Snackbar.make(getView(), "Actualizando Marcadores", Snackbar.LENGTH_LONG).show();
        }
    }

    private void iniciarRuta() {
        addMarcadorInicio(posicionActual);
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask asincrona = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (rutaIniciada) {
                            try {
                                //Obtenemos la posición, que a su vez almacena en la lista la nueva posición
                                obtenerPosicionActualMapa();
                                /**
                                 * Gestión de la línea del recorrido. Nos creamos un objeto
                                 * PolylineOptions, al que vamos añadiendo todos los objetos
                                 * LatLng que están registrados en nuestra lista y finalmente
                                 * se lo pasamos a nuestro objeto PoliLyne para que se muestre
                                 * en el mapa
                                 */
                                PolylineOptions opciones = new PolylineOptions();
                                opciones.color(Color.RED);
                                opciones.width(3);
                                for (int i = 0; i < listaPosiciones.size(); i++) {
                                    opciones.add(listaPosiciones.get(i));
                                }
                                lineaRecorrido = map.addPolyline(opciones);

                            } catch (Exception e) {
                                Log.e("TIMER", "Error: " + e.getMessage());
                            }
                        }
                    }
                });
            }
        };
        //Esperamos 5 segundos para volver a realizar el proceso
        timer.schedule(asincrona, 0, 5000);
    }

    private void finalizarRuta() {
        rutaIniciada = false;
        //Añadimos el marcador final para ver el principio y el final de la ruta
        addMarcadorFinal(listaPosiciones.get(listaPosiciones.size() - 1));
        guardarRuta();
    }

    private void guardarRuta(){
        GpxParser.escribirXML(listaPosiciones, getContext());
        Snackbar.make(getView(),"Ruta guardada",Snackbar.LENGTH_LONG).show();
    }

}
