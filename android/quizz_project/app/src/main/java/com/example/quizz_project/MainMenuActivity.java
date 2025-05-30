package com.example.quizz_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;

public class MainMenuActivity extends AppCompatActivity {
    private TextView coinBalanceText;

    private SoundManager soundManager;
    private RequestQueue requestQueue;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        soundManager = SoundManager.getInstance(this);
        soundManager.startBackgroundMusic(R.raw.background_music);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Get user ID from session
        userId = SessionManager.getInstance(this).getCurrentUser().id;

        // Initialize views
        coinBalanceText = findViewById(R.id.coinBalanceText);

        // Set up button click listeners
        findViewById(R.id.profileButton).setOnClickListener(v -> {
            soundManager.playButtonClick();
            startActivity(new Intent(this, ProfileActivity.class));
        });

        findViewById(R.id.questionsButton).setOnClickListener(v -> {
            soundManager.playButtonClick();
            startActivity(new Intent(this, QuestionsListActivity.class));
        });

        findViewById(R.id.leaderboardButton).setOnClickListener(v -> {
            soundManager.playButtonClick();
            startActivity(new Intent(this, LeaderboardActivity.class));
        });

        findViewById(R.id.chestButton).setOnClickListener(v -> {
            soundManager.playButtonClick();
            startActivity(new Intent(this, ChestActivity.class));
        });

        findViewById(R.id.settingsButton).setOnClickListener(v -> {
            soundManager.playButtonClick();
            startActivity(new Intent(MainMenuActivity.this, SettingsActivity.class));
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        soundManager.pauseBackgroundMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        soundManager.resumeBackgroundMusic();
        loadUserCoins();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void loadUserCoins() {
        String url = DatabaseHelper.SERVER_URL + "get_user_coins.php?user_id=" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            String coinsStr = response.getString("coins");
                            // Fixed the String.format issue by parsing to int first
                            int coins = Integer.parseInt(coinsStr);
                            coinBalanceText.setText(String.format("Coins: %d", coins));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NumberFormatException e) {
                        coinBalanceText.setText("Coins: 0");
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle error
                    coinBalanceText.setText("Coins: 0");
                }
        );

        requestQueue.add(request);
    }
}