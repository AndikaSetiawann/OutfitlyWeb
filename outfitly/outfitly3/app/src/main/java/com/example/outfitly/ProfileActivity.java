package com.example.outfitly;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.example.outfitly.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        // Check if user is logged in, redirect to login if not
        if (auth.getCurrentUser() == null) {
            redirectToLogin();
            return;
        }

        currentUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        setupToolbar();
        setupProfile();
        setupActions();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recheck authentication state when activity resumes
        if (auth.getCurrentUser() == null) {
            redirectToLogin();
            return;
        }

        // Refresh profile data
        setupProfile();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarProfile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbarProfile.setNavigationOnClickListener(v -> handleBackPressed());
    }

    private void setupProfile() {
        if (currentUser == null) {
            currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                redirectToLogin();
                return;
            }
        }

        binding.tvEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "No Email");
        binding.tvUsername.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Guest User");
        binding.tvUid.setText(currentUser.getUid());

        Uri photoUrl = currentUser.getPhotoUrl();
        if (photoUrl != null) {
            Glide.with(this).load(photoUrl).into(binding.ivProfilePic);
        } else {
            binding.ivProfilePic.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    private void setupActions() {
        setupProfilePicPicker();
        binding.btnEditProfile.setOnClickListener(v -> showEditUsernameDialog());
        binding.btnLogout.setOnClickListener(v -> logout());
    }

    private void setupProfilePicPicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        binding.ivProfilePic.setImageURI(uri);
                        updateProfilePicture(uri);
                    }
                }
        );

        binding.ivProfilePic.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    }

    private void showEditUsernameDialog() {
        if (currentUser == null) {
            currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                redirectToLogin();
                return;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Username");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newUsername = input.getText().toString().trim();
            if (!newUsername.isEmpty()) {
                updateUsername(newUsername);
            } else {
                Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateUsername(String newUsername) {
        if (currentUser == null) {
            currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                redirectToLogin();
                return;
            }
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnSuccessListener(unused -> {
                    binding.tvUsername.setText(newUsername);
                    updateUsernameInFirestore(newUsername);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update username.", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateUsernameInFirestore(String newUsername) {
        if (currentUser == null) {
            currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                redirectToLogin();
                return;
            }
        }

        firestore.collection("users")
                .document(currentUser.getUid())
                .update("username", newUsername)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Username updated!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update Firestore.", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateProfilePicture(Uri photoUri) {
        if (currentUser == null) {
            currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                redirectToLogin();
                return;
            }
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUri)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnSuccessListener(unused -> updatePhotoInFirestore(photoUri))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update profile picture.", Toast.LENGTH_SHORT).show()
                );
    }

    private void updatePhotoInFirestore(Uri photoUri) {
        if (currentUser == null) {
            currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                redirectToLogin();
                return;
            }
        }

        firestore.collection("users")
                .document(currentUser.getUid())
                .update("photoUrl", photoUri.toString())
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update Firestore.", Toast.LENGTH_SHORT).show()
                );
    }

    private void logout() {
        auth.signOut();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        binding.bottomNav.setSelectedItemId(R.id.nav_profile);

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                navigateTo(HomeActivity.class, R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
            } else if (id == R.id.nav_wardrobe) {
                navigateTo(WardrobeActivity.class, R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (id == R.id.nav_outfits) {
                Toast.makeText(this, "Outfits feature coming soon!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                // Don't navigate away, we're already here
                return true;
            }
            return false;
        });
    }

    private void navigateTo(Class<?> targetActivity, int enterAnim, int exitAnim) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        overridePendingTransition(enterAnim, exitAnim);
        finish();
    }

    private void handleBackPressed() {
        navigateTo(HomeActivity.class, R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleBackPressed();
    }
}