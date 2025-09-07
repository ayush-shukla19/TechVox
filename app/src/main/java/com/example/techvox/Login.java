package com.example.techvox;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    FirebaseAuth f1;
    EditText e1,e2;
    ProgressBar p1;
    Button b1 ,b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        e1=(EditText)findViewById(R.id.editTextText3) ;
        e2=(EditText)findViewById(R.id.editTextText4) ;
        e2.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        b1=(Button) findViewById(R.id.button5) ;
        b2=(Button)findViewById(R.id.button6) ;
        p1=(ProgressBar)findViewById(R.id.progressBar) ;
        f1=FirebaseAuth.getInstance();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = e1.getText().toString().trim();
                String s2 = e2.getText().toString();

                if (s1.equals("")) {
                    e1.setText("Please fill email");
                    return;
                } else {
                    if (s2.equals("")) {
                        e2.setText("Please fill password");
                        return;
                    } else {
                        p1.setVisibility(View.VISIBLE);
                        f1.signInWithEmailAndPassword(s1,s2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    p1.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Login.this, "Login done", Toast.LENGTH_SHORT).show();
                                    Intent i2 = new Intent(Login.this, All.class);
                                    startActivity(i2);
                                    finish();
                                    Toast.makeText(Login.this, "Welcome to Login", Toast.LENGTH_SHORT).show();
                                } else {
                                    p1.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Login.this, "Not done", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            }});
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(Login.this, MainActivity.class);
                startActivity(i1);
                finish();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}