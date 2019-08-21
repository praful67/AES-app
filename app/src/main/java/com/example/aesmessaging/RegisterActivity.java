package com.example.aesmessaging;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    AutoCompleteTextView emailreg, regpasswrd;
    Button registerbtn;
    TextView loginpage;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        loginpage = (TextView) findViewById(R.id.loginpage);
        loginpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        emailreg = (AutoCompleteTextView) findViewById(R.id.register_email);
        regpasswrd = (AutoCompleteTextView) findViewById(R.id.register_password);
        registerbtn = (Button) findViewById(R.id.register_button);
        mAuth = FirebaseAuth.getInstance();
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(emailreg.getText()) && !TextUtils.isEmpty(regpasswrd.getText())) {
                    String email = emailreg.getText().toString();
                    String password = regpasswrd.getText().toString();
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Please wait...");
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnFailureListener(RegisterActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("Users")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("id")
                                                .setValue(getSaltString());
                                        Toast.makeText(RegisterActivity.this, "Signed Up !", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, Home.class);
                                        startActivity(intent);
                                        finish();

                                    }

                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "FILL ALL", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 5) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

}
