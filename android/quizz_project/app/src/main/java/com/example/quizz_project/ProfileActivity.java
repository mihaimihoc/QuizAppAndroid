package com.example.quizz_project;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private SoundManager soundManager;
    private ConstraintLayout profileLayout;
    private Button changeColorButton;
    private RequestQueue requestQueue;

    private String currentColorCode = "default";

    public String getCurrentColorCode() {
        return currentColorCode;
    }

    public void setCurrentColorCode(String colorCode) {
        this.currentColorCode = colorCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        soundManager = SoundManager.getInstance(this);

        profileLayout = findViewById(R.id.profileLayout);
        changeColorButton = findViewById(R.id.changeColorButton);
        requestQueue = Volley.newRequestQueue(this);

        // Load user data and current selected color
        changeColorButton.setOnClickListener(v -> {
            soundManager.playButtonClick();
            showColorSelectionDialog();
        });
        loadUserProfile();
    }

    private void loadUserProfile() {
        SessionManager.User user = SessionManager.getInstance(this).getCurrentUser();
        if (user != null) {
            TextView welcomeText = findViewById(R.id.welcomeText);
            TextView emailText = findViewById(R.id.emailText);
            TextView idText = findViewById(R.id.idText);

            welcomeText.setText("Hello, " + user.name);
            emailText.setText(user.email);
            idText.setText("ID: " + user.id);

            // Load current selected color
            loadSelectedColor();
        }
    }

    public void loadSelectedColor() {
        int userId = SessionManager.getInstance(this).getCurrentUser().id;
        String url = DatabaseHelper.SERVER_URL + "get_selected_color.php?user_id=" + userId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            String colorCode = jsonResponse.getString("color_code");
                            currentColorCode = colorCode;
                            updateBackground(colorCode);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error loading color", Toast.LENGTH_SHORT).show());

        requestQueue.add(request);
    }

    private void updateBackground(String colorCode) {
        runOnUiThread(() -> {
            ConstraintLayout profileLayout = findViewById(R.id.profileLayout);
            if (colorCode.equals("default")) {
                profileLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
            } else {
                try {
                    GradientDrawable drawable = GradientParser.parseGradient(colorCode, "rectangle");
                    profileLayout.setBackground(drawable);
                } catch (Exception e) {
                    profileLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
                }
            }
        });
    }

    private void showColorSelectionDialog() {
        ColorSelectionDialog dialog = new ColorSelectionDialog(this,
                SessionManager.getInstance(this).getCurrentUser().id);
        dialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        soundManager.resumeBackgroundMusic();
        loadSelectedColor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        soundManager.pauseBackgroundMusic();
    }


}