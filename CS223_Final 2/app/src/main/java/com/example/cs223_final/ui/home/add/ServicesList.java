package com.example.cs223_final.ui.home.add;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.cs223_final.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServicesList extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ServicesImageAdapter mAdapter;
    private ProgressBar progress;


    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Toast.makeText(this, "HI", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_list);

        Toolbar toolbar = findViewById(R.id.service_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        progress = findViewById(R.id.progress_circle);


        mUploads = new ArrayList<>();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Services");

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Upload upload = snapshot.getValue(Upload.class);
                    mUploads.add(upload);
                    Collections.sort(mUploads,Collections.<Upload>reverseOrder());
                }
                mAdapter = new ServicesImageAdapter(ServicesList.this, mUploads);

                mRecyclerView.setAdapter(mAdapter);
                progress.setVisibility(View.INVISIBLE);

                mAdapter.setOnItemClickListener(new ServicesImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        Intent intent = getIntent();
                        intent.putExtra("SERVICE", mUploads.get(position));
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ServicesList.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progress.setVisibility(View.INVISIBLE);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //CALLLED WHEN YOU CLICK SEARCH BUTTON
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //CALLED AS YOU ARE TYPING

                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

}
