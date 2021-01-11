package com.example.cs223_final.ui.calender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs223_final.R;
import com.example.cs223_final.Subscription;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CalenderSubCardAdapter extends RecyclerView.Adapter<CalenderSubCardAdapter.ViewHolder> {

    private Context context;
    private List<Subscription> subscriptions;

    public CalenderSubCardAdapter(Context context, List<Subscription> subscriptions) {
        this.context = context;
        this.subscriptions = subscriptions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.calender_cards, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subscription currentSub = subscriptions.get(position);

        holder.name.setText(subscriptions.get(position).getName());
        holder.cost.setText(subscriptions.get(position).getPrice());
        holder.date.setText(subscriptions.get(position).getModifiedDate(subscriptions.get(position).getDueDate()));
        holder.category.setText(subscriptions.get(position).getCategory());


        //Log.i("THREADNAME", "This thread is:" + Thread.currentThread().getName());
        Picasso.get()
                .load(currentSub.getUrl())
                .placeholder(R.drawable.img_money)
                .fit()
                .centerCrop()
                .into(holder.icon);
        Picasso.get().setLoggingEnabled(true);
    }

    @Override
    public int getItemCount() {
        return subscriptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, date, cost, category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.cal_icon);
            name = itemView.findViewById(R.id.cal_name);
            date = itemView.findViewById(R.id.cal_due);
            cost = itemView.findViewById(R.id.cal_cost);
            category = itemView.findViewById(R.id.cal_category);
        }
    }
}
