package com.example.outfitly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

import com.example.outfitly.databinding.ActivityAvaBinding;

import java.util.Random;

public class AvaActivity extends AppCompatActivity {

    private ActivityAvaBinding binding;
    private String[] outfits = {
            "Light Shirt + Linen Pants + Loafers",
            "Hoodie + Jeans + Sneakers",
            "Blazer + Turtleneck + Dress Shoes",
            "Denim Jacket + Black Jeans + Boots",
            "Sweater + Chinos + Sneakers",
            "Polo Shirt + Shorts + Sandals",
            "Cardigan + Tee + Slim Fit Jeans"
    };

    private int[] outfitImages = {
            R.drawable.baju,
            R.drawable.hoodie,
            R.drawable.blazer,
            R.drawable.jacket,
            R.drawable.sweather,
            R.drawable.polo,
            R.drawable.cardigan
    };

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAvaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupInitialOutfit();
        setupButton();
        setupBackHomeButton();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupInitialOutfit() {
        randomizeOutfit();
    }

    private void setupButton() {
        binding.btnRandomAgain.setOnClickListener(v -> animateOutfitChange());
    }

    private void setupBackHomeButton() {
        binding.btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(AvaActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });
    }

    private void animateOutfitChange() {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(300);
        fadeOut.setFillAfter(true);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(300);
        fadeIn.setFillAfter(true);

        binding.tvOutfitToday.startAnimation(fadeOut);
        binding.ivOutfit.startAnimation(fadeOut);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                randomizeOutfit();
                binding.tvOutfitToday.startAnimation(fadeIn);
                binding.ivOutfit.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void randomizeOutfit() {
        int index = random.nextInt(outfits.length);
        binding.tvOutfitToday.setText(outfits[index]);
        binding.ivOutfit.setImageResource(outfitImages[index]);
    }
}
