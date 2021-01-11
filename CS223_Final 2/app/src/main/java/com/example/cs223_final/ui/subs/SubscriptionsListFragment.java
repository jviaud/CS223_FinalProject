package com.example.cs223_final.ui.subs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs223_final.App;
import com.example.cs223_final.MainActivity;
import com.example.cs223_final.R;
import com.example.cs223_final.Subscription;
import com.example.cs223_final.ui.home.add.NewSubscription;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SubscriptionsListFragment extends Fragment  {
    private List<Subscription> subscriptions;
    private RecyclerView recycler;
    private SubscriptionImageAdapter adapter;
    private Subscription deleted_sub = null, renew_sub = null;
    private FrameLayout nocontent;
    private int renewPostition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.subscription_fragment_layout, container, false);

        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).setActionBarTitle("Subscriptions");
        setHasOptionsMenu(true);


        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycler = view.findViewById(R.id.sub_recycler);

        // LinearLayoutManager layoutManagerWeek = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);


        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setHasFixedSize(true);

        subscriptions = new ArrayList<>(App.all_subscriptions);
        nocontent = view.findViewById(R.id.no_content_subs);


        adapter = new SubscriptionImageAdapter(getContext(), subscriptions);
        recycler.setAdapter(adapter);
        App.db.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subscriptions.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Subscription upload = snapshot.getValue(Subscription.class);
                    upload.setKey(snapshot.getKey());

                    subscriptions.add(upload);


                }
                Collections.sort(subscriptions, Collections.reverseOrder());
                adapter.notifyDataSetChanged();

                if (subscriptions.isEmpty()) {
                    nocontent.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(scb);
        itemTouchHelper.attachToRecyclerView(recycler);
    }

    ItemTouchHelper.SimpleCallback scb = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();


            if (direction == ItemTouchHelper.RIGHT) {
                deleted_sub = subscriptions.get(position);
                subscriptions.remove(position);
                adapter.notifyItemRemoved(position);
                undo(position);

            }

            if (direction == ItemTouchHelper.LEFT) {
                renewPostition = position;
                renew_sub = subscriptions.get(position);
                Intent intent = new Intent(getActivity(), NewSubscription.class);
                intent.putExtra("RENEW", renew_sub);
                startActivityForResult(intent, 1);
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.RED))
                    .addSwipeRightActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate();

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.TEAL))
                    .addSwipeLeftActionIcon(R.drawable.ic_renew)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        }
    };

    private void undo(final int position) {
        Snackbar.make(recycler, deleted_sub.getName(), Snackbar.LENGTH_SHORT)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subscriptions.add(position, deleted_sub);
                        adapter.notifyItemInserted(position);
                    }
                })
                .setActionTextColor(ContextCompat.getColor(getContext(), R.color.TEAL))
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
//
                        App.db.child(deleted_sub.getKey()).removeValue();
                        if (event == Snackbar.Callback.DISMISS_EVENT_ACTION) {
                            App.db.push().setValue(deleted_sub);
                            Toast.makeText(getContext(), "UNDO", Toast.LENGTH_SHORT).show();
                        }
                        if (position == 0) {
                            LinearLayoutManager layoutManager = (LinearLayoutManager) recycler
                                    .getLayoutManager();
                            layoutManager.scrollToPositionWithOffset(0, 0);
                        }
                    }
                })
                .show();


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //MAKES
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //CALLLED WHEN YOU CLICK SEARCH BUTTON
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //CALLED AS YOU ARE TYPING

                adapter.getFilter().filter(newText);
                return false;
            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //IF THE REQUEST IS RECEIVED
        if (requestCode == 1) {
            //REQUEST RESULT IS ONLY SET TO OKAY AFTER THE NEW ENTRY IS MADE SUCCESSFULLY
            if (resultCode == Activity.RESULT_OK) {
                //REMOVE ENTRY FROM DB AND SUBSCRIPTION LIST
                App.db.child(renew_sub.getKey()).removeValue();
                subscriptions.remove(renewPostition);
            }
        }
        //NOTIFY ADAPTER THAT THERE HAS BEEN A CHANGE AT THIS POSITION
        //SINCE SWIPING REMOVES THE CARD REGARDLESS IF RENEW IS SUCCESSFUL
        //THIS MUST BE DONE OUTSIDE THE IF STATEMENTS
        adapter.notifyItemChanged(renewPostition);
    }
}
