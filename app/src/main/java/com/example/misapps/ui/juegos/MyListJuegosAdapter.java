package com.example.misapps.ui.juegos;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.misapps.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyListJuegosAdapter extends RecyclerView.Adapter<MyListJuegosAdapter.ViewHolder> {

    private ArrayList<Juego> listJuegos;
    private FragmentManager fm;
    Resources res;

    public MyListJuegosAdapter(ArrayList<Juego> listJuegos, FragmentManager fm, Resources res) {
        this.listJuegos = listJuegos;
        this.fm = fm;
        this.res = res;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemListaJuegos = layoutInflater.inflate(R.layout.item_listajuegos, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemListaJuegos);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyListJuegosAdapter.ViewHolder holder, int position) {

        final Juego juegoLista = listJuegos.get(position);
        /**
         * Gestion de la imagen, sacamos la string del objeto en el recycler
         * construimos el bitmap, lo redondeamos y lo pintamos en el imagageView
         * del item en concreto del recyclerview
         */


        Bitmap imagenJuego = base64ToBitmap(juegoLista.getImg());
        float proporcion = 600 / (float) imagenJuego.getWidth();
        Bitmap imagenFinal = Bitmap.createScaledBitmap(imagenJuego, 600, (int) (imagenJuego.getHeight() * proporcion), false);
        //RoundedBitmapDrawable redondeado = RoundedBitmapDrawableFactory.create(res, imagenFinal);
        //redondeado.setCornerRadius(imagenFinal.getHeight());
        holder.ivJuegosJuego.setImageBitmap(imagenFinal);

        //Resto de datos a mostrar
        holder.tvJuegosTitulo.setText(juegoLista.getNombre());
        holder.tvJuegosFecha.setText(juegoLista.getFechaLanzamiento());
        holder.tvJuegosPlataforma.setText(juegoLista.getPlataforma());
        //Precio en dos decimales
        String precio = gestionarPrecioDosDecimales(String.valueOf(juegoLista.getPrecio()));
        holder.tvJuegosPrecio.setText(precio);


        holder.relativeJuegos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetalleJuego detalle = new DetalleJuego(juegoLista, "visualizar");
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.nav_host_fragment, detalle);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listJuegos.size();
    }


    /**
     * Método para borrar elemento de la lista
     *
     * @param position
     */
    public void removeItem(int position) {
        listJuegos.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listJuegos.size());

    }

    /**
     * Método para recuperar un elemento de la lista
     *
     * @param item
     * @param position
     */
    public void restoreItem(Juego item, int position) {
        listJuegos.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, listJuegos.size());
    }


    /**
     * De la base de datos nos llegará una cadena, por lo que a la hora
     * de pintar el objeto en el recycler, tendremos que pasar esa string
     * en base64 a bitmap y mostrarlo en el ImageView destinado a contener
     * la imagen del juego
     *
     * @param b64
     * @return
     */
    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    /**
     * Nos quedamos solo con dos decimales del precio
     *
     * @param precio
     * @return
     */
    private String gestionarPrecioDosDecimales(String precio) {
        String precioFinal;
        int posicion = -1;
        boolean encontrado = false;

        if (precio.length() > 5) {
            for (int i = 0; i < precio.length() && !encontrado; i++) {
                if (precio.charAt(i) == '.') {
                    posicion = i;
                    encontrado = true;
                }
            }
            precioFinal = precio.substring(0, (posicion + 3));

        } else {
            precioFinal = precio;
        }
        return precioFinal;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivJuegosJuego;
        public TextView tvJuegosTitulo;
        public TextView tvJuegosPlataforma;
        public TextView tvJuegosPrecio;
        public TextView tvJuegosFecha;
        public CardView relativeJuegos;


        public ViewHolder(View itemView) {
            super(itemView);
            this.ivJuegosJuego = (ImageView) itemView.findViewById(R.id.ivJuegosJuego);
            this.tvJuegosTitulo = (TextView) itemView.findViewById(R.id.tvJuegosTitulo);
            this.tvJuegosPlataforma = (TextView) itemView.findViewById(R.id.tvJuegosPlataforma);
            this.tvJuegosPrecio = (TextView) itemView.findViewById(R.id.tvJuegosPrecio);
            this.tvJuegosFecha = (TextView) itemView.findViewById(R.id.tvJuegosFecha);
            relativeJuegos = (CardView) itemView.findViewById(R.id.relativeJuegos);
        }
    }

}
