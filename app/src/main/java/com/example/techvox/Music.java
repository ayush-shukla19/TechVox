package com.example.techvox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Music extends AppCompatActivity {
    Button btnThriller, btnPop, btnLofi, btnRock, btnMotivation, btnDevotion, btnBack;
    MediaPlayer mediaPlayer;  // to play songs
    int currentSong = 0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music);
        btnThriller = findViewById(R.id.btnThriller);
        btnPop = findViewById(R.id.btnPop);
        btnLofi = findViewById(R.id.btnLofi);
        btnRock = findViewById(R.id.btnRock);
        btnMotivation = findViewById(R.id.btnMotivation);
        btnDevotion = findViewById(R.id.btnDevotion);
        btnBack = findViewById(R.id.btnBack);

        // Smooth Button
        btnThriller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(R.raw.thriller);
            }
        });
        // Pop Button
        btnPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(R.raw.pop);
            }
        });

        // Lofi Button
        btnLofi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(R.raw.lofi);
            }
        });

        // Rock Button
        btnRock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(R.raw.rock);
            }
        });

        // Motivation Button
        btnMotivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(R.raw.moti);
            }
        });

        // Devotion Button
        btnDevotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(R.raw.devotion);
            }
        });

        // Back Button
        btnBack.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            Intent i1=new Intent(Music.this,All.class);
            startActivity(i1);

        });
    }

    private void playSong(int songResId) {
        // If already playing this song, pause it
        if (mediaPlayer != null && currentSong == songResId) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();
            } else {
                mediaPlayer.start();
                Toast.makeText(this, "Resumed", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Stop previous song
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Play new song
        mediaPlayer = MediaPlayer.create(this, songResId);
        mediaPlayer.start();
        currentSong = songResId;
        Toast.makeText(this, "Playing new song", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}