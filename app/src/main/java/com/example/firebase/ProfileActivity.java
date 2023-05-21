package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.text.WordUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    Button btnBack,btnUpdate;
    EditText etFirstname,etLastname;
    DAOUser dao;
    Context c = this;
    String key;
    Bitmap btmpPicture;
    static  final int REQUEST_IMAGE_CAPTURE = 10;

    ImageView ivPicture;

    Button  btnTakePicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initilize();
    }

    private void initilize() {
        dao = new DAOUser();
        btnBack = findViewById(R.id.btnBack);
        btnUpdate = findViewById(R.id.btnUpdate);

        etFirstname = findViewById(R.id.etFirstname);
        etLastname = findViewById(R.id.etLastname);
        Intent i = getIntent();
        if(i.hasExtra("email")) {

            String email = i.getStringExtra("email");
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserData");
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                        String userEmail = snapshot1.child("email").getValue(String.class);
                        String firstname = snapshot1.child("firstname").getValue(String.class);
                        String lastname = snapshot1.child("lastname").getValue(String.class);
                        if (email.equals(userEmail)) {
                           etLastname.setText(lastname);
                            etFirstname.setText(firstname);
                             key = snapshot1.getKey();
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YoYo.with(Techniques.Pulse).duration(50).playOn(btnUpdate);
                    boolean isValid = true;
                    String updateLastname = etLastname.getText().toString();
                    String updateFirstname = etFirstname.getText().toString();
                    if (updateLastname.isEmpty()) {
                        etLastname.setError("Please enter your last name");
                        isValid = false;
                    }
                    if (updateFirstname.isEmpty()) {
                        etFirstname.setError("Please enter your first name");
                        isValid = false;
                    }

                    if (isValid) {
                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("lastname", updateLastname);
                        hashMap.put("firstname", updateFirstname);

                        dao.update(key, hashMap).addOnSuccessListener(suc -> {
                            finish();
                            Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(er -> {
                            Toast.makeText(getApplicationContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            });
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Pulse).duration(50).playOn(btnBack);
                Intent putAddEntryIntent = new Intent();
                setResult(RESULT_OK,putAddEntryIntent);
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}