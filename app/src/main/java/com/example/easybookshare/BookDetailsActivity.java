package com.example.easybookshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easybookshare.adapters.BooksLoadAdapter;
import com.example.easybookshare.adapters.SliderAdapter;
import com.example.easybookshare.helpers.SharedPreferenceManager;
import com.example.easybookshare.models.BookModel;
import com.example.easybookshare.models.BooksLoadModel;
import com.example.easybookshare.models.UserModel;
import com.example.easybookshare.models.WishBooksLoadModel;
import com.example.easybookshare.models.WishListModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class BookDetailsActivity extends AppCompatActivity {

    Button btnDial;
    MenuItem emptyBookMark;
    MenuItem filledBookMark;
    SliderView sliderView;

    TextView textBookDetail, txtUname;
    ArrayList<String> arrayListBookImages;

    String userId, phNo;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    BookModel bookModel;
    String userIdFromItemClicked;

    public static ArrayList<WishBooksLoadModel> wishList;
    WishBooksLoadModel wishBooksLoadModel;

    String bookClickedId;
    boolean isWished;
    String userIdFromBookClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.e("call", "onCreate: called");

        btnDial = findViewById(R.id.btn_dial);
        sliderView = findViewById(R.id.imageSlider);
        wishList = new ArrayList<>();

        textBookDetail = findViewById(R.id.txt_book_detail);
        txtUname = findViewById(R.id.txt_uname);
        arrayListBookImages = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("books");
        wishBooksLoadModel = new WishBooksLoadModel();

        Intent intent = getIntent();
        bookClickedId = intent.getStringExtra("BOOK_CLICKED_ID");
        userIdFromBookClicked = intent.getStringExtra("USER_ID");

        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        userId = sharedPreferenceManager.sharedPreferencesData.getString("USER_ID", "");

        Log.e("clicked", "onCreate: " + bookClickedId);
        Log.e("clicked", "onCreate: " + userId);
        //isWished = intent.getBooleanExtra("IS_WISHED",false);
        Query query = databaseReference.orderByChild("bookId").equalTo(bookClickedId);

        // getting book detail on click on that book
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("menu", "onCreate :" + userId);
                arrayListBookImages.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        bookModel = dataSnapshot.getValue(BookModel.class);
                        String bookName = bookModel.getBookName();
                        String bookOPrice = bookModel.getoPrice();
                        String bookSPrice = bookModel.getsPrice();
                        String pages = bookModel.getNoOfPages();
                        String categoryName = bookModel.getCategoryName();
                        String subCategoryName = bookModel.getSubCategoryName();
                        String authorName = bookModel.getAuthorName();
                        userIdFromBookClicked = bookModel.getUserId();
                        boolean wished = bookModel.isWished();
                        textBookDetail.setText("Book name :" + bookName + "\nbook original price :" + bookOPrice
                                + "\nbook selling price :" + bookSPrice + "\npages :" + pages
                                + "\nCategory name :" + categoryName
                                + "\nSub-Category name :" + subCategoryName
                                + "\nAuthor name :" + authorName);

                        arrayListBookImages.add(bookModel.getImgUrl1());
                        arrayListBookImages.add(bookModel.getImgUrl2());
                        arrayListBookImages.add(bookModel.getImgUrl3());

                    }

                    // adapter to view images
                    SliderAdapter sliderAdapter = new SliderAdapter(BookDetailsActivity.this, arrayListBookImages);
                    sliderView.setSliderAdapter(sliderAdapter);

                    // getting user name from the userId
                    databaseReference = firebaseDatabase.getReference("users");
                    Query query1 = databaseReference.orderByChild("uniqueKey").equalTo(userIdFromBookClicked);
                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.e("uId", "onDataChange: count " + snapshot.getChildrenCount());
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                    txtUname.setText("Posted By :" + userModel.getuName());
                                    phNo = userModel.getPhoneNumber();
                                    //btnDial.setText(userModel.getPhoneNumber());

                                    Log.e("uId", "onDataChange: from pref " + userId);
                                    Log.e("uId", "onDataChange: from db " + userModel.getUniqueKey());
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(BookDetailsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(BookDetailsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // if current user ej book post user hoy to call na lagvo joiye so...
        if(userIdFromBookClicked.equals(userId))
        {
            btnDial.setVisibility(View.GONE);
        }

        btnDial.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //String txtOfBtn = btnDial.getText().toString();
                if (!phNo.equals("not provided"))
                {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri u = Uri.parse("tel:" + phNo);
                    intent.setData(u);
                    // Uri u = Uri.parse("tel:" + e.getText().toString());
                    //intent.setData(Uri.parse("tel:<9848383838>"));
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(BookDetailsActivity.this, "phone number is not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.e("call", "onCreate: finished");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.e("call", "onCreateOptionsMenu: called");


        // inflates the menu , this adds the item to the action bar if present
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_bookmark, menu);

        emptyBookMark = menu.findItem(R.id.bookmark_empty);
        filledBookMark = menu.findItem(R.id.bookmark_filled);


        // if current user ej book post user hoy to  , ene potani book wishlist
        // na thavi joiye...
        if(userIdFromBookClicked.equals(userId))
        {
            emptyBookMark.setVisible(false);
            filledBookMark.setVisible(false);
        }
        else
        {
            // getting wish list status from the wishlists table , if id is present
            // then book is wishlist so set filled book marked icon
            databaseReference = firebaseDatabase.getReference("wishlists").child(userId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.e("bookId", "onDataChange: " + snapshot.getChildrenCount());
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            WishListModel wishListModel = dataSnapshot.getValue(WishListModel.class);
                            if (wishListModel != null) {
                                if (wishListModel.getBookId().equals(bookClickedId)) {
                                    Log.e("bookId", "onDataChange: from clicked => " + bookClickedId);
                                    Log.e("bookId", "onDataChange: from db => " + wishListModel.getBookId());

                                    emptyBookMark.setVisible(false);
                                    filledBookMark.setVisible(true);
                                    break; // as if there are multiple wishlisted books are there
                                    // then we need to stop execution further as if further
                                    // we don't get same book so it will go in else and
                                    // set empty icon and visible , so it will become wrong
                                } else {
                                    emptyBookMark.setVisible(true);
                                    filledBookMark.setVisible(false);
                                }
                            } else {
                                Log.e("bookId", "onDataChange: wishlist object is null");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BookDetailsActivity.this, "something went wrong with bookmark", Toast.LENGTH_SHORT).show();
                }
            });

        }

        Log.e("call", "onCreateOptionsMenu: finished");

        return true;
    }

    // determines if action bar item was selected. if true then do the corresponding action
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.e("wished", "onOptionsItemSelected: " + bookClickedId);
        switch (item.getItemId()) {
            case R.id.bookmark_empty:
                Log.e("wished", "switch case , empty: " + bookClickedId);
                changeWishedStatus("markWished");
                filledBookMark.setVisible(true);
                emptyBookMark.setVisible(false);
                return true;
            case R.id.bookmark_filled:
                Log.e("wished", "switch case , filled: " + bookClickedId);
                changeWishedStatus("removeWished");
                filledBookMark.setVisible(false);
                emptyBookMark.setVisible(true);
                return true;
            default:
                Toast.makeText(BookDetailsActivity.this, "something went wrong...", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeWishedStatus(String wished) {
        databaseReference = firebaseDatabase.getReference("wishlists").child(userId);
        if (wished.equals("markWished")) {
            String wishKey = databaseReference.push().getKey();
            WishListModel wishListModel = new WishListModel();
            wishListModel.setBookId(bookClickedId);
            wishListModel.setWishListId(wishKey);
            databaseReference.child(bookClickedId).setValue(wishListModel);
        } else if (wished.equals("removeWished")) {
            databaseReference.child(bookClickedId).removeValue();
        }
    }

    // Note => how to update children , without changing entire object means update
    //         particular child , without updating.

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return super.onSupportNavigateUp();
    }
}