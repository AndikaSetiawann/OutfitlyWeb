package com.example.outfitly;

public class OutfitRecommendation {
    private String description;
    private String imageUrl;
    private String shopLink;

    public OutfitRecommendation(String description, String imageUrl, String shopLink) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.shopLink = shopLink;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getShopLink() {
        return shopLink;
    }
}
