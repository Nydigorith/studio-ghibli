package com.example.firebase;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.ViewHolder>{
    Context context;
    int layout;
    private TextView tvNoFilmsFound = null;
    ArrayList<FilmData> kapeList;
    ArrayList<FilmData> filteredList; // new list for filtered data
    OnItemClickListener listener;

    public FilmAdapter(Context context, int layout, ArrayList<FilmData> kapeList,TextView tvNoFilmsFound) {
        this.context = context;
        this.layout = layout;
        this.kapeList = kapeList;
        this.filteredList = new ArrayList<>(kapeList); // initially, filteredList has all the data
        this.tvNoFilmsFound = tvNoFilmsFound;
    }

    public interface OnItemClickListener {
        void onItemClick(String title);
    }


    public void setOnItemClickListener(OnItemClickListener listenerActivity) {
        listener = listenerActivity;
    }


    @NonNull
    @Override
    public FilmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(layout,parent, false);
        ViewHolder vh = new ViewHolder(convertView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int position) {

        FilmData isangKape = filteredList.get(position); // get item from filteredList
        vh.rlTitle.setText(isangKape.getTitle());
        Picasso.get().load(isangKape.getPoster()).into(vh.rlPoster);
    }

    @Override
    public int getItemCount() {
        return filteredList.size(); // return size of filteredList
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView rlPoster;
        TextView rlTitle;
        TextView tvNoFilmsFound;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            this.rlPoster = convertView.findViewById(R.id.rlPoster);
            this.rlTitle = convertView.findViewById(R.id.rlTitle);
            tvNoFilmsFound = itemView.findViewById(R.id.tvNoFilmsFound);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            String title = filteredList.get(position).getTitle(); // get item from filteredList
                            listener.onItemClick(title);
                        }
                    }
                }
            });
        }
    }

    public void filter(String query) {
        filteredList.clear(); // clear previous filtered data
        if (query.isEmpty()) {
            filteredList.addAll(kapeList); // if query is empty, add all items to filteredList
        } else {
            for (FilmData film : kapeList) {
                if (film.getTitle().toLowerCase().contains(query)) {
                    filteredList.add(film); // add filtered items to filteredList
                }
            }
        }
        if (filteredList.isEmpty()) {
            tvNoFilmsFound.setVisibility(View.VISIBLE);
        } else {
            tvNoFilmsFound.setVisibility(View.GONE);
        }
        notifyDataSetChanged();
    }
}
