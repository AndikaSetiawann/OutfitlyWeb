package com.example.outfitly;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class WardrobeActivity extends AppCompatActivity {

    private RecyclerView rvClothes;
    private BottomNavigationView bottomNav;
    private ClothesAdapter clothesAdapter;
    private List<Clothes> clothesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbarWardrobe);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Wardrobe");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize RecyclerView and Bottom Navigation
        rvClothes = findViewById(R.id.rvClothes);
        bottomNav = findViewById(R.id.bottomNavWardrobe);

        // Set up RecyclerView with clothes data
        setupRecyclerView();

        // Set up Bottom Navigation for easy navigation between activities
        setupBottomNavigation();
    }

    @Override
    public void onBackPressed() {
        // Handle back press safely
        navigateToHome();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle back button on Toolbar safely
            navigateToHome();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToHome() {
        Intent intent = new Intent(WardrobeActivity.this, HomeActivity.class);
        // Use CLEAR_TOP flag to reuse existing Home activity instead of creating new one
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        // Make sure to call finish AFTER starting new activity
        finish();
    }

    private void setupRecyclerView() {
        clothesList = new ArrayList<>();
        clothesList.add(new Clothes("White T-Shirt", "Tops"));
        clothesList.add(new Clothes("Blue Jeans", "Bottoms"));
        clothesList.add(new Clothes("Black Jacket", "Outerwear"));
        clothesList.add(new Clothes("Sneakers", "Shoes"));

        clothesAdapter = new ClothesAdapter(clothesList);
        rvClothes.setLayoutManager(new LinearLayoutManager(this));
        rvClothes.setAdapter(clothesAdapter);
    }

    private void setupBottomNavigation() {
        // Default selected item is Wardrobe
        bottomNav.setSelectedItemId(R.id.nav_wardrobe);

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    // Use centralized navigation method for consistency
                    navigateToHome();
                    return true;
                } else if (itemId == R.id.nav_wardrobe) {
                    return true; // Already on wardrobe
                } else if (itemId == R.id.nav_outfits) {
                    Toast.makeText(WardrobeActivity.this, "Outfits feature coming soon!", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(WardrobeActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Ensure any pending operations are canceled
    }

    @Override
    protected void onDestroy() {
        // Make sure to call super.onDestroy() AFTER cleanup
        if (clothesList != null) {
            clothesList.clear();
            clothesList = null;
        }
        clothesAdapter = null;
        rvClothes.setAdapter(null);
        super.onDestroy();
    }
}