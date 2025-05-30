package com.example.quizz_project;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChestActivity extends AppCompatActivity {
    private SoundManager soundManager;
    private ImageView chestImage, rewardImage;
    private Button openChestButton;
    private TextView coinBalanceText, rewardText, unlockStatusText;
    private int userCoins = 0;
    private boolean isOpening = false;
    private int userId; // You need to get this from your login system
    private RequestQueue requestQueue;
    private static final String BASE_URL = DatabaseHelper.SERVER_URL; // Change this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chest_activity);
        soundManager = SoundManager.getInstance(this);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Get user ID - you should replace this with your actual user ID retrieval
        userId = SessionManager.getInstance(this).getCurrentUser().id;

        chestImage = findViewById(R.id.chestImage);
        rewardImage = findViewById(R.id.rewardImage);
        openChestButton = findViewById(R.id.openChestButton);
        coinBalanceText = findViewById(R.id.coinBalanceText);
        rewardText = findViewById(R.id.rewardText);
        unlockStatusText = findViewById(R.id.unlockStatusText);

        loadUserCoins();

        openChestButton.setOnClickListener(v -> {
            soundManager.playButtonClick();
            if (!isOpening) {
                if (userCoins >= 1000) {
                    openChest();
                } else {
                    soundManager.playWrongAnswer();
                    showNotEnoughCoinsDialog();
                }
            }
        });
    }

    private void loadUserCoins() {
        String url = BASE_URL + "get_user_coins.php?user_id=" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        // First check if the response has "status" and it's "success"
                        if (response.has("status") && response.getString("status").equals("success")) {
                            // Get coins as string and parse to int
                            String coinsStr = response.getString("coins");
                            userCoins = Integer.parseInt(coinsStr);
                            coinBalanceText.setText(String.format("Coins: %d", userCoins));
                        } else {
                            // Handle error case
                            Log.e("ChestActivity", "Server returned error status");
                            Toast.makeText(this, "Error loading coins", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("ChestActivity", "Error parsing coins", e);
                        Toast.makeText(this, "Error loading coins", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Log.e("ChestActivity", "Error parsing coins value", e);
                        Toast.makeText(this, "Error loading coins", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ChestActivity", "Error getting coins", error);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    private void openChest() {
        soundManager.playChestOpen();
        isOpening = true;
        openChestButton.setEnabled(false);

        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        chestImage.startAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                chestImage.setImageResource(R.drawable.opened_chest);
                revealReward();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void revealReward() {
        String url = BASE_URL + "open_chest.php?user_id=" + userId;

        Map<String, String> params = new HashMap<>();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(params),
                response -> {
                    try {
                        String status = response.getString("status");

                        if (status.equals("error")) {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            resetChest();
                        } else if (status.equals("success")) {
                            JSONObject data = response.getJSONObject("data");
                            String name = data.getString("name");
                            String colorCode = data.getString("color_code");
                            String rarity = data.getString("rarity");
                            boolean isNew = data.getBoolean("is_new");
                            showReward(name, colorCode, rarity,isNew);

                            // Update coins from the response instead of hardcoding -1000
                            userCoins = data.getInt("remaining_coins");
                            coinBalanceText.setText(String.format("Coins: %d", userCoins));
                        } else {
                            Log.e("ChestActivity", "Unknown status in response: " + response.toString());
                            Toast.makeText(this, "Unexpected response from server", Toast.LENGTH_SHORT).show();
                            resetChest();
                        }
                    } catch (JSONException e) {
                        Log.e("ChestActivity", "Error parsing reward", e);
                        Toast.makeText(this, "Error opening chest", Toast.LENGTH_SHORT).show();
                        resetChest();
                    }
                },
                error -> {
                    Log.e("ChestActivity", "Error opening chest", error);
                    Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
                    resetChest();
                }
        );

        requestQueue.add(request);
    }
    private void showReward(String itemName, String colorCode, String rarity, boolean isNew) {
        rewardImage.setVisibility(View.VISIBLE);
        rewardText.setVisibility(View.VISIBLE);
        unlockStatusText.setVisibility(View.VISIBLE);

        String message = isNew
                ? String.format("You got: %s (%s)", itemName, rarity)
                : String.format("Already owned: %s (%s)", itemName, rarity);
        rewardText.setText(message);

        GradientDrawable drawable = GradientParser.parseGradient(colorCode, "oval");
        rewardImage.setBackground(drawable);

        rewardText.setText(String.format("You got: %s (%s)", itemName, rarity));


        unlockStatusText.setText(isNew ? "NEW COLOUR UNLOCKED 🤩" : "You already own this colour 😔");


        new Handler().postDelayed(() -> {
            resetChest();
            isOpening = false;
        }, 3000);
    }

    private void resetChest() {
        chestImage.setImageResource(R.drawable.closed_chest);
        rewardImage.setVisibility(View.INVISIBLE);
        rewardText.setVisibility(View.INVISIBLE);
        unlockStatusText.setVisibility(View.INVISIBLE);
        openChestButton.setEnabled(true);
    }

    private void showNotEnoughCoinsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Not Enough Coins")
                .setMessage("You need 1000 coins to open a chest. Complete more quizzes to earn coins!")
                .setPositiveButton("OK", null)
                .show();
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

}