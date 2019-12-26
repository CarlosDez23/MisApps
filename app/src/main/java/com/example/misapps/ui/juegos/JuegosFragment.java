package com.example.misapps.ui.juegos;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.misapps.MainActivity;
import com.example.misapps.R;
import com.example.misapps.ui.detalle.fragment_detalle;
import com.example.misapps.ui.noticias.Noticia;
import com.example.misapps.ui.noticias.NoticiasFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class JuegosFragment extends Fragment {

    private ArrayList<Juego> juegos = new ArrayList<>();
    private RecyclerView recyclerViewJuegos;
    private MyListJuegosAdapter adapterJuegos;
    private FloatingActionButton fabJuegos;
    private Paint p = new Paint();
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner spinnerFiltro;
    private String [] listaFiltro = {"Filtros","Ordenar por precio", "Ordenar por título",
    "Ordenar por fecha de lanzamiento","Ordenar por plataforma"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.juegos_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        insercionDatosPrueba();
        swipeRecarga();
        gestionSpinner();

        recyclerViewJuegos = (RecyclerView) getView().findViewById(R.id.recyclerJuegos);
        recyclerViewJuegos.setLayoutManager(new LinearLayoutManager(getContext()));
        gestionarfab();
        iniciarSwipeHorizontal();

        //Gestión del menú superior
        MainActivity.menuArriba.findItem(R.id.itemCompartirJuego).setVisible(false);
        //Para ocultar el acceso al menú lateral
        //Se hace un getActivity haciendole un casting a MainActivity
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Método para gestionar el floating action button
     */
    private void gestionarfab() {
        //Llamamos a la vista
        this.fabJuegos = (FloatingActionButton) getView().findViewById(R.id.fabJuegos);
        this.fabJuegos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DetalleJuego detalle = new DetalleJuego();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, detalle);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    /**
     * Gestionamos el filtro a través de un spinner. En función de la posición del array
     * de strings que componen el adaptador del filtro, mandamos al método de consultar
     * un filtro u otro (que concatenamos a la select)
     */
    private void gestionSpinner(){
        this.spinnerFiltro = (Spinner)getView().findViewById(R.id.spinnerJuegosFiltro);
        ArrayAdapter<String>dataAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,listaFiltro);
        spinnerFiltro.setAdapter(dataAdapter);
        this.spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipoFiltro = "";
                switch(spinnerFiltro.getSelectedItemPosition()){
                    case 1:
                        tipoFiltro = "ORDER BY precio";
                        break;
                    case 2:
                        tipoFiltro = "ORDER BY nombre";
                        break;
                    case 3:
                        tipoFiltro = "ORDER BY fechaLanzamiento";
                        break;
                    case 4:
                        tipoFiltro = "ORDER BY plataforma";
                        break;
                    default:
                        break;
                }
                consultarDatos(tipoFiltro);
                adapterJuegos = new MyListJuegosAdapter(juegos, getFragmentManager(), getResources());
                recyclerViewJuegos.setAdapter(adapterJuegos);
                adapterJuegos.notifyDataSetChanged();
                recyclerViewJuegos.setHasFixedSize(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //No hace nada, como Paquirrín
            }
        });
    }

    /**
     * Swipe de recarga, se llama a su vista correspondiente y se le setea el listener
     */
    private void swipeRecarga() {

        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefreshLayoutJuegos);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Se le ponen los colores que queramos
                swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
                swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.textColor);
                //Le pasamos el fragment manager para gestionar las transacciones necesarias
                consultarDatos("");
                adapterJuegos = new MyListJuegosAdapter(juegos, getFragmentManager(), getResources());
                recyclerViewJuegos.setAdapter(adapterJuegos);
                adapterJuegos.notifyDataSetChanged();
                recyclerViewJuegos.setHasFixedSize(true);
                swipeRefreshLayout.setRefreshing(false);

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

                Juego juego = juegos.get(position);
                FragmentManager fm = getFragmentManager();
                DetalleJuego detalle;
                FragmentTransaction transaction;

                if (direction == ItemTouchHelper.LEFT) {
                    detalle = new DetalleJuego(juego, "borrar");
                    transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.animacion_fragment1,
                            R.anim.animacion_fragment1, R.anim.animacion_fragment2,
                            R.anim.animacion_fragment1);
                    //Llamamos al replace
                    transaction.replace(R.id.nav_host_fragment, detalle);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {


                    //Instanciamos un objeto de nuestro nuevo fragment
                    detalle = new DetalleJuego(juego, "actualizar");
                    transaction = fm.beginTransaction();
                    transaction.setCustomAnimations(R.anim.animacion_fragment1,
                            R.anim.animacion_fragment1, R.anim.animacion_fragment2,
                            R.anim.animacion_fragment1);
                    //Llamamos al replace
                    transaction.replace(R.id.nav_host_fragment, detalle);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    //Esto es para que no se quede con el color del deslizamiento
                    adapterJuegos.removeItem(position);
                    adapterJuegos.restoreItem(juego, position);

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
                    } else {
                        p.setColor(Color.RED);
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewJuegos);
    }

    /**
     * Método que comprueba si existe la BD (como es un fichero lo podemos comprobar con la clase
     * File). Si no existe, inserta en la tabla de Plataformas los datos necesarios. Si existe no
     * hace nada
     */
    private void insercionDatosPrueba() {
        File fichero = new File("//data/data/com.example.misapps/databases/BDJuegos");
        if (!fichero.exists()) {
            ControladorBD bdJuegos = new ControladorBD(getContext(), "BDJuegos", null, 1);
            SQLiteDatabase bd = bdJuegos.getWritableDatabase();
            bd.execSQL("INSERT INTO Plataforma (nombre) VALUES ('PS4')");
            bd.execSQL("INSERT INTO Plataforma (nombre) VALUES ('XBOXONE')");
            bd.execSQL("INSERT INTO Plataforma (nombre) VALUES ('SWITCH')");
            bd.execSQL("INSERT INTO Plataforma (nombre) VALUES ('3DS/2DS')");
            bd.execSQL("INSERT INTO Plataforma (nombre) VALUES ('PC')");
            bd.execSQL("INSERT INTO Plataforma (nombre) VALUES ('DIGITAL')");
            bd.close();
            bdJuegos.close();

        }
    }

    /**
     * Nos traemos los datos de la BD con una consulta y construimos la lista
     * que necesitamos pasarle al adaptador para formar nuestro recycler view.
     * Primero limpiamos la lista para que no haya repetidos. Le concatenamos
     * el filtro que deseamos
     */
    private void consultarDatos(String filtro) {
        juegos.clear();
        //Log.i("SELECT", "entrando a la select");
        Juego aux;
        ControladorBD bdJuegos = new ControladorBD(getContext(), "BDJuegos", null, 1);
        SQLiteDatabase bd = bdJuegos.getReadableDatabase();
        if (bd != null) {
            Cursor c = bd.rawQuery("SELECT * FROM Juego "+filtro, null);
            if (c.moveToFirst()) {
                do {

                    aux = new Juego(c.getInt(0), c.getString(1),
                            c.getString(2), c.getFloat(3),
                            c.getString(4), c.getString(5));
                    //Log.i("JUEGO",aux.toString());
                    this.juegos.add(aux);
                } while (c.moveToNext());
            }
            bd.close();
            bdJuegos.close();
        }

    }
}
