package com.example.cs223_final.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs223_final.App;
import com.example.cs223_final.R;
import com.example.cs223_final.Subscription;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ServicesFragment extends Fragment {
    private List<Subscription> subscriptionsDueWeek, subscriptionsDueToday, subscriptionsPast;
    private HomeImageAdapter adapterWeek, adapterToday, adapterPast;


    private FrameLayout noDataWeek, noDataToday, noDataPie, noDataBar,pastDueLayout;
    private TextView weeklySubs;
    private TextView weeklyCost;
    private PieChart pieChart;
    private BarChart barChart;
    private int numOfSubs = 0;
    private double totalCost = 0.0;
    private ArrayList<Subscription> allSubscriptions = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.tab_services_layout, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //INITIALIZE RECYCLERS

        RecyclerView recyclerWeek = view.findViewById(R.id.weeky_recycler);
        RecyclerView recyclerToday = view.findViewById(R.id.today_recycler);
        RecyclerView recyclerPast = view.findViewById(R.id.past_due_recycler);


        //LAYOUT MANAGER FOR EACH RECYCLER WITH A HORIZONTAL LAYOUT
        LinearLayoutManager layoutManagerWeek = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerToday = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerPast = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        //ASSIGN EACH LAYOUT MANAGER TO ITS CORRESPONDING RECYCLER
        recyclerWeek.setLayoutManager(layoutManagerWeek);
        recyclerWeek.setHasFixedSize(true);

        recyclerToday.setLayoutManager(layoutManagerToday);
        recyclerToday.setHasFixedSize(true);

        recyclerPast.setLayoutManager(layoutManagerPast);
        recyclerPast.setHasFixedSize(true);


        //INITIALIZE LIST & ADAPTER / PASS LIST TO ADAPTER / PASS ADAPTER TO RECYCLER
        subscriptionsDueWeek = new ArrayList<>();
        adapterWeek = new HomeImageAdapter(getContext(), subscriptionsDueWeek);
        recyclerWeek.setAdapter(adapterWeek);

        subscriptionsDueToday = new ArrayList<>();
        adapterToday = new HomeImageAdapter(getContext(), subscriptionsDueToday);
        recyclerToday.setAdapter(adapterToday);


        subscriptionsPast = new ArrayList<>();
        adapterPast = new HomeImageAdapter(getContext(), subscriptionsPast);
        recyclerPast.setAdapter(adapterPast);

        ///INITIALIZE ALL FIELDS IN LAYOUT THAT ARE GOING TO BE USED OR INTERACTED WITH DYNAMICALLY, VISIBILITY GONE BY DEFAULT
        //PAST DUE LAYOUT
        pastDueLayout = view.findViewById(R.id.past_due_layout);


        //CARD


        //SET TODAY'S DATE
        TextView today = view.findViewById(R.id.today_date);
        today.setText(setTodayDate());


        //INITIALIZE CHARTS
        pieChart = view.findViewById(R.id.piechart_category);
        barChart = view.findViewById(R.id.barchart_cost);


        //DISPLAY NO DATA WHEN RECYCLER IS EMPTY, VISIBILITY GONE BY DEFAULT
        noDataWeek = view.findViewById(R.id.no_content_week);
        noDataToday = view.findViewById(R.id.no_content_today);
        noDataPie = view.findViewById(R.id.no_content_pie);
        noDataBar = view.findViewById(R.id.no_content_bar);


        ///DISPLAYS SERVICES COUNT AND PRICE COUNT
        weeklySubs = view.findViewById(R.id.sub_count);
        weeklyCost = view.findViewById(R.id.cost_count);


        App.db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ServicesFragment.PopulateViews initTask = new ServicesFragment.PopulateViews();
                initTask.execute(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    //POPULATE ALL VIEWS WITH RELEVANT DATA
    public class PopulateViews extends AsyncTask<DataSnapshot, Void, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {
            DataSnapshot dataSnapshot = dataSnapshots[0];
            //PREVENTS DATA FROM APPENDING TO LIST WHEN DATA CHANGES
            subscriptionsPast.clear();
            subscriptionsDueWeek.clear();
            subscriptionsDueToday.clear();
            allSubscriptions.clear();
            numOfSubs = 0;
            totalCost = 0.0;

            //

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Subscription upload = snapshot.getValue(Subscription.class);
                upload.setKey(snapshot.getKey());

                String date = upload.getDueDate();
                if (!dueThisWeek(date)) {
                    if (dueToday(date)) {
                        subscriptionsDueToday.add(upload);
                    }
                    else if (pastDue(date)) {
                        subscriptionsPast.add(upload);
                    } else {
                        subscriptionsDueWeek.add(upload);
                    }


                }
                Collections.sort(subscriptionsPast,Collections.reverseOrder());
                Collections.sort(subscriptionsDueToday,Collections.reverseOrder());
                Collections.sort(subscriptionsDueWeek, Collections.reverseOrder());

                adapterWeek.setOnItemClickListener(new HomeImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, ImageView v) {
                        Intent intent = new Intent(getActivity(), CardActivity.class);
                        intent.putExtra("Sub", subscriptionsDueWeek.get(position));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v, "icon");
                            startActivity(intent, options.toBundle());
                        } else {
                            startActivity(intent);
                        }


                    }
                });

                adapterToday.setOnItemClickListener(new HomeImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, ImageView v) {
                        Intent intent = new Intent(getActivity(), CardActivity.class);
                        intent.putExtra("Sub", subscriptionsDueToday.get(position));


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v, "icon");
                            startActivity(intent, options.toBundle());
                        } else {
                            startActivity(intent);
                        }


                    }

                });

                adapterPast.setOnItemClickListener(new HomeImageAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, ImageView v) {
                        Intent intent = new Intent(getActivity(), CardActivity.class);
                        intent.putExtra("Sub", subscriptionsPast.get(position));


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v, "icon");
                            startActivity(intent, options.toBundle());
                        } else {
                            startActivity(intent);
                        }


                    }

                });


            }
            createCostChart();
            createCategoriesChart();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //CHART IS CREATED BEFORE DATA IS INSERTED, SO INVALIDATE CHART SO IT CAN BE REFRESHED WITH NEW DATA
            pieChart.invalidate();
            pieChart.animateY(1000);

            //THIS FINALIZED THE CHART
            //CAN'T BE RUN IN BATCH BECAUSE OF ANIMATION
            barChart.startAnimation();


            if(subscriptionsPast.isEmpty()){
                pastDueLayout.setVisibility(View.GONE);
            }else{
                pastDueLayout.setVisibility(View.VISIBLE);
            }

            if (subscriptionsDueWeek.isEmpty()) {
                noDataWeek.setVisibility(View.VISIBLE);
            } else {
                noDataWeek.setVisibility(View.GONE);
            }

            if (subscriptionsDueToday.isEmpty()) {
                noDataToday.setVisibility(View.VISIBLE);
            } else {
                noDataToday.setVisibility(View.GONE);
            }

            if (allSubscriptions.isEmpty()) {
                noDataPie.setVisibility(View.VISIBLE);
                noDataBar.setVisibility(View.VISIBLE);
            } else {
                noDataPie.setVisibility(View.GONE);
                noDataPie.setVisibility(View.GONE);
            }

            //CHANGE THE SERVICES COUNT AND COST COUNT FOR THIS WEEK
            String cost = "$" + Math.round(totalCost);
            weeklyCost.setText(cost);

            String subs = String.valueOf(numOfSubs);
            weeklySubs.setText(subs);

            //NOTIFY ADAPTERS OF CHANGES
            adapterWeek.notifyDataSetChanged();
            adapterToday.notifyDataSetChanged();
        }
    }


    //DATA HELPERS
    private boolean dueThisWeek(String dueDate) {
        //CHECKS TO SEE IF GIVEN DATES OCCUR WITHIN CURRENT WEEK
        SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH);
        Date due = null;
        try {
            due = sdf.parse(dueDate);

        } catch (ParseException e) {

        }
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());


        Date start = cal.getTime();

        Date end = new Date(start.getTime() + 7 * 24 * 60 * 60 * 1000);


        return start.compareTo(due) <= 0 & end.compareTo(due) <= 0;
    }

    private String setTodayDate() {
        //String today;
        Calendar cal = Calendar.getInstance();


        return String.format(Locale.ENGLISH, "%1$tB %1$td, %1$tY", cal);
    }

    private boolean dueToday(String dueDate) {
        //CHECKS TO SEE IF GIVEN DATE IS TODAY
        DateTimeFormatter formatter = DateTimeFormat.forPattern("M/d/yyyy");
        DateTime due = formatter.parseDateTime(dueDate);
        DateTime today = DateTime.now();


        return due.getDayOfYear() == today.getDayOfYear() & due.getYear() == today.getYear();
    }


    private boolean pastDue(String dueDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("M/d/yyyy");
        DateTime due = formatter.parseDateTime(dueDate);
        DateTime today = DateTime.now();


        return due.getDayOfYear() < today.getDayOfYear() & due.getYear() <= today.getYear();
    }
    //END DATA HELPERS


    //CHART CREATION
    private void createCategoriesChart() {
        //INITIALIZE LOOK OF CHART
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(30f);

        Legend legend = pieChart.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setTextSize(12);

        //THIS IS DONE PURELY TO SATISFY MY OWN OCD
        //AND THE INEFFICIENCY OF THE GRAPH LIBRARY
        float[] intervals = {1, 2, 3, 4, 5, 6};
        DashPathEffect effect = new DashPathEffect(intervals, 5f);

        LegendEntry entertainment = new LegendEntry("Entertainment", Legend.LegendForm.DEFAULT, 5f, 5f, effect, ColorTemplate.rgb("#3700b3"));
        LegendEntry utility = new LegendEntry("Utility", Legend.LegendForm.DEFAULT, 5f, 5f, effect, ColorTemplate.rgb("#bb86fc"));
        LegendEntry membership = new LegendEntry("Membership", Legend.LegendForm.DEFAULT, 5f, 5f, effect, ColorTemplate.rgb("#009688"));

        LegendEntry[] entry = {entertainment, utility, membership};
        legend.setCustom(entry);


        //CREATE A HASH-MAP FOR THE DIFFERENT CATEGORIES & TRACK THE NUMBER OF OCCURRENCES FOR EACH CATEGORY
        //OCCURRENCES ARE INITIALLY SET TO 0
        HashMap<String, Integer> categories = new HashMap<>();
        categories.put("Entertainment", 0);
        categories.put("Utility", 0);
        categories.put("Membership", 0);
        ////

        //ADD ALL SERVICES DUE THIS WEEK & TODAY TO HASH-MAP BASED ON ITS CATEGORY
        for (Subscription sub : allSubscriptions) {
            switch (sub.getCategory()) {
                case "Entertainment":
                    //PUT THE OCCURRENCE WITH ITS RESPECTIVE GET AND INCREMENT OCCURRENCE BY 1, SAME AS ++ OR +=1
                    categories.put("Entertainment", categories.get("Entertainment") + 1);
                    break;
                case "Utility":
                    categories.put("Utility", categories.get("Utility") + 1);
                    break;
                case "Membership":
                    categories.put("Membership", categories.get("Membership") + 1);
                    break;
            }
            numOfSubs++;
        }


        //INITIALIZE NEW ENTRY SET FOR PIR CHART
        //ADD VALUES FROM HASH-MAP TO ENTRY SET
        ArrayList<PieEntry> yValues = new ArrayList<>();
        for (String key : categories.keySet()) {
            if (categories.get(key) != 0) {
                yValues.add(new PieEntry(categories.get(key), key));
            } else {
                yValues.add(new PieEntry(0, ""));
            }
        }

        //FINALIZE THE PIE CHART
        PieDataSet dataSet = new PieDataSet(yValues, "");

        dataSet.setSliceSpace(4f);
        dataSet.setSelectionShift(8f);


        //ASSIGN COLORS TO EACH DATA-SET, ORDER MATTERS
        dataSet.setColors(ColorTemplate.rgb("#bb86fc"), ColorTemplate.rgb("#3700b3"), ColorTemplate.rgb("#009688"));
        dataSet.setUsingSliceColorAsValueLineColor(true);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);


        pieChart.setData(data);
    }

    private void createCostChart() {
        HashMap<String, Double> categories = new HashMap<>();
        categories.put("Entertainment", 0.0);
        categories.put("Membership", 0.0);
        categories.put("Utility", 0.0);


        allSubscriptions.addAll(subscriptionsDueWeek);
        allSubscriptions.addAll(subscriptionsDueToday);
        allSubscriptions.addAll(subscriptionsPast);
        allSubscriptions.addAll(subscriptionsPast);

        for (Subscription sub : allSubscriptions) {
            switch (sub.getCategory()) {
                case "Entertainment":
                    categories.put("Entertainment", categories.get("Entertainment") + Double.parseDouble(sub.getPrice()));
                    break;
                case "Utility":
                    categories.put("Utility", categories.get("Utility") + Double.parseDouble(sub.getPrice()));
                    break;
                case "Membership":
                    categories.put("Membership", categories.get("Membership") + Double.parseDouble(sub.getPrice()));
                    break;
            }
            totalCost += Double.parseDouble(sub.getPrice());
        }

        float entertainmentCost = 0;
        float utilityCost = 0;
        float membershipCost = 0;

        for (String key : categories.keySet()) {
            switch (key) {
                case "Entertainment":
                    entertainmentCost += categories.get(key).floatValue();
                    break;
                case "Utility":
                    utilityCost += categories.get(key).floatValue();
                    break;
                case "Membership":
                    membershipCost += categories.get(key).floatValue();
                    break;
            }
        }

        final float finalUtilityCost = utilityCost;
        final float finalEntertainmentCost = entertainmentCost;
        final float finalMembershipCost = membershipCost;


        //RUN ON UI THREAD TO PREVENT CRASHING ON OLDER API LEVELS
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                barChart.clearChart();

                barChart.addBar(new BarModel("Utility", finalUtilityCost, ColorTemplate.rgb("#bb86fc")));
                barChart.addBar(new BarModel("Entertainment", finalEntertainmentCost, ColorTemplate.rgb("#3700b3")));
                barChart.addBar(new BarModel("Membership", finalMembershipCost, ColorTemplate.rgb("#009688")));
            }
        });

    }
    //END CHART CREATION


}
