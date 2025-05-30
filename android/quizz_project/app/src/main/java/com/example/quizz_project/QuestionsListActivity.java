package com.example.quizz_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestionsListActivity extends AppCompatActivity implements QuestionsAdapter.OnQuestionClickListener {

    private RecyclerView questionsRecyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private QuestionsAdapter questionsAdapter;
    private List<Question> questionsList = new ArrayList<>();
    private List<Question> filteredQuestionsList = new ArrayList<>();
    private RequestQueue requestQueue;

    private SoundManager soundManager;

    private Spinner categorySpinner;
    private Spinner statusSpinner;
    private Button applyFilterButton;

    private String selectedCategory = "Any";
    private String selectedStatus = "Any";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_list);
        soundManager = SoundManager.getInstance(this);

        questionsRecyclerView = findViewById(R.id.questionsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        categorySpinner = findViewById(R.id.categorySpinner);
        statusSpinner = findViewById(R.id.statusSpinner);
        applyFilterButton = findViewById(R.id.applyFilterButton);

        // Setup RecyclerView
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionsAdapter = new QuestionsAdapter(filteredQuestionsList, this);
        questionsRecyclerView.setAdapter(questionsAdapter);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Set up filter button
        applyFilterButton.setOnClickListener(v -> {
            soundManager.playButtonClick();
            applyFilters();
        });

        loadQuestions();
    }

    private void loadQuestions() {
        progressBar.setVisibility(View.VISIBLE);
        questionsList.clear();

        String url = DatabaseHelper.SERVER_URL + "get_questions.php?user_id=" + SessionManager.getInstance(this).getCurrentUser().id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            if (response.getString("status").equals("success")) {
                                JSONArray questionsArray = response.getJSONArray("data");
                                Set<String> categories = new HashSet<>();
                                categories.add("Any");

                                for (int i = 0; i < questionsArray.length(); i++) {
                                    JSONObject questionJson = questionsArray.getJSONObject(i);
                                    int isCorrect = questionJson.isNull("is_correct") ? 0 : questionJson.getInt("is_correct");

                                    Question question = new Question(
                                            questionJson.getInt("id"),
                                            questionJson.getString("question_text"),
                                            questionJson.getString("category"),
                                            isCorrect
                                    );

                                    questionsList.add(question);
                                    categories.add(question.getCategory());
                                }

                                // Set up category spinner
                                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                                        QuestionsListActivity.this,
                                        android.R.layout.simple_spinner_item,
                                        new ArrayList<>(categories)
                                );
                                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                categorySpinner.setAdapter(categoryAdapter);
                                int anyPosition = categoryAdapter.getPosition("Any");
                                if (anyPosition >= 0) {
                                    categorySpinner.setSelection(anyPosition);
                                }
                                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        soundManager.playRadioButtonSound();
                                        selectedCategory = parent.getItemAtPosition(position).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });

                                // Set up status spinner
                                ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                                        QuestionsListActivity.this,
                                        R.array.status_options,
                                        android.R.layout.simple_spinner_item
                                );
                                statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                statusSpinner.setAdapter(statusAdapter);
                                statusSpinner.setSelection(0);
                                statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        soundManager.playRadioButtonSound();
                                        selectedStatus = parent.getItemAtPosition(position).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });

                                // Apply initial filters
                                applyFilters();

                            } else {
                                Toast.makeText(QuestionsListActivity.this, "Error loading questions", Toast.LENGTH_SHORT).show();
                                showEmptyView();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(QuestionsListActivity.this, "JSON Parsing Error", Toast.LENGTH_SHORT).show();
                            showEmptyView();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(QuestionsListActivity.this, "Error loading questions", Toast.LENGTH_SHORT).show();
                        showEmptyView();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void applyFilters() {
        filteredQuestionsList.clear();

        for (Question question : questionsList) {
            boolean matchesCategory = selectedCategory.equals("Any") || question.getCategory().equals(selectedCategory);
            boolean matchesStatus = false;

            switch (selectedStatus) {
                case "Any":
                    matchesStatus = true;
                    break;
                case "Answered":
                    matchesStatus = question.getIsCorrect() == 1 || question.getIsCorrect() == 2;
                    break;
                case "Unanswered":
                    matchesStatus = question.getIsCorrect() == 0;
                    break;
                case "Correct":
                    matchesStatus = question.getIsCorrect() == 1;
                    break;
                case "Incorrect":
                    matchesStatus = question.getIsCorrect() == 2;
                    break;
            }

            if (matchesCategory && matchesStatus) {
                filteredQuestionsList.add(question);
            }
        }

        questionsAdapter.notifyDataSetChanged();

        if (filteredQuestionsList.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        questionsRecyclerView.setVisibility(View.GONE);
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

    private void hideEmptyView() {
        emptyView.setVisibility(View.GONE);
        questionsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onQuestionClick(int position) {
        soundManager.playButtonClick();
        Question selectedQuestion = filteredQuestionsList.get(position);
        openQuestionDetail(selectedQuestion.getId());
    }

    private void openQuestionDetail(int questionId) {
        List<Integer> questionIds = new ArrayList<>();

        // Only add unanswered questions (isCorrect == 0) to the queue
        for (Question question : filteredQuestionsList) {
            if (question.getIsCorrect() == 0) {  // Only include unanswered questions
                questionIds.add(question.getId());
            }
        }

        // If the selected question is answered, we still want to include it
        boolean selectedQuestionIsAnswered = true;
        for (Question question : filteredQuestionsList) {
            if (question.getId() == questionId) {
                selectedQuestionIsAnswered = question.getIsCorrect() != 0;
                break;
            }
        }

        // Add the selected question if it's not already in the list
        if (!questionIds.contains(questionId) && selectedQuestionIsAnswered) {
            questionIds.add(0, questionId); // Add it at the beginning
        }

        // If no questions found (shouldn't happen since we're clicking on one)
        if (questionIds.isEmpty()) {
            questionIds.add(questionId);
        }

        // Randomize the list (except keep the selected question first)
        if (questionIds.size() > 1) {
            // Ensure selected question is first
            int selectedIndex = questionIds.indexOf(questionId);
            if (selectedIndex > 0) {
                Collections.swap(questionIds, 0, selectedIndex);
            }
            // Randomize the rest of the list
            Collections.shuffle(questionIds.subList(1, questionIds.size()));
        }

        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("question_id", questionId);
        intent.putIntegerArrayListExtra("question_queue", new ArrayList<>(questionIds));
        startActivity(intent);
    }

    // Question model class remains the same
    public static class Question {
        private int id;
        private String text;
        private String category;
        private int isCorrect; // 0 = not answered, 1 = correct, 2 = incorrect

        public Question(int id, String text, String category, int isCorrect) {
            this.id = id;
            this.text = text;
            this.category = category;
            this.isCorrect = isCorrect;
        }

        public int getId() {
            return id;
        }

        public String getText() {
            return text;
        }

        public String getCategory() {
            return category;
        }

        public int getIsCorrect() {
            return isCorrect;
        }
    }
}