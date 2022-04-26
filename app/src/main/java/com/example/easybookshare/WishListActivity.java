package com.example.easybookshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.easybookshare.adapters.WishListLoadAdapter;
import com.example.easybookshare.helpers.SharedPreferenceManager;
import com.example.easybookshare.models.BookModel;
import com.example.easybookshare.models.WishBooksLoadModel;
import com.example.easybookshare.models.WishListModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WishListActivity extends AppCompatActivity
{

    public static RecyclerView recyclerViewWishList;
    ArrayList<WishBooksLoadModel> arrayListWishList;
    SharedPreferenceManager sharedPreferenceManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    ArrayList<String> wishListedBookIds;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        recyclerViewWishList = findViewById(R.id.recycler_wishlist);
        progressBar = findViewById(R.id.progress);

        arrayListWishList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();

        sharedPreferenceManager = new SharedPreferenceManager(this);
        String userId = sharedPreferenceManager.sharedPreferencesData.getString("USER_ID","");
        Log.e("wishList", "onCreate: " + userId);

        databaseReference = firebaseDatabase.getReference("wishlists").child(userId);

        wishListedBookIds = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                wishListedBookIds.clear();
                Log.e("wishListedBooks", "onDataChange: " +snapshot.getChildrenCount());
                if(snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        WishListModel wishListModel = dataSnapshot.getValue(WishListModel.class);
                        wishListedBookIds.add(wishListModel.getBookId());
                    }
                    // now fetching those books only which are wishListed by that user only
                    databaseReference = firebaseDatabase.getReference("books");
                    databaseReference.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            arrayListWishList.clear();
                            if (snapshot.exists())
                            {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                {
                                    BookModel bookModel = dataSnapshot.getValue(BookModel.class);
                                    if(wishListedBookIds.contains(bookModel.getBookId()))
                                    {
                                        //Log.e("wishList", "onDataChange: wished ?" + bookModel.isWished());
                                        WishBooksLoadModel wishBooksLoadModel = new WishBooksLoadModel(bookModel.getBookId(), bookModel.getImgUrl1(), bookModel.getBookName(), bookModel.isWished());
                                        arrayListWishList.add(wishBooksLoadModel);
                                    }
                                }
                                Log.e("wishList", "onCreate: " + arrayListWishList);
                                WishListLoadAdapter wishListLoadAdapter = new WishListLoadAdapter(WishListActivity.this, arrayListWishList);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(WishListActivity.this, 3);
                                recyclerViewWishList.setLayoutManager(gridLayoutManager);
                                recyclerViewWishList.setAdapter(wishListLoadAdapter);
                                progressBar.setVisibility(View.GONE);
                                recyclerViewWishList.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(WishListActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(WishListActivity.this, "empty wishlist", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay_here,R.anim.bottom_out_profile);
    }
}