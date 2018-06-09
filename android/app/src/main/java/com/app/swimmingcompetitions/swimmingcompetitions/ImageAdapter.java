package com.app.swimmingcompetitions.swimmingcompetitions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private int mResource;
    private ArrayList<JSONObject> mediaList;

    public ImageAdapter(Context context, int resource, ArrayList<JSONObject> bitmapList) {
        this.mContext = context;
        this.mResource = resource;
        this.mediaList = bitmapList;
    }

    public int getCount() {
        return this.mediaList.size();
    }

    public Object getItem(int position) {
        return this.mediaList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @NonNull @Override public View getView(int position, View v, @NonNull ViewGroup parent) {
        ImageView imageView;
        View listItem = v;

        if(listItem == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            listItem = inflater.inflate(R.layout.media_item, parent, false);
        }

        imageView = listItem.findViewById(R.id.image_view);


        imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        try {
            System.out.println("width " + this.mediaList.get(position).getInt("width"));
            System.out.println("height " + this.mediaList.get(position).getInt("height"));
            Glide.with(imageView.getContext())
                    .load(this.mediaList.get(position).getString("url"))
                    .apply(new RequestOptions().override(this.mediaList.get(position).getInt("width"), this.mediaList.get(position).getInt("height")).fitCenter())
                    .into(imageView);
        }
        catch(Exception e) {
            System.out.println("ImageAdapter getView Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
        }

        return imageView;
    }
}
