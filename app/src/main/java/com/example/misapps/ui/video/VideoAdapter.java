package com.example.misapps.ui.video;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.example.misapps.R;
import java.util.ArrayList;

/**
 * Nuestra clase adaptador para los vídeos
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private ArrayList<Video> listVideos;
    private Context context;
    private FragmentManager fm;

    public VideoAdapter() {
    }

    public VideoAdapter(ArrayList<Video> listVideos, Context context, FragmentManager fm) {
        this.listVideos = listVideos;
        this.context = context;
        this.fm = fm;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemListaVideos = layoutInflater.inflate(R.layout.item_video, parent, false);
        VideoAdapter.ViewHolder  viewHolder = new VideoAdapter.ViewHolder(itemListaVideos);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {

        final Video videoLista = listVideos.get(position);
        holder.textViewTitulo.setText(videoLista.getTitulo());
        holder.imageView.setImageBitmap(videoLista.getThumbnail());
        holder.tvPath.setText(videoLista.getRuta());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VisualizarVideoFragment reproductor = new VisualizarVideoFragment(videoLista.getRuta());
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.nav_host_fragment,reproductor);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listVideos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;
        private ImageView imageView;
        private TextView textViewTitulo;
        private TextView tvPath;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView = (CardView) itemView.findViewById(R.id.cardVideo);
            this.imageView = (ImageView) itemView.findViewById(R.id.ivVideosPreview);
            this.textViewTitulo = (TextView)itemView.findViewById(R.id.tvVideoTitulo);
            this.tvPath = (TextView)itemView.findViewById(R.id.tvVideoPath);

        }
    }
}
