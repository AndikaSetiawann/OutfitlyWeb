package com.example.outfitly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OutfitAdapter extends RecyclerView.Adapter<OutfitAdapter.OutfitViewHolder> {

    private List<Outfit> outfits;

    public OutfitAdapter(List<Outfit> outfits) {
        this.outfits = outfits;
    }

    @NonNull
    @Override
    public OutfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_outfit, parent, false);
        return new OutfitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutfitViewHolder holder, int position) {
        Outfit outfit = outfits.get(position);
        holder.tvOutfitName.setText(outfit.getName());
        holder.tvOutfitDesc.setText(outfit.getDescription());

        // If we had real images, we would load them here
        // For now, using a placeholder set in the layout
    }

    @Override
    public int getItemCount() {
        return outfits.size();
    }

    static class OutfitViewHolder extends RecyclerView.ViewHolder {
        ImageView ivOutfit;
        TextView tvOutfitName, tvOutfitDesc;

        public OutfitViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOutfit = itemView.findViewById(R.id.ivOutfit);
            tvOutfitName = itemView.findViewById(R.id.tvOutfitName);
            tvOutfitDesc = itemView.findViewById(R.id.tvOutfitDesc);
        }
    }
}