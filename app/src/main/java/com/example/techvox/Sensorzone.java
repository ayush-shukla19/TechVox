package com.example.techvox;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Sensorzone extends AppCompatActivity implements SensorEventListener {

    CardView btnSmartTorch, btnTiltVibrate;
    Button btnBack;

    CameraManager cameraManager;
    String cameraId;
    SensorManager sensorManager;
    Sensor accelerometer, lightSensor;
    Vibrator vibrator;

    boolean tiltMode = false;
    boolean torchOn = false;
    boolean smartTorchEnabled = false;

    private static final int CAMERA_REQUEST = 101;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensorzone);

        btnSmartTorch = findViewById(R.id.btnSmartTorch);
        btnTiltVibrate = findViewById(R.id.btnTiltVibrate);
        btnBack = findViewById(R.id.btnBack);

        // Camera setup
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (cameraManager != null) {
                for (String id : cameraManager.getCameraIdList()) {
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);

                    Boolean hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);

                    if (Boolean.TRUE.equals(hasFlash) &&
                            lensFacing != null &&
                            lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                        cameraId = id;
                        break;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // Permission check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        }

        // Sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Torch button click
        btnSmartTorch.setOnClickListener(v -> {
            smartTorchEnabled = !smartTorchEnabled;
            if (smartTorchEnabled) {
                if (lightSensor != null) {
                    sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    Toast.makeText(this, "Smart Torch Activated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No Light Sensor Found", Toast.LENGTH_SHORT).show();
                }
            } else {
                sensorManager.unregisterListener(this, lightSensor);
                toggleTorch(false);  // turn off torch when disabled
                torchOn = false;
                Toast.makeText(this, "Smart Torch Deactivated", Toast.LENGTH_SHORT).show();
            }
        });

        // Tilt vibrate
        btnTiltVibrate.setOnClickListener(v -> {
            tiltMode = !tiltMode;
            if (tiltMode) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                Toast.makeText(this, "Tilt-to-Vibrate Activated", Toast.LENGTH_SHORT).show();
            } else {
                sensorManager.unregisterListener(this, accelerometer);
                Toast.makeText(this, "Tilt-to-Vibrate Stopped", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button
        btnBack.setOnClickListener(v -> {
            Intent i1 = new Intent(Sensorzone.this, All.class);
            startActivity(i1);
            finish();
        });
    }

    // Sensor changed
    @Override
    public void onSensorChanged(SensorEvent event) {
        // 🔹 Smart Torch (Light Sensor Control)
        if (smartTorchEnabled && event.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (event.values[0] <= 5) {  // Dark environment → Torch ON
                if (!torchOn) {
                    toggleTorch(true);
                    torchOn = true;
                }
            } else {  // Light → Torch OFF
                if (torchOn) {
                    toggleTorch(false);
                    torchOn = false;
                }
            }
        }

        // 🔹 Tilt Vibrate
        if (tiltMode && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];

            if (Math.abs(x) > 7 || Math.abs(y) > 7) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(android.os.VibrationEffect.createOneShot(
                            300, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(300);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // Torch helper
    private void toggleTorch(boolean state) {
        if (cameraManager != null && cameraId != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    cameraManager.setTorchMode(cameraId, state);
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    // Pause
    @Override
    protected void onPause() {
        super.onPause();
        if (tiltMode) {
            sensorManager.unregisterListener(this, accelerometer);
        }
        if (smartTorchEnabled) {
            sensorManager.unregisterListener(this, lightSensor);
        }
    }

    // Resume
    @Override
    protected void onResume() {
        super.onResume();
        if (tiltMode) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (smartTorchEnabled && lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    // Permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
