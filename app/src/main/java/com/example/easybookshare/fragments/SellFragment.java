package com.example.easybookshare.fragments;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easybookshare.R;
import com.example.easybookshare.adapters.SellBookAdapter;
import com.example.easybookshare.helpers.SharedPreferenceManager;
import com.example.easybookshare.models.BookModel;
import com.example.easybookshare.models.CategoryModel;
import com.example.easybookshare.models.SellBookModel;
import com.example.easybookshare.models.SubCategoryModel;
import com.example.easybookshare.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellFragment extends Fragment {


    FloatingActionButton fabSellBook;
    Uri uriPdfData;
    Uri imgUri;
    TextView txtUri, txtPath;
    CircleImageView civs;
    CircleImageView civ1;
    CircleImageView civ2;
    CircleImageView civ3;
    EditText edtBook, edtBookPages, edtOPrice, edtSPrice, edtAuthor, edtVolume,edtQuantity;
    int id;
    ArrayList<Bitmap> bitmaps;

    Map<String, Uri> uris;
    Spinner spinnerCategory, spinnerSubCategory;

    ArrayList<String> listCategories;
    String strSubCategoryGeneral[] = {"Select"};
    // 2nd way
    Map<String,String> mapCategoryData;

    String categoryName = null;
    String subcategoryName = null;
    Uri img1 = null;
    Uri img2 = null;
    Uri img3 = null;
    String bookName = null;
    String bookPages = null;
    String bookOPrice = null;
    String bookSPrice = null;
    String catName = null;
    String subCatName = null;
    String volume= null;
    String quantity = null;
    String authorName = null;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;

    String url1 = "";
    String url2 = "";
    String url3 = "";

    ProgressDialog progressDialog;
    ProgressBar progressBar;
    SharedPreferenceManager sharedPreferenceManager;
    String userId;
    RecyclerView recyclerViewSellBook;
    TextView txtNTS;
    ArrayList<SellBookModel> arrayList;

    SellBookAdapter sellBookAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sell, container, false);

        fabSellBook = view.findViewById(R.id.fab_sell_book);
        recyclerViewSellBook = view.findViewById(R.id.recycler_sell_book);
        progressBar = view.findViewById(R.id.progressbar);
        txtNTS = view.findViewById(R.id.txt_nts);

        uris = new HashMap<>();
        arrayList = new ArrayList();
        listCategories = new ArrayList<>();
        mapCategoryData = new LinkedHashMap<>();

        sellBookAdapter = new SellBookAdapter();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        sharedPreferenceManager = new SharedPreferenceManager(getActivity());
        userId = sharedPreferenceManager.sharedPreferencesData.getString("USER_ID", "");
        // for thr default loading data
        loadBookUploadedByMe();

        uris.put("civ1", null);
        uris.put("civ2", null);
        uris.put("civ3", null);

        fabSellBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater layoutInflater = getLayoutInflater();
                View view1 = layoutInflater.inflate(R.layout.layout_book_detail, null);
                builder.setView(view1);
                builder.setCancelable(false);

                // civs = view1.findViewById(R.id.img_book1);
                civ1 = view1.findViewById(R.id.img_book1);
                civ2 = view1.findViewById(R.id.img_book2);
                civ3 = view1.findViewById(R.id.img_book3);
                edtBook = view1.findViewById(R.id.edt_bookName);
                edtBookPages = view1.findViewById(R.id.edt_no_pages);
                //EditText edtCategory = view1.findViewById(R.id.edt_catName);
                edtOPrice = view1.findViewById(R.id.edt_oPrice);
                edtSPrice = view1.findViewById(R.id.edt_sPrice);
                edtAuthor = view1.findViewById(R.id.edt_author);
                edtQuantity = view1.findViewById(R.id.edt_quantity);
                edtVolume = view1.findViewById(R.id.edt_volume);


                spinnerCategory = view1.findViewById(R.id.spinner_category);
                spinnerSubCategory = view1.findViewById(R.id.spinner_sub_category);

                Button btnUpload = view1.findViewById(R.id.btn_upload);
                ImageView imgCancel = view1.findViewById(R.id.img_cancel);

                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                //spinnerDataCategory();
                spinnerDataCategory2();

                civ1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectSingleImg(civ1);
                    }
                });
                civ2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectSingleImg(civ2);
                    }
                });
                civ3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        selectSingleImg(civ3);
                    }
                });

                btnUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        bookPages = edtBookPages.getText().toString();
                        bookSPrice = edtSPrice.getText().toString();
                        bookOPrice = edtOPrice.getText().toString();
                        bookName = edtBook.getText().toString();
                        authorName = edtAuthor.getText().toString();
                        volume = edtVolume.getText().toString();
                        quantity = edtQuantity.getText().toString();
                        img1 = uris.get("civ1");
                        img2 = uris.get("civ2");
                        img3 = uris.get("civ3");
                        catName = categoryName;
                        subCatName = subcategoryName;

                        if (img1 == null | img2 == null | img3 == null | bookName == null | bookOPrice == null
                                | catName == null | subCatName == null | volume==null| quantity==null| bookSPrice == null | bookPages == null
                                | authorName==null)
                        {
                            Toast.makeText(getActivity(), "Fill all details...", Toast.LENGTH_SHORT).show();
                        } else {
                            // txtUri.setText("Uri :\n" + uriPdfData);
                            // txtPath.setText("Path :\n" +uriPdfData.getPath());
//                            DatabaseReference userRef = firebaseDatabase.getReference("users").child(userId).child("phoneNumber");


                            alertDialog.cancel();
                            //Toast.makeText(getActivity(), "" + categoryName +"=>"+subcategoryName, Toast.LENGTH_SHORT).show();
                            progressDialog = new ProgressDialog(getActivity(), R.style.CustomProgressDialog);
                            progressDialog.setMessage("uploading book...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            uploadAndDownloadUrl(uris.get("civ1"));

                        }
                    }
                });
                imgCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        return view;
    }

    private void storeBooks(BookModel bookModel)
    {

        databaseReference = firebaseDatabase.getReference("books");
        databaseReference.child(bookModel.getBookId()).setValue(bookModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // removing dialog
                progressDialog.dismiss();

                // shows current users uploaded book
                loadBookUploadedByMe();

                Toast.makeText(getActivity(), "Book Added Successfully...", Toast.LENGTH_SHORT).show();
                categoryName = null;
                subcategoryName = null;
                uris.put("civ1", null);
                uris.put("civ2", null);
                uris.put("civ3", null);
                bookName = null;
                bookPages = null;
                bookOPrice = null;
                bookSPrice = null;
                catName = null;
                volume = null;
                quantity = null;
                subCatName = null;
                authorName = null;
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBookUploadedByMe()
    {
        databaseReference = firebaseDatabase.getReference("books");
        Query query = databaseReference.orderByChild("userId").equalTo(userId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                arrayList.clear();
                Log.e("hey", "inside onDataChange: ");
                Log.e("hey", "match userId : " + userId);
                if (snapshot.exists())
                {
                    Log.e("hey", "inside onDataChange exists(): ");
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Log.e("hey", "inside onDataChange for-loop: ");
                        BookModel bookModel = dataSnapshot.getValue(BookModel.class);
                        String imgUrl = bookModel.getImgUrl1();
                        String bookName = bookModel.getBookName();
                        // will use to perform delete and edit operations
                        String bookId = bookModel.getBookId();

                        SellBookModel book = new SellBookModel(imgUrl, bookName,bookId);
                        Log.e("hey", "onDataChange: " + book.getBookName() + " " + book.getImgUrl1());
                        arrayList.add(book);
                    }
                    Log.e("hey", "for-loop completed : ");
                    txtNTS.setVisibility(View.GONE);
                    sellBookAdapter = new SellBookAdapter(getActivity(), arrayList);
                    recyclerViewSellBook.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    // showing books in grid manner
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),3);
                    recyclerViewSellBook.setLayoutManager(gridLayoutManager);
                    recyclerViewSellBook.setAdapter(sellBookAdapter);
                }
                else
                {
                    Log.e("hey", " data does not exist: ");
                    progressBar.setVisibility(View.GONE);
                    txtNTS.setVisibility(View.VISIBLE);
                    recyclerViewSellBook.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadAndDownloadUrl(Uri uriDownload) {
        String uniqueId = "images/" + UUID.randomUUID().toString();
        StorageReference storageReference = firebaseStorage.getReference("books_storage").child(uniqueId);
        storageReference.putFile(uriDownload)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.e("thread", "onComplete: => " + Thread.currentThread().getName());
                            Toast.makeText(getActivity(), "uploaded successfully...", Toast.LENGTH_SHORT).show();

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (uriDownload == uris.get("civ1")) {
                                        url1 = uri.toString();
                                        uploadAndDownloadUrl(uris.get("civ2"));
                                    } else if (uriDownload == uris.get("civ2")) {
                                        url2 = uri.toString();
                                        uploadAndDownloadUrl(uris.get("civ3"));
                                    } else {
                                        url3 = uri.toString();
                                        Toast.makeText(getActivity(), "downloaded click :" + url2.isEmpty() + " " + url1.isEmpty() + " " + url3.isEmpty(), Toast.LENGTH_SHORT).show();

                                        if (!TextUtils.isEmpty(url1) && !TextUtils.isEmpty(url2) && !TextUtils.isEmpty(url3)) {
                                            String bookId = databaseReference.push().getKey();
                                            BookModel bookModel = new BookModel(bookId, userId,
                                                    url1, url2, url3,
                                                    bookName, bookPages,
                                                    categoryName, subcategoryName,
                                                    volume, quantity ,bookOPrice, bookSPrice,false,authorName);
                                            storeBooks(bookModel);
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "not added...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Download error" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "upload error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
    }

    private void loadSubCatGeneral()
    {
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                strSubCategoryGeneral)
        {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView txtView = (TextView) super.getView(position, convertView, parent);
                txtView.setTextColor(Color.WHITE);
                txtView.setTextSize(20);
                txtView.setEllipsize(TextUtils.TruncateAt.END);
                txtView.setSingleLine();
                return txtView;
            }
        };
        spinnerSubCategory.setAdapter(arrayAdapter);
    }

    // 2nd way
    private void spinnerDataCategory2()
    {
        // fetching categories from the database
        databaseReference = firebaseDatabase.getReference("category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                mapCategoryData.clear();
                if(snapshot.exists())
                {
                    mapCategoryData.put("Select","sdkl30032022");
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        CategoryModel categoryModel = dataSnapshot.getValue(CategoryModel.class);
                        mapCategoryData.put(categoryModel.getCategoryName(),categoryModel.getCategoryId());
                    }
                    ArrayAdapter arrayAdapterCategory = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,
                            mapCategoryData.keySet().toArray())
                    {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                            TextView txtView = (TextView) super.getView(position, convertView, parent);
                            txtView.setTextColor(Color.WHITE);
                            txtView.setTextSize(20);
                            txtView.setEllipsize(TextUtils.TruncateAt.END);
                            txtView.setSingleLine();
                            return txtView;
                        }
                    };
                    spinnerCategory.setAdapter(arrayAdapterCategory);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        // for the default , to set "select" in subcategory
        loadSubCatGeneral();

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                categoryName = adapterView.getItemAtPosition(i).toString();
                Log.e("spinner", "onItemSelected: " + categoryName );
                loadSubCategories(mapCategoryData.get(categoryName));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    private void loadSubCategories(String catId)
    {
        ArrayList<String> listCategories = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference("sub_category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                listCategories.clear();
                listCategories.add("Select");
                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        SubCategoryModel subCategoryModel = dataSnapshot.getValue(SubCategoryModel.class);
                        if(catId.contains(subCategoryModel.getCategoryId()))
                        {
                            listCategories.add(subCategoryModel.getSubCategoryName());
                        }
                    }
                    Log.e("spinner", "onDataChange: " + listCategories);
                    ArrayAdapter arrayAdapterBio = new ArrayAdapter(getActivity()
                            , android.R.layout.simple_list_item_1, listCategories.toArray())
                    {
                        @NonNull
                        @Override
                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                            TextView txtView = (TextView) super.getView(position, convertView, parent);
                            txtView.setTextColor(Color.WHITE);
                            txtView.setTextSize(20);
                            txtView.setEllipsize(TextUtils.TruncateAt.END);
                            txtView.setSingleLine();
                            return txtView;
                        }
                    };
                    spinnerSubCategory.setAdapter(arrayAdapterBio);
                    spinnerSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            subcategoryName = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(getActivity(), "not available", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void selectPdf() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        //intent.setType("application/pdf/docs/ppt");
        startActivityForResult(Intent.createChooser(intent, "Pdf file select"), 11);
    }

    // for the multiple image selection
    private void selectImageMultipleOrSingle() {
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        // to allow to user to select multiple
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 12);
    }

    private void selectSingleImg(CircleImageView view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 13);

        Toast.makeText(getActivity(), "fn call return", Toast.LENGTH_SHORT).show();
        // then adding image to that view
        // view.setImageURI(imgUri);
        // imgUri = null;
        id = view.getId();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(getActivity(), "thread => " + Thread.currentThread().getName(), Toast.LENGTH_SHORT).show();
        // for the pdf file
//        if(requestCode==11 && resultCode==getActivity().RESULT_OK)
//        {
//            if(data!=null)
//            {
//                uriPdfData = data.getData();
//                //Toast.makeText(getActivity(), " data => "+uriPdfData.toString(), Toast.LENGTH_SHORT).show();
//            }
//        }

        // for the multiple image selection
//        if (requestCode==12 && resultCode==getActivity().RESULT_OK)
//        {
//            List<Bitmap> bitmaps = new ArrayList<>();
//            // Checking whether data is null or not
//            if(data!=null)
//            {
//                ClipData clipData = data.getClipData();
//                            // returns null if user select only one image
//                if(clipData!=null)
//                {
//                    // means user selected multiple images , so will retrieve multiple images
//                    for(int i=0;i<clipData.getItemCount();i++)
//                    {
//                        Uri uri = clipData.getItemAt(i).getUri();
//                        try
//                        {
//                            InputStream is = getActivity().getContentResolver().openInputStream(uri);
//                            Bitmap bitmap = BitmapFactory.decodeStream(is);
//                            bitmaps.add(bitmap);
//                        }
//                        catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//                else  // if there is one image selection , then retrieve only one image
//                {
//                    Uri uri = data.getData();
//                    try {
//                        // will open an input stream
//                        InputStream is = getActivity().getContentResolver().openInputStream(uri);
//                        // decoding input stream into bitmap
//                        Bitmap bitmap = BitmapFactory.decodeStream(is);
//                        // adding this bitmap into our list
//                        bitmaps.add(bitmap);
//
//                    }
//                    catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                // displaying images in imageview on a new thread
//                new Thread(new Runnable() {
//                    @Override
//                    public void run()
//                    {
//                        for (final Bitmap img:bitmaps)
//                        {
//                            // showing bitmap in imageview using UI thread
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run()
//                                {
//                                    civs.setImageBitmap(img);
//                                }
//                            });
//                        }
//                    }
//                }).start();
//            }
//        }

        // single select , single
        if (requestCode == 13 && resultCode == getActivity().RESULT_OK) {
            if (data != null) {
                imgUri = data.getData();
                switch (id) {
                    case R.id.img_book1:
                        civ1.setBackground(null);
                        civ1.setImageURI(imgUri);
                        uris.put("civ1", imgUri);
                        break;
                    case R.id.img_book2:
                        civ2.setBackground(null);
                        civ2.setImageURI(imgUri);
                        uris.put("civ2", imgUri);
                        break;
                    case R.id.img_book3:
                        civ3.setBackground(null);
                        civ3.setImageURI(imgUri);
                        uris.put("civ3", imgUri);
                        break;
                }
                imgUri = null;
            }
        }
    }
}