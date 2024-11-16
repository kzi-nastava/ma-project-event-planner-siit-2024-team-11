package com.example.eventy.register;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventy.R;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {
    private List<Uri> images;

    public CarouselAdapter(List<Uri> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext()).load(images.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carouselImageView);
        }
    }

    public void updateImages(List<Uri> newImages) {
        this.images = newImages;
        notifyDataSetChanged();
    }
}