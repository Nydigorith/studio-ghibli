package com.example.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DAOWatchlist {
    private DatabaseReference databaseReference;

    public DAOWatchlist(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(WatchlistData.class.getSimpleName());

    }

    public Task<Void> add(WatchlistData emp){
        return databaseReference.push().setValue(emp);
    }
    public Task<Void> update(String key, HashMap<String, Object> hashMap){
        return databaseReference.child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key){
        return databaseReference.child(key).removeValue();
    }
}
