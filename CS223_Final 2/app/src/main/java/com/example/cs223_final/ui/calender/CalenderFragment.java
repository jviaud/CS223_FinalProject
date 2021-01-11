package com.example.cs223_final.ui.calender;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs223_final.App;
import com.example.cs223_final.MainActivity;
import com.example.cs223_final.R;
import com.example.cs223_final.Subscription;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;


public class CalenderFragment extends Fragment implements OnDateSelectedListener {
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();
    private MaterialCalendarView calender;
    private List<Subscription> subscriptions;
    private CalenderSubCardAdapter adapter;
    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.calender_fragment_layout, container, false);
        ((MainActivity) getActivity()).setActionBarTitle("Calender");
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        RecyclerView recycler = view.findViewById(R.id.calender_recycler);
        LinearLayoutManager layoutManagerAll = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recycler.setLayoutManager(layoutManagerAll);
        recycler.setHasFixedSize(true);

        subscriptions = new ArrayList<>();

        //Log.i("Subscription",""+subscriptions);
        adapter = new CalenderSubCardAdapter(getContext(), subscriptions);
        recycler.setAdapter(adapter);


        calender = view.findViewById(R.id.sub_calender);

        calender.setCurrentDate(CalendarDay.today());
        calender.setDateSelected(CalendarDay.today(), true);

        calender.setOnDateChangedListener(this);
        calender.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

        final LocalDate instance = LocalDate.now();
        calender.setSelectedDate(instance);
        calender.setDateSelected(CalendarDay.today(), true);


        final LocalDate min = LocalDate.of(instance.getYear(), Month.JANUARY, 1);

        calender.state()
                .edit()
                .setMinimumDate(min)
                .commit();

        calender.addDecorators(oneDayDecorator);


        ArrayList<CalendarDay> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        LocalDate today = LocalDate.now();

        Log.i("DATE", "SIZE: " + App.all_subscriptions.size());
        for (Subscription sub : App.all_subscriptions) {
            LocalDate localDate = LocalDate.parse(sub.getDueDate(), formatter);
            CalendarDay day = CalendarDay.from(localDate);
            dates.add(day);
            if (localDate.isEqual(today)) {
            subscriptions.add(sub);
            }
        }

        calender.addDecorator(new CurrentDayDecorator(getContext()));
        calender.addDecorator(new EventDecorator(Color.parseColor("#225e5e"), dates));


    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        subscriptions.clear();
        oneDayDecorator.setDate(date.getDate());
        String selectedDate = date.getDate().format(DateTimeFormatter.ofPattern("M/d/yyyy"));
        for (Subscription upload : App.all_subscriptions) {
            String due_date = upload.getDueDate();
            if (due_date.equals(selectedDate)) {
                subscriptions.add(upload);
            }
        }
        adapter.notifyDataSetChanged();
        widget.invalidateDecorators();
    }
}
