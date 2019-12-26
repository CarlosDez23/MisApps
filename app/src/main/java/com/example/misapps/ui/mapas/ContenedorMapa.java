package com.example.misapps.ui.mapas;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.misapps.R;

public class ContenedorMapa extends Fragment {

    private ContenedorMapaViewModel mViewModel;

    public static ContenedorMapa newInstance() {
        return new ContenedorMapa();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contenedor_mapa_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ContenedorMapaViewModel.class);
        // TODO: Use the ViewModel
    }

}
