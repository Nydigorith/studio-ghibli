package com.example.firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Review extends AppCompatActivity {
    RatingBar etRating;
    EditText etReview;

    private FirebaseAuth mAuth;
    Boolean hasEntry;
    String search,email,status,   key,dateToday, review,rating;
    DAOReview dao;
    Button btnSubmit,btnUpdate, btnBack;
    Context c = this;
    TextView tvQuestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initialize() {
        dao = new DAOReview();
        etReview = findViewById(R.id.etReview);
        tvQuestion = findViewById(R.id.tvQuestion);
        etRating = findViewById(R.id.etRating);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnBack);

        Date currentDate = new Date();
// format date and time as string
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy h:mmaa");
         dateToday = dateFormat.format(currentDate);


        Intent i = getIntent();
        if(i.hasExtra("search") && i.hasExtra("email")) {
            search = i.getStringExtra("search");
            email = i.getStringExtra("email");
            status = i.getStringExtra("status");
            key = i.getStringExtra("key");
            review = i.getStringExtra("review");
            rating = i.getStringExtra("rating");

            if(status.equals("create")) {
                btnSubmit.setVisibility(View.VISIBLE);
                btnUpdate.setVisibility(View.GONE);

            }else if(status.equals("update")) {
                btnSubmit.setVisibility(View.GONE);
                btnUpdate.setVisibility(View.VISIBLE);
                etRating.setRating(Float.parseFloat(rating));
                etReview.setText(review);

            }
            tvQuestion.setText("How was the " + search.toUpperCase()+"?");
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YoYo.with(Techniques.Pulse).duration(50).playOn(btnSubmit);
                    boolean isValid = true;
                    String review = etReview.getText().toString();

                    if (review.isEmpty()) {
                        etReview.setError("Please enter a review");
                        isValid = false;
                    }
                    float rating = etRating.getRating();
                    if (rating == 0) {
                        // Show an error message
                        Toast.makeText(c, "Please select a rating", Toast.LENGTH_SHORT).show();
                        isValid = false;
                    }
                    if (isValid) {

                        ReviewData emp = new ReviewData(email, etReview.getText().toString(), Float.toString(etRating.getRating()), search.toLowerCase(), dateToday);

                        dao.add(emp).addOnSuccessListener(suc -> {
                            Toast.makeText(c, "Thanks for your review!", Toast.LENGTH_SHORT).show();

                            resetToDefault();
                            finish();

                            Intent j = new Intent(c, FilmDetailsActivity.class);
                            j.putExtra("search", search);
                            j.putExtra("email", email);
                            j.putExtra("success", "success");
                            startActivity(j);

                        }).addOnFailureListener(er -> {
                            Toast.makeText(c, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.Pulse).duration(50).playOn(btnBack);
                    Intent putAddEntryIntent = new Intent();
                    setResult(RESULT_OK, putAddEntryIntent);
                    finish();
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YoYo.with(Techniques.Pulse).duration(50).playOn(btnUpdate);
                    boolean isValid = true;
                    String review = etReview.getText().toString();
                    if (review.isEmpty()) {
                        etReview.setError("Please enter an email address");
                        isValid = false;
                    }
                    float rating = etRating.getRating();
                    if (rating == 0) {
                        // Show an error message
                        Toast.makeText(c, "Please select a rating", Toast.LENGTH_SHORT).show();
                        isValid = false;
                    }
                    if (isValid) {
                        HashMap<String, Object> hashMap = new HashMap<>();


                        hashMap.put("date", dateToday);
                        hashMap.put("email", email);
                        hashMap.put("rating", Float.toString(etRating.getRating()));
                        hashMap.put("review", etReview.getText().toString());
                        hashMap.put("title", search.toLowerCase());


                        ReviewData emp = new ReviewData(email, etReview.getText().toString(), Float.toString(etRating.getRating()), search.toLowerCase(), dateToday);
                        dao.update(key, hashMap).addOnSuccessListener(suc -> {
                            Toast.makeText(c, "Review Updated", Toast.LENGTH_SHORT).show();
                            resetToDefault();
                            finish();
                        }).addOnFailureListener(er -> {
                            Toast.makeText(c, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        }
    }
    public void resetToDefault(){
        etReview.setText("");
        etRating.setRating(0);
    }
}