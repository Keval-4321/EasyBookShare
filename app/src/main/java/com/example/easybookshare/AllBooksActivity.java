package com.example.easybookshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.easybookshare.adapters.BooksLoadAdapter;
import com.example.easybookshare.models.BookModel;
import com.example.easybookshare.models.BooksLoadModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllBooksActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LinearLayout linearLayoutContainer;
    ArrayList<BooksLoadModel> arrayList;
    ArrayList<BooksLoadModel> backupListByCategory;
    ArrayList<String> categoryList;
    RecyclerView recyclerView;

    BooksLoadAdapter booksLoadAdapter;
    String clickedItem;
    TextView txtCategoryName;


    private static final String TAG = "AllBooksActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_books);

        linearLayoutContainer = findViewById(R.id.category_container);
        recyclerView = findViewById(R.id.recycler_view);
        txtCategoryName = findViewById(R.id.txt_category_name);

        arrayList = new ArrayList<>();
        backupListByCategory = new ArrayList<>();
        categoryList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("books");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                int count = (int) snapshot.getChildrenCount();
                Log.e(TAG, "onDataChange: no of child : " +count);
                arrayList.clear();
                categoryList.add("All");

                if(snapshot.exists())
                {

                    defaultAllCategory();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        BookModel bookModel = dataSnapshot.getValue(BookModel.class);
                        BooksLoadModel booksLoadModel =
                                new BooksLoadModel(bookModel.getBookId(), bookModel.getImgUrl1(),
                                        bookModel.getBookName(), false, bookModel.getUserId());
                        booksLoadModel.setCategoryName(bookModel.getCategoryName());

                        // adding to the list
                        arrayList.add(booksLoadModel);
                        backupListByCategory.add(booksLoadModel);


                        // if same book again aave to e arraylist ma store kari but
                        // category list name ma nahi , or else repeat thase.
                        if (!categoryList.contains(bookModel.getCategoryName()))
                        {
                            categoryList.add(bookModel.getCategoryName());

                            // creating text view for the no of categories
                            TextView txtCategory = new TextView(getApplicationContext());
                            txtCategory.setText(bookModel.getCategoryName());
                            txtCategory.setElevation(20);
                            txtCategory.setTextSize(18);
                            txtCategory.setBackground(getResources().getDrawable(R.drawable.bg_view_more));
                            txtCategory.setPadding(32, 32, 32, 32);
                            // for the margin
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(16,0,16,0);
                            txtCategory.setLayoutParams(layoutParams);

                            // adding text view to the layout container
                            linearLayoutContainer.addView(txtCategory);

                            txtCategory.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {
                                    clickedItem = txtCategory.getText().toString();
                                    txtCategoryName.setText(clickedItem);
                                    if (!txtCategory.getText().toString().equalsIgnoreCase("All")) {
                                        arrayList.clear();
                                        Log.e(TAG, "onClick: clicked item list before " + arrayList.size());
                                        for (BooksLoadModel booksLoadModel1 : backupListByCategory) {
                                            if (clickedItem.equalsIgnoreCase(booksLoadModel1.getCategoryName())) {
                                                arrayList.add(booksLoadModel1);
                                            }
                                        }
                                        Log.e(TAG, "onClick: categories " + categoryList);
                                        Log.e(TAG, "onClick: clicked item " + clickedItem);
                                        Log.e(TAG, "onClick: clicked item list " + arrayList.size());
                                        booksLoadAdapter.notifyDataSetChanged();
                                    }
                                }

                            });
                        }

                    }

                    booksLoadAdapter = new BooksLoadAdapter(AllBooksActivity.this, arrayList);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(AllBooksActivity.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(booksLoadAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void defaultAllCategory()
    {
        // creating default text view for the no of categories
        TextView txtCategory1 = new TextView(getApplicationContext());
        txtCategory1.setText("All");
        txtCategory1.setElevation(20);
        txtCategory1.setTextSize(18);
        txtCategory1.setBackground(getResources().getDrawable(R.drawable.bg_view_more));
        txtCategory1.setPadding(32, 32, 32, 32);

        // for the margin
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(16,0,16,0);
        txtCategory1.setLayoutParams(layoutParams);


        // adding text view to the layout container
        linearLayoutContainer.addView(txtCategory1);

        txtCategory1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                txtCategoryName.setText("All");
                arrayList.clear();
                arrayList.addAll(backupListByCategory);
                booksLoadAdapter.notifyDataSetChanged();
            }
        });

    }
}