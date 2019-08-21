package com.example.aesmessaging;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MessageDecrytActivity extends AppCompatActivity {

    TextView id, number, message, decrytedmessage;
    Button favoriteit, decrypt;
    String AES = "AES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_decryt);
        id = (TextView) findViewById(R.id.id);
        number = (TextView) findViewById(R.id.number);
        message = (TextView) findViewById(R.id.message);
        decrytedmessage = (TextView) findViewById(R.id.decryptedmessage);
        decrypt = (Button) findViewById(R.id.decryptmessage);
        favoriteit = (Button) findViewById(R.id.fav);
        if (getIntent().getExtras() != null) {
            id.setText("ID : " + getIntent().getStringExtra("id"));
            number.setText("Number : " + getIntent().getStringExtra("number"));
            message.setText("Message : " + getIntent().getStringExtra("message"));
        }
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);
        favoriteit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                progressDialog.setMessage("Please wait...");
                Message messageinfo = new Message(getIntent().getStringExtra("id"), getIntent().getStringExtra("number"), getIntent().getStringExtra("message"));
                FirebaseDatabase.getInstance().getReference().child("Fav").child(FirebaseAuth.getInstance().getUid())
                        .child(getIntent().getStringExtra("id"))
                        .setValue(messageinfo)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(MessageDecrytActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                Toast.makeText(MessageDecrytActivity.this, "Added", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.typepindialog, null);
        builder.setView(view);
        final EditText pin = (EditText) view.findViewById(R.id.pin);
        Button submit = (Button) view.findViewById(R.id.submit);
        final AlertDialog alertDialog = builder.create();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(pin.getText())) {
                    try {
                        String s = decrytText(getIntent().getStringExtra("message"), pin.getText().toString());
                        decrytedmessage.setText("Decrypted Message : '" + s + "'.");
                        alertDialog.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(MessageDecrytActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.show();
                alertDialog.setCancelable(true);
            }
        });

    }

    private SecretKeySpec generateKey(String passwordofencrytText0) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = passwordofencrytText0.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }

    private String decrytText(String stringtobedecryted, String passwordofencrytedText) throws Exception {

        SecretKeySpec key = generateKey(passwordofencrytedText);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodevalue = Base64.decode(stringtobedecryted, Base64.DEFAULT);
        byte[] decvalue = c.doFinal(decodevalue);
        String decrytedValue = new String(decvalue);
        return decrytedValue;
    }

}
