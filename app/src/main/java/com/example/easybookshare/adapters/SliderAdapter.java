package com.example.easybookshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.easybookshare.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderHolder>
{

    Context context;
    ArrayList<String> arrayList;
    public SliderAdapter(Context context , ArrayList<String> arrayList)
    {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public SliderHolder onCreateViewHolder(ViewGroup parent)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_image_slider,null);
        return new SliderHolder(view);
    }

    @Override
    public void onBindViewHolder(SliderHolder viewHolder, int position)
    {
        Glide.with(context)
                .load(arrayList.get(position))
                .placeholder(R.drawable.img_book)
                .centerCrop()
                .into(viewHolder.imageView);
    }

    @Override
    public int getCount()
    {
        return arrayList.size();
    }

    public class SliderHolder extends ViewHolder
    {
        ImageView imageView;
        public SliderHolder(View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_slide);
        }
    }
}
