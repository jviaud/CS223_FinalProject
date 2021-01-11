package com.example.cs223_final;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class App extends Application {
    public static final String CHANNEL_1_ID = String.valueOf(R.string.alerts_channel);
    //public static Database db;
    public static DatabaseReference db;
    Subscription subscription;
    public static List<Subscription> all_subscriptions;

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseDatabase.getInstance().getReference().child("Subscriptions");
        StorageReference sf = FirebaseStorage.getInstance().getReference("Icons");
//
        all_subscriptions = new ArrayList<>();

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                all_subscriptions.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Subscription upload = snapshot.getValue(Subscription.class);

                    upload.setKey(snapshot.getKey());
                    all_subscriptions.add(upload);

                    //new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
                } Collections.sort(all_subscriptions, Collections.reverseOrder());
              //  Log.i("CARD", "ALL_SUB" + all_subscriptions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
