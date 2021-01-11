package com.example.cs223_final.ui.home.add;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cs223_final.App;
import com.example.cs223_final.R;
import com.example.cs223_final.Subscription;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewSubscription extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Spinner spinner;
    String[] time_duration = {"", "7 Days", "14 Days", "1 Month", "3 Months", "6 Months", "1 Year"};
    private TextView startDate, dueDate, serviceName, cost, error, category;
    ImageView icon;
    Subscription subscription;
    String url;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subscription_layout);



        Toolbar toolbar = findViewById(R.id.newSub_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //SET NAME VIEW & COST & Error Info % Icon & Category
        serviceName = findViewById(R.id.service_name);
        cost = findViewById(R.id.edit_cost);
        error = findViewById(R.id.error_info);
        icon = findViewById(R.id.img_card);
        category = findViewById(R.id.service_category);






        //NAME
        serviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewSubscription.this, ServicesList.class);
                startActivityForResult(intent, 1);
            }
        });


        //DATE
        startDate = findViewById(R.id.start_date);
        dueDate = findViewById(R.id.due_date);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();

            }
        });
        //END DATE


        //SPINNER
        spinner = findViewById(R.id.subscription_amount);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_layout, time_duration);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        dueDate(1,0);
                        break;
                    case 2:
                        dueDate(2,0);
                        break;
                    case 3:
                        dueDate(4,2);
                        break;
                    case 4:
                        dueDate(13,0);
                        break;
                    case 5:
                        dueDate(26,1);
                        break;
                    case 6:
                        dueDate(52,2);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        Subscription app = intent.getParcelableExtra("RENEW");
        //Toast.makeText(this, ""+app, Toast.LENGTH_SHORT).show();
        if(app!=null){
           // Toast.makeText(this, "NONO", Toast.LENGTH_SHORT).show();
            serviceName.setClickable(false);


            serviceName.setText(app.getName());
            category.setText(app.getCategory());

            Picasso.get()
                    .load(app.getUrl())
                    .fit()
                    .centerCrop()
                    .into(icon);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Upload upload = data.getParcelableExtra("SERVICE");
                serviceName.setText(upload.getName());
                category.setText(upload.getCategory());
                url = upload.getImageUrl();


                Picasso.get()
                        .load(upload.getImageUrl())
                        .fit()
                        .centerCrop()
                        .into(icon);

            }
        }

    }

    private void showDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(
                NewSubscription.this,
                NewSubscription.this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month = month + 1;
        String date = month + "/" + dayOfMonth + "/" + year;
        startDate.setText(date);
        //Toast.makeText(getContext(), date, Toast.LENGTH_SHORT).show();
    }

    public void dueDate(int week,int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        String start = startDate.getText().toString();
        String end = "";


        if (!start.equals("")) {
            try {
                Date date = sdf.parse(start);

                cal.setTime(date);
                cal.add(Calendar.WEEK_OF_YEAR, week);
                cal.add(Calendar.DAY_OF_YEAR, days);
                Log.i("DATE", "Date:" + date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            end = (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.YEAR);
        }
        dueDate.setText(end);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.newsub_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            Intent intent = getIntent();
            setResult(Activity.RESULT_OK, intent);
            insertData();
        }
        return super.onOptionsItemSelected(item);
    }

    public void insertData() {
        String mCost = cost.getText().toString();
        //String mLength = spinner.getSelectedItem().toString();
        String mStart = startDate.getText().toString();
        String mEnd = dueDate.getText().toString();
        String mService_name = serviceName.getText().toString();
        String mCategory = category.getText().toString();


        if (!mService_name.equals("")) {
            if (!mCost.equals("")) {
                if (!mStart.equals("")) {
                    if (!mEnd.equals("")) {
                        subscription = new Subscription(mService_name, mCost, mStart, mEnd, mCategory, url);
                        App.db.push().setValue(subscription);
                        Toast.makeText(NewSubscription.this, "SAVED", Toast.LENGTH_LONG).show();
                        finish();
                    } else
                        error.setVisibility(View.VISIBLE);
                } else
                    error.setVisibility(View.VISIBLE);
            } else
                error.setVisibility(View.VISIBLE);
        } else
            error.setVisibility(View.VISIBLE);

    }
}
