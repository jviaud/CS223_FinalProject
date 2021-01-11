package com.example.cs223_final;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class Subscription implements Comparable<Subscription>, Parcelable {
    private String Name, Price, DueDate, RenewDate, StartDate, Category, Url, Key;

    //EMPTY CONSTRUCTOR IS NEEDED FOR FIRE BASE
    public Subscription() {

    }

    public Subscription(String Name, String Price, String StartDate, String DueDate, String Category, String Url) {
        this.Name = Name;
        this.Price = Price;
        this.StartDate = StartDate;
        this.DueDate = DueDate;
        this.Category = Category;
        this.Url = Url;
    }


    protected Subscription(Parcel in) {
        Name = in.readString();
        Price = in.readString();
        DueDate = in.readString();
        RenewDate = in.readString();
        StartDate = in.readString();
        Category = in.readString();
        Url = in.readString();
        Key = in.readString();
    }

    public static final Creator<Subscription> CREATOR = new Creator<Subscription>() {
        @Override
        public Subscription createFromParcel(Parcel in) {
            return new Subscription(in);
        }

        @Override
        public Subscription[] newArray(int size) {
            return new Subscription[size];
        }
    };

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getDueDate() {

       // Log.i("PARSE", DueDate);
        return DueDate;

    }

    @Exclude
    public String getModifiedDate(String givenDate) {
        SimpleDateFormat original_sdf = new SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH);
        // SimpleDateFormat sdf = new SimpleDateFormat("EE, MMM d,yyyy", Locale.ENGLISH);
        //String formattedDate="";

        Calendar cal = Calendar.getInstance();
        try {
            Date date = original_sdf.parse(givenDate);

            cal.setTime(date);
            cal.add(Calendar.YEAR, 1);
            cal.add(Calendar.MONTH, -1);

            //formattedDate = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return String.format(Locale.ENGLISH, "%1$ta, %1$tb %1$td, %1$tY", cal);
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public String getRenewDate() {
        return RenewDate;
    }

    public void setRenewDate(String renewDate) {
        RenewDate = renewDate;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    @Exclude
    public String getKey() {
        return Key;
    }

    @Exclude
    public void setKey(String key) {
        Key = key;
    }


    @Exclude
    @Override
    public int compareTo(Subscription o) {
        SimpleDateFormat original_sdf = new SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH);
        Date d1 = null;
        Date d2 = null;

        try {
            d1 = original_sdf.parse(o.getDueDate());
            d2 = original_sdf.parse(DueDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return d1.compareTo(d2);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Name);
        dest.writeString(Price);
        dest.writeString(DueDate);
        dest.writeString(RenewDate);
        dest.writeString(StartDate);
        dest.writeString(Category);
        dest.writeString(Url);
        dest.writeString(Key);
    }


    public static class PriceCompare implements Comparator<Subscription>{
        public int compare(Subscription s1,Subscription s2){
            double priceOne = Double.parseDouble(s1.getPrice());
            double priceTwo = Double.parseDouble(s2.getPrice());

            return Double.compare(priceOne, priceTwo);
        }
    }

}
