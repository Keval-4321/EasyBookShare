package com.example.easybookshare.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easybookshare.R;
import com.example.easybookshare.models.BookModel;
import com.example.easybookshare.models.CategoryModel;
import com.example.easybookshare.models.SellBookModel;
import com.example.easybookshare.models.SubCategoryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SellBookAdapter extends RecyclerView.Adapter<SellBookAdapter.SellBookViewHolder> {
    Context context;
    ArrayList<SellBookModel> arrayList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    BookModel model;
    BookModel updatedModel;
    EditText editBookName;
    EditText editBookPages;
    EditText editBookOPrice;
    EditText editBookSPrice;
    EditText editAuthor;
    Spinner spinnerCategoryEdit;
    Spinner spinnerSubCategoryEdit;
    String categoryName;
    String subCategoryName;
    // for the default list item to be selected in spinner
    ArrayList<String> strCategoryGeneral;
    ArrayList<String> strSubCategoryGeneral;
    ArrayList<String> listCategories;
    Map<String,String> mapCatDataList;
    boolean firstTime;


    View viewExistingDetail;

    public SellBookAdapter(Context context, ArrayList<SellBookModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        firebaseDatabase = FirebaseDatabase.getInstance();

        // dialog
        strCategoryGeneral = new ArrayList<>();
        strSubCategoryGeneral = new ArrayList<>();
        listCategories = new ArrayList<>();
        mapCatDataList = new LinkedHashMap<>();
    }

    public SellBookAdapter() {
    }

    @NonNull
    @Override
    public SellBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout_book, null);
        return new SellBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellBookViewHolder holder, int position) {
        holder.txtBook.setText(arrayList.get(position).getBookName());
        String URL = arrayList.get(position).getImgUrl1();
        String bookId = arrayList.get(position).getBookId();
        int pos = position;
        Glide.with(context)
                .load(URL)
                .centerCrop()
                .placeholder(R.drawable.img_book)
                .into(holder.imgBook);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                firstTime = true;

                // long click vibration effect , ong-press-feedback vibration just like ActionMode or ContextMenu do
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view1 = inflater.inflate(R.layout.layout_custom_dialog, new RelativeLayout(context), false);
                builder.setView(view1);
                builder.setCancelable(false);

                AlertDialog alertDialogConfirmation = builder.create();
                alertDialogConfirmation.show();
                alertDialogConfirmation.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Button btnDelete = view1.findViewById(R.id.btn_delete);
                Button btnEdit = view1.findViewById(R.id.btn_edit);

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogConfirmation.cancel();
                        deleteBookFromSell(bookId, pos);
                    }
                });
                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogConfirmation.cancel();
                        fetchExistedBookData(bookId);
                    }
                });
                return false;
            }
        });
    }

    private void fetchExistedBookData(String bookId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewExistingDetail = inflater.inflate(R.layout.layout_custom_edit_data_dialog, null);
        builder.setView(viewExistingDetail);
        editBookName = viewExistingDetail.findViewById(R.id.edt_bookName_edit);
        editBookPages = viewExistingDetail.findViewById(R.id.edt_no_pages_edit);
        editBookOPrice = viewExistingDetail.findViewById(R.id.edt_oPrice_edit);
        editBookSPrice = viewExistingDetail.findViewById(R.id.edt_sPrice_edit);
        editAuthor = viewExistingDetail.findViewById(R.id.edt_author_edit);
        spinnerCategoryEdit = viewExistingDetail.findViewById(R.id.spinner_category_edit);
        spinnerSubCategoryEdit = viewExistingDetail.findViewById(R.id.spinner_sub_category_edit);


        Log.e("bookId", "fetchExistedBookData: " + bookId);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnUpdate = viewExistingDetail.findViewById(R.id.btn_update);
        ImageView imgCancel = viewExistingDetail.findViewById(R.id.img_cancel);

        databaseReference = firebaseDatabase.getReference("books").child(bookId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    model = snapshot.getValue(BookModel.class);
                    updatedModel = new BookModel(model);

                    editBookName.setText(model.getBookName());
                    editBookOPrice.setText(model.getoPrice());
                    editBookSPrice.setText(model.getsPrice());
                    editBookPages.setText(model.getNoOfPages());
                    editAuthor.setText(model.getAuthorName());
                    categoryName = model.getCategoryName();
                    subCategoryName = model.getSubCategoryName();

                    // to fill the category and subcategory
                    //spinnerDataCategory();
                    spinnerDataCategory2();

                } else
                {
                    Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                //alertDialog.cancel();
                updateBookFromSell(bookId,alertDialog);
            }
        });

        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    private void updateBookFromSell(String bookId,AlertDialog alertDialog)
    {
        String bName = editBookName.getText().toString();
        String bPages = editBookPages.getText().toString();
        String bOPrice = editBookOPrice.getText().toString();
        String bSPrice = editBookSPrice.getText().toString();
        String bAuthor = editAuthor.getText().toString();

        if(TextUtils.isEmpty(bName) | TextUtils.isEmpty(bPages)  | TextUtils.isEmpty(bOPrice)
                | TextUtils.isEmpty(bSPrice) | TextUtils.isEmpty(bAuthor)
                | categoryName.equalsIgnoreCase("Select") | subCategoryName.equalsIgnoreCase("Select"))
        {
            Log.e("newDetail", "updateBookFromSell: " +bName +" " +bAuthor + " "+bOPrice+" "+bSPrice+" "+categoryName+" "+subCategoryName );
            Toast.makeText(context, "provide all details...", Toast.LENGTH_SHORT).show();
            return;
        }
        alertDialog.cancel();
        BookModel bookModel = new BookModel(updatedModel.getBookId(), updatedModel.getUserId(),
                updatedModel.getImgUrl1(), updatedModel.getImgUrl2(), updatedModel.getImgUrl3(),
                bName, bPages, categoryName, subCategoryName,
                bOPrice, bSPrice, true, bAuthor);
        databaseReference = firebaseDatabase.getReference("books");
        // inserting updated to the same node => update
        databaseReference.child(updatedModel.getBookId()).setValue(bookModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused)
            {
                Toast.makeText(context, "updated successfully...", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteBookFromSell(String bookId, int i) {
        databaseReference = firebaseDatabase.getReference("books").child(bookId);
        databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Item deleted Successfully...", Toast.LENGTH_SHORT).show();
                // notifying adapter that the data has been changed.
                notifyItemRemoved(i);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class SellBookViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBook;
        TextView txtBook;

        public SellBookViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.img_book);
            txtBook = itemView.findViewById(R.id.txt_book_name);
        }
    }

    private void spinnerDataCategory() {

        // for the default , to set "select" in subcategory
        loadCatSubCatGeneral();

        // for the sub category

        //spinnerCategory.setSelection(0);
        Log.e("Adapter", "spinnerDataCategory: before ");

        spinnerCategoryEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                categoryName = adapterView.getItemAtPosition(position).toString();

                if (categoryName.equals("Biographies"))
                {
                    Log.e("spinner", "onItemSelected: " + categoryName);
                    loadSubCatBiographies();
                } else if (categoryName.equals("sci-fi")) {
                    Log.e("spinner", "onItemSelected: " + categoryName);
                    loadSubCatSciFi();

                } else if (categoryName.equals("Education")) {
                    Log.e("spinner", "onItemSelected: " + categoryName);
                    loadSubCatEducation();
                } else {
                    loadCatSubCatGeneral();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    private void loadCatSubCatGeneral() {
        strCategoryGeneral.clear();
        strSubCategoryGeneral.clear();

        strCategoryGeneral.add(categoryName);
        strSubCategoryGeneral.add(subCategoryName);

        // fetching categories from the database
        databaseReference = firebaseDatabase.getReference("categories");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String category = dataSnapshot.getValue(String.class);
                        if (category.equals(model.getCategoryName())) {
                            continue;
                        }
                        strCategoryGeneral.add(category);
                    }
                    Log.e("categories", "onDataChange: " + strCategoryGeneral);
                    // for the category
                    ArrayAdapter arrayAdapterCategory = new ArrayAdapter(context, android.R.layout.simple_list_item_1,
                            strCategoryGeneral.toArray()) {
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
                    spinnerCategoryEdit.setAdapter(arrayAdapterCategory);

                    ArrayAdapter arrayAdapterSubCategory = new ArrayAdapter(context, android.R.layout.simple_list_item_1,
                            strSubCategoryGeneral.toArray()) {
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
                    spinnerSubCategoryEdit.setAdapter(arrayAdapterSubCategory);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadSubCatEducation() {
        ArrayList<String> listEducation = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference("subcategories").child("sc3_education");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listEducation.clear();
                listEducation.add("Select");
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String subCat = dataSnapshot.getValue(String.class);
                        listEducation.add(subCat);
                    }

                    ArrayAdapter arrayAdapterEdu = new ArrayAdapter(context
                            , android.R.layout.simple_list_item_1, listEducation.toArray()) {

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

                    spinnerSubCategoryEdit.setAdapter(arrayAdapterEdu);
                    spinnerSubCategoryEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            subCategoryName = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                } else {
                    Toast.makeText(context, "not available", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void loadSubCatSciFi() {
        ArrayList<String> listSciFi = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference("subcategories").child("sc2_scifi");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listSciFi.clear();
                listSciFi.add("Select");
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String subCat = dataSnapshot.getValue(String.class);
                        listSciFi.add(subCat);
                    }
                    ArrayAdapter arrayAdapterSciFi = new ArrayAdapter(context
                            , android.R.layout.simple_list_item_1, listSciFi.toArray()) {
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
                    spinnerSubCategoryEdit.setAdapter(arrayAdapterSciFi);
                    spinnerSubCategoryEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            subCategoryName = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } else {
                    Toast.makeText(context, "not available", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void loadSubCatBiographies() {

        ArrayList<String> listBiographies = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference("subcategories").child("sc1_biographies");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBiographies.clear();
                listBiographies.add("Select");
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        String subCat = dataSnapshot.getValue(String.class);
                        listBiographies.add(subCat);
                    }
                    Log.e("spinner", "onDataChange: " + listBiographies);
                    ArrayAdapter arrayAdapterBio = new ArrayAdapter(context
                            , android.R.layout.simple_list_item_1, listBiographies.toArray()) {
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
                    spinnerSubCategoryEdit.setAdapter(arrayAdapterBio);
                    spinnerSubCategoryEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            subCategoryName = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } else {
                    Toast.makeText(context, "not available", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void spinnerDataCategory2() {

        // for the default , to set "select" in subcategory
        loadCatSubCatGeneral2();
        // for the sub category

        //spinnerCategory.setSelection(0);
        Log.e("Adapter", "spinnerDataCategory: before ");

        spinnerCategoryEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                categoryName = adapterView.getItemAtPosition(position).toString();

                if (categoryName.equals("Biographies"))
                {
                    Log.e("spinner", "onItemSelected: " + categoryName);
                    loadSubCatBiographies2(mapCatDataList.get("Biographies"));
                } else if (categoryName.equals("sci-fi")) {
                    Log.e("spinner", "onItemSelected: " + categoryName);
                    loadSubCatSciFi2(mapCatDataList.get("sci-fi"));

                } else if (categoryName.equals("Education")) {
                    Log.e("spinner", "onItemSelected: " + categoryName);
                    loadSubCatEducation2(mapCatDataList.get("Education"));
                } else
                {
                    loadCatSubCatGeneral2();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    private void loadCatSubCatGeneral2()
    {
        strCategoryGeneral.clear();
        strSubCategoryGeneral.clear();

        mapCatDataList.put(categoryName,"dummy292929");

        strCategoryGeneral.add(categoryName);
        strSubCategoryGeneral.add(subCategoryName);

        // fetching categories from the database
        databaseReference = firebaseDatabase.getReference("category");
        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        CategoryModel categoryModel = dataSnapshot.getValue(CategoryModel.class);
                        mapCatDataList.put(categoryModel.getCategoryName(),categoryModel.getCategoryId());
                    }
                    // mapCatDataList.put("Select","0");

                    Log.e("categories", "onDataChange: " + strCategoryGeneral);
                    // for the category
                    ArrayAdapter arrayAdapterCategory = new ArrayAdapter(context, android.R.layout.simple_list_item_1,
                            mapCatDataList.keySet().toArray())
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
                    spinnerCategoryEdit.setAdapter(arrayAdapterCategory);

                    ArrayAdapter arrayAdapterSubCategory = new ArrayAdapter(context, android.R.layout.simple_list_item_1,
                            strSubCategoryGeneral.toArray()) {
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
                    spinnerSubCategoryEdit.setAdapter(arrayAdapterSubCategory);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadSubCatEducation2(String catId)
    {

        ArrayList<String> listEducation = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference("sub_category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listEducation.clear();
                listEducation.add("Select");
                if (snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        SubCategoryModel subCategoryModel = dataSnapshot.getValue(SubCategoryModel.class);
                        if(subCategoryModel.getCategoryId().equals(catId))
                        {
                            listEducation.add(subCategoryModel.getSubCategoryName());
                        }
                    }

                    ArrayAdapter arrayAdapterEdu = new ArrayAdapter(context
                            , android.R.layout.simple_list_item_1, listEducation.toArray()) {

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

                    spinnerSubCategoryEdit.setAdapter(arrayAdapterEdu);
                    spinnerSubCategoryEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            subCategoryName = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                } else {
                    Toast.makeText(context, "not available", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void loadSubCatSciFi2(String catId)
    {
        ArrayList<String> listSciFi = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference("sub_category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listSciFi.clear();
                listSciFi.add("Select");
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        SubCategoryModel subCategoryModel = dataSnapshot.getValue(SubCategoryModel.class);
                        if(subCategoryModel.getCategoryId().equals(catId))
                        {
                            listSciFi.add(subCategoryModel.getSubCategoryName());
                        }
                    }
                    ArrayAdapter arrayAdapterSciFi = new ArrayAdapter(context
                            , android.R.layout.simple_list_item_1, listSciFi.toArray()) {
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
                    spinnerSubCategoryEdit.setAdapter(arrayAdapterSciFi);
                    spinnerSubCategoryEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            subCategoryName = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } else {
                    Toast.makeText(context, "not available", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void loadSubCatBiographies2(String catId) {

        ArrayList<String> listBiographies = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference("sub_category");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBiographies.clear();
                listBiographies.add("Select");
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        SubCategoryModel subCategoryModel = dataSnapshot.getValue(SubCategoryModel.class);
                        if(subCategoryModel.getCategoryId().equals(catId))
                        {
                            listBiographies.add(subCategoryModel.getSubCategoryName());
                        }
                    }
                    Log.e("spinner", "onDataChange: " + listBiographies);
                    ArrayAdapter arrayAdapterBio = new ArrayAdapter(context
                            , android.R.layout.simple_list_item_1, listBiographies.toArray()) {
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
                    spinnerSubCategoryEdit.setAdapter(arrayAdapterBio);
                    spinnerSubCategoryEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            subCategoryName = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                } else {
                    Toast.makeText(context, "not available", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
