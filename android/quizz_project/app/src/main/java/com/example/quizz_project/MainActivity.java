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

public class MainActivity extends AppCompatActivity implements DatabaseHelper.DatabaseListener {

    private EditText emailEditText, passwordEditText;
    private TextView statusTextView;
    private Button loginButton, registerButton;
    private DatabaseHelper dbHelper;

    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundManager = SoundManager.getInstance(this);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        statusTextView = findViewById(R.id.statusTextView);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        dbHelper = new DatabaseHelper(this, this);

        loginButton.setOnClickListener(v -> {
            soundManager.playButtonClick(); // Add this
            attemptLogin();
        });
        registerButton.setOnClickListener(v -> {
            soundManager.playButtonClick();
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            statusTextView.setText("Please fill in all fields");
            return;
        }

        statusTextView.setText("Logging in...");
        dbHelper.loginUser(email, password);
    }

    @Override
    public void onSuccess(JSONObject response) {
        runOnUiThread(() -> {
            try {
                if (response.getString("status").equals("success")) {
                    JSONObject userJson = response.getJSONObject("user");

                    // Extract user info from the nested "user" object
                    int id = userJson.getInt("id");
                    String name = userJson.getString("name");
                    String email = userJson.getString("email");
                    int coins = userJson.getInt("coins");

                    // Save user to session
                    SessionManager.User user = new SessionManager.User();
                    user.id = id;
                    user.name = name;
                    user.email = email;
                    user.coins = coins;
                    SessionManager.getInstance(MainActivity.this).saveUser(user);

                    statusTextView.setText("Login successful!");

                    soundManager.startAppBackgroundMusic(R.raw.background_music);

                    new Handler().postDelayed(() -> {
                        Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                        startActivity(intent);
                        finish();
                    }, 2000);
                } else {
                    // Optional: handle failure response
                    statusTextView.setText("Login failed: " + response.getString("message"));
                }
            } catch (Exception e) {
                statusTextView.setText("Login error");
                e.printStackTrace();
            }
        });
    }


    @Override
    public void onError(String error) {
        runOnUiThread(() -> statusTextView.setText("Login failed: " + error));
    }
}