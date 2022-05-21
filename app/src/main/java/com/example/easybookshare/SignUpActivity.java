package com.example.easybookshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easybookshare.helpers.SharedPreferenceManager;
import com.example.easybookshare.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    EditText edtUname, edtEmail, edtCity, edtPwd;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    SharedPreferenceManager sharedPreferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtUname = findViewById(R.id.edt_uname);
        edtEmail = findViewById(R.id.edt_email);
        edtPwd = findViewById(R.id.edt_pwd);
        edtCity = findViewById(R.id.edt_city);

        firebaseDatabase = FirebaseDatabase.getInstance("https://easybookshare-cbe54-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this, R.style.CustomProgressDialog);

    }

    private boolean validateUserName()
    {
        //as we don't want to store any white space
        String uName = edtUname.getText().toString().trim();
        String checkSpaces = "Aw{1,20}z";
        // can write from capital-A to small-z and w=white spaces {1,20} =>length limitation
        if (uName.isEmpty()) {
            edtUname.setError("Can't be empty");
            return false;
        } else if (edtUname.length() > 20) {
            edtUname.setError("username is too long");
            return false;
        } else {
            edtUname.setError(null);  // first of all error remove karse
            return true;
        }
    }

    private boolean validateEmail() {
        //as we don't want to store any white space
        String email = edtEmail.getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";
        // can write from capital-A to small-z and w=white spaces {1,20} =>length limitation
        if (email.isEmpty()) {
            edtEmail.setError("Can't be empty");
            return false;
        } else if (!email.matches(checkEmail)) {
            edtEmail.setError("Invalid Email !");
            return false;
        } else {
            edtEmail.setError(null);  // first of all error remove karse
            return true;
        }
    }

    public boolean validatePassword() {

        String pwd = edtPwd.getText().toString().trim();
        if (pwd.isEmpty())
        {
            edtPwd.setError("Can't Be Empty!");
            return false;
        }else if(pwd.length()<4)
        {
            edtPwd.setError("too short , provide 6 digit password");
            return false;
        }
        else {
            edtPwd.setError(null);
            return true;
        }

    }

    public void LoginHere(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void SignUp(View view)
    {

        progressDialog.setMessage("creating account...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (!validateUserName() | !validateEmail() | !validatePassword())
        {
            progressDialog.cancel();
            return;
        }

        String strEmail = edtEmail.getText().toString();
        String strUname = edtUname.getText().toString();
        String strPwd = edtPwd.getText().toString();
        String strCity = edtCity.getText().toString();



        // creating user with email and pwd then storing it to the realtime database
        firebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult)
            {
                String uniqueKey = authResult.getUser().getUid();
                String phoneNumber = "not provided";
                UserModel user = new UserModel(uniqueKey, strUname, strEmail, strPwd, strCity, phoneNumber);
                databaseReference.child("users").child(uniqueKey).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.e("account", "onComplete: ");
                        if (!task.isSuccessful())
                        {
                            Log.e("account", "successful: ");
                            progressDialog.cancel();
                            Toast.makeText(SignUpActivity.this, "something went wrong...", Toast.LENGTH_LONG).show();
                        }
                        else
                        {

                            // cancelling dialog
                            progressDialog.cancel();

                            sharedPreferenceManager = new SharedPreferenceManager(SignUpActivity.this);
                            SharedPreferences.Editor editor = sharedPreferenceManager.sharedPreferencesData.edit();
                            editor.putBoolean("IS_SIGNUP", true);
                            editor.putString("USER_ID", uniqueKey);
                            editor.commit();

                            // moving to the new activity
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(SignUpActivity.this, "Account created successfully...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, "something went wrong...", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                if (e instanceof FirebaseAuthUserCollisionException) {
                    progressDialog.cancel();
                    Toast.makeText(SignUpActivity.this, "user already exist", Toast.LENGTH_SHORT).show();
                }
                if(e instanceof FirebaseNetworkException)
                {
                    progressDialog.cancel();
                    Toast.makeText(SignUpActivity.this, "check your network connection...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}