package com.example.techvox;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class All extends AppCompatActivity {
    Switch s1,s2,s3,s4;
    FirebaseAuth f1;
    Button b1, b2, b3, b4, b5;

    WifiManager w1;
    BluetoothAdapter ba;
    CameraManager cm;
    Vibrator v1;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all);

        // Initialize views
        s1 = findViewById(R.id.switch1);
        s2 = findViewById(R.id.switch2);
        s3 = findViewById(R.id.switch3);
        s4 = findViewById(R.id.switch4);
        b1 = findViewById(R.id.button7);
        b2 = findViewById(R.id.button8);
        b3 = findViewById(R.id.button9);
        b4 = findViewById(R.id.button10);
        b5 = findViewById(R.id.button11);

        f1 = FirebaseAuth.getInstance();
        cm = (CameraManager) getSystemService(CAMERA_SERVICE);
        w1 = (WifiManager) getSystemService(WIFI_SERVICE);
        ba = BluetoothAdapter.getDefaultAdapter();
        v1 = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        setupWiFiSwitch();
        setupBluetoothSwitch();
        setupTorchSwitch();
        setupVibrationSwitch();
        setupButtons();
        setupWindowInsets();
    }

    // Wi-Fi Switch (updated for Android 10+)
    private void setupWiFiSwitch() {
        s1.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Open system Wi-Fi panel
                Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                startActivity(panelIntent);
            } else {
                boolean enable = !w1.isWifiEnabled();
                w1.setWifiEnabled(enable);
                s1.setChecked(enable);
            }
        });
    }

    // Bluetooth Switch (safe, crash-free)
    @SuppressLint("MissingPermission")
    private void setupBluetoothSwitch() {
        if (ba == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            s2.setEnabled(false);
            return;
        }

        s2.setChecked(ba.isEnabled());

        s2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!ba.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBtIntent);
                }
            } else {
                if (ba.isEnabled()) {
                    ba.disable();
                }
            }
        });
    }

    // Torch/Flashlight Switch
    private void setupTorchSwitch() {
        s3.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            try {
                if (cm != null) {
                    String cameraId = cm.getCameraIdList()[0];
                    cm.setTorchMode(cameraId, isChecked);
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        });
    }

    // Vibration Switch
    private void setupVibrationSwitch() {
        s4.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (v1 != null) {
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v1.vibrate(android.os.VibrationEffect.createOneShot(10000, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        v1.vibrate(10000);
                    }
                } else {
                    v1.cancel();
                }
            }
        });
    }

    // Buttons
    private void setupButtons() {
        b1.setOnClickListener(view -> {
            startActivity(new Intent(All.this, Music.class));
            finish();
        });
        b2.setOnClickListener(view -> {
            startActivity(new Intent(All.this, Sensorzone.class));
            finish();
        });
        b3.setOnClickListener(view -> {
            startActivity(new Intent(All.this, TtoS.class));
            finish();
        });
        b4.setOnClickListener(view -> {
            startActivity(new Intent(All.this, Calculator.class));
            finish();
        });
        b5.setOnClickListener(view -> {
            f1.signOut();
            startActivity(new Intent(All.this, MainActivity.class));
            finish();
        });
    }

    // Handle window insets
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Refresh switch states on resume
    @Override
    protected void onResume() {
        super.onResume();

        // Update Wi-Fi switch state
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            s1.setChecked(w1.isWifiEnabled());
        } else {
            s1.setChecked(false); // Can't reliably check on Android 10+
        }

        // Update Bluetooth switch state
        if (ba != null && s2 != null) {
            s2.setChecked(ba.isEnabled());
        }
    }
}