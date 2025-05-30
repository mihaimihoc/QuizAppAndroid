package com.example.quizz_project;

import android.app.Application;

public class QuizApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Any app-wide initialization can go here
    }

    @Override
    public void onTerminate() {
        // This ensures sound resources are properly released when app closes
        SoundManager.getInstance(this).release();
        super.onTerminate();
    }
}