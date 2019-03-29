package com.android.reseller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.reseller.models.Upload;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.ArrayList;

interface RecyclerViewClickListener {
    void onClick(View v, int pos);
}
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Upload> uploads;
    private RecyclerViewClickListener mListener;

    public RecyclerViewAdapter(Context context, List<Upload> uploads, RecyclerViewClickListener mListener) {
        this.uploads = uploads;
        this.context = context;
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v,mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Upload upload = uploads.get(position);
        holder.textViewName.setText(upload.getName());
        Glide.with(context).load(upload.getUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public void filterList(ArrayList<Upload> filteredList) {
        uploads = filteredList;
        notifyDataSetChanged();
    }

    public List<Upload> getList()
    {
        return this.uploads;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener mListener;

       public ViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            mListener = listener;
            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View view) {

            mListener.onClick(view,getAdapterPosition());
        }

        public TextView textViewName;
        public ImageView imageView;



    }
}
