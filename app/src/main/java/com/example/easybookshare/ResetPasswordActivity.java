package com.example.easybookshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText edt_email;
    Button btn_email;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        edt_email = findViewById(R.id.edt_email);
        btn_email = findViewById(R.id.btn_email);
        firebaseAuth = FirebaseAuth.getInstance();

        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String checkEmail="[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";
                String mail = edt_email.getText().toString().trim();
                if(mail.isEmpty()) {
                    edt_email.setError("Please enter email first...");
                }
                else if(!mail.matches(checkEmail)){
                    edt_email.setError("Please enter valid email...");
                }
                else {
                    edt_email.setError(null);
                    resetPassword();
                }
            }
        });
    }
    public void redirectLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void resetPassword() {
        String email = edt_email.getText().toString();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "We have sent you an email to reset your password!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}