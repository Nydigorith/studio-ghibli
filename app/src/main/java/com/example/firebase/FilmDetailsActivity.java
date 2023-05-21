package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;


public class FilmDetailsActivity extends AppCompatActivity {

    final int REVIEW_REQUEST_CODE  =2;
    TextView tvTitle, tvAppRating,tvGenre, tvRating, tvRelease, tvDirector, tvScreenwriters, tvProducers, tvRuntimeMinutes, tvAwards, tvSynopsis, tvRottenTomatoes, tvImdb, tvMusic;
    ImageView ivPoster;
    Button btnBack,btnSubmit,btnUpdate,btnDelete;
    YouTubePlayerView ypTrailer;
    ToggleButton tbMusic,tbWatchlist;
    Boolean hasEntry;
    RecyclerView rvReview;
    RecyclerView.LayoutManager layoutManager;
    ReviewAdapter reviewAdapter;
    String search,email,key,keyWatch,topassPoster;
    ArrayList<ReviewData> reviewDataList = new ArrayList<>();
    String myReview,myRating,myDate,passedPoster,buttonState;

    private FirebaseAuth mAuth;
    private MediaPlayer mediaPlayer;
    private DatabaseReference databaseReference;
    DAOReview dao;
    DAOWatchlist daow;
    Context c = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_details);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void initialize() {


        mAuth = FirebaseAuth.getInstance();
         dao = new DAOReview();
        btnSubmit = findViewById(R.id.btnSubmit);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        tvAppRating = findViewById(R.id.tvAppRating);
        tvMusic = findViewById(R.id.tvMusic);
        tbMusic = findViewById(R.id.tbMusic);
        tvTitle = findViewById(R.id.tvTitle);
        tvGenre = findViewById(R.id.tvGenre);
        tvRating = findViewById(R.id.tvRating);
        tvRelease = findViewById(R.id.tvRelease);
        tvDirector = findViewById(R.id.tvDirector);
        tvScreenwriters = findViewById(R.id.tvScreenwriters);
        tvProducers = findViewById(R.id.tvProducers);
        tvRuntimeMinutes = findViewById(R.id.tvRuntimeMinutes);
        tvAwards = findViewById(R.id.tvAwards);
        tvSynopsis = findViewById(R.id.tvSynopsis);
        tvRottenTomatoes = findViewById(R.id.tvRottenTomatoes);
        tvImdb = findViewById(R.id.tvImdb);
        ivPoster = findViewById(R.id.ivPoster);
        rvReview = findViewById(R.id.rvReview);
        btnBack = findViewById(R.id.btnBack);
        ypTrailer = findViewById(R.id.ypTrailer);
        getLifecycle().addObserver(ypTrailer);

        btnSubmit.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);
        btnUpdate.setVisibility(View.VISIBLE);
        tvAppRating.setVisibility(View.VISIBLE);

        rvReview.setVisibility(View.VISIBLE);
        ypTrailer.setVisibility(View.VISIBLE);
        tbMusic.setVisibility(View.VISIBLE);
        tbWatchlist = findViewById(R.id.tbWatchlist);
        tbWatchlist.setChecked(false);
        Intent i = getIntent();

        if(i.hasExtra("search") && i.hasExtra("email")) {
            buttonState = i.getStringExtra("button");
            search = i.getStringExtra("search");
            email = i.getStringExtra("email");
            passedPoster =   i.getStringExtra("toPassImage");
            getWatchlist();

            if(i.hasExtra("success")){
                finish();
            }
          String url = "";
            if(search.toLowerCase().replace("?", "").equals("the tale of the princess kaguya")) {
                url = "https://studio-ghibli-films-api.herokuapp.com/api/" + "the tale of princess kaguya";
            } else {
                url = "https://studio-ghibli-films-api.herokuapp.com/api/" + search.toLowerCase().replace("?", "");
            }

            RequestQueue r = Volley.newRequestQueue(c);
            r.start();

            JsonObjectRequest json = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {

                            getReview();

                            String responseTitle = response.getString("title");
                            tvTitle.setText(responseTitle);

                            String responseSynopsis = response.getString("synopsis");
                            tvSynopsis.setText(responseSynopsis);

                            String responseGenre = response.getString("genre");
                            tvGenre.setText(responseGenre);

                            String responseRating = response.getString("rating");
                            tvRating.setText(responseRating);

                            String responseRelease = response.getString("release");
                            tvRelease.setText(responseRelease);

                            String responseDirector = response.getString("director");
                            tvDirector.setText(responseDirector);


                            JSONArray screenwritersArray = response.getJSONArray("screenwriters");
                            String[] screenwriters = new String[screenwritersArray.length()];

                            for (int k = 0; k < screenwritersArray.length(); k++) {
                                screenwriters[k] = screenwritersArray.getString(k);
                            }

                            String joinedScreenwriters = TextUtils.join(", ", screenwriters);
                            tvScreenwriters.setText(joinedScreenwriters);

                            JSONArray producersArray = response.getJSONArray("producers");
                            String[] producers = new String[producersArray.length()];

                            for (int l = 0; l < producersArray.length(); l++) {
                                producers[l] = producersArray.getString(l);
                            }

                            String joinedProducers = TextUtils.join(", ", producers);
                            tvProducers.setText(joinedProducers);

                            String responseRuntimeMinutes = response.getString("runtimeMinutes");
                            tvRuntimeMinutes.setText(responseRuntimeMinutes + " Minutes");

                            JSONArray responseAwardsArray = response.getJSONArray("awards");
                            String[] awards = new String[responseAwardsArray.length()];

                            for (int j = 0; j < responseAwardsArray.length(); j++) {
                                awards[j] = responseAwardsArray.getString(j);
                            }

                            String joinedAwards = TextUtils.join(", ", awards);
                            if(joinedAwards.length() == 0) {
                                tvAwards.setText("No Award");
                            } else {
                                tvAwards.setText(joinedAwards);
                            }


                            JSONObject reviewsObject = response.getJSONObject("reviews");
                            String responseRottenTomatoes = reviewsObject.getString("rottenTomatoes");
                            tvRottenTomatoes.setText(responseRottenTomatoes);

                            String responseImdb = reviewsObject.getString("imdb");
                            tvImdb.setText(responseImdb);

                            String responseMusic = response.getString("music");
                            tvMusic.setText(responseMusic);

                            String responsePoster = response.getString("poster");
                            topassPoster = responsePoster;
                            Picasso.get()
                                    .load(responsePoster)
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.ic_launcher_background)
                                    .into(ivPoster);

                            switch (search.toLowerCase()) {
                                case "castle in the sky":
                                    loadYoutubeVideo("FWjiXOqRKhk");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.castle_in_the_sky);
                                    break;
                                case "grave of the fireflies":
                                    loadYoutubeVideo("4vPeTSRd580");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.grave_of_fireflies);
                                    break;
                                case "my neighbor totoro":
                                    loadYoutubeVideo("92a7Hj0ijLs");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.my_neighbor_totoro);
                                    break;
                                case "kiki's delivery service":
                                    loadYoutubeVideo("4bG17OYs-GA");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.kiki_s_delivery_service);
                                    break;
                                case "only yesterday":
                                    loadYoutubeVideo("OfkQlZArxw0");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.only_yesterday);
                                    break;
                                case "porco rosso":
                                    loadYoutubeVideo("awEC-aLDzjs");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.porco_rosso);
                                    break;
                                case "ocean waves":
                                    loadYoutubeVideo("tfkHiHjrqa8");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.ocean_waves);
                                    break;
                                case "pom poko":
                                    loadYoutubeVideo("_7cowIHjCD4");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.pom_poko);
                                    break;
                                case "whisper of the heart":
                                    loadYoutubeVideo("0pVkiod6V0U");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.whisper_of_the_heart);
                                    break;
                                case "princess mononoke":
                                    loadYoutubeVideo("4OiMOHRDs14");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.princess_mononoke);
                                    break;
                                case "my neighbors the yamadas":
                                    loadYoutubeVideo("1C9ujuCPlnY");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.my_neighbors_the_yamadas);
                                    break;
                                case "spirited away":
                                    loadYoutubeVideo("ByXuk9QqQkk");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.spirited_away);
                                    break;
                                case "the cat returns":
                                    loadYoutubeVideo("Gp-H_YOcYTM");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.the_cat_returns);
                                    break;
                                case "howl's moving castle":
                                    loadYoutubeVideo("iwROgK94zcM");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.howl_s_moving_castle);
                                    break;
                                case "tales from earthsea":
                                    loadYoutubeVideo("8hxYx3Jq3kI");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.tales_from_earthsea);
                                    break;
                                case "ponyo":
                                    loadYoutubeVideo("CsR3KVgBzSM");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.ponyo);
                                    break;
                                case "arrietty":
                                    loadYoutubeVideo("VlMe7PavaRQ");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.arrietty);
                                    break;
                                case "from up on poppy hill":
                                    loadYoutubeVideo("9nzpk_Br6yo");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.from_up_on_poppy_hill);
                                    break;
                                case "the wind rises":
                                    loadYoutubeVideo("PhHoCnRg1Yw");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.the_wind_rises);
                                    break;
                                case "the tale of the princess kaguya":
                                    loadYoutubeVideo("W71mtorCZDw");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.the_tale_of_princess_kaguya);
                                    break;
                                case "when marnie was there":
                                    loadYoutubeVideo("jjmrxqcQdYg");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.when_marnie_was_there);
                                    break;
                                case "earwig and the witch":
                                    loadYoutubeVideo("_PfhotgXEeQ");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.earwig_and_the_witch);
                                    break;
                                case "how do you live?":
                                    loadYoutubeVideo("tCya5pP2s04");
                                    mediaPlayer = MediaPlayer.create(this, R.raw.howl_s_moving_castle);
                                    break;
                                default:
                                    // Show an error message if the movie title is not found
                                    //Toast.makeText(this, "Movie not found", Toast.LENGTH_SHORT).show();
                                    ypTrailer.setVisibility(View.GONE);
                                    tbMusic.setVisibility(View.GONE);
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {

                        btnUpdate.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                        rvReview.setVisibility(View.GONE);
                        ypTrailer.setVisibility(View.GONE);
                        tbMusic.setVisibility(View.GONE);
                        tvAppRating.setVisibility(View.GONE);
                        tvTitle.setText(search);
                        tvGenre.setText("TBA");
                        tvRating.setText("TBA");
                        tvRelease.setText("TBA");
                        tvDirector.setText("TBA");
                        tvScreenwriters.setText("TBA");
                        tvSynopsis.setText("TBA");
                        tvProducers.setText("TBA");
                        tvRuntimeMinutes.setText("TBA");
                        tvAwards.setText("TBA");
                        tvRottenTomatoes.setText("TBA");
                        tvImdb.setText("TBA");
                        tvMusic.setText("TBA");
                        Picasso.get()
                                .load(passedPoster)
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_launcher_background)
                                .into(ivPoster);

                        topassPoster = passedPoster;
                    });

            tbMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    YoYo.with(Techniques.Pulse).duration(50).playOn(tbMusic);
                    if (isChecked) {
                        mediaPlayer.start();
                    } else {
                        mediaPlayer.pause();
                    }
                }
            });
            r.add(json);

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YoYo.with(Techniques.Pulse).duration(50).playOn(btnSubmit);
                    //createUpdateReview(search, email,"create","","","");

                    Intent j = new Intent(c, Review.class);
                    j.putExtra("search", search);
                    j.putExtra("email", email);
                    j.putExtra("status", "create");
                     startActivity(j);
                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YoYo.with(Techniques.Pulse).duration(50).playOn(btnDelete);
                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setTitle("Delete Review");
                    builder.setMessage("Are you sure you want to delete this review?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Delete the review
                            dao.remove(key).addOnSuccessListener(suc->{
                                Toast.makeText(c, "Review Deleted", Toast.LENGTH_SHORT).show();
                                reviewDataList.clear();
                                getReviewsForTitle(search);
                                getReview();
                            }).addOnFailureListener(er->{
                                Toast.makeText(c, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                            });

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Do nothing
                        }
                    });
                    builder.show();
                }
            });


            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    YoYo.with(Techniques.Pulse).duration(50).playOn(btnUpdate);

                    createUpdateReview(search, email,"update",key,myReview,myRating);
                }
            });

            tbWatchlist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    YoYo.with(Techniques.Pulse).duration(100).playOn(tbWatchlist);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("WatchlistData");

                    if (isChecked) {
                        // Add to watchlist
                        if (keyWatch != null) {
                            databaseReference.child(keyWatch).child("title").setValue(search.toLowerCase());
                            //Toast.makeText(getApplicationContext(), "Added to Watchlist", Toast.LENGTH_SHORT).show();
                        } else {
                            String key = databaseReference.push().getKey();
                            WatchlistData watchlistData = new WatchlistData(email,search.toLowerCase(),topassPoster);
                            databaseReference.child(key).setValue(watchlistData);
                            keyWatch = key;
                           // Toast.makeText(getApplicationContext(), "Added to Watchlist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Remove from watchlist
                        if (keyWatch != null) {
                            databaseReference.child(keyWatch).removeValue();
                            keyWatch = null;
                           // Toast.makeText(getApplicationContext(), "Removed from Watchlist", Toast.LENGTH_SHORT).show();
                        }
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

    public void getReview() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ReviewData");
        Query query = databaseReference.orderByChild("title").equalTo(search.toLowerCase());
        hasEntry = false;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewDataList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String dbEmail = snapshot.child("email").getValue(String.class);
                    String dbReview = snapshot.child("review").getValue(String.class);
                    String dbRating = snapshot.child("rating").getValue(String.class);
                    String dbTitle = snapshot.child("title").getValue(String.class);
                    String dbDate = snapshot.child("date").getValue(String.class);

                    if (dbEmail.equals(email) && dbTitle.equals(search.toLowerCase())) {
                        key = snapshot.getKey();
                        myReview = dbReview;
                        myRating = dbRating;
                        myDate = dbDate;

                        btnSubmit.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.VISIBLE);
                        btnUpdate.setVisibility(View.VISIBLE);
                        hasEntry = true;
                    } else {
                        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserData");
                        userReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                List<ReviewData> newReviews = new ArrayList<>();
                                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                                    String userEmail = snapshot1.child("email").getValue(String.class);
                                    String firstname = snapshot1.child("firstname").getValue(String.class);
                                    String lastname = snapshot1.child("lastname").getValue(String.class);
                                    String fullname = firstname+" "+lastname;
                                    if (dbEmail.equals(userEmail)) {
                                        newReviews.add(new ReviewData(WordUtils.capitalizeFully(fullname), dbReview, dbRating, dbTitle, dbDate));
                                      //  Toast.makeText(FilmDetailsActivity.this, WordUtils.capitalizeFully(fullname), Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                                // add new reviews to the main list here
                                reviewDataList.addAll(newReviews);
                                // update the adapter here
                                reviewAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

                if (hasEntry) {
                    reviewDataList.add(0, new ReviewData("You", myReview, myRating, "", myDate));
                } else {
                    btnSubmit.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.GONE);
                    btnUpdate.setVisibility(View.GONE);
                }

                layoutManager = new LinearLayoutManager(c);
                rvReview.setLayoutManager(layoutManager);
                reviewAdapter = new ReviewAdapter(c, R.layout.recycler_view_review, reviewDataList);
                rvReview.setAdapter(reviewAdapter);

                getReviewsForTitle(search);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors
            }
        });
    }

    public void getWatchlist() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("WatchlistData");

        Query query1 = databaseReference.orderByChild("title").equalTo(search.toLowerCase());

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String dbEmail = snapshot.child("email").getValue(String.class);
                    String dbTitle = snapshot.child("title").getValue(String.class);
                    keyWatch = snapshot.getKey();
                    if (dbEmail.equals(email) && dbTitle.equals(search.toLowerCase())) {
                        tbWatchlist.setChecked(true);
                        isFound = true;
                        break; // exit the loop since we found a match
                    }
                }
                if (!isFound) {
                    tbWatchlist.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors
            }
        });
    }

    public void getReviewsForTitle(String title) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ReviewData");
        Query query = databaseReference.orderByChild("title").equalTo(title.toLowerCase());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float totalRating = 0;
                int reviewCount = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String ratingStr = snapshot.child("rating").getValue(String.class);

                    if (ratingStr != null && !ratingStr.isEmpty()) {
                        float rating = Float.parseFloat(ratingStr);
                        totalRating += rating;
                        reviewCount++;
                    }
                }

                if (reviewCount > 0) {
                    float averageRating = totalRating / reviewCount;
                  //  rbAppRating.setRating(averageRating);
                    tvAppRating.setText("RATING: " + averageRating);
                } else {
                    tvAppRating.setText("No ratings yet, rate " + WordUtils.capitalizeFully(search) + " now!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors
            }
        });
    }

    private void loadYoutubeVideo(String videoId) {
        ypTrailer.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0);
            }

//            @Override
//            public void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError error) {
//                super.onError(youTubePlayer, error);
//                if (error == PlayerConstants.PlayerError.VIDEO_NOT_FOUND) {
//                    Toast.makeText(FilmDetailsActivity.this, "Video not found!", Toast.LENGTH_SHORT).show();
//                }
//            }
        });
    }

    public void createUpdateReview(String title, String currentIdentifier,String status, String key, String review, String rating) {
        Intent j = new Intent(c, Review.class);
        j.putExtra("search", title);
        j.putExtra("email", currentIdentifier);
        j.putExtra("status", status);
        j.putExtra("key", key);
        j.putExtra("review", review);
        j.putExtra("rating", rating);
        // startActivity(j);
        startActivityForResult(j,REVIEW_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REVIEW_REQUEST_CODE && resultCode == RESULT_OK) {

            reviewDataList.clear();
            getReview();

                        rvReview.getLayoutManager().scrollToPosition(0);
        }
    }
}