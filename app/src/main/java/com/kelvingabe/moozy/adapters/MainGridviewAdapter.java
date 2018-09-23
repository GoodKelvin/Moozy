package com.kelvingabe.moozy.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.kelvingabe.moozy.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.net.HttpURLConnection;

/**
 * Created by kelvox on 12/3/2016.
 */

public class MainGridviewAdapter extends BaseAdapter {
    //attempt grabbing from the internet
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    // Will contain the raw JSON response as a string.
    String forecastJsonStr = null;
    private Context mContext;
    String[] bc;

    public MainGridviewAdapter(Context c, String[] bg) {
        mContext = c;
        bc = bg;
    }

    public int getCount() {
        return bc.length;
    }

    public Object getItem(int position) {
        return null;
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
            //imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        //imageView.setImageResource(mThumbIds[position]);

        Picasso picasso = Picasso.with(mContext);
        picasso.setIndicatorsEnabled(true);
        picasso.load(bc[position])
                .placeholder(mContext.getDrawable(R.mipmap.image_placeholder))
                .error(mContext.getDrawable(R.mipmap.image_placeholder))
                .into(imageView);

        return imageView;
    }


}
