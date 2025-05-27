package com.example.outfitly;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.outfitly.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding binding;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        setupToolbar();
        setupUserInfo();
        setupWeatherCard();
        setupRecyclerView();
        setupClickListeners();
        setupBottomNavigation();
    }

    private void setupToolbar() {
        Toolbar toolbar = binding.getRoot().findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupUserInfo() {
        if (currentUser != null && currentUser.getDisplayName() != null) {
            binding.tvUsername.setText(currentUser.getDisplayName());
        } else {
            binding.tvUsername.setText("Guest");
        }
    }

    private void setupWeatherCard() {
        WeatherHelper.getCurrentWeather(this, (description, temperature) -> {
            if (description != null && temperature != null) {
                binding.tvWeatherDesc.setText(description + ", " + temperature + "°C");
            } else {
                binding.tvWeatherDesc.setText("Failed to load weather");
            }
        });

        binding.btnGetOutfit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AvaActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        binding.rvRecentOutfits.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        List<Outfit> outfits = getDummyOutfits();
        OutfitAdapter adapter = new OutfitAdapter(outfits);
        binding.rvRecentOutfits.setAdapter(adapter);
    }

    private List<Outfit> getDummyOutfits() {
        List<Outfit> outfits = new ArrayList<>();
        outfits.add(new Outfit(
                "Casual Look",
                "Perfect for weekends",
                "https://images.unsplash.com/photo-1600180758890-87c2b4ffeaed"
        ));
        outfits.add(new Outfit(
                "Work Attire",
                "Professional and comfortable",
                "https://images.unsplash.com/photo-1618354691329-9955f6f98a29"
        ));
        outfits.add(new Outfit(
                "Night Out",
                "Stylish evening outfit",
                "https://images.unsplash.com/photo-1589561253898-768105ca91a0"
        ));
        return outfits;
    }

    private void setupClickListeners() {
        binding.btnAskAi.setOnClickListener(v -> startActivity(new Intent(this, AiActivity.class)));
        binding.btnAddClothes.setOnClickListener(v -> showToast("Add clothes feature coming soon!"));

        binding.cardTops.setOnClickListener(v -> showToast("Tops category coming soon!"));
        binding.cardBottoms.setOnClickListener(v -> showToast("Bottoms category coming soon!"));
        binding.cardOuterwear.setOnClickListener(v -> showToast("Outerwear category coming soon!"));
        binding.cardShoes.setOnClickListener(v -> showToast("Shoes category coming soon!"));
    }

    private void setupBottomNavigation() {
        binding.bottomNav.setSelectedItemId(R.id.nav_home);

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_wardrobe) {
                    startActivityWithTransition(WardrobeActivity.class);
                    finish();
                    return true;
                } else if (id == R.id.nav_outfits) {
                    showToast("Outfits feature coming soon!");
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivityWithTransition(ProfileActivity.class);
                    return true;
                }
                return false;
            }
        });
    }

    private void startActivityWithTransition(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}
