package com.example.cs223_final.ui.subs;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.cs223_final.R;
import com.example.cs223_final.Subscription;

import java.util.ArrayList;
import java.util.List;

///extends RecyclerView.Adapter<ServicesImageAdapter.ImageViewHolder>
public class SubscriptionImageAdapter extends RecyclerView.Adapter<SubscriptionImageAdapter.ImageViewHolder> implements Filterable {

    private Context mContext;
    private List<Subscription> mSubscriptions;
    private List<Subscription> subscriptions_full;

    public SubscriptionImageAdapter(Context context, List<Subscription> subscriptions) {
        mContext = context;
        mSubscriptions = subscriptions;


        subscriptions_full = new ArrayList<>(mSubscriptions);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.subscription_fragment_card, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Subscription sub = mSubscriptions.get(position);

        holder.name.setText(sub.getName());
        holder.category.setText(sub.getCategory());
        holder.cost.setText(sub.getPrice());
        holder.due.setText(sub.getModifiedDate(sub.getDueDate()));

        holder.icon.setClipToOutline(true);


        ColorGenerator generator = ColorGenerator.MATERIAL;

        int color = generator.getColor(sub.getName());

        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .bold()
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .fontSize(90)
                .endConfig()
                .round();


        char letter = sub.getName().trim().charAt(0);


        TextDrawable img = builder.build(Character.toString(letter), color);
        holder.icon.setImageDrawable(img);
    }

    @Override
    public int getItemCount() {
        return mSubscriptions.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Subscription> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(subscriptions_full);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Subscription item : subscriptions_full) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mSubscriptions.clear();
            mSubscriptions.addAll((List) results.values);

            notifyDataSetChanged();
        }
    };
    //END FILTERABLE

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, category, cost, due;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.category);
            cost = itemView.findViewById(R.id.cost);
            due = itemView.findViewById(R.id.due);
            itemView.setClipToOutline(true);
        }
    }
}
