package com.example.aesmessaging;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Home extends AppCompatActivity {

    ListView listView;
    List<Message> messageList = new ArrayList<>();
    FloatingActionButton compose;
    String AES = "AES";

    String id, pin;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        listView = (ListView) findViewById(R.id.listview);
        getSupportActionBar().setTitle("INBOX");
        checkForSmsPermission();
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("pin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    pin = dataSnapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {

                    id = dataSnapshot.getValue(String.class);
                    getSupportActionBar().setSubtitle("Your ID : " + dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.composemessage, null);
        builder1.setView(view1);
        final EditText number = (EditText) view1.findViewById(R.id.number);
        final EditText message = (EditText) view1.findViewById(R.id.message);
        final Button sendsms = (Button) view1.findViewById(R.id.sendsms);
        final AlertDialog alertDialog = builder1.create();
        alertDialog.setCancelable(true);

        compose = (FloatingActionButton) findViewById(R.id.compose);
        compose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
                sendsms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Set the destination phone number to the string in editText.
                        String destinationAddress = number.getText().toString();
                        // Find the sms_message view.
                        // Get the text of the sms message.
                        String smsMessage = null;
                        try {
                            String encrtedmessage = encrytText(message.getText().toString(), pin);
                            smsMessage = id + "AES" + encrtedmessage;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Set the service center address if needed, otherwise null.
                        String scAddress = null;
                        // Set pending intents to broadcast
                        // when message sent and when delivered, or set to null.
                        PendingIntent sentIntent = null, deliveryIntent = null;
                        // Check for permission first.
                        checkForSmsPermission();
                        // Use SmsManager.
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(destinationAddress, scAddress, smsMessage,
                                sentIntent, deliveryIntent);
                        Toast.makeText(Home.this, "Sent!", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });
            }
        });

        addEventFirebaselistener();

    }


    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, getString(R.string.permission_not_granted));
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted. Enable sms button.
                } else {
                    // Permission denied.
                    Log.d(TAG, getString(R.string.failure_permission));
                    Toast.makeText(this, getString(R.string.failure_permission),
                            Toast.LENGTH_LONG).show();
                    // Disable the sms button.
                }
            }
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS: {
                //check whether the length of grantResults is greater than 0 and is equal to PERMISSION_GRANTED
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Now broadcastreceiver will work in background
                    Toast.makeText(this, "Thankyou for permitting!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Well I can't do anything until you permit me", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
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

    private String encrytText(String texttoencryt, String passwordofencrytText) throws Exception {

        SecretKeySpec key = generateKey(passwordofencrytText);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(texttoencryt.getBytes());
        String encrytvalue = Base64.encodeToString(encVal, Base64.DEFAULT);
        return encrytvalue;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.setpin) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view = inflater.inflate(R.layout.pindialog, null);
            builder.setView(view);
            final EditText pin1 = (EditText) view.findViewById(R.id.pin);
            if (pin != null) {
                pin1.setHint("Your Current PIN : " + pin);
            }
            Button setpin = (Button) view.findViewById(R.id.updatepin);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(true);
            alertDialog.show();
            setpin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(FirebaseAuth.getInstance().getUid())
                            .child("pin").setValue(pin1.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Home.this, "Done!", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    });
                }
            });
        } else if (item.getItemId() == R.id.Fav) {

            Intent intent = new Intent(Home.this, Favorites.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void addEventFirebaselistener() {

        FirebaseDatabase.getInstance().getReference().child("Inbox").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (messageList.size() > 0)
                    messageList.clear();
                if (dataSnapshot != null) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Message order = dataSnapshot1.getValue(Message.class);
                        messageList.add(order);
                    }
                    ListAdapter adapter = new ListAdapter(Home.this, messageList);

                    TextView t = (TextView) findViewById(R.id.t);

                    listView.setEmptyView(t);

                    listView.setAdapter(adapter);

                    // showt();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
