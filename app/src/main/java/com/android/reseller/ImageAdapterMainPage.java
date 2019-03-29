package com.android.reseller;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class ImageAdapterMainPage extends BaseAdapter {
    private Context mContext;

    public ImageAdapterMainPage(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public String getItem(int position) {
        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        String url = getItem(position);
        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.ic_load)
                .fit()
                .centerCrop().into(imageView);
        return imageView;
    }
    private String[] mThumbIds = {
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300",
            "http://via.placeholder.com/300x300", "http://via.placeholder.com/300x300"

    };
}
