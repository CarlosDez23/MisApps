package com.example.misapps.ui.carrusel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.misapps.R;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Esta clase extiende de PagerAdapter, que es muy parecido a nuestro adaptador
 * del recyclerview pero específico para hacer ese efecto de desplazamiento
 */
public class ImageAdapter extends PagerAdapter {

    private Context mContex;
    private LayoutInflater inflater;
    private ArrayList<String> listaPath;

    /**
     * Le pasamos como siempre en el constructor la lista que vamos a pintar
     * En este caso es una lista de los paths de las imágenes en la galería.
     * También le pasamos el contexto porque lo necesitamos
     * @param context
     * @param listaPath
     */
    public ImageAdapter(Context context, ArrayList<String> listaPath){
        this.mContex = context;
        this.listaPath = listaPath;
    }

    /**
     * Como en el getItemCount, devolvemos el tamaño de la lista
     * @return La longitud de la lista
     */
    @Override
    public int getCount() {
        return this.listaPath.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * Este método es el homólogo del onBindViewHolder. Se utiliza para ir cogiendo el
     * elemento en la posición de la lista. En este caso le pasamos el container que
     * luego castearemos a un viewpager, que es donde se va a pintar la imagen.
     * Por otro lado el procedimiento para pintar la imagen es crear un File
     * con el path del elemento, y se lo pasamos a un bitmap factory para que haga
     * un decodeStream. De esta manera obtenemos un bitmap que ya podemos pintar en nuestro
     * contendor ViewPager
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        inflater = (LayoutInflater) mContex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_layout,null);
        ImageView imageView = (ImageView) view.findViewById(R.id.ivCarrusel);

        try{
            Bitmap bitmap = null;
            File fichero = new File(listaPath.get(position));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bitmap = BitmapFactory.decodeStream(new FileInputStream(fichero),null,options);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ViewPager vp = (ViewPager) container;
            vp.addView(view,0);
            return view;

        } catch(Exception e){
            return null;

        }
    }

    /**
     * Para eliminar el item cuando se pasa de un pager a otro
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
