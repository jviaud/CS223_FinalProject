package com.example.cs223_final.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs223_final.R;
import com.example.cs223_final.Subscription;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeImageAdapter extends RecyclerView.Adapter<HomeImageAdapter.ImageViewHolder> {
    private HomeImageAdapter.OnItemClickListener mListener;//CUSTOM ON CLICK LISTENER
    private Context context;
    private List<Subscription> subscriptions;


    public HomeImageAdapter(Context context, List<Subscription> subscriptions) {
        this.context = context;
        this.subscriptions = subscriptions;


    }

    @NonNull
    @Override
    public HomeImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.home_icons_recycled, parent, false);

        return new HomeImageAdapter.ImageViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeImageAdapter.ImageViewHolder holder, int position) {
        Subscription currentSub = subscriptions.get(position);


        // Log.i("THREADNAME", "This thread is:" + Thread.currentThread().getName());
        Picasso.get()
                .load(currentSub.getUrl())
                .fit()
                .centerCrop()
                .into(holder.icon);
      //  Picasso.get().setLoggingEnabled(true);

    }

    @Override
    public int getItemCount() {
        return subscriptions.size();
    }


    //CUSTOM ONCLICK LISTENER INTERFACE
    public interface OnItemClickListener {
        void onItemClick(int position,ImageView v);
    }

    void setOnItemClickListener(HomeImageAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    ////END CUSTOM ONCLICK LISTENER INTERFACE


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;

        public ImageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);



            //INITIALIZE ON CLICK LISTENER IN VIEW HOLDER
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position,icon);

                        }
                    }
                }
            });
        }
    }
}
