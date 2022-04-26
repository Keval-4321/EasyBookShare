package com.example.easybookshare.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.easybookshare.AllBooksActivity;
import com.example.easybookshare.R;
import com.example.easybookshare.adapters.BooksLoadAdapter;
import com.example.easybookshare.models.BooksLoadModel;
import com.example.easybookshare.models.BookModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BuyFragment extends Fragment
{

    RecyclerView rvBiographies, rvSciFi, rvEducation;
    ArrayList<BooksLoadModel> listBiographies, listSciFi, listEducation;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ScrollView scrollView;
    ProgressBar progressBar;
    BooksLoadAdapter booksLoadAdapter1;
    BooksLoadAdapter booksLoadAdapter2;
    BooksLoadAdapter booksLoadAdapter3;

    boolean taskBiographiesDone = false;
    boolean taskSciFiDone = false;
    boolean taskEducationsDone = false;

    EditText edtSearch;
    TextView txtViewMore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buy, container, false);
        rvBiographies = view.findViewById(R.id.rv_biographies);
        rvSciFi = view.findViewById(R.id.rv_scifi);
        rvEducation = view.findViewById(R.id.rv_education);
        scrollView = view.findViewById(R.id.scrollview_buy);
        progressBar = view.findViewById(R.id.progressbar_buy);
        edtSearch = view.findViewById(R.id.edt_search);
        txtViewMore = view.findViewById(R.id.txt_view_more);

        listBiographies = new ArrayList<>();
        listSciFi = new ArrayList<>();
        listEducation = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // loading all data
        loadBiographies();
        loadSciFies();
        loadEducations();

        edtSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                String searchedText = charSequence.toString();
                booksLoadAdapter1.filterData(searchedText);
                booksLoadAdapter2.filterData(searchedText);
                booksLoadAdapter3.filterData(searchedText);
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        txtViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), AllBooksActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void checkAllTaskDone()
    {
        Log.e("DoneTask", "onDataChange: checkAllTaskDone() :" +" biography=> "+taskBiographiesDone
          + " education=> " +taskEducationsDone +" scifi=> "+taskSciFiDone);
        if(taskBiographiesDone | taskEducationsDone | taskSciFiDone)
        {
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }

        if(listBiographies.isEmpty() | listSciFi.isEmpty() | listEducation.isEmpty())
        {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void loadEducations()
    {
        databaseReference = firebaseDatabase.getReference("books");
        Query query = databaseReference.orderByChild("categoryName").equalTo("Education");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                listEducation.clear();
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        BookModel bookModel = dataSnapshot.getValue(BookModel.class);
                        BooksLoadModel booksLoadModel = new BooksLoadModel(bookModel.getBookId(),bookModel.getImgUrl1(), bookModel.getBookName(),bookModel.isWished(),bookModel.getUserId());
                        listEducation.add(booksLoadModel);
                    }
                    booksLoadAdapter3 = new BooksLoadAdapter(getActivity(), listEducation);
                    rvEducation.setAdapter(booksLoadAdapter3);
                    taskEducationsDone = true;
                    Log.e("DoneTask", "onDataChange: education" );
                    // checking all tasks done or not
                    checkAllTaskDone();
                }
                else
                {
                    booksLoadAdapter3 = new BooksLoadAdapter(getActivity(), listEducation);
                    rvEducation.setAdapter(booksLoadAdapter3);
                    // if this list is empty and nothing to show here then...
                    taskEducationsDone = false;
                    checkAllTaskDone();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });


    }

    private void loadSciFies()
    {
        databaseReference = firebaseDatabase.getReference("books");
        Query query = databaseReference.orderByChild("categoryName").equalTo("sci-fi");
        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                listSciFi.clear();
                if(snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        BookModel bookModel = dataSnapshot.getValue(BookModel.class);
                        BooksLoadModel booksLoadModel = new BooksLoadModel(bookModel.getBookId(),bookModel.getImgUrl1(), bookModel.getBookName(),bookModel.isWished(),bookModel.getUserId());
                        listSciFi.add(booksLoadModel);
                    }
                    booksLoadAdapter2 = new BooksLoadAdapter(getActivity(), listSciFi);
                    rvSciFi.setAdapter(booksLoadAdapter2);
                    taskSciFiDone = true;
                    Log.e("DoneTask", "onDataChange: scifi" );
                    checkAllTaskDone();
                }
                else
                {
                    booksLoadAdapter2 = new BooksLoadAdapter(getActivity(), listSciFi);
                    rvSciFi.setAdapter(booksLoadAdapter2);
                    // if this list is empty and nothing to show here then...
                    taskSciFiDone = false;
                    checkAllTaskDone();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadBiographies()
    {
        databaseReference = firebaseDatabase.getReference("books");
        Query query = databaseReference.orderByChild("categoryName").equalTo("Biographies");
        query.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                listBiographies.clear();
                if(snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        BookModel bookModel = dataSnapshot.getValue(BookModel.class);
                        BooksLoadModel booksLoadModel = new BooksLoadModel(bookModel.getBookId(), bookModel.getImgUrl1(), bookModel.getBookName(),bookModel.isWished(),bookModel.getUserId());
                        booksLoadModel.setUsedId(bookModel.getUserId());
                        listBiographies.add(booksLoadModel);
                    }
                    booksLoadAdapter1 = new BooksLoadAdapter(getActivity(), listBiographies);
                    rvBiographies.setAdapter(booksLoadAdapter1);
                    taskBiographiesDone = true;
                    Log.e("DoneTask", "onDataChange: biographies" );
                    checkAllTaskDone();
                }
                else
                {
                    booksLoadAdapter1 = new BooksLoadAdapter(getActivity(), listBiographies);
                    rvBiographies.setAdapter(booksLoadAdapter1);
                    // if this list is empty and nothing to show here then...
                    taskBiographiesDone = false;
                    checkAllTaskDone();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}