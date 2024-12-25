package com.example.newsapplication.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.newsapplication.R;
import com.example.newsapplication.activity.EditProfileActivity;
import com.example.newsapplication.activity.LoginActivity;
import com.example.newsapplication.model.User;
import com.example.newsapplication.util.DatabaseHelper;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int EDIT_PROFILE_REQUEST = 2;
    private static final int STORAGE_PERMISSION_REQUEST = 100;

    private DatabaseHelper dbHelper;
    private TextView tvUsername;
    private TextView tvFullName;
    private TextView tvGender;
    private TextView tvDateOfBirth;
    private Button btnLogout, btnEditProfile;
    private ImageView ivProfilePicture;
    private String profileImagePath;

    private String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        tvUsername = view.findViewById(R.id.tvUsername);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvGender = view.findViewById(R.id.tvGender);
        tvDateOfBirth = view.findViewById(R.id.tvDateOfBirth);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        ivProfilePicture = view.findViewById(R.id.ivProfileImage);

        // Initialize the DatabaseHelper
        dbHelper = new DatabaseHelper(requireContext());

        // Retrieve username from SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("AppPrefs", requireContext().MODE_PRIVATE);
         username = prefs.getString("username", null);

        // Load user data
        loadUserData();


        ivProfilePicture.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                openImagePicker();
            } else {
                requestStoragePermission();
            }
        });

        // Logout button clears SharedPreferences and returns to LoginActivity
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        // Edit profile button navigates to EditProfileActivity
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("fullName", tvFullName.getText().toString());
            intent.putExtra("gender", tvGender.getText().toString());
            intent.putExtra("dateOfBirth", tvDateOfBirth.getText().toString());
            startActivityForResult(intent, EDIT_PROFILE_REQUEST);
        });

        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                profileImagePath = selectedImageUri.toString();
                boolean isUpdated = dbHelper.updateProfilePicture(username, profileImagePath);
                if (isUpdated) {
                    Toast.makeText(requireContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                    Glide.with(this).load(selectedImageUri).into(ivProfilePicture); // Display selected image
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            loadUserData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }

    private boolean checkStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, STORAGE_PERMISSION_REQUEST);
        } else {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
        }
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // Ensures only images are shown
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    /**
     * Load the profile image or set a placeholder if no image exists.
     */
    private void loadProfileImage(String username) {
        String profileImagePath = dbHelper.getProfilePicture(username);

        if (profileImagePath != null) {
            Glide.with(this).load(Uri.parse(profileImagePath)).into(ivProfilePicture);
        } else {
            ivProfilePicture.setImageResource(R.drawable.profile_placeholder);
        }
    }


    /**
     * Set default profile data when user information is available.
     *
     */
    private void loadUserData(){
        if (username != null) {
            // Fetch the user object from the database
            User user = dbHelper.getUserByUsername(username);

            if (user != null) {
                // Populate UI with user data
                tvUsername.setText(user.getUsername());
                tvFullName.setText(user.getFullName());
                tvGender.setText(user.getGender());
                tvDateOfBirth.setText(user.getBirthDate());

                // Load profile image
                loadProfileImage(user.getUsername());
            } else {
                setDefaultProfileData();
            }
        } else {
            // No username found in SharedPreferences
            setDefaultProfileData();
        }
    }


    /**
     * Set default profile data when user information is unavailable.
     */
    private void setDefaultProfileData() {
        tvUsername.setText("Not Available");
        tvFullName.setText("Not Available");
        tvGender.setText("Not Available");
        tvDateOfBirth.setText("Not Available");
        ivProfilePicture.setImageResource(R.drawable.profile_placeholder);
    }


}