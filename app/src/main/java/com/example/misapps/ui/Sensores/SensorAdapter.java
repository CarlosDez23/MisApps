package com.example.misapps.ui.Sensores;

import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.misapps.R;
import java.util.List;

/**
 * Adaptador para presentar la lista de sensores
 */
public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.ViewHolder> {

    private List<Sensor> listSensores;

    public SensorAdapter(List<Sensor> listSensores) {
        this.listSensores = listSensores;

    }

    @NonNull
    @Override
    public SensorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemListaSensores = layoutInflater.inflate(R.layout.item_sensor, parent, false);
        SensorAdapter.ViewHolder viewHolder = new SensorAdapter.ViewHolder(itemListaSensores);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SensorAdapter.ViewHolder holder, int position) {
        final Sensor sensorLista = listSensores.get(position);
        holder.textView.setText(sensorLista.getName());

    }

    @Override
    public int getItemCount() {
        return listSensores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout relativeLayout;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeSensores);
            this.textView = (TextView) itemView.findViewById(R.id.tvNombreSensor);
        }
    }
}
