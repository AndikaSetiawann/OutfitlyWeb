package com.example.outfitly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClothesAdapter extends RecyclerView.Adapter<ClothesAdapter.ClothesViewHolder> {

    private List<Clothes> clothesList;

    public ClothesAdapter(List<Clothes> clothesList) {
        this.clothesList = clothesList;
    }

    @NonNull
    @Override
    public ClothesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clothes, parent, false);
        return new ClothesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClothesViewHolder holder, int position) {
        Clothes clothes = clothesList.get(position);
        holder.tvClothesName.setText(clothes.getName());
        holder.tvClothesCategory.setText(clothes.getCategory());
    }

    @Override
    public int getItemCount() {
        return clothesList.size();
    }

    static class ClothesViewHolder extends RecyclerView.ViewHolder {
        TextView tvClothesName, tvClothesCategory;

        public ClothesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClothesName = itemView.findViewById(R.id.tvClothesName);
            tvClothesCategory = itemView.findViewById(R.id.tvClothesCategory);
        }
    }
}
