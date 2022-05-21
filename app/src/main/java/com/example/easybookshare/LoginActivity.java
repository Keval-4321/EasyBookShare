package com.example.easybookshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.easybookshare.helpers.SharedPreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnReset;
    EditText edtEmail,edtPwd;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    // shared preferences manager
    SharedPreferenceManager sharedPreferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_email);
        edtEmail = findViewById(R.id.edt_email);
        edtPwd = findViewById(R.id.edt_pwd);
        btnReset = findViewById(R.id.btn_email);

        firebaseDatabase = FirebaseDatabase.getInstance("https://easybookshare-cbe54-default-rtdb.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this,R.style.CustomProgressDialog);

//        btnReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    public void resetOnClick(View view)
    {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    public void SignUpHere(View view)
    {
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validateEmail()
    {
        //as we don't want to store any white space
        String email = edtEmail.getText().toString().trim();
        String checkEmail="[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";
        // can write from capital-A to small-z and w=white spaces {1,20} =>length limitation
        if(email.isEmpty())
        {
            edtEmail.setError("Can't be empty");
            return false;
        }
        else if(!email.matches(checkEmail))
        {
            edtEmail.setError("Invalid Email !");
            return false;
        }
        else
        {
            edtEmail.setError(null);
            return true;
        }
    }

    public boolean validatePassword()
    {
        String pwd = edtPwd.getText().toString().trim();
        if(pwd.isEmpty())
        {
            edtPwd.setError("Can't Be Empty!");
            return false;
        }
        else
        {
            edtPwd.setError(null);
            return true;
        }

    }
    public void openDashBoard(View view)
    {
        progressDialog.setMessage("wait...");
        progressDialog.setCancelable(false);

        progressDialog.show();
        if(!validateEmail() | !validatePassword())
        {
            progressDialog.cancel();
            return;
        }

        String strEmail = edtEmail.getText().toString();
        String strPwd = edtPwd.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(strEmail,strPwd).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult)
            {
                progressDialog.cancel();

                sharedPreferenceManager = new SharedPreferenceManager(LoginActivity.this);
                SharedPreferences.Editor editor = sharedPreferenceManager.sharedPreferencesData.edit();
                editor.putBoolean("IS_LOGIN",true);
                editor.putBoolean("IS_SIGNUP",true);
                        // why => if user changes device , so in another device does not need
                        //        to signup again. so if user exist ,he exist in DB , means he only
                        //        needs to login.
                editor.putString("USER_ID",firebaseAuth.getCurrentUser().getUid());
                editor.commit();

                Intent intent = new Intent(LoginActivity.this,DashBoardActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                if(e instanceof FirebaseNetworkException)
                {
                    Toast.makeText(LoginActivity.this, "check your network connection...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(e instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        Toast.makeText(LoginActivity.this, "Invalid password...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Invalid user , please signup first...", Toast.LENGTH_SHORT).show();
                    }
                }

                progressDialog.cancel();
            }
        });
    }
}