package com.example.firebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    Context context;
    int layout;
    ArrayList<ReviewData> reviewDataList;
    OnItemClickListener listener;
    Button rlDelete,rlEdit;

    public ReviewAdapter(Context context, int layout, ArrayList<ReviewData> reviewDataList) {
        this.context = context;
        this.layout = layout;
        this.reviewDataList = reviewDataList;
    }

    public interface OnItemClickListener {
        void onItemEditClicked(int position);
        void onItemDeleteClicked(int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        listener = clickListener;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(layout,parent, false);
        ViewHolder vh = new ViewHolder(convertView);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int position) {
        ReviewData isangReviewData = reviewDataList.get(position);
        vh.rlUser.setText(isangReviewData.getEmail());
        vh.rlReview.setText(isangReviewData.getReview());
        vh.rlRating.setRating(Float.parseFloat(isangReviewData.getRating()));
        vh.rlDate.setText(isangReviewData.getDate());
    }

    @Override
    public int getItemCount() {
        return reviewDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView rlUser,rlReview,rlDate;
        RatingBar rlRating;


        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            this.rlUser = convertView.findViewById(R.id.rlUser);
            this.rlReview = convertView.findViewById(R.id.rlReview);
            this.rlRating = convertView.findViewById(R.id.rlRating);
            this.rlDate = convertView.findViewById(R.id.rlDate);
        }
    }
}
