package com.example.easybookshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easybookshare.helpers.SharedPreferenceManager;
import com.example.easybookshare.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity
{

    EditText edtEmail,edtPwd,edtUname,edtCity,edtPhoneNumber;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    Button btnSaveProfile;
    String userId;

    SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        edtEmail = findViewById(R.id.edt_email);
        edtPwd = findViewById(R.id.edt_pwd);
        edtUname = findViewById(R.id.edt_uname);
        edtCity = findViewById(R.id.edt_city);
        edtPhoneNumber = findViewById(R.id.edt_phoNo);
        btnSaveProfile = findViewById(R.id.btn_save_profile);

        sharedPreferenceManager = new SharedPreferenceManager(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this,R.style.CustomProgressDialog);


        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(UserProfileActivity.this);
        userId = sharedPreferenceManager.sharedPreferencesData.getString("USER_ID",null);

        loadUserProfileData();

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String strUserName = edtUname.getText().toString();
                String strEmail = edtEmail.getText().toString();
                String strPassword = edtPwd.getText().toString();
                String strPhoneNumber = edtPhoneNumber.getText().toString();
                String strCity = edtCity.getText().toString();

//                sharedPreferenceManager = new SharedPreferenceManager(UserProfileActivity.this);
//                String checkPhone = ^(\+91[\-\s]?)?[0]?(91)?[789]\d{9}$;
//                SharedPreferences.Editor editor = sharedPreferenceManager.sharedPreferencesData.edit();
//                editor.putBoolean("IS_PHONE", false);

                UserModel userModel = new UserModel(userId,strUserName,strEmail,strPassword,
                        strCity,strPhoneNumber);
                progressDialog.setMessage("updating Profile...");
                progressDialog.show();

                databaseReference = firebaseDatabase.getReference("users");
                databaseReference.child(userId).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused)
                    {
                        progressDialog.cancel();
                        loadUserProfileData();
                        Intent intent = new Intent(UserProfileActivity.this, DashBoardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        progressDialog.cancel();
                        Toast.makeText(UserProfileActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void loadUserProfileData()
    {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Log.e("storedId", "loadUserProfileData: " + userId );

        databaseReference = firebaseDatabase.getReference("users");
        Query query = databaseReference.orderByChild("uniqueKey")
                      .equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    if(dataSnapshot.exists())
                    {
                        UserModel user = dataSnapshot.getValue(UserModel.class);
                        edtEmail.setText(user.getEmail());
                        edtPwd.setText(user.getPassword());
                        edtUname.setText(user.getuName());
                        edtCity.setText(user.getCity());

                        if(user.getPhoneNumber().equals("not provided"))
                        {
                            edtPhoneNumber.setHint(user.getPhoneNumber());
                        }
                        else
                        {
                            edtPhoneNumber.setText(user.getPhoneNumber());
                        }
                        progressDialog.cancel();
                        break;
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay_here,R.anim.bottom_out_profile);
    }

    public void SignOut(View view)
    {
        firebaseAuth.signOut();
        SharedPreferences.Editor editor = sharedPreferenceManager.sharedPreferencesData.edit();
        editor.putBoolean("IS_LOGIN",false);
        editor.putBoolean("IS_SIGNUP",false);
        editor.putString("USER_ID",null);
        editor.commit();

        Intent intent = new Intent(UserProfileActivity.this,LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}