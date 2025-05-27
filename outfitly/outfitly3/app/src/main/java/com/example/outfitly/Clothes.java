package com.example.outfitly;

public class Clothes {
    private String name;
    private String category;

    public Clothes(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }
}
