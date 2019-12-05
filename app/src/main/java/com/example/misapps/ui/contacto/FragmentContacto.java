package com.example.misapps.ui.contacto;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.misapps.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class FragmentContacto extends Fragment {

    private TextInputEditText itContactoUsuario;
    private TextInputEditText itContactoEmail;
    private TextInputLayout tilContactoEmail;
    private Switch swContactoOK;
    private FloatingActionButton fabContactoEnviar;
    private String email;
    private PackageManager pm;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_contacto, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle state){
        super.onActivityCreated(state);
        llamarVistas();
        setearListeners();
    }

    /**
     * Método para llamar a las vistas
     */
    private void llamarVistas() {

        this.itContactoUsuario = (TextInputEditText)getView().findViewById(R.id.itContactoUsuario);
        this.itContactoEmail = (TextInputEditText)getView().findViewById(R.id.itContactoMail);
        this.tilContactoEmail = (TextInputLayout)getView().findViewById(R.id.tilContactoEmail);
        this.swContactoOK = (Switch)getView().findViewById(R.id.swContactoOK);
        this.fabContactoEnviar = (FloatingActionButton)getView().findViewById(R.id.fabContactoEnviar);
    }

    /**
     * Método para setear los listeners
     */
    private void setearListeners() {
        this.itContactoEmail.setOnFocusChangeListener(listenerValidaEmail);
        this.fabContactoEnviar.setOnClickListener(listenerMail);
    }

    /**
     * Listener que se encarga de comprobar, cuando se cambia el foco
     * si el campo del email tiene datos
     */
    private View.OnFocusChangeListener listenerValidaEmail = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            String errorMail = null;
            if (TextUtils.isEmpty(itContactoEmail.getText())) {
                errorMail = "Este campo es obligatorio";
            }
            mostrarErrorCampo(tilContactoEmail, errorMail);
        }
    };

    /**
     * Método que recibe la vista del tipo textInputLayout y una string
     * Si la string esta a n ulo, significa que el campo  tiene datos y
     * si no, significa que no tiene y entonces muestra ese mensaje de
     * error
     *
     * @param textInputLayout
     * @param msg
     */
    private static void mostrarErrorCampo(@NonNull TextInputLayout textInputLayout,
                                          String msg) {
        textInputLayout.setError(msg);
        if (msg == null) {
            textInputLayout.setErrorEnabled(false);
        } else {
            textInputLayout.setErrorEnabled(true);
        }
    }

    /**
     * Listener del botón para enviar un mail
     */
    private View.OnClickListener listenerMail = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //Controlamos que los campos no estén vacíos
            if (itContactoEmail.getText().toString().equals("")|| itContactoUsuario.getText().toString().equals("")) {
                Snackbar.make(v, "Rellena todos los campos", Snackbar.LENGTH_LONG)
                        .show();
            } else {
                //Recogerá el valor del switch
                boolean contactar = swContactoOK.isChecked();

                if (contactar) {
                    //Si habilita el contacto entonces se llama al método para enviar un email
                    mandarMail(v);

                } else {
                    Snackbar.make(v, "No has habilitado la opción de contacto", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        }
    };

    /**
     * Método para enviar un email
     */
    private void mandarMail(View v) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        //Destinatario del email
        intent.setData(Uri.parse("mailto:cgonzalezd10@gmail.com"));
        intent.putExtra(Intent.EXTRA_EMAIL, this.email);
        //Se le da un título
        intent.putExtra(Intent.EXTRA_SUBJECT, "Notificación de misApps");
        //Se le da un contenido
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "He usado mis Apps este es mi " +
                "usuario "
                + itContactoUsuario.getText().toString() + " y mi correo" +
                itContactoEmail.getText().toString());
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Snackbar.make(v,"Instala un cliente de e-mail", Snackbar.LENGTH_LONG)
                    .show();
        }
    }
}