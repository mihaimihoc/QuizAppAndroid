package com.example.quizz_project;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private SoundManager soundManager;

    private RecyclerView leaderboardRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private Spinner categorySpinner;
    private TextView currentUserRankView;
    private LeaderboardAdapter leaderboardAdapter;
    private List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        soundManager = SoundManager.getInstance(this);


        initializeViews();
        setupRecyclerView();
        setupCategorySpinner();
    }

    private void initializeViews() {
        leaderboardRecyclerView = findViewById(R.id.leaderboardRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        categorySpinner = findViewById(R.id.categorySpinner);
        currentUserRankView = findViewById(R.id.currentUserRank);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupRecyclerView() {
        leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        int currentUserId = SessionManager.getInstance(this).isLoggedIn() ?
                SessionManager.getInstance(this).getCurrentUser().id : -1;
        leaderboardAdapter = new LeaderboardAdapter(leaderboardEntries, currentUserId);
        leaderboardRecyclerView.setAdapter(leaderboardAdapter);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setSelection(0);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                soundManager.playRadioButtonSound();
                String category = parent.getItemAtPosition(position).toString();
                loadLeaderboard(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadLeaderboard("All Categories");
            }
        });
    }

    private void loadInitialLeaderboard() {
        loadLeaderboard("All Categories");
    }

    private void loadLeaderboard(String category) {
        progressBar.setVisibility(View.VISIBLE);
        leaderboardEntries.clear();
        currentUserRankView.setText("Loading...");

        String url = buildLeaderboardUrl(category);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                this::handleLeaderboardResponse,
                this::handleErrorResponse
        );

        requestQueue.add(request);
    }

    private String buildLeaderboardUrl(String category) {
        String url = DatabaseHelper.SERVER_URL + "get_leaderboard.php?category=" + category;
        if (SessionManager.getInstance(this).isLoggedIn()) {
            url += "&user_id=" + SessionManager.getInstance(this).getCurrentUser().id;
        }
        return url;
    }

    private void handleLeaderboardResponse(JSONObject response) {
        progressBar.setVisibility(View.GONE);
        try {
            if (response.getString("status").equals("success")) {
                processLeaderboardData(response);
                processUserRank(response);
                toggleEmptyView();
            } else {
                showError(response.optString("message", "Unknown error"));
            }
        } catch (JSONException e) {
            showError("Data error: " + e.getMessage());
        }
    }

    private void processLeaderboardData(JSONObject response) throws JSONException {
        JSONArray leaderboardArray = response.getJSONArray("leaderboard");
        for (int i = 0; i < leaderboardArray.length(); i++) {
            JSONObject entry = leaderboardArray.getJSONObject(i);
            leaderboardEntries.add(new LeaderboardEntry(
                    entry.getInt("user_id"),
                    entry.getString("name"),
                    entry.getDouble("total_score"),
                    i + 1,
                    entry.getString("category"),
                    entry.getLong("total_time"),
                    entry.optString("color_code", "default")
            ));
        }
        leaderboardAdapter.notifyDataSetChanged();
    }

    private void processUserRank(JSONObject response) throws JSONException {
        if (!response.isNull("user_rank")) {
            JSONObject rank = response.getJSONObject("user_rank");
            currentUserRankView.setText(String.format(
                    "Your rank: #%d with %.1f points",
                    rank.getInt("rank"),
                    rank.getDouble("total_score")
            ));
        } else if (SessionManager.getInstance(this).isLoggedIn()) {
            currentUserRankView.setText("Complete quizzes to get ranked!");
        } else {
            currentUserRankView.setText("Sign in to see your rank");
        }
    }

    private void handleErrorResponse(VolleyError error) {
        progressBar.setVisibility(View.GONE);
        showError("Network error: " + error.getMessage());
    }

    private void toggleEmptyView() {
        if (leaderboardEntries.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            leaderboardRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            leaderboardRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        emptyView.setVisibility(View.VISIBLE);
        leaderboardRecyclerView.setVisibility(View.GONE);


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

    public static class LeaderboardEntry {
        private final int userId;
        private final String userName;
        private final double totalScore;
        private final int rank;
        private final String category;
        private final long totalTime;
        private final String colorCode; // Add this field

        public LeaderboardEntry(int userId, String userName, double totalScore,
                                int rank, String category, long totalTime, String colorCode) {
            this.userId = userId;
            this.userName = userName;
            this.totalScore = totalScore;
            this.rank = rank;
            this.category = category;
            this.totalTime = totalTime;
            this.colorCode = colorCode;
        }

        // Getters
        public int getUserId() { return userId; }
        public String getUserName() { return userName; }

        public String getColorCode() { return colorCode; }
        public double getTotalScore() { return totalScore; }
        public int getRank() { return rank; }
        public String getCategory() { return category != null ? category : "Unknown"; }
        public long getTotalTime() { return totalTime; }
    }
}