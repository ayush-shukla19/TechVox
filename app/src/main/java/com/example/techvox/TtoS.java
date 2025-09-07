package com.example.techvox;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Locale;

public class TtoS extends AppCompatActivity {

    EditText etInput;
    Button btnSpeak,b13;
    TextToSpeech tts;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tto_s);

        etInput = findViewById(R.id.etInput);
        btnSpeak = findViewById(R.id.btnSpeak);
        b13=(Button) findViewById(R.id.button13);

        // Initialize TextToSpeech
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US); // You can change to Locale.UK, Locale.CANADA, etc.
                }
            }
        });

        // Speak button click
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etInput.getText().toString().trim();
                if (text.isEmpty()) {
                    Toast.makeText(TtoS.this, "Please enter text", Toast.LENGTH_SHORT).show();
                } else {
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
        b13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(TtoS.this, All.class);
                startActivity(i1);
                finish();
            }
        });
    }

    // Shutdown TTS when activity is destroyed
    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}