package com.example.quizz_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.quizz_project.SoundManager;
import com.example.quizz_project.SessionManager;

public class SettingsActivity extends AppCompatActivity {
    private SoundManager soundManager;
    private SeekBar backgroundVolumeSeekBar;
    private SeekBar effectsVolumeSeekBar;
    private SwitchCompat backgroundMuteSwitch;
    private SwitchCompat effectsMuteSwitch;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        soundManager = SoundManager.getInstance(this);

        backgroundVolumeSeekBar = findViewById(R.id.backgroundVolumeSeekBar);
        effectsVolumeSeekBar = findViewById(R.id.effectsVolumeSeekBar);
        backgroundMuteSwitch = findViewById(R.id.backgroundMuteSwitch);
        effectsMuteSwitch = findViewById(R.id.effectsMuteSwitch);
        logoutButton = findViewById(R.id.logoutButton);

        // Initialize values
        backgroundVolumeSeekBar.setProgress((int)(soundManager.getBackgroundVolume() * 100));
        effectsVolumeSeekBar.setProgress((int)(soundManager.getEffectsVolume() * 100));
        backgroundMuteSwitch.setChecked(soundManager.isBackgroundMuted());
        effectsMuteSwitch.setChecked(soundManager.isEffectsMuted());

        // Set up listeners
        backgroundVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                soundManager.setBackgroundVolume(volume);
                if (volume > 0 && soundManager.isBackgroundMuted()) {
                    soundManager.toggleBackgroundMute();
                    backgroundMuteSwitch.setChecked(false);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        effectsVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100f;
                soundManager.setEffectsVolume(volume);
                if (volume > 0 && soundManager.isEffectsMuted()) {
                    soundManager.toggleEffectsMute();
                    effectsMuteSwitch.setChecked(false);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        backgroundMuteSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            soundManager.toggleBackgroundMute();
            if (isChecked) {
                backgroundVolumeSeekBar.setProgress(0);
            } else {
                backgroundVolumeSeekBar.setProgress((int)(soundManager.getBackgroundVolume() * 100));
            }
        });

        effectsMuteSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            soundManager.toggleEffectsMute();
            if (isChecked) {
                effectsVolumeSeekBar.setProgress(0);
            } else {
                effectsVolumeSeekBar.setProgress((int)(soundManager.getEffectsVolume() * 100));
            }
        });

        logoutButton.setOnClickListener(v -> {
            soundManager.playButtonClick();
            soundManager.stopBackgroundMusic();
            SessionManager.getInstance(this).clearUser();
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity(); // Close all activities
        });
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
