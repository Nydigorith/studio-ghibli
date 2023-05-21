package com.example.firebase;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
Button  btnLogin;
TextView tvRegister;
EditText etEmail, etPassword;
boolean passwordVisible;

    Context c =this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        etPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right=2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=etPassword.getRight()-etPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection=etPassword.getSelectionEnd();
                        if(passwordVisible){
                            etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.pass,0,R.drawable.btn_hide,0);

                            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }
                        else{
                            etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.pass,0,R.drawable.btn_show,0);

                            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;

                        }
                        etPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Pulse).duration(50).playOn(btnLogin);
                login();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Pulse).duration(50).playOn(tvRegister);
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the app
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog and continue with the app
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        boolean isValid = true;

        if (email.isEmpty()) {
            etEmail.setError("Please enter you email address.");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address.");
            isValid = false;
        }

        if(password.isEmpty()) {
            etPassword.setError("Please enter your password.");
            isValid = false;
        }

        if (isValid) {
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()) {
                                SignInMethodQueryResult result = task.getResult();
                                if (result.getSignInMethods().isEmpty()) {
                                    etEmail.setError("Account not found. Please register.");
                                } else {
                                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(c, "Login Successful", Toast.LENGTH_SHORT).show();
                                                //startActivity(new Intent(LoginActivity.this, MainActivity.class));

                                                        Intent k = new Intent(LoginActivity.this, MainActivity.class);
                                        k.putExtra("email", email);
                                        startActivity(k);
                                        finish();
                                            } else {
                                                String errorMessage = task.getException().getMessage();
                                                if (errorMessage.contains("password")) {
                                                    etPassword.setError("Incorrect password. Please try again.");
                                                } else {
                                                    Toast.makeText(c, "Login Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(c, "Error. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}