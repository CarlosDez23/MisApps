package com.example.misapps.ui.carrusel;



import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.misapps.R;

import java.util.ArrayList;


/**
 * Fragment en el que se va a mostrar el carrusel de imágenes
 * El recurso utilizado para la realización de esta parte de la práctica
 * --> https://www.youtube.com/watch?v=GqcFEvBCnIk
 *
 */

public class CarouselFragment extends Fragment {

    private ViewPager viewPager;
    private ArrayList<String> listaPaths = new ArrayList<>();



    public static CarouselFragment newInstance() {
        return new CarouselFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.carousel_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        llamarVistas();
        obtenerImagenesGaleria();
        gestionarAdaptador();

    }

    /**
     * Para llamar a las vistas del Layout
     */
    private void llamarVistas(){
        viewPager = getView().findViewById(R.id.viewPager);

    }

    /**
     * Método que recorre la galería y obtiene los paths de las imágenes. Se utiliza un provider
     * para la URI y se utiliza un contentresolver para consultar por las imagenes en MediaStore.
     * De esta manera podemos construir una lista de paths que pasarle al adaptador
     *
     */
    private void obtenerImagenesGaleria(){
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = getActivity().getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            listaPaths.add(absolutePathOfImage);
        }
    }

    /**
     * Le pasamos a nuestro Layout de tipo viewPager el adaptador para que pueda
     * ir pintando las imágenes
     */
    private void gestionarAdaptador(){
        ImageAdapter adapter = new ImageAdapter(getContext(),listaPaths);
        viewPager.setAdapter(adapter);
    }



}
