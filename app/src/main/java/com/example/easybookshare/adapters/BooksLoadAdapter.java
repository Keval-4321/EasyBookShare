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
import com.example.easybookshare.models.BooksLoadModel;

import java.util.ArrayList;
import java.util.Locale;

public class BooksLoadAdapter extends RecyclerView.Adapter<BooksLoadAdapter.BooksViewHolder>
{
    Activity activity;
    ArrayList<BooksLoadModel> arrayList ;
    ArrayList<BooksLoadModel> backupArrayList ;

    public BooksLoadAdapter(Activity activity, ArrayList<BooksLoadModel> arrayList)
    {
        this.activity = activity;
        this.arrayList = arrayList;
        this.backupArrayList = new ArrayList<>();
        this.backupArrayList.addAll(arrayList);
    }

    @NonNull
    @Override
    public BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_layout_book,null);
        return new BooksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksViewHolder holder, int position)
    {
        BooksLoadModel model = arrayList.get(position);
        holder.txtBookName.setText(model.getStrBookName());

        Glide.with(activity)
             .load(model.getImgBook())
             .placeholder(R.drawable.img_book)
             .into(holder.imgBook);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(activity, BookDetailsActivity.class);
                intent.putExtra("BOOK_CLICKED_ID",model.getId());
                //intent.putExtra("USER_ID",model.getUsedId());
                intent.putExtra("IS_WISHED",model.isWished());
                intent.putExtra("USER_ID",model.getUsedId());
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }

    public void filterData(String searchedText)
    {
        searchedText = searchedText.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if(searchedText.length()==0)
        {
            arrayList.addAll(backupArrayList);
        }
        else
        {
            for(BooksLoadModel bookModel : backupArrayList)
            {
                if(bookModel.getStrBookName().toLowerCase(Locale.getDefault()).contains(searchedText))
                {
                    arrayList.add(bookModel);
                }
            }
        }
        this.notifyDataSetChanged();
    }

    public class BooksViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imgBook;
        TextView txtBookName;
        public BooksViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imgBook = itemView.findViewById(R.id.img_book);
            txtBookName = itemView.findViewById(R.id.txt_book_name);
        }
    }

}
