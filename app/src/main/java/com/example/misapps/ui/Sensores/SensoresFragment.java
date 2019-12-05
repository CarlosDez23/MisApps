package com.example.misapps.ui.Sensores;


import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


import com.example.misapps.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class SensoresFragment extends Fragment implements SensorEventListener {


    private List<Sensor> listaSensores;
    private SensorManager sensorManager;
    private TextView tvAcelerometroX;
    private TextView tvAcelerometroY;
    private TextView tvAcelerometroZ;
    private TextView tvOrientacionX;
    private TextView tvOrientacionY;
    private TextView tvOrientacionZ;
    private TextView tvCampoMagneticoX;
    private TextView tvCampoMagneticoY;
    private TextView tvCampoMagneticoZ;
    private TextView tvTemperatura;
    private TextView tvProximidad;
    private Switch switchLinterna;

    public static SensoresFragment newInstance() {
        return new SensoresFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sensores_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        llamarVistas();
        rescatarListaSensores();
        initRecycler();
        registrarListeners();
    }

    /**
     * Llamamos a las vistas de este layout
     */
    private void llamarVistas() {
        this.tvAcelerometroX = getView().findViewById(R.id.tvAcelerometroX);
        this.tvAcelerometroY = getView().findViewById(R.id.tvAcelerometroY);
        this.tvAcelerometroZ = getView().findViewById(R.id.tvAcelerometroZ);
        this.tvOrientacionX = getView().findViewById(R.id.tvOrientacionX);
        this.tvOrientacionY = getView().findViewById(R.id.tvOrientacionY);
        this.tvOrientacionZ = getView().findViewById(R.id.tvOrientacionZ);
        this.tvCampoMagneticoX = getView().findViewById(R.id.tvCampoMagneticoX);
        this.tvCampoMagneticoY = getView().findViewById(R.id.tvCampoMagneticoY);
        this.tvCampoMagneticoZ = getView().findViewById(R.id.tvCampoMagneticoZ);
        this.tvTemperatura = getView().findViewById(R.id.tvTemperatura);
        this.tvProximidad = getView().findViewById(R.id.tvProximidad);
        this.switchLinterna = getView().findViewById(R.id.switchLinterna);
        gestionLinterna();

    }

    /**
     * Cuando se vuelve a la aplicación, volvemos a registrar los listener
     */
    @Override
    public void onResume() {
        super.onResume();
        registrarListeners();
    }

    /**
     * En el onPause, como nuestra clase implementa el listener del sensor de eventos,
     * tenemos que dejar de registrarlo y que deje de escuhar
     */
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    /**
     * Sacamos la lista de sensores y se la pasamos al adaptador para mostrársela al usuario
     */
    private void rescatarListaSensores() {
        sensorManager = (SensorManager) getActivity().getSystemService(getContext().SENSOR_SERVICE);
        listaSensores = sensorManager.getSensorList(Sensor.TYPE_ALL);
    }

    /**
     * Método para iniciar nuestro RecyclerView
     */
    private void initRecycler() {
        SensorAdapter adapter = new SensorAdapter(listaSensores);
        RecyclerView recyclerView = getView().findViewById(R.id.recylerSensores);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);

    }


    /**
     * Este método sirve para registrar los diferentes sensores con los que vamos a
     * trabajar y de los cuales queremos capturar sus eventos para obtener valores
     */
    private void registrarListeners() {
        listaSensores = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (!listaSensores.isEmpty()) {
            Sensor orientationSensor = listaSensores.get(0);
            sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_UI);
        }

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (!listaSensores.isEmpty()) {

            Sensor acelerometerSensor = listaSensores.get(0);

            sensorManager.registerListener(this, acelerometerSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);

        if (!listaSensores.isEmpty()) {

            Sensor magneticSensor = listaSensores.get(0);

            sensorManager.registerListener(this, magneticSensor,
                    SensorManager.SENSOR_DELAY_UI);
        }

        listaSensores = sensorManager.getSensorList(Sensor.TYPE_TEMPERATURE);

        if (!listaSensores.isEmpty()) {

            Sensor temperatureSensor = listaSensores.get(0);

            sensorManager.registerListener(this, temperatureSensor,
                    SensorManager.SENSOR_DELAY_UI);
        } else {
            tvTemperatura.setTextColor(Color.RED);
            tvTemperatura.setText("Temperatura no disponible");
        }


        listaSensores = sensorManager.getSensorList(Sensor.TYPE_PROXIMITY);

        if (!listaSensores.isEmpty()) {
            Sensor proximidad = listaSensores.get(0);
            sensorManager.registerListener(this, proximidad, SensorManager.SENSOR_PROXIMITY);
        }

    }

    /**
     * Este evento recibe como parámetro un evento de cambio en los sensores. En un switch
     * identificamos cada sensor con su tag y obtenemos sus valores, que nos vienen dados en
     * un Array. Una vez los tenemos los mostramos en campos de texto
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {

            case Sensor.TYPE_ACCELEROMETER:
                tvAcelerometroX.setText(Float.toString(event.values[0]));
                tvAcelerometroY.setText(Float.toString(event.values[1]));
                tvAcelerometroZ.setText(Float.toString(event.values[2]));
                break;
            case Sensor.TYPE_ORIENTATION:

                tvOrientacionX.setText(Float.toString(event.values[0]));
                tvOrientacionY.setText(Float.toString(event.values[1]));
                tvOrientacionZ.setText(Float.toString(event.values[2]));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                tvCampoMagneticoX.setText(Float.toString(event.values[0]));
                tvCampoMagneticoY.setText(Float.toString(event.values[1]));
                tvCampoMagneticoZ.setText(Float.toString(event.values[2]));
                break;

            case Sensor.TYPE_PROXIMITY:
                tvProximidad.setText(Float.toString(event.values[0]));

                //Si hay un objeto próximo, se lo decimos al usuario con un Snackbar
                if (event.values[0] == 0.0) {
                    Snackbar.make(getView(), "Objeto detectado", Snackbar.LENGTH_SHORT).show();
                    break;

                }
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Método para gestionar el switch de la linterna. Este método recibe en una variable
     * booleana el estado del switch. De esta forma, si está el switch checked encenderemos
     * la linterna y si no la apagaremos. La gestión de la linterna se realiza con un cameraManager
     * Este cameraManager nos permite obtener una lista de todas las cámaras de nuestro dispositivo.
     * Generalmente en la primera posicion de la lista está la cámara trasera, a través de la cual
     * podemos activar y desactivar el flash (torchMode)
     */
    private void gestionLinterna(){

        switchLinterna.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /**
                 * Con isChecked pillamos el valor del switch
                 */
                if(isChecked){

                    CameraManager cameraManager = (CameraManager) getActivity().getSystemService(getContext().CAMERA_SERVICE);
                    try {
                        String cameraID = cameraManager.getCameraIdList()[0];
                        cameraManager.setTorchMode(cameraID,true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }else{

                    CameraManager cameraManager = (CameraManager) getActivity().getSystemService(getContext().CAMERA_SERVICE);
                    try {
                        String cameraID = cameraManager.getCameraIdList()[0];
                        cameraManager.setTorchMode(cameraID,false);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}


