package com.example.cs223_final.ui.home.add;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cs223_final.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ServicesImageAdapter extends RecyclerView.Adapter<ServicesImageAdapter.ImageViewHolder> implements Filterable {

    private OnItemClickListener mListener;//CUSTOM ON CLICK LISTENER
    private Context mContext;
    private List<Upload> mUploads;

    private List<Upload> mUploads_full;

    public ServicesImageAdapter(Context context, List<Upload> uploads) {
        mContext = context;
        mUploads = uploads;

        mUploads_full = new ArrayList<>(mUploads);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v, mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.name.setText(uploadCurrent.getName());

        holder.category.setText(uploadCurrent.getCategory());


        Picasso.get()
                .load(uploadCurrent.getImageUrl())
                .placeholder(R.drawable.img_money)
                .fit()
                .centerCrop()
                .into(holder.icon);
        Picasso.get().setLoggingEnabled(true);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }


    //METHODS TO MAKE RECYCLER VIEW FILTERABLE
    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Upload> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mUploads_full);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Upload item : mUploads_full) {
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
            mUploads.clear();
            mUploads.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
    //END FILTERABLE


    //CUSTOM ONCLICK LISTENER INTERFACE
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    ////END CUSTOM ONCLICK LISTENER INTERFACE


    //VIEW HOLDER
    static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView name, category;


        ImageViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.category);


            //INITIALIZE ON CLICK LISTENER IN VIEW HOLDER
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
