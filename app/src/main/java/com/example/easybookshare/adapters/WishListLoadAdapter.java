package com.example.easybookshare.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easybookshare.BookDetailsActivity;
import com.example.easybookshare.R;
import com.example.easybookshare.models.WishBooksLoadModel;

import java.util.ArrayList;

public class WishListLoadAdapter extends RecyclerView.Adapter<WishListLoadAdapter.WishListViewHolder>
{

    Activity activity;
    ArrayList<WishBooksLoadModel> arrayList ;

    public WishListLoadAdapter(Activity activity, ArrayList<WishBooksLoadModel> arrayList)
    {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public WishListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_layout_book,null);
        return new WishListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishListViewHolder holder, int position)
    {
        WishBooksLoadModel model = arrayList.get(position);
        holder.txtBookName.setText(model.getStrBookName());
        Glide.with(activity)
             .load(model.getImgBook())
             .placeholder(R.drawable.img_book)
             .into(holder.imgBook);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                Intent intent = new Intent(activity, BookDetailsActivity.class);
//                intent.putExtra("BOOK_CLICKED_ID",model.getId());
//                activity.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }
    public class WishListViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imgBook;
        TextView txtBookName;
        public WishListViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imgBook = itemView.findViewById(R.id.img_book);
            txtBookName = itemView.findViewById(R.id.txt_book_name);
        }
    }

}
