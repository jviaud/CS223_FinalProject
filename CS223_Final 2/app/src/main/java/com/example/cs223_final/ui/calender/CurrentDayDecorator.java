package com.example.cs223_final.ui.calender;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import com.example.cs223_final.R;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class CurrentDayDecorator implements DayViewDecorator {

    private Drawable highlightDrawable;

    public CurrentDayDecorator( Context context) {
        highlightDrawable = context.getResources().getDrawable(R.drawable.circlebackground);
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(CalendarDay.today());
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.setBackgroundDrawable(highlightDrawable);
        view.addSpan(new ForegroundColorSpan(ColorTemplate.rgb("#171f24")));
        //view.addSpan(new RelativeSizeSpan(1.5f));

    }
}
