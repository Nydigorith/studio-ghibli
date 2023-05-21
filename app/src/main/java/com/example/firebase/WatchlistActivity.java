package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;

public class WatchlistActivity extends AppCompatActivity {
    RecyclerView rvWatchlist;
    Button btnBack;
    RecyclerView.LayoutManager layoutManager;
    FilmAdapter myRecyclerViewAdapter;
    TextView tvNoWatchlist,tvWatchlist;
    Context c =this;
    final int FILM_DETAILS_REQUEST_CODE  =1;
    ArrayList<FilmData> watchlistData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        initialize();
    }

    private void initialize() {

        Intent i = getIntent();
        if(i.hasExtra("email")) {

            String email = i.getStringExtra("email");
            String toPassImage = i.getStringExtra("toPassImage");


            tvNoWatchlist = findViewById(R.id.tvNoWatchlist);
            tvWatchlist = findViewById(R.id.tvWatchlist);

            rvWatchlist = findViewById(R.id.rvWatchlist);
            btnBack = findViewById(R.id.btnBack);

            layoutManager = new LinearLayoutManager(c);
            rvWatchlist.setLayoutManager(layoutManager);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("WatchlistData");

            Query query1 = databaseReference.orderByChild("email").equalTo(email);

            query1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ArrayList<FilmData> tempList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String dbEmail = snapshot.child("email").getValue(String.class);
                        String dbTitle = snapshot.child("title").getValue(String.class);
                        String dbPoster = snapshot.child("poster").getValue(String.class);

                        if (dbEmail.equals(email)) {
                            tempList.add(new FilmData(dbPoster, WordUtils.capitalizeFully(dbTitle)));
                        }
                    }
                    watchlistData.clear(); // clear the old data
                    watchlistData.addAll(tempList); // add the new data

                    // set up the adapter and the RecyclerView here
                    myRecyclerViewAdapter = new FilmAdapter(c, R.layout.recycler_view_film, watchlistData, tvNoWatchlist);
                    rvWatchlist.setAdapter(myRecyclerViewAdapter);

                    myRecyclerViewAdapter.setOnItemClickListener(new FilmAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String title) {
                            for (int i = 0; i < watchlistData.size(); i++) {
                                FilmData film = watchlistData.get(i);
                                if (film.getTitle().equals(title)) {

                                    Intent j = new Intent(c, FilmDetailsActivity.class);
                                    j.putExtra("search", title);
                                    j.putExtra("email", email);
                                    j.putExtra("toPassImage", toPassImage);
                                    j.putExtra("button", "watch");
                                    startActivityForResult(j,FILM_DETAILS_REQUEST_CODE);
                                    break;
                                }
                            }
                        }
                    });

                    // notify the adapter that the data has changed
                    myRecyclerViewAdapter.notifyDataSetChanged();

                    // check if watchlistData is empty and show/hide tvNoWatchlist accordingly
                    if (watchlistData.isEmpty()) {
                        tvNoWatchlist.setVisibility(View.VISIBLE);
                        tvWatchlist.setVisibility(View.GONE);
                    } else {
                        tvNoWatchlist.setVisibility(View.GONE);
                        tvWatchlist.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database errors
                }
            });


            myRecyclerViewAdapter = new FilmAdapter(c, R.layout.recycler_view_film, watchlistData, tvNoWatchlist);
            rvWatchlist.setAdapter(myRecyclerViewAdapter);

            myRecyclerViewAdapter.setOnItemClickListener(new FilmAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(String title) {
                    for (int i = 0; i < watchlistData.size(); i++) {
                        FilmData film = watchlistData.get(i);
                        if (film.getTitle().equals(title)) {
                            //comment lang
                            //currentIdentifier= "darwinsaluis.ramos14@gmail.com";
                            Intent j = new Intent(c, FilmDetailsActivity.class);
                            j.putExtra("search", title);
                            j.putExtra("email", email);
                            j.putExtra("toPassImage", toPassImage);
                            j.putExtra("button", "watch");
                            startActivityForResult(j,FILM_DETAILS_REQUEST_CODE);
                            break;
                        }
                    }
                }
            });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.Pulse).duration(30).playOn(btnBack);
                    Intent putAddEntryIntent = new Intent();
                    setResult(RESULT_OK, putAddEntryIntent);
                    finish();
                }
            });
        }
    }
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILM_DETAILS_REQUEST_CODE && resultCode == RESULT_OK) {
        }

    }
}