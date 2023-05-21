package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class RegistrationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button btnRegister;
    TextView tvLogin;
    EditText etRegisterEmail, etRegisterPassword,etConfirmPassword,etRegisterLastName,etRegisterFirstName;
    boolean passwordVisible;

    Context c =this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);

        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etRegisterLastName = findViewById(R.id.etRegisterLastName);
        etRegisterFirstName = findViewById(R.id.etRegisterFirstName);



        etRegisterPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right=2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=etRegisterPassword.getRight()-etRegisterPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection=etRegisterPassword.getSelectionEnd();
                        if(passwordVisible){
                            etRegisterPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.pass,0,R.drawable.btn_hide,0);

                          etRegisterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }
                        else{
                            etRegisterPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.pass,0,R.drawable.btn_show,0);

                            etRegisterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;

                        }
                        etRegisterPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        etConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right=2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=etConfirmPassword.getRight()-etConfirmPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection=etConfirmPassword.getSelectionEnd();
                        if(passwordVisible){
                           etConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.pass,0,R.drawable.btn_hide,0);

                           etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }
                        else{
                           etConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.pass,0,R.drawable.btn_show,0);

                            etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;

                        }
                     etConfirmPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Pulse).duration(50).playOn(btnRegister);
                String email = etRegisterEmail.getText().toString().trim();
                String password = etRegisterPassword.getText().toString().trim();
                String firstname = etRegisterFirstName.getText().toString().trim();
                String lastname = etRegisterLastName.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                boolean isValid = true;
                if (firstname.isEmpty()) {
                    etRegisterFirstName.setError("Please enter your first name.");
                    isValid = false;
                }
                if (lastname.isEmpty()) {
                    etRegisterLastName.setError("Please enter your last name.");
                    isValid = false;
                }
                if (email.isEmpty()) {
                    etRegisterEmail.setError("Please enter your email address.");
                    isValid = false;
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    etRegisterEmail.setError("Please enter a valid email address.");
                    isValid = false;
                }

                if (password.isEmpty()) {
                    etRegisterPassword.setError("Please enter your password.");
                    isValid = false;
                } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*+=.,])(?=\\S+$).{8,16}$")) {
                    etRegisterPassword.setError("Please enter a password between 8-16 characters with uppercase and lowercase letters, one special character, and one number.");
                    isValid = false;
                }

                if (confirmPassword.isEmpty()) {
                    etConfirmPassword.setError("Please reenter your password.");
                    isValid = false;
                }

                if (!confirmPassword.equals(password)) {
                    etConfirmPassword.setError("Passwords do not match. Please try again.");
                    isValid = false;
                }

                if (isValid) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(c, "Registration Successful", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                       Intent j = new Intent(RegistrationActivity.this, MainActivity.class);
        j.putExtra("firstname", firstname);
        j.putExtra("lastname", lastname);
        j.putExtra("email", email);
        startActivity(j);
        finish();
                                //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(c, "Registration Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Pulse).duration(50).playOn(tvLogin);
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                        finish();
    }

}