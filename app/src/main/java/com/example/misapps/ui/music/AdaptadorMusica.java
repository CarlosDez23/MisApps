package com.example.misapps.ui.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.example.misapps.R;

import java.util.ArrayList;

import java.util.List;


/**
 * Nuestro adaptador para pintar la lista de las canciones
 */
public class AdaptadorMusica extends RecyclerView.Adapter<AdaptadorMusica.ViewHolder> {

    List<Audio> list = new ArrayList<>();
    Context context;

    public AdaptadorMusica (List<Audio> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView play_pause;


        ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            play_pause = (ImageView) itemView.findViewById(R.id.play_pause);
        }
    }


}

