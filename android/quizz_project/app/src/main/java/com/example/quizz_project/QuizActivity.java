package com.example.quizz_project;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QuizActivity extends AppCompatActivity {

    private TextView questionText, timerText, resultText, explanationText, scoreText;
    private RadioGroup optionsGroup;
    private Button submitButton, nextButton, backButton;
    private ImageView resultIcon;
    private CardView resultCard;
    private View dimBackground;
    private int currentQuestionId;
    private int correctOption;
    private long startTime;
    private RequestQueue requestQueue;
    private List<Integer> questionQueue;
    private int currentPosition = 0;
    private CountDownTimer countDownTimer;
    private long timeRemaining = 90000; // 1 min 30 sec in milliseconds
    private boolean isAnswered = false;
    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        soundManager = SoundManager.getInstance(this);

        // Initialize views
        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);
        timerText = findViewById(R.id.timerText);
        resultText = findViewById(R.id.resultText);
        explanationText = findViewById(R.id.explanationText);
        scoreText = findViewById(R.id.scoreText);
        resultIcon = findViewById(R.id.resultIcon);
        resultCard = findViewById(R.id.resultCard);
        dimBackground = findViewById(R.id.dimBackground);
        requestQueue = Volley.newRequestQueue(this);


        // Get question queue from intent
        questionQueue = getIntent().getIntegerArrayListExtra("question_queue");
        currentQuestionId = getIntent().getIntExtra("question_id", -1);

        if (questionQueue != null && !questionQueue.isEmpty()) {
            currentPosition = questionQueue.indexOf(currentQuestionId);
            if (currentPosition == -1) {
                currentPosition = 0;
            }
        }

        startTime = System.currentTimeMillis();
        startTimer();

        loadQuestion(currentQuestionId);

        submitButton.setOnClickListener(v -> {
            soundManager.playButtonClick();
            checkAnswer();
        });
        nextButton.setOnClickListener(v -> {
            soundManager.playButtonClick();
            loadNextQuestion();
        });
        backButton.setOnClickListener(v -> {
            soundManager.playButtonClick();
            Intent intent = new Intent(QuizActivity.this, QuestionsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                soundManager.playRadioButtonSound();
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timeRemaining = 0;
                updateTimerText();
                if (!isAnswered) {
                    // Time's up, automatically submit empty answer
                    checkAnswer();
                }
            }
        }.start();
    }

    private void updateTimerText() {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeRemaining);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeRemaining) -
                TimeUnit.MINUTES.toSeconds(minutes);
        timerText.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void loadNextQuestion() {
        // Cancel any animations
        resultCard.clearAnimation();
        dimBackground.clearAnimation();

        // Hide result popup
        resultCard.setVisibility(View.GONE);
        dimBackground.setVisibility(View.GONE);

        if (questionQueue == null || questionQueue.isEmpty()) {
            finish();
            return;
        }

        currentPosition++;
        if (currentPosition >= questionQueue.size()) {
            showEndOfQueueMessage();
            return;
        }

        currentQuestionId = questionQueue.get(currentPosition);
        startTime = System.currentTimeMillis();
        isAnswered = false;

        // Reset timer for new question
        timeRemaining = 90000;
        countDownTimer.cancel();
        startTimer();

        loadQuestion(currentQuestionId);

        // Reset UI for new question
        optionsGroup.clearCheck();
        optionsGroup.setEnabled(true);
        submitButton.setEnabled(true);
        submitButton.setVisibility(View.VISIBLE);
    }

    private void showEndOfQueueMessage() {
        // Create a custom dialog or toast
        Toast.makeText(this, "You've completed all questions in this category!", Toast.LENGTH_LONG).show();

        // Return to main menu after delay
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(QuizActivity.this, QuestionsListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }, 2000); // 2 second delay
    }

    private void loadQuestion(int questionId) {
        if (questionId == -1) {
            Toast.makeText(this, "Invalid question", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String url = DatabaseHelper.SERVER_URL + "get_question.php?id=" + questionId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONObject questionData = response.getJSONObject("data");
                            displayQuestion(questionData);
                        } else {
                            throw new JSONException(response.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e("QuizActivity", "Error parsing question: " + e.getMessage());
                        Toast.makeText(QuizActivity.this, "Error loading question", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                error -> {
                    Log.e("QuizActivity", "Volley error: " + error.getMessage());
                    Toast.makeText(QuizActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    finish();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private String questionExplanation;
    private void displayQuestion(JSONObject questionData) throws JSONException {
        questionText.setText(questionData.getString("question_text"));

        ((RadioButton) findViewById(R.id.option1)).setText(questionData.getString("option1"));
        ((RadioButton) findViewById(R.id.option2)).setText(questionData.getString("option2"));
        ((RadioButton) findViewById(R.id.option3)).setText(questionData.getString("option3"));
        ((RadioButton) findViewById(R.id.option4)).setText(questionData.getString("option4"));

        correctOption = questionData.getInt("correct_option");
        questionExplanation = questionData.optString("explanation", "No explanation available");
    }

    private void checkAnswer() {
        isAnswered = true;
        countDownTimer.cancel();

        int selectedId = optionsGroup.getCheckedRadioButtonId();
        boolean isCorrect;
        int selectedOption = 0;

        if (selectedId == -1) {
            // No answer selected (time ran out)
            isCorrect = false;
        } else {
            if (selectedId == R.id.option1) {
                selectedOption = 1;
            } else if (selectedId == R.id.option2) {
                selectedOption = 2;
            } else if (selectedId == R.id.option3) {
                selectedOption = 3;
            } else if (selectedId == R.id.option4) {
                selectedOption = 4;
            }
            isCorrect = (selectedOption == correctOption);
        }

        long responseTime = System.currentTimeMillis() - startTime;
        float score = calculateScore(responseTime, isCorrect);

        // Disable UI during submission
        submitButton.setEnabled(false);
        optionsGroup.setEnabled(false);

        submitAnswer(isCorrect, responseTime, score);
        showResultWithAnimation(isCorrect, score);
        if (isCorrect) {
            soundManager.playCorrectAnswer();
        } else {
            soundManager.playWrongAnswer();
        }
    }

    private float calculateScore(long responseTimeMs, boolean isCorrect) {
        if (!isCorrect) return 0;

        float responseTimeSeconds = responseTimeMs / 1000f;

        if (responseTimeSeconds <= 10) {
            return 100;
        } else if (responseTimeSeconds <= 70) {
            // From 10-70 seconds: subtract 1 point per second after 10 seconds
            return 100 - (responseTimeSeconds - 10);
        } else {
            // From 70-90 seconds: minimum score is 40
            return Math.max(40, 100 - (responseTimeSeconds - 10));
        }
    }

    private void submitAnswer(boolean isCorrect, long responseTime, float score) {
        SessionManager.User user = SessionManager.getInstance(this).getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String url = DatabaseHelper.SERVER_URL + "submit_answer.php";

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user.id));
        params.put("question_id", String.valueOf(currentQuestionId));
        params.put("is_correct", isCorrect ? "2" : "1");  // 2 = correct, 1 = wrong
        params.put("response_time", String.valueOf(responseTime));
        params.put("score", String.valueOf(score));

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(params),
                response -> {
                    try {
                        if (!response.getString("status").equals("success")) {
                            throw new JSONException(response.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e("QuizActivity", "Error parsing response: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("QuizActivity", "Volley error: " + error.getMessage());
                }
        );

        requestQueue.add(request);
    }

    private void updateUserCoins(int coinsEarned) {
        SessionManager.User user = SessionManager.getInstance(this).getCurrentUser();
        if (user == null) return;

        String url = DatabaseHelper.SERVER_URL + "update_coins.php";

        Map<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user.id));
        params.put("coins", String.valueOf(coinsEarned));

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(params),
                response -> {
                    try {
                        if (!response.getString("status").equals("success")) {
                            Log.e("QuizActivity", "Coin update failed: " + response.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e("QuizActivity", "Error parsing coin response", e);
                    }
                },
                error -> Log.e("QuizActivity", "Coin update error", error)
        );

        requestQueue.add(request);
    }

    // Modify your showResultWithAnimation method
    private void showResultWithAnimation(boolean isCorrect, float score) {
        int coinsEarned = (int) score; // Convert score to coins

        // Update the UI first
        if (isCorrect) {
            resultText.setText(String.format("Correct! 🎉\n+%d coins", coinsEarned));
            resultText.setTextColor(ContextCompat.getColor(this, R.color.green));
            resultIcon.setImageResource(R.drawable.ic_correct);
        } else {
            resultText.setText("Wrong answer 😕\n+0 coins");
            resultText.setTextColor(ContextCompat.getColor(this, R.color.red));
            resultIcon.setImageResource(R.drawable.ic_wrong);
        }

        explanationText.setText(questionExplanation);
        scoreText.setText(String.format("Score: %.0f points", score));

        // Update coins in background if answer was correct
        if (isCorrect) {
            updateUserCoins(coinsEarned);
        }

        // Rest of your animation code remains the same...
        dimBackground.setVisibility(View.VISIBLE);
        dimBackground.setAlpha(0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(dimBackground, "alpha", 0f, 1f);
        fadeIn.setDuration(300);
        fadeIn.start();

        resultCard.setVisibility(View.VISIBLE);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        resultCard.startAnimation(slideUp);

        submitButton.setVisibility(View.GONE);

        if (isCorrect) {
            Animation correctAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
            resultIcon.startAnimation(correctAnim);
        } else {
            Animation wrongAnim = AnimationUtils.loadAnimation(this, R.anim.shake);
            resultIcon.startAnimation(wrongAnim);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
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