package com.example.quizz_project;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class DatabaseHelper {

    private static final String TAG = "DatabaseHelper";
    public static final String SERVER_URL = "http://192.168.56.1/quiz_api/"; // Replace with your local IP

    private Context context;
    private DatabaseListener listener;

    public interface DatabaseListener {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public DatabaseHelper(Context context, DatabaseListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void registerUser(String email, String name, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("name", name);
        params.put("password", password);
        new DatabaseTask().execute(SERVER_URL + "register.php", params);
    }

    public void loginUser(String email, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        new DatabaseTask().execute(SERVER_URL + "login.php", params);
    }

    private class DatabaseTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... objects) {
            String urlString = (String) objects[0];
            HashMap<String, String> params = (HashMap<String, String>) objects[1];

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String response = "";

            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                // Write params to output stream
                JSONObject jsonParam = new JSONObject(params);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(jsonParam.toString());
                writer.flush();
                writer.close();
                os.close();

                // Read response
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                response = buffer.toString();

            } catch (Exception e) {
                Log.e(TAG, "Error ", e);
                return e.getMessage();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null && !response.isEmpty()) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.has("status") && jsonResponse.getString("status").equals("success")) {
                        listener.onSuccess(jsonResponse);
                    } else {
                        String errorMsg = jsonResponse.getString("message");
                        listener.onError(errorMsg);
                    }
                } catch (JSONException e) {
                    listener.onError("Invalid server response");
                    Log.e(TAG, "Error parsing JSON", e);
                }
            } else {
                listener.onError("No response from server");
            }



        }

        public void loginUser(String email, String password) {
            HashMap<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("password", password);
            new DatabaseTask().execute(SERVER_URL + "login.php", params);
        }

        public void registerUser(String email, String name, String password) {
            HashMap<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("name", name);
            params.put("password", password);
            new DatabaseTask().execute(SERVER_URL + "register.php", params);
        }

        public void fetchQuestions(DatabaseListener listener) {
            HashMap<String, String> params = new HashMap<>();
            new DatabaseTask().execute(SERVER_URL + "get_questions.php", params);
        }

        public void submitAnswer(int userId, int questionId, boolean isCorrect, int responseTimeMs, float score) {
            HashMap<String, String> params = new HashMap<>();
            params.put("user_id", String.valueOf(userId));
            params.put("question_id", String.valueOf(questionId));
            params.put("is_correct", String.valueOf(isCorrect));
            params.put("response_time", String.valueOf(responseTimeMs));
            params.put("score", String.valueOf(score));

            new DatabaseTask().execute(SERVER_URL + "submit_answer.php", params);
        }
    }
}