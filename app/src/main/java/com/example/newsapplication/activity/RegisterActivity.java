package com.example.newsapplication.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapplication.R;
import com.example.newsapplication.util.DatabaseHelper;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etNewUsername, etNewPassword, etConfirmPassword;
    private Button etBirthDate;
    private RadioGroup rgGender;
    private TextView tvAlreadyHaveAccount;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etBirthDate = findViewById(R.id.btnBirthDate);
        etFullName = findViewById(R.id.etFullName);
        rgGender = findViewById(R.id.rgGender);
        btnRegister = findViewById(R.id.btnRegister);
        tvAlreadyHaveAccount = findViewById(R.id.tvAlreadyHaveAccount);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Set up birth date picker dialog
        etBirthDate.setOnClickListener(v -> showDatePickerDialog());

        // Handle register button click
        btnRegister.setOnClickListener(v -> {
            String username = etNewUsername.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            String password = etNewPassword.getText().toString().trim();
            String birthDate = etBirthDate.getText().toString().trim();

            // Check if a gender is selected
            int selectedId = rgGender.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedGender = findViewById(selectedId);
            String gender = selectedGender.getText().toString();


            if (validateInputs(username, fullName, birthDate, gender, password)) {
                if (dbHelper.registerUser(username, fullName, birthDate, gender, password)) {
                    // Registration successful, navigate to LoginActivity
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Username already exists
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle login link click
        tvAlreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }


    /**
     * Validate input fields.
     *
     * @param username  User's entered username.
     * @param fullName  User's entered full name.
     * @param birthDate User's entered birth date.
     * @param gender    User's selected gender.
     * @param password  User's entered password.
     * @return True if inputs are valid, otherwise false.
     */
    private boolean validateInputs(String username, String fullName, String birthDate, String gender, String password) {
        if (username.isEmpty() || fullName.isEmpty() || birthDate.isEmpty() || gender.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(etConfirmPassword.getText().toString().trim())) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Show a DatePickerDialog for selecting the birth date.
     */
    private void showDatePickerDialog() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the date and set it in the EditText
                    String birthDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    etBirthDate.setText(birthDate);
                },
                year, month, day);
        datePickerDialog.show();
    }
}