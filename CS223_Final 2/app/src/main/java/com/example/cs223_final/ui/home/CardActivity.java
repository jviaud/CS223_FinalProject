package com.example.cs223_final.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.cs223_final.R;
import com.example.cs223_final.Subscription;
import com.squareup.picasso.Picasso;

public class CardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_activity);

        Intent intent = getIntent();
        Subscription subscription =  intent.getParcelableExtra("Sub");




        ImageView icon = findViewById(R.id.icon);
        ImageView close = findViewById(R.id.btn_close);
        TextView name = findViewById(R.id.name);
        TextView category = findViewById(R.id.category);
        TextView due = findViewById(R.id.due);
        TextView cost = findViewById(R.id.cost);
        ConstraintLayout layout = findViewById(R.id.constraint_overlay);


        name.setText(subscription.getName());
        category.setText(subscription.getCategory());
        cost.setText(subscription.getPrice());
        due.setText(subscription.getModifiedDate(subscription.getDueDate()));


        String url =subscription.getUrl();

        Picasso.get()
                .load(url)
                .noFade()
                .fit()
                .placeholder(R.drawable.circlebackground)
                .centerCrop()
                .into(icon);
        Picasso.get().setLoggingEnabled(true);

        layout.setOnContextClickListener(new View.OnContextClickListener() {
            @Override
            public boolean onContextClick(View v) {
                finish();
                return true;
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void clicked(View view) {
        finishAfterTransition();
    }
}
