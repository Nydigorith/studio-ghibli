package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageButton;

public class MainActivity extends AppCompatActivity {

    String currentIdentifier, toPassImage;
    Context context;
    int layout;
    RecyclerView rvFilmList;
    TextView tvMyName;
    RecyclerView.LayoutManager layoutManager;
    List<SlideModel> slideModels;
    FilmAdapter myRecyclerViewAdapter;
    AdapterView.OnLongClickListener listener;
    ImageSlider imageSlider;
    ArrayList<FilmData> filmDataList = new ArrayList<>();
    final int WATCH_REQUEST_CODE = 10;
    final int FILM_DETAILS_REQUEST_CODE = 1;
    final int FILM_DETAILS_1REQUEST_CODE = 1111;
    final int PROFILE_REQUEST_CODE = 5;
    private FirebaseAuth mAuth;
    GifImageButton btnWatchlist;
    ImageButton btnProfile,btnLogout;
    Context c = this;
    DAOUser dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
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

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // currentIdentifier = currentUser.getEmail();
        } else {
            // startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        getName();
    }

    private void initialize() {

        imageSlider = findViewById(R.id.imageSlider);

        slideModels = new ArrayList<>();

        dao = new DAOUser();
        Intent i = getIntent();
        if (i.hasExtra("firstname") && i.hasExtra("lastname") && i.hasExtra("email")) {
            String firstname = i.getStringExtra("firstname");
            String lastname = i.getStringExtra("lastname");
            String email = i.getStringExtra("email");
            currentIdentifier = email;

            UserData emp = new UserData(firstname, lastname, email,
                    BitmapFactory.decodeResource(c.getResources(), R.drawable.ic_launcher_background));

            dao.add(emp).addOnSuccessListener(suc -> {
                Toast.makeText(c, "Details Stored!", Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(er -> {
                Toast.makeText(c, "" + er.getMessage(), Toast.LENGTH_SHORT).show();
            });

        } else {
            String email = i.getStringExtra("email");
            currentIdentifier = email;
        }
        tvMyName = findViewById(R.id.tvMyName);
        mAuth = FirebaseAuth.getInstance();
        btnLogout = findViewById(R.id.btnLogout);
        btnProfile = findViewById(R.id.btnProfile);
        btnWatchlist = findViewById(R.id.btnWatchlist);
        TextView tvNoFilmsFound = findViewById(R.id.tvNoFilmsFound);

        DAOEmplyee dao = new DAOEmplyee();
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myRecyclerViewAdapter.filter(newText);
                return true;
            }
        });

        String url = "https://studio-ghibli-films-api.herokuapp.com/api";
        RequestQueue r = Volley.newRequestQueue(c);
        r.start();

        JsonObjectRequest json = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Iterator<String> keys = response.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONObject movie = response.getJSONObject(key);
                            String title = movie.getString("title");
                            String poster = movie.getString("poster");
                            toPassImage = poster;
                            filmDataList.add(new FilmData(poster, title));
                        }

                        rvFilmList = findViewById(R.id.rvFilmList);
                        layoutManager = new LinearLayoutManager(c);
                        rvFilmList.setLayoutManager(layoutManager);
                        myRecyclerViewAdapter = new FilmAdapter(c, R.layout.recycler_view_film, filmDataList,tvNoFilmsFound);
                        rvFilmList.setAdapter(myRecyclerViewAdapter);

                        myRecyclerViewAdapter.setOnItemClickListener(new FilmAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(String title) {
                                for (int i = 0; i < filmDataList.size(); i++) {
                                    FilmData film = filmDataList.get(i);
                                    if (film.getTitle().equals(title)) {

                                        Intent j = new Intent(c, FilmDetailsActivity.class);
                                        j.putExtra("search", title);
                                        j.putExtra("email", currentIdentifier);
                                        j.putExtra("toPassImage", toPassImage);
                                        j.putExtra("button", "main");
                                        startActivityForResult(j, FILM_DETAILS_REQUEST_CODE);
                                        break;
                                    }
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(c, "API Error", Toast.LENGTH_SHORT).show();
                });

        r.add(json);

        rvFilmList = findViewById(R.id.rvFilmList);
        layoutManager = new LinearLayoutManager(c);
        rvFilmList.setLayoutManager(layoutManager);
        myRecyclerViewAdapter = new FilmAdapter(c, R.layout.recycler_view_film, filmDataList, tvNoFilmsFound);
        rvFilmList.setAdapter(myRecyclerViewAdapter);
        getTopMovies();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Flash).duration(50).playOn(btnLogout);
                logout();
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Flash).duration(30).playOn(btnProfile);
                Intent z = new Intent(c, ProfileActivity.class);
                z.putExtra("email", currentIdentifier);
                startActivityForResult(z, PROFILE_REQUEST_CODE);
            }
        });

        btnWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Bounce).duration(30).playOn(btnWatchlist);
                Intent y = new Intent(c, WatchlistActivity.class);
                y.putExtra("email", currentIdentifier);
                y.putExtra("toPassImage", toPassImage);
                startActivityForResult(y, WATCH_REQUEST_CODE);
            }
        });

    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirmation")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void getName() {

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserData");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    String userEmail = snapshot1.child("email").getValue(String.class);
                    String firstname = snapshot1.child("firstname").getValue(String.class);
                    String lastname = snapshot1.child("lastname").getValue(String.class);
                    String fullname = firstname + " " + lastname;
                    if (currentIdentifier.equals(userEmail)) {
                        tvMyName.setText(WordUtils.capitalizeFully(fullname));
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getTopMovies() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ReviewData");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                slideModels.clear();
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    Map<String, Double> movieRatings = new HashMap<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String title = snapshot.child("title").getValue(String.class);
                        String ratingString = snapshot.child("rating").getValue(String.class);
                        double rating = Double.parseDouble(ratingString);

                        // Calculate the average rating for each movie
                        if (movieRatings.containsKey(title)) {
                            double currentRating = movieRatings.get(title);
                            double updatedRating = (currentRating + rating) / 2;
                            movieRatings.put(title, updatedRating);
                        } else {
                            movieRatings.put(title, rating);
                        }
                    }

                    List<Map.Entry<String, Double>> sortedMovies = new ArrayList<>(movieRatings.entrySet());
                    Collections.sort(sortedMovies, (a, b) -> Double.compare(b.getValue(), a.getValue()));

                    List<String> topMovies = new ArrayList<>();
                    int count = 0;
                    for (Map.Entry<String, Double> entry : sortedMovies) {
                        topMovies.add(entry.getKey());
                        count++;
                        if (count == 3) {
                            break;
                        }
                    }
                    for (String movieTitle : topMovies) {
                        // Create a condition based on the movie title
                        if (movieTitle.equals("grave of the fireflies")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/2001933.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Grave of the Fireflies");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("my neighbor totoro")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/33505.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("My Neighbor Totoro");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("castle in the sky")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/125880.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Castle in the Sky");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("kiki's delivery service")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/496068.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Kiki's Delivery Service");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("only yesterday")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/2406664.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Only Yesterday");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("porco rosso")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/1542170.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Porco Rosso");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("ocean waves")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/4694116.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Ocean Waves");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("pom poko")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/8105925.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Pom Poko");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("whisper of the heart")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/1459716.png",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Whisper of the Heart");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("princess mononoke")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/1343485.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Princess Mononoke");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("my neighbors the yamadas")) {
                            SlideModel slideModel = new SlideModel(
                                    "https://images8.alphacoders.com/808/thumb-1920-808053.jpg", ScaleTypes.FIT);
                            slideModel.setTitle("My Neighbors the Yamadas");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("spirited away")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/436243.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Spirited Away");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("the cat returns")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/2350971.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("The Cat Returns");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("howl's moving castle")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/1085356.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Howl's Moving Castle");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("tales from earthsea")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/2407418.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Tales from Earthsea");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("ponyo")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/715896.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Ponyo");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("arrietty")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/2189085.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Arrietty");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("from up on poppy hill")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/2682126.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("From Up on Poppy Hill");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("the wind rises")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/2135774.png",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("The Wind Rises");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("the tale of the princess kaguya")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/2323323.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("The Tale of the Princess Kaguya");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("when marnie was there")) {
                            SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/2298409.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("When Marnie Was There");
                            slideModels.add(slideModel);
                        }
                        if (movieTitle.equals("earwig and the witch")) {
                            SlideModel slideModel = new SlideModel(
                                    "https://images6.fanpop.com/image/photos/43700000/Earwig-and-the-Witch-Wallpaper-studio-ghibli-43798829-1920-1080.jpg",
                                    ScaleTypes.FIT);
                            slideModel.setTitle("Earwig and the Witch");
                            slideModels.add(slideModel);
                        }
                    }
                    ;

                } else {
                    SlideModel slideModel = new SlideModel("https://wallpaperaccess.com/full/2323323.jpg",
                            ScaleTypes.FIT);
                    slideModel.setTitle("The Tale of the Princess Kaguya");
                    slideModels.add(slideModel);
                    SlideModel slideModel1 = new SlideModel("https://wallpaperaccess.com/full/2406664.jpg",
                            ScaleTypes.FIT);
                    slideModel1.setTitle("Only Yesterday");
                    slideModels.add(slideModel1);
                    SlideModel slideModel2 = new SlideModel("https://wallpaperaccess.com/full/2001933.jpg",
                            ScaleTypes.FIT);
                    slideModel2.setTitle("Grave of the Fireflies");
                    slideModels.add(slideModel2);
                }

                imageSlider.setImageList(slideModels);
                imageSlider.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onItemSelected(int position) {
                        // Handle the click event for the image at the given position
                        // You can retrieve the clicked image from the imageList using the position
                        // parameter

                        // Example: Display a toast message with the title of the clicked image
                        SlideModel clickedImage = slideModels.get(position);
                        // Toast.makeText(getApplicationContext(), clickedImage.getTitle(),
                        // Toast.LENGTH_SHORT).show();

                        String selectedTitle = clickedImage.getTitle();
                        Intent j = new Intent(c, FilmDetailsActivity.class);
                        j.putExtra("search", selectedTitle);
                        j.putExtra("email", currentIdentifier);
                        j.putExtra("toPassImage", toPassImage);
                        j.putExtra("button", "main");
                        startActivityForResult(j, FILM_DETAILS_1REQUEST_CODE);
                    }

                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILM_DETAILS_REQUEST_CODE && resultCode == RESULT_OK) {
            getName();
            getTopMovies();
        }

        if (requestCode == FILM_DETAILS_1REQUEST_CODE && resultCode == RESULT_OK) {

            getName();
            getTopMovies();
        }
        if (requestCode == PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {

            getName();
            getTopMovies();
        }
        if (requestCode == WATCH_REQUEST_CODE && resultCode == RESULT_OK) {

            getName();
            getTopMovies();
        }
    }

}