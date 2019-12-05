package com.example.misapps.ui.juegos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.Spinner;

import com.example.misapps.MainActivity;
import com.example.misapps.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class DetalleJuego extends Fragment {

    private Juego juego;
    private Spinner spinnerJuegosDetallePlataforma;
    /**
     * Hago un Array para las plataformas que voy a sacar de la BBDD, ya que
     * sé que son 7 y no van a cambiar, por lo que no veo necesario un ArrayList
     */
    private String[] listaPlataformas = new String[7];
    private EditText tvFecha;
    private Button btnFecha;
    private TextInputLayout etNombre;
    private TextInputLayout etPrecio;
    private TextInputLayout etPlataforma;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private FloatingActionButton fabInsertar;
    private String modoVisualización;
    private FloatingActionButton btnCamara;
    private ImageView ivJuego;
    public static Juego juegoMain;
    private String b64;

    //Constantes
    private static final int GALERIA = 1;
    private static final int CAMARA = 2;


    /**
     * En los constructores se le pasa qué tipo de aspecto va a tomar el layout del
     * fragment
     * Edición y Actualización --> Los componentes del fragment estarán activados
     * Visualización y Borrado --> Los componentes del fragment estarán desactivados
     */
    public DetalleJuego() {
        modoVisualización = "insertar";


    }

    public DetalleJuego(Juego juego, String modoVisualización) {
        this.juego = juego;
        this.modoVisualización = modoVisualización;
        //Para el evento de compartir, que se gestiona en el main (es donde se crea el Toolbar)
        juegoMain = juego;
        b64 = juego.getImg();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detalle_juego_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        llamarVistas();
        gestionarTipoVisualizacion();
        //Para mostrar el item de compartir
        MainActivity.menuArriba.findItem(R.id.itemCompartirJuego).setVisible(true);
        //Para ocultar el acceso al menú lateral
        //Se hace un getActivity haciendole un casting a MainActivity
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private void llamarVistas() {
        this.spinnerJuegosDetallePlataforma = (Spinner) getView().findViewById(R.id.spinnerJuegosDetallePlataforma);
        this.tvFecha = (EditText) getView().findViewById(R.id.etDetalleJuegoFechaLanzamiento);
        this.btnFecha = (Button) getView().findViewById(R.id.btnFecha);
        this.btnFecha.setOnClickListener(listeners);
        this.fabInsertar = (FloatingActionButton) getView().findViewById(R.id.fabDetalleJuegoInsertar);
        this.fabInsertar.setOnClickListener(listeners);
        this.etNombre = (TextInputLayout) getView().findViewById(R.id.tvDetalleJuegoNombre);
        this.etPrecio = (TextInputLayout) getView().findViewById(R.id.tvDetalleJuegoPrecio);
        this.etPlataforma = (TextInputLayout) getView().findViewById(R.id.tvDetalleJuegoPlataforma);
        this.btnCamara = (FloatingActionButton) getView().findViewById(R.id.ibDetalleJuegoCamara);
        this.btnCamara.setOnClickListener(listeners);
        this.ivJuego = (ImageView) getView().findViewById(R.id.ivDetalleJuego);
    }

    /**
     * Método para la imagen que sacamos codificada de la BBDD
     */
    private void gestionarImg(){
        Bitmap imagenJuego = base64ToBitmap(juego.getImg());
        float proporcion = 600 / (float) imagenJuego.getWidth();
        Bitmap imagenFinal = Bitmap.createScaledBitmap(imagenJuego, 600, (int) (imagenJuego.getHeight() * proporcion), false);
        this.ivJuego.setImageBitmap(imagenFinal);
    }

    private Bitmap base64ToBitmap(String imgCodificada) {
        byte[] imageAsBytes = Base64.decode(imgCodificada.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    /**
     * En función del modo de visualización recibido en el constructor la visualización
     * de los componentes para el usuario será distinta. Así utilizamos siempre el mismo
     * fragment, pero activando y desactivando componentes. Por ejemplo, en el modo visualización
     * no se deja activo ningún componente porque solo hay que "visualizar" un detalle del juego
     */
    private void gestionarTipoVisualizacion() {
        switch (modoVisualización) {
            case "visualizar":
                desactivarComponentes();
                mostrarDatosJuego();
                gestionarImg();
                this.fabInsertar.hide();
                break;
            case "insertar":
                MainActivity.menuArriba.findItem(R.id.itemCompartirJuego).setVisible(false);
                consultarPlataformas();
                gestionSpinner();
                break;
            case "borrar":
                desactivarComponentes();
                mostrarDatosJuego();
                gestionarImg();
                this.fabInsertar.setImageResource(R.drawable.ic_eliminar);
                break;
            case "actualizar":
                consultarPlataformas();
                mostrarDatosJuego();
                gestionSpinner();
                gestionarImg();
                this.fabInsertar.setImageResource(R.drawable.ic_update);
                break;
            default:
                break;
        }
    }

    /**
     * Se muestran los datos del juego. Sucede en todas las vistas menos en la
     * vista de inserción de un nuevo juego
     */
    private void mostrarDatosJuego() {
        this.etNombre.getEditText().setText(this.juego.getNombre());
        this.tvFecha.setText(this.juego.getFechaLanzamiento());
        this.etPrecio.getEditText().setText(String.valueOf(this.juego.getPrecio()));
        this.etPlataforma.getEditText().setText(this.juego.getPlataforma());
    }

    /**
     * Se desactivan y ocultan componentes (vista visualización y borrado)
     */
    private void desactivarComponentes() {
        this.etNombre.setEnabled(false);
        this.tvFecha.setEnabled(false);
        this.etPrecio.setEnabled(false);
        this.etPlataforma.setEnabled(false);
        this.spinnerJuegosDetallePlataforma.setEnabled(false);
        this.btnFecha.setEnabled(false);
        this.btnCamara.hide();
    }

    /**
     * Gestión del spinner. le pasamos como se va a mostrar el layout y la lista que va a recibir.
     * Se trata de un proceso muy parecido al de construir un adaptador para un recyclerView
     * solo que este ya nos lo "regala" Android
     */

    private void gestionSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listaPlataformas);
        this.spinnerJuegosDetallePlataforma.setAdapter(dataAdapter);
        /**
         * Listener para el Spinner
         */
        this.spinnerJuegosDetallePlataforma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //El valor escogido en el spinner se lo seteamos al TextInputLayout de la plataforma
                etPlataforma.getEditText().setText(spinnerJuegosDetallePlataforma.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Nada
            }
        });
    }

    /**
     * Para manejar los listeners
     */
    private View.OnClickListener listeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnFecha:
                    //Escogemos fecha
                    escogerFecha();
                    tvFecha.setEnabled(false);
                    break;
                case R.id.fabDetalleJuegoInsertar:
                    //El fab hará una cosa u otra en función del modo de visualización
                    switch (modoVisualización) {
                        case "insertar":
                            //Primero comprobamos que no exita el juego que queremos insertar
                            boolean existeJuego = comprobarExisteJuego(etNombre.getEditText().getText().toString(),etPlataforma.getEditText().getText().toString());
                            if(existeJuego){
                                Snackbar.make(getView(),"¡El juego ya existe en los registros!",Snackbar.LENGTH_LONG).show();
                            }else{
                                insertarJuego();
                                Snackbar.make(getView(), "¡Juego añadido!", Snackbar.LENGTH_LONG).show();
                            }
                            break;
                        case "borrar":
                            mostrarDialogoEliminar();
                            break;
                        case "actualizar":
                            //Igualmente comprobamos que no existe ya un juego con los datos actualizados
                            boolean existe = comprobarExisteJuego(etNombre.getEditText().getText().toString(), etPlataforma.getEditText().getText().toString());
                            if (existe){
                                Snackbar.make(getView(),"¡El juego ya existe en los registros!",Snackbar.LENGTH_LONG).show();
                            }else{
                                actualizarJuego();
                                Snackbar.make(getView(), "¡Juego actualizado!", Snackbar.LENGTH_LONG).show();
                            }

                        default:
                            break;

                    }
                    break;
                case R.id.ibDetalleJuegoCamara:
                    mostrarDialogoFoto();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Método que muestra un calendario para escoger fecha y lo setea al campo
     * de texto. Además bloquea el campo de texto para que no sea editable
     */
    private void escogerFecha() {
        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        //Abrimos el DataPickerDialog
        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int mYear, int mMonth, int mDay) {
                tvFecha.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
            }
        }, day, month, year);
        datePickerDialog.show();
    }

    /**
     * Método para limpiar la lista de plataformas porque si no siempre
     * que hacemos el onCreate del fragment, añade las mismas al spinner
     * por lo que se duplican
     */

    private void vaciarLista() {
        for (int i = 0; i < listaPlataformas.length; i++) {
            listaPlataformas[i] = null;
        }
    }

    /**
     * Dialog que pide la confirmación del usuario para hacer el delete en la BBDD
     */

    private void mostrarDialogoEliminar() {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
        deleteDialog.setTitle("¿Estás seguro de querer eliminar "+juego.getNombre()+" ?");
        String[] deleteDialogItems = {
                "Sí",
                "No"};
        deleteDialog.setItems(deleteDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                borrarJuego();
                                Snackbar.make(getView(),"¡Juego eliminado!",Snackbar.LENGTH_LONG).show();
                                break;
                            case 1:
                                Snackbar.make(getView(),"El juego no se ha borrado",Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
        deleteDialog.show();
    }

    /**
     * GESTIÓN DE LA CÁMARA
     */

    /**
     * Se muestra un dialog que da al usuario a elegir entre sacar una foto
     * con la cámara o bien elegirla de la galería
     */
    private void mostrarDialogoFoto() {
        AlertDialog.Builder fotoDialogo = new AlertDialog.Builder(getContext());
        fotoDialogo.setTitle("Elige un método de entrada");
        String[] fotoDialogoItems = {
                "Seleccionar fotografía de galería",
                "Capturar fotografía desde la cámara"};
        fotoDialogo.setItems(fotoDialogoItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                elegirFotoGaleria();
                                break;
                            case 1:
                                tomarFotoCamara();
                                break;
                        }
                    }
                });
        fotoDialogo.show();
    }

    /**
     * Llamamos a al galería
     */
    public void elegirFotoGaleria() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALERIA);
    }

    /**
     * Llamamos a la cámara
     */
    private void tomarFotoCamara() {
        // Eso para alta o baja
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMARA);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALERIA) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    b64 = convertirenBytes(bitmap);

                    this.ivJuego.setImageBitmap(bitmap);

                } catch (IOException e) {
                    Snackbar.make(getView(), "Fallo en la galería", Snackbar.LENGTH_LONG).show();

                }
            }
        } else if (requestCode == CAMARA) {
            Bitmap thumbnail = null;
            try {
                thumbnail = (Bitmap) data.getExtras().get("data");
                b64 = convertirenBytes(thumbnail);
                this.ivJuego.setImageBitmap(thumbnail);

            } catch (Exception e) {
                Snackbar.make(getView(), "Fallo en la cámara", Snackbar.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Para convertir bitmap en Array de Bites
     */

    public String convertirenBytes(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);

    }

    /**
     *                  PARTE DE GESTIÓN DE BASE DE DATOS
     * ==============================================================================
     */

    /**
     * SELECT
     */
    private void consultarPlataformas() {
        //Primero vaciamos la lista
        vaciarLista();

        ControladorBD bdJuegos = new ControladorBD(getContext(), "BDJuegos", null, 1);
        SQLiteDatabase bd = bdJuegos.getReadableDatabase();
        String aux;
        //Le metemos una por defecto
        listaPlataformas[0] = "Plataforma";
        int posicionLista = 1;
        if (bd != null) {
            Cursor c = bd.rawQuery("SELECT * FROM Plataforma", null);
            if (c.moveToFirst()) {
                do {
                    aux = c.getString(1);
                    listaPlataformas[posicionLista] = aux;
                    posicionLista++;
                } while (c.moveToNext());
            }
        }
        bd.close();
        bdJuegos.close();
    }

    /**
     * INSERT
     */
    private void insertarJuego() {
        String nombre = this.etNombre.getEditText().getText().toString();
        String fecha = this.tvFecha.getText().toString();
        String plataforma = this.spinnerJuegosDetallePlataforma.getSelectedItem().toString();
        String imagen = b64;
        float precio = Float.parseFloat(this.etPrecio.getEditText().getText().toString());
        System.out.println(b64);

        //Abrimos la conexión a la BD
        try {
            ControladorBD bdJuegos = new ControladorBD(getContext(), "BDJuegos", null, 1);
            SQLiteDatabase bd = bdJuegos.getWritableDatabase();
            bd.execSQL("INSERT INTO Juego (nombre,plataforma,precio,imagen,fechaLanzamiento) VALUES" +
                    "('" + nombre.trim() + "','" + plataforma.trim() + "'," + precio + ",'" + imagen + "','" + fecha.trim() + "')");
            bd.close();
            bdJuegos.close();
        } catch (Exception e) {

        }
    }

    /**
     * DELETE
     */
    private void borrarJuego() {
        try {
            ControladorBD bdJuegos = new ControladorBD(getContext(), "BDJuegos", null, 1);
            SQLiteDatabase bd = bdJuegos.getWritableDatabase();
            bd.execSQL("DELETE FROM Juego WHERE id = " + juego.getIdJuego());
            bd.close();
            bdJuegos.close();

        } catch (Exception e) {

        }
    }

    /**
     * UPDATE
     */
    private void actualizarJuego() {
        String nombre = this.etNombre.getEditText().getText().toString();
        String fecha = this.tvFecha.getText().toString();
        String plataforma = this.spinnerJuegosDetallePlataforma.getSelectedItem().toString();
        String imagen = b64;
        float precio = Float.parseFloat(this.etPrecio.getEditText().getText().toString());
        ControladorBD bdJuegos = new ControladorBD(getContext(), "BDJuegos", null, 1);
        SQLiteDatabase bd = bdJuegos.getWritableDatabase();
        bd.execSQL("UPDATE Juego SET nombre = '" + nombre.trim() + "', plataforma = '" + plataforma.trim() + "', precio= " + precio + ",imagen = '" + imagen +
                "', fechaLanzamiento = '" + fecha.trim() + "' WHERE id = " + juego.getIdJuego());
        bd.close();
        bdJuegos.close();
    }

    /**
     * Método para comprobar que un juego no existe en la BBDD. Consideramos que un juego está
     * repetido si tiene el mismo titulo y pertenece a la misma plataforma
     * @param nombreJuego
     * @param plataforma
     * @return
     */
    private boolean comprobarExisteJuego(String nombreJuego, String plataforma){
        boolean existe = false;
        int conteo;

        try{
            ControladorBD bdJuegos = new ControladorBD(getContext(), "BDJuegos", null, 1);
            SQLiteDatabase bd = bdJuegos.getReadableDatabase();
            if (bd != null){
                Cursor c = bd.rawQuery("SELECT * FROM Juego WHERE nombre LIKE '"+nombreJuego.trim()+"'AND plataforma LIKE '"+plataforma.trim()+"'",null);
                conteo = c.getCount();
                if (conteo == 1){
                    existe = true;
                }
            }
        }catch(Exception e){

        }
        return existe;
    }
}
