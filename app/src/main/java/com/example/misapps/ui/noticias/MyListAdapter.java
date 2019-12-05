package com.example.misapps.ui.noticias;


import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.example.misapps.R;
import com.example.misapps.ui.detalle.fragment_detalle;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import com.squareup.picasso.Transformation;



public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {

    private ArrayList<Noticia> listNoticias;
    /**
     * Le pasamos el fragmentmanager para poder gestionar el listener
     */
    private FragmentManager fm;

    public MyListAdapter(ArrayList<Noticia> listNoticias, FragmentManager fm) {
        this.listNoticias = listNoticias;
        this.fm = fm;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_lista, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Noticia noticiaLista = listNoticias.get(position);
        String titular = listNoticias.get(position).getTitulo();
        //Controlamos la longitud para que si llega a una cantidad de caracteres, recortarlo
        if (titular.length() >= 30){
            titular = titular.substring(0,30);
            holder.tvTitular.setText(titular+"...");
        }else{
            holder.tvTitular.setText(titular);
        }

        //Formateamos la fecha
        Date date = new Date(listNoticias.get(position).getFecha());
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        String fechaFormato = formatoFecha.format(date);
        holder.tvFecha.setText(fechaFormato);
        //Sacamos la hora
        holder.tvHora.setText(listNoticias.get(position).getFecha().substring(17,25));
        //Usando Picasso para poder obtener las fotos y redondearlas
        Picasso.get().load(listNoticias.get(position).getImagen())
                //Instanciamos un objeto de la clase (creada más abajo) para redondear la imagen
                .transform(new CircleTransform())
                .resize(375, 200)
                .into(holder.ivNoticia);


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Transacción entre fragments. Lo primero es llamar al fragment manager
                 * A continuación instanciamos un objeto fragment correspondiente al
                 * fragment que va a entrar y le pasamos el objeto noticia en la posición de
                 * la lista correspondiente. Iniciamos la transacción, dándole si queremos
                 * animaciones. Lo más importante es el replace, en el que remplazamos el
                 * host por el objeto fragment detalle
                 */
                fragment_detalle detalle = new fragment_detalle(noticiaLista);
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.setCustomAnimations(R.anim.animacion_fragment1,
                        R.anim.animacion_fragment1, R.anim.animacion_fragment2, R.anim.animacion_fragment1);
                //Llamamos al replace
                transaction.replace(R.id.nav_host_fragment,detalle);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

    }

    /**
     * Método para borrar elemento de la lista
     * @param position
     */
    public void removeItem(int position) {
        listNoticias.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listNoticias.size());

    }

    /**
     * Método para recuperar un elemento de la lista
     * @param item
     * @param position
     */
    public void restoreItem(Noticia item, int position) {
        listNoticias.add(position, item);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, listNoticias.size());
    }

    /**
     * Sobreescritura del método getItemCount(). Devolvemos la longitud
     * del ArrayList de noticias
     * @return
     */
    @Override
    public int getItemCount() {
        return listNoticias.size();
    }


    //Esta clase Holder representa a los elementos que vamos a manejar en cada item
    //Sus atributos coinciden con la clase que simboliza cada item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivNoticia;
        public TextView tvTitular;
        public TextView tvFecha;
        public TextView tvHora;
        public RelativeLayout relativeLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            this.ivNoticia = (ImageView) itemView.findViewById(R.id.ivNoticia);
            this.tvTitular = (TextView) itemView.findViewById(R.id.tvTitular);
            this.tvFecha = (TextView) itemView.findViewById(R.id.tvFechaNoticia);
            this.tvHora = (TextView)itemView.findViewById(R.id.tvHoraNoticia);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeJuegos);

        }
    }

    /**
     * Clase que se encarga de redondear la foto. Hereda de transformation
     */
    class CircleTransform implements Transformation {

        boolean mCircleSeparator = false;

        public CircleTransform() {
        }

        public CircleTransform(boolean circleSeparator) {
            mCircleSeparator = circleSeparator;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
            Canvas canvas = new Canvas(bitmap);
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
            paint.setShader(shader);
            float r = size / 2f;
            canvas.drawCircle(r, r, r - 1, paint);
            // Make the thin border:
            Paint paintBorder = new Paint();
            paintBorder.setStyle(Paint.Style.STROKE);
            paintBorder.setColor(Color.argb(84, 0, 0, 0));
            paintBorder.setAntiAlias(true);
            paintBorder.setStrokeWidth(1);
            canvas.drawCircle(r, r, r - 1, paintBorder);

            // Optional separator for stacking:
            if (mCircleSeparator) {
                Paint paintBorderSeparator = new Paint();
                paintBorderSeparator.setStyle(Paint.Style.STROKE);
                paintBorderSeparator.setColor(Color.parseColor("#ffffff"));
                paintBorderSeparator.setAntiAlias(true);
                paintBorderSeparator.setStrokeWidth(4);
                canvas.drawCircle(r, r, r + 1, paintBorderSeparator);
            }
            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
