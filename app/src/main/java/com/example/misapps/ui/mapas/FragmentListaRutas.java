package com.example.misapps.ui.mapas;



import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.misapps.R;
import com.example.misapps.ui.mapas.util.RutasAdapter;

import java.io.File;
import java.util.ArrayList;

public class FragmentListaRutas extends Fragment {

    private ArrayList<String>listaRutas = new ArrayList<>();

    private MapaFragment mapaFragment;
    public FragmentListaRutas(MapaFragment mapaFragment){
        this.mapaFragment = mapaFragment;

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista_rutas_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cargarLista();
        initRecyclerView();

    }

    private void initRecyclerView(){
        RutasAdapter adapter = new RutasAdapter(listaRutas,mapaFragment);
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerRutas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);

    }

    private void cargarLista(){
        String ruta = Environment.getExternalStorageDirectory().toString()+"/misApps/gpx";
        System.out.println(ruta);
        File fichero = new File(ruta);
        File listaFicheros[] = fichero.listFiles();
        for (int i=0;i<listaFicheros.length;i++){
            listaRutas.add(listaFicheros[i].getName());
        }
    }

}
