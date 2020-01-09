package com.example.misapps.ui.mapas.util;


import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.misapps.R;
import com.example.misapps.ui.mapas.MapaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


import java.io.File;
import java.util.ArrayList;

public class RutasAdapter extends RecyclerView.Adapter<RutasAdapter.ViewHolder> {

    private ArrayList<String> listRutas;
    private MapaFragment mapa;

    public RutasAdapter(ArrayList<String> listRutas, MapaFragment mapa) {
        this.listRutas = listRutas;
        this.mapa = mapa;
    }

    @NonNull
    @Override
    public RutasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemListaRuta = layoutInflater.inflate(R.layout.item_ruta,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemListaRuta);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RutasAdapter.ViewHolder holder, int position) {
        final String ruta = listRutas.get(position);
        holder.tvRutaNombre.setText(ruta);
        holder.cardRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fichero = new File(Environment.getExternalStorageDirectory()+"/misApps/gpx/"+ruta);
                ArrayList<LatLng> listRutas = GpxParser.leerFicheroXML(fichero);
                pintarRutaGuardada(listRutas);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listRutas.size();
    }

    private void pintarRutaGuardada(ArrayList<LatLng>listaRuta){
        PolylineOptions opciones = new PolylineOptions();
        opciones.color(Color.RED);
        opciones.width(3);
        for(int i=0;i<listaRuta.size();i++){
            opciones.add(listaRuta.get(i));
        }
        //Limpiamos el mapa y pintamos la ruta guardada
        this.mapa.getMap().clear();
        this.mapa.addMarcadorInicio(listaRuta.get(0));
        this.mapa.addMarcadorFinal(listaRuta.get(listaRuta.size()-1));
        Polyline linea = this.mapa.getMap().addPolyline(opciones);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvRutaNombre;
        public CardView cardRuta;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvRutaNombre = itemView.findViewById(R.id.tvRutaNombre);
            this.cardRuta = itemView.findViewById(R.id.cardRuta);
        }
    }
}
