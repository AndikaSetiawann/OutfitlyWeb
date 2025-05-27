package com.example.outfitly;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.outfitly.databinding.ActivityEditProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private Uri selectedImageUri;
    private static final int PICK_IMAGE_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        loadUserData();
        setupListeners();
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            binding.etUsername.setText(user.getDisplayName());
            binding.tvEmail.setText(user.getEmail());

            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(binding.ivProfilePic);
            } else {
                binding.ivProfilePic.setImageResource(R.drawable.ic_default_avatar);
            }
        }
    }

    private void setupListeners() {
        binding.ivProfilePic.setOnClickListener(v -> pickImageFromGallery());
        binding.btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.ivProfilePic.setImageURI(selectedImageUri);
        }
    }

    private void saveProfile() {
        String newUsername = binding.etUsername.getText().toString().trim();
        if (newUsername.isEmpty()) {
            binding.etUsername.setError("Username cannot be empty");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            uploadProfilePicture(user, newUsername);
        } else {
            updateUserProfile(user, newUsername, user.getPhotoUrl());
        }
    }

    private void uploadProfilePicture(FirebaseUser user, String username) {
        StorageReference profilePicRef = storage.getReference("profile_pics/" + user.getUid() + ".jpg");

        profilePicRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> profilePicRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            updateUserProfile(user, username, uri);
                        }))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to upload photo", Toast.LENGTH_SHORT).show());
    }

    private void updateUserProfile(FirebaseUser user, String username, @Nullable Uri photoUri) {
        UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoUri)
                .build();

        user.updateProfile(updates)
                .addOnSuccessListener(unused -> {
                    updateFirestoreUser(user.getUid(), username, photoUri != null ? photoUri.toString() : null);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update Firebase profile", Toast.LENGTH_SHORT).show());
    }

    private void updateFirestoreUser(String uid, String username, @Nullable String photoUrl) {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("username", username);
        if (photoUrl != null) {
            updates.put("photoUrl", photoUrl);
        }

        firestore.collection("users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT).show());
    }
}
