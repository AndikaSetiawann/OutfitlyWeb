package com.example.outfitly;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds
    private FirebaseAuth mAuth;
    private CardView logoContainer;
    private ImageView ivLogo;
    private TextView tvAppLogo, tvTagline, tvVersion;
    private View decorCircle1, decorCircle2;
    private ProgressBar progressBar;
    private AnimatorSet fullAnimSet;
    private Button btnLogin, btnRegister;
    private ConstraintLayout buttonsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize views
        logoContainer = findViewById(R.id.logoContainer);
        ivLogo = findViewById(R.id.ivLogo);
        tvAppLogo = findViewById(R.id.tvAppLogo);
        tvTagline = findViewById(R.id.tvTagline);
        tvVersion = findViewById(R.id.tvVersion);
        decorCircle1 = findViewById(R.id.decorCircle1);
        decorCircle2 = findViewById(R.id.decorCircle2);
        progressBar = findViewById(R.id.progressBar);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        buttonsContainer = findViewById(R.id.buttonsContainer);

        // Hide buttons initially
        if (buttonsContainer != null) {
            buttonsContainer.setVisibility(View.GONE);
            buttonsContainer.setAlpha(0f);
        }

        // Set click listeners
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Start animations
        startEnhancedAnimations();

        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, redirect after splash animation
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }, SPLASH_DURATION);
        } else {
            // User is not logged in, show login/register buttons after animation
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Hide progress bar
                ObjectAnimator progressFadeOut = ObjectAnimator.ofFloat(progressBar, View.ALPHA, 1f, 0f);
                progressFadeOut.setDuration(500);

                // Show buttons
                ObjectAnimator buttonsFadeIn = ObjectAnimator.ofFloat(buttonsContainer, View.ALPHA, 0f, 1f);
                buttonsFadeIn.setDuration(600);
                buttonsFadeIn.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        buttonsContainer.setVisibility(View.VISIBLE);
                    }
                });

                // Animate buttons appearing from bottom
                ObjectAnimator buttonsSlideUp = ObjectAnimator.ofFloat(buttonsContainer,
                        View.TRANSLATION_Y, 100f, 0f);
                buttonsSlideUp.setDuration(600);
                buttonsSlideUp.setInterpolator(new DecelerateInterpolator());

                // Play animations together
                AnimatorSet buttonsAnim = new AnimatorSet();
                buttonsAnim.playTogether(buttonsFadeIn, buttonsSlideUp);

                // Sequence animations
                AnimatorSet finalAnim = new AnimatorSet();
                finalAnim.play(progressFadeOut);
                finalAnim.play(buttonsAnim).after(progressFadeOut);
                finalAnim.start();
            }, SPLASH_DURATION);
        }
    }

    private void startEnhancedAnimations() {
        // Prepare initial states
        logoContainer.setScaleX(0.5f);
        logoContainer.setScaleY(0.5f);
        logoContainer.setAlpha(0f);
        tvAppLogo.setAlpha(0f);
        tvTagline.setAlpha(0f);
        tvVersion.setAlpha(0f);

        // Rotating animation for background circles
        animateBackgroundElements();

        // Animated letter-by-letter text reveal for app name
        final String appName = tvAppLogo.getText().toString();
        tvAppLogo.setText("");
        tvAppLogo.setAlpha(1f);

        // Prepare main animations
        AnimatorSet logoAnimSet = createLogoAnimation();
        AnimatorSet taglineAnimSet = createTaglineAnimation();
        AnimatorSet versionAnimSet = createVersionAnimation();

        // Create the letter-by-letter animation for app name
        Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < appName.length(); i++) {
            final int index = i;
            handler.postDelayed(() -> {
                tvAppLogo.setText(appName.substring(0, index + 1));

                // Add a small scale effect for each letter
                if (index < appName.length() - 1) {
                    ObjectAnimator pulse = ObjectAnimator.ofPropertyValuesHolder(
                            tvAppLogo,
                            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.05f, 1.0f),
                            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.05f, 1.0f)
                    );
                    pulse.setDuration(120);
                    pulse.start();
                }
            }, i * 100);
        }

        // Sequence all animations
        fullAnimSet = new AnimatorSet();
        fullAnimSet.play(logoAnimSet).after(appName.length() * 100 + 200);
        fullAnimSet.play(taglineAnimSet).after(logoAnimSet);
        fullAnimSet.play(versionAnimSet).after(taglineAnimSet);
        fullAnimSet.start();
    }

    private void animateBackgroundElements() {
        // Circle 1 rotation and slight movement
        ObjectAnimator circle1Rotation = ObjectAnimator.ofFloat(decorCircle1, View.ROTATION, 0f, 360f);
        circle1Rotation.setDuration(30000);
        circle1Rotation.setRepeatCount(ValueAnimator.INFINITE);
        circle1Rotation.setInterpolator(new AccelerateDecelerateInterpolator());
        circle1Rotation.start();

        // Circle 2 rotation in opposite direction
        ObjectAnimator circle2Rotation = ObjectAnimator.ofFloat(decorCircle2, View.ROTATION, 0f, -360f);
        circle2Rotation.setDuration(40000);
        circle2Rotation.setRepeatCount(ValueAnimator.INFINITE);
        circle2Rotation.setInterpolator(new AccelerateDecelerateInterpolator());
        circle2Rotation.start();

        // Breathing effect for circles (slow scale animation)
        animateBreathingEffect(decorCircle1, 0.95f, 1.1f, 6000);
        animateBreathingEffect(decorCircle2, 0.9f, 1.05f, 7000);
    }

    private void animateBreathingEffect(View view, float minScale, float maxScale, long duration) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, minScale, maxScale);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, minScale, maxScale);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);

        AnimatorSet breathe = new AnimatorSet();
        breathe.playTogether(scaleX, scaleY);
        breathe.setDuration(duration);
        breathe.setInterpolator(new AccelerateDecelerateInterpolator());
        breathe.start();
    }

    private AnimatorSet createLogoAnimation() {
        // Create logo reveal animations
        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logoContainer, View.SCALE_X, 0.5f, 1.0f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logoContainer, View.SCALE_Y, 0.5f, 1.0f);
        ObjectAnimator logoFadeIn = ObjectAnimator.ofFloat(logoContainer, View.ALPHA, 0f, 1f);
        ObjectAnimator logoRotation = ObjectAnimator.ofFloat(logoContainer, View.ROTATION, -30f, 0f);

        // Combine logo animations
        AnimatorSet logoAnimSet = new AnimatorSet();
        logoAnimSet.playTogether(logoScaleX, logoScaleY, logoFadeIn, logoRotation);
        logoAnimSet.setDuration(800);
        logoAnimSet.setInterpolator(new AnticipateOvershootInterpolator(1.5f));

        // Add a subtle shadow animation
        logoAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Add a subtle bounce effect after main animation
                ObjectAnimator bounce = ObjectAnimator.ofFloat(logoContainer, View.TRANSLATION_Y, 0f, -10f, 0f);
                bounce.setDuration(600);
                bounce.setInterpolator(new DecelerateInterpolator());
                bounce.start();

                // Also add logo shine effect
                addShineEffect();
            }
        });

        return logoAnimSet;
    }

    private void addShineEffect() {
        // Create a shine effect (diagonal light reflection)
        View shineEffect = new View(this);
        shineEffect.setBackgroundColor(0x55FFFFFF);
        logoContainer.addView(shineEffect);
        shineEffect.setLayoutParams(new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                20));

        // Animate the shine diagonally across the logo
        ObjectAnimator shineAnim = ObjectAnimator.ofFloat(
                shineEffect,
                View.TRANSLATION_Y,
                -50f, logoContainer.getHeight() + 50f);
        shineAnim.setDuration(1000);
        shineAnim.setStartDelay(300);
        shineAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        shineAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                logoContainer.removeView(shineEffect);
            }
        });
        shineAnim.start();
    }

    private AnimatorSet createTaglineAnimation() {
        ObjectAnimator taglineFadeIn = ObjectAnimator.ofFloat(tvTagline, View.ALPHA, 0f, 1f);
        ObjectAnimator taglineMoveUp = ObjectAnimator.ofFloat(tvTagline, View.TRANSLATION_Y, 40f, 0f);

        AnimatorSet taglineAnimSet = new AnimatorSet();
        taglineAnimSet.playTogether(taglineFadeIn, taglineMoveUp);
        taglineAnimSet.setDuration(700);
        taglineAnimSet.setInterpolator(new DecelerateInterpolator());

        return taglineAnimSet;
    }

    private AnimatorSet createVersionAnimation() {
        ObjectAnimator versionFadeIn = ObjectAnimator.ofFloat(tvVersion, View.ALPHA, 0f, 1f);

        AnimatorSet versionAnimSet = new AnimatorSet();
        versionAnimSet.play(versionFadeIn);
        versionAnimSet.setDuration(500);
        versionAnimSet.setInterpolator(new DecelerateInterpolator());

        return versionAnimSet;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any ongoing animations
        if (fullAnimSet != null) {
            fullAnimSet.cancel();
        }
    }
}