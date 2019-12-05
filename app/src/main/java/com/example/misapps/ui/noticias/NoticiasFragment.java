package com.example.misapps.ui.noticias;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.misapps.MainActivity;
import com.example.misapps.R;
import com.example.misapps.ui.detalle.fragment_detalle;
import com.google.android.material.snackbar.Snackbar;

import java.io.*;
import java.util.ArrayList;


public class NoticiasFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyListAdapter adapter;
    private ArrayList<Noticia> noticias;
    private tarea2Plano tarea;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View root;
    private Paint p = new Paint();


    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_noticias, container, false);

        return root;
    }

    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        swipeRecarga();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MainActivity.menuArriba.findItem(R.id.itemCompartir).setVisible(false);
        MainActivity.menuArriba.findItem(R.id.itemCompartirJuego).setVisible(false);
        //Para ocultar el acceso al menú lateral
        //Se hace un getActivity haciendole un casting a MainActivity
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        tarea = new tarea2Plano();
        tarea.execute("http://ep00.epimg.net/rss/tags/ultimas_noticias.xml");
        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        iniciarSwipeHorizontal();
    }

    /**
     * Este método comprueba que hay internet. Se deben poner los permisos oportunos
     * en el manifest
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) root.getContext().getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Swipe de recarga, se llama a su vista correspondiente y se le setea el listener
     */
    private void swipeRecarga() {

        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Se le ponen los colores que queramos
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.textColor);
                //Al refrescar llama a la tarea asíncrona
                tarea = new tarea2Plano();
                tarea.execute("http://ep00.epimg.net/rss/tags/ultimas_noticias.xml");
            }
        });
    }

    /**
     * Swipe horizontal (ya visto en clase)
     */
    private void iniciarSwipeHorizontal() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    borrarElemento(position);

                } else {
                    /**
                     * Transacción entre fragments. Lo primero es llamar al fragment manager
                     * A continuación instanciamos un objeto fragment correspondiente al
                     * fragment que va a entrar y le pasamos el objeto noticia en la posición de
                     * la lista correspondiente. Iniciamos la transacción, dándole si queremos
                     * animaciones. Lo más importante es el replace, en el que remplazamos el
                     * host por el objeto fragment detalle
                     */
                    Noticia noticia = noticias.get(position);
                    FragmentManager fm = getFragmentManager();
                    //Instanciamos un objeto de nuestro nuevo fragment
                    fragment_detalle detalle = new fragment_detalle(noticia);
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.animacion_fragment1,
                            R.anim.animacion_fragment1, R.anim.animacion_fragment2,
                            R.anim.animacion_fragment1);
                    //Llamamos al replace
                    transaction.replace(R.id.nav_host_fragment, detalle);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    //Esto es para que no se quede con el color del deslizamiento
                    adapter.removeItem(position);
                    adapter.restoreItem(noticia, position);

                }
            }

            // Dibujamos los botones y evenetos
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    // Si es dirección a la derecha: izquierda->derecta
                    if (dX > 0) {
                        p.setColor(Color.BLUE);
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_detalles);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                        // Caso contrario
                    } else {
                        p.setColor(Color.RED);
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_eliminar);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Método para borrar un elemento de la lista
     *
     * @param position
     */
    private void borrarElemento(int position) {
        final Noticia deletedModel = noticias.get(position);
        final int deletedPosition = position;
        adapter.removeItem(position);
        // Mostramos la barra. Se la da opción al usuario de recuperar lo borrado con el el snackbar
        Snackbar snackbar = Snackbar.make(getView(), "Noticia eliminada", Snackbar.LENGTH_LONG);
        snackbar.setAction("DESHACER", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                adapter.restoreItem(deletedModel, deletedPosition);
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    /**
     * Tarea asíncrona, es la que se va a encargar de realizar la recuperación del
     * rss a través de internet
     */
    class tarea2Plano extends AsyncTask<String, Void, Void> {

        /**
         * Primero comprobamos si hay internet, si no lo hay, pintamos en el thread de la
         * interfaz de usuario un snackbar que avise de que no hay conexión, y le decimos
         * al refreshlayout que deje de refrescar.  Tambíen pararemos la ejecución de la
         * tarea asíncrona (no se ejecutará el doInBackground)
         */
        @Override
        protected void onPreExecute() {
            if (!isNetworkAvailable()) {

                this.cancel(true);
                ((Activity) root.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(getView(), "Activa una conexión a internet",
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }

        /**
         * Se llama al parseador para generar el ArrayList de noticias
         *
         * @param url
         * @return
         */
        @Override
        protected Void doInBackground(String... url) {

            try {
                Parser parseador = new Parser(url[0]);
                noticias = parseador.getNoticias();
            } catch (Exception e) {
                Log.e("Error ", e.getMessage());
            }
            return null;
        }


        /**
         * Después de la ejecución, seteamos la lista al adaptador y dejamos también de refrescar
         * el refreshlayout
         *
         * @param args
         */
        @Override
        protected void onPostExecute(Void args) {

            adapter = new MyListAdapter(noticias, getFragmentManager());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            recyclerView.setHasFixedSize(true);
            swipeRefreshLayout.setRefreshing(false);
        }
        //ObjectInputStream ois = new ObjectInputStream((getActivity().openFileInput("datos.txt")));
        //ObjectOutputStream oos = new ObjectOutputStream(getActivity().openFileOutput("datos.txt",getActivity().MODE_PRIVATE));


    }

    /**
     * Para escribir en un fichero en memoria interna (mejora)
     */

    private void escribirFichero() {
        String nombre;
        OutputStreamWriter fos;
        BufferedWriter br;
        try {


            fos = new OutputStreamWriter(getActivity().openFileOutput("datos.txt", getActivity().MODE_PRIVATE));
            br = new BufferedWriter(fos);
            //Recorremos el Array que rellena la lista
            for (int i = 0; i < noticias.size(); i++) {
                nombre = noticias.get(i).getTitulo();
                br.write(nombre);
                br.newLine();
            }
            //fos.close();
            br.close();
        } catch (Exception e) {
            Log.e("ESCRITURA", "ERROR ESCRIBIENDO EN LA MEMORIA INTERNA");
        }


    }

    /**
     * Para leer de un fichero en memoria interna
     */


    private void cargarDatos(){
        //Leemos del fichero para ello
        Noticia aux;
        String leido;
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(getActivity().openFileInput("datos.txt")));
            while((leido = br.readLine()) != null){
                aux = new Noticia();
                aux.setTitulo(leido);
                noticias.add(aux);
            }
            br.close();
        } catch (Exception e){

        }


    }



    /**
     * Para leer un fichero como recurso interno
     */

    /*
    private void cargarDatosRecurso() {

        try {
            InputStream is = getResources().openRawResource(R.raw.fichero);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String nombre;
            Noticia aux;
            while ((nombre = br.readLine()) != null) {
                aux = new Noticia();
                aux.setTitulo(nombre);
                noticias.add(aux);
            }
            br.close();

        } catch (Exception e) {
            Log.e("FICHEROS", "ERROR LEYENDO DESDE RAW");
        }
    }

     */



}