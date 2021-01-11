package com.example.cs223_final.ui.home;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UsageFragment extends Fragment {
    private FrameLayout noExpensiveChart, noCategoryChart;

    private List<Subscription> subscriptions;
    private BarChart hBarChart;
    private PieChart pieChart;
    private int uppbound = 5;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_usage_layout, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //INITIALIZE ARRAY OF SUBSCRIPTIONS
        subscriptions = new ArrayList<>();


        //INITIALIZE CHARTS
        hBarChart = view.findViewById(R.id.expensive_chart);
        pieChart = view.findViewById(R.id.categories_chart);

        //NOTIFY NO DATA
        noExpensiveChart = view.findViewById(R.id.no_content_subs);
        noCategoryChart = view.findViewById(R.id.no_data_categories);

        subscriptions.clear();
        App.db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hBarChart.clearChart();
                UsageFragment.PopulatViews initTask = new UsageFragment.PopulatViews();
                initTask.execute(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void createPieChart() {
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
        ///

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

        //ADD ALL SERVICES DUE THIS WEEK TO HASH-MAP BASED ON ITS CATEGORY
        for (Subscription sub : subscriptions) {
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
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);


        pieChart.setData(data);

    }


    private void createBarChart() {

        //   final HashMap<String, Double> prices = new HashMap<>();
        final ArrayList<String[]> prices = new ArrayList<>();

        if (!subscriptions.isEmpty()) {
            if (subscriptions.size() < uppbound) {
                uppbound = subscriptions.size();
            }
            for (int i = subscriptions.size() - 1; i >= subscriptions.size() - uppbound; i--) {
                Log.i("NAME-0", subscriptions.get(i).getName());
                String[] sub = {subscriptions.get(i).getName(), subscriptions.get(i).getPrice(), subscriptions.get(i).getCategory()};
                //prices.put(subscriptions.get(i).getName(),Double.parseDouble(subscriptions.get(i).getPrice()));
                prices.add(sub);
            }


            //PREVENTS CRASHING ON OLDER API LEVELS
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //THIS LIBRARY ANIMATES THE BAR GRAPHS WHEN THEY ARE ADDED, SO ADDING THE BARS MUST BE DONE ON THE MAIN THREAD
                    for (int i = 0; i < prices.size(); i++) {
                        if (prices.get(i)[2].equals("Entertainment")) {
                            hBarChart.addBar(new BarModel(prices.get(i)[0], Float.valueOf(prices.get(i)[1]), ColorTemplate.rgb("#3700b3")));
                        }
                        if (prices.get(i)[2].equals("Utility")) {
                            hBarChart.addBar(new BarModel(prices.get(i)[0], Float.valueOf(prices.get(i)[1]), ColorTemplate.rgb("#bb86fc")));
                        }
                        if (prices.get(i)[2].equals("Membership")) {
                            hBarChart.addBar(new BarModel(prices.get(i)[0], Float.valueOf(prices.get(i)[1]), ColorTemplate.rgb("#009688")));
                        }


                    }
                    hBarChart.startAnimation();
                }
            });//END UI THREAD RUNNABLE
        }

    }


    class PopulatViews extends AsyncTask<DataSnapshot, Void, Void> {
        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {
            subscriptions.clear();
            DataSnapshot dataSnapshot = dataSnapshots[0];
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Subscription upload = snapshot.getValue(Subscription.class);
                //upload.setKey(snapshot.getKey());

                subscriptions.add(upload);
            }

            Subscription.PriceCompare prices = new Subscription.PriceCompare();
            Collections.sort(subscriptions, prices);


            //CREATE CHARTS
            createBarChart();
            createPieChart();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            pieChart.invalidate();
            pieChart.animateY(1000);

            if (subscriptions.isEmpty()) {
                noExpensiveChart.setVisibility(View.VISIBLE);
                noCategoryChart.setVisibility(View.VISIBLE);
            } else {
                noExpensiveChart.setVisibility(View.GONE);
                noCategoryChart.setVisibility(View.GONE);
            }
        }
    }

}
