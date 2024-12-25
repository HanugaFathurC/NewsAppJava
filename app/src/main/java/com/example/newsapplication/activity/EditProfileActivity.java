package com.example.newsapplication.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.example.newsapplication.R;
import com.example.newsapplication.util.DatabaseHelper;

import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etPassword, etConfirmPassword, etDateOfBirth;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnSave, btnPickDate;
    private DatabaseHelper dbHelper;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        rgGender = findViewById(R.id.rgGenderEditProfile);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSave = findViewById(R.id.btnSave);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        etFullName.setText(intent.getStringExtra("fullName"));
        String gender = intent.getStringExtra("gender");
        String dateOfBirth = intent.getStringExtra("dateOfBirth");

        // Set gender radio button
        if (gender != null) {
            if (gender.equals("Male")){
                rbMale.setChecked(true);
            } else {
                rbFemale.setChecked(true);
            }
        }

        // Set date of birth
        etDateOfBirth.setText(dateOfBirth);

        btnPickDate.setOnClickListener(v -> showDatePickerDialog());

        btnSave.setOnClickListener(v -> saveProfileChanges());
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    // Format the selected date
                    String formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etDateOfBirth.setText(formattedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void saveProfileChanges(){
        String fullName = etFullName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String gender = "";

        if (selectedGenderId == R.id.rbMale) {
            gender = "Male";
        } else {
            gender = "Female";

        }

        // Validation checks
        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Full Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(dateOfBirth)) {
            Toast.makeText(this, "Date of Birth cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(password)) {
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Update user profile
        boolean isUpdated = dbHelper.updateUserProfile(username, fullName, gender, dateOfBirth);
        boolean isPasswordUpdated = false;
        if (!TextUtils.isEmpty(password)) {
            isPasswordUpdated = dbHelper.updatePassword(username, password);
        }


        if (isUpdated || isPasswordUpdated) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }






    }
}