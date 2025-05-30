package com.example.quizz_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

public class SoundManager {
    private static SoundManager instance;
    private MediaPlayer backgroundPlayer;
    private MediaPlayer soundEffectPlayer;
    private Context context;
    private SharedPreferences preferences;

    // Sound effect resource IDs
    private int buttonClickSound;
    private int radioButtonSound;
    private int correctAnswerSound;
    private int wrongAnswerSound;
    private int questionSelectSound;
    private int categoryChangeSound;
    private int chestOpenSound;

    // Volume levels (0.0 to 1.0)
    private float backgroundVolume = 1.0f;
    private float effectsVolume = 1.0f;
    private boolean backgroundMuted = false;
    private boolean effectsMuted = false;
    private boolean isBackgroundPrepared = false;

    private SoundManager(Context context) {
        this.context = context.getApplicationContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        loadSettings();

        // Initialize your sound resources here
        buttonClickSound = R.raw.button_click;
        radioButtonSound = R.raw.radio_button;
        correctAnswerSound = R.raw.correct_answer;
        wrongAnswerSound = R.raw.wrong_answer;
        questionSelectSound = R.raw.question_select;
        categoryChangeSound = R.raw.category_change;
        chestOpenSound = R.raw.chest_open;
    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    private void loadSettings() {
        backgroundVolume = preferences.getFloat("background_volume", 1.0f);
        effectsVolume = preferences.getFloat("effects_volume", 1.0f);
        backgroundMuted = preferences.getBoolean("background_muted", false);
        effectsMuted = preferences.getBoolean("effects_muted", false);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("background_volume", backgroundVolume);
        editor.putFloat("effects_volume", effectsVolume);
        editor.putBoolean("background_muted", backgroundMuted);
        editor.putBoolean("effects_muted", effectsMuted);
        editor.apply();
    }

    public void playBackgroundMusic(int resourceId) {
        if (backgroundPlayer != null) {
            backgroundPlayer.release();
        }

        backgroundPlayer = MediaPlayer.create(context, resourceId);
        backgroundPlayer.setLooping(true);
        setBackgroundVolume(backgroundMuted ? 0.0f : backgroundVolume);
        backgroundPlayer.start();
    }

    public void stopBackgroundMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
            backgroundPlayer.release();
            backgroundPlayer = null;
        }
    }

    public void pauseBackgroundMusic() {
        if (backgroundPlayer != null && backgroundPlayer.isPlaying()) {
            backgroundPlayer.pause();
        }
    }

    public void resumeBackgroundMusic() {
        if (backgroundPlayer != null && !backgroundPlayer.isPlaying() && isBackgroundPrepared) {
            backgroundPlayer.start();
        }
    }

    public void playSoundEffect(int soundResource) {
        if (effectsMuted) return;

        // Release any previous sound effect
        if (soundEffectPlayer != null) {
            soundEffectPlayer.release();
        }

        soundEffectPlayer = MediaPlayer.create(context, soundResource);
        soundEffectPlayer.setVolume(effectsVolume, effectsVolume);
        soundEffectPlayer.setOnCompletionListener(MediaPlayer::release);
        soundEffectPlayer.start();
    }

    // Convenience methods for specific sounds
    public void playButtonClick() {
        playSoundEffect(buttonClickSound);
    }

    public void playRadioButtonSound() {
        playSoundEffect(radioButtonSound);
    }

    public void playCorrectAnswer() {
        playSoundEffect(correctAnswerSound);
    }

    public void playWrongAnswer() {
        playSoundEffect(wrongAnswerSound);
    }

    public void playQuestionSelect() {
        playSoundEffect(questionSelectSound);
    }

    public void playCategoryChange() {
        playSoundEffect(categoryChangeSound);
    }

    public void playChestOpen() {
        playSoundEffect(chestOpenSound);
    }

    // Volume control methods
    public void setBackgroundVolume(float volume) {
        this.backgroundVolume = volume;
        if (backgroundPlayer != null) {
            backgroundPlayer.setVolume(volume, volume);
        }
        saveSettings();
    }

    public void setEffectsVolume(float volume) {
        this.effectsVolume = volume;
        saveSettings();
    }

    public void toggleBackgroundMute() {
        backgroundMuted = !backgroundMuted;
        setBackgroundVolume(backgroundMuted ? 0.0f : backgroundVolume);
        saveSettings();
    }

    public void startAppBackgroundMusic(int resourceId) {
        if (backgroundPlayer != null && isBackgroundPrepared) {
            // Music is already prepared and ready
            if (!backgroundPlayer.isPlaying()) {
                backgroundPlayer.start();
            }
            return;
        }

        // First time setup
        if (backgroundPlayer != null) {
            backgroundPlayer.release();
        }

        backgroundPlayer = MediaPlayer.create(context, resourceId);
        backgroundPlayer.setLooping(true);
        backgroundPlayer.setVolume(backgroundMuted ? 0 : backgroundVolume, backgroundMuted ? 0 : backgroundVolume);
        backgroundPlayer.start();
        isBackgroundPrepared = true;
    }

    public void toggleEffectsMute() {
        effectsMuted = !effectsMuted;
        saveSettings();
    }

    public float getBackgroundVolume() {
        return backgroundVolume;
    }

    public float getEffectsVolume() {
        return effectsVolume;
    }

    public boolean isBackgroundMuted() {
        return backgroundMuted;
    }

    public boolean isEffectsMuted() {
        return effectsMuted;
    }

    public void release() {
        if (backgroundPlayer != null) {
            backgroundPlayer.release();
            backgroundPlayer = null;
        }
        if (soundEffectPlayer != null) {
            soundEffectPlayer.release();
            soundEffectPlayer = null;
        }
        instance = null;
    }

    // In your SoundManager class, add these methods:
    public void startBackgroundMusic(int resourceId) {
        if (backgroundPlayer != null) {
            if (backgroundPlayer.isPlaying()) {
                return; // Already playing
            }
            backgroundPlayer.release();
        }

        backgroundPlayer = MediaPlayer.create(context, resourceId);
        backgroundPlayer.setLooping(true);
        setBackgroundVolume(backgroundMuted ? 0.0f : backgroundVolume);
        backgroundPlayer.start();
    }


}