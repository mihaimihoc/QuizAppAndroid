package com.example.quizz_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements DatabaseHelper.DatabaseListener {

    private EditText emailEditText, nameEditText, passwordEditText;
    private TextView statusTextView;
    private Button registerButton, backButton;
    private DatabaseHelper dbHelper;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        soundManager = SoundManager.getInstance(this);

        emailEditText = findViewById(R.id.registerEmailEditText);
        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.registerPasswordEditText);
        statusTextView = findViewById(R.id.registerStatusTextView);
        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);

        dbHelper = new DatabaseHelper(this, this);

        registerButton.setOnClickListener(v -> {
            soundManager.playButtonClick();
            attemptRegistration();
        });

        backButton.setOnClickListener(v -> {
            soundManager.playButtonClick();
            finish();
        });
    }

    private void attemptRegistration() {
        String email = emailEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            statusTextView.setText("Please fill in all fields");
            return;
        }

        statusTextView.setText("Registering...");
        dbHelper.registerUser(email, name, password);
    }

    @Override
    public void onSuccess(JSONObject response) {
        runOnUiThread(() -> {
            try {
                statusTextView.setText("Registration successful!");

                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }, 2000);

            } catch (Exception e) {
                statusTextView.setText("Registration error");
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundManager.resumeBackgroundMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundManager.pauseBackgroundMusic();
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            if (error.contains("email") || error.contains("Duplicate")) {
                statusTextView.setText("Email already in use");
            } else {
                statusTextView.setText("Registration failed: " + error);
            }
        });
    }
}