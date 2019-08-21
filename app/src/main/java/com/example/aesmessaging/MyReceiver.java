package com.example.aesmessaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MyReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsBroadcastReceiver";
    String msg, phoneNo = "";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction() == SMS_RECEIVED) {
            //retrieves a map of extended data from the intent
            Bundle dataBundle = intent.getExtras();
            if (dataBundle != null) {
                //creating PDU(Protocol Data Unit) object which is a protocol for transferring message
                Object[] mypdu = (Object[]) dataBundle.get("pdus");
                final SmsMessage[] message = new SmsMessage[mypdu.length];

                for (int i = 0; i < mypdu.length; i++) {
                    //for build versions >= API Level 23
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = dataBundle.getString("format");
                        //From PDU we get all object and SmsMessage Object using following line of code
                        message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i], format);
                    } else {
                        //<API level 23
                        message[i] = SmsMessage.createFromPdu((byte[]) mypdu[i]);
                    }
                    msg = message[i].getMessageBody();
                    phoneNo = message[i].getOriginatingAddress();
                }
              //  Toast.makeText(context, "Message: " + msg + "\nNumber: " + phoneNo, Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();

            String s = msg;
            String userid = Character.toString(s.charAt(0)) + Character.toString(s.charAt(1)) + Character.toString(s.charAt(2)) +
                    Character.toString(s.charAt(3)) + Character.toString(s.charAt(4));
            String ss = Character.toString(s.charAt(5));
            String ss1 = Character.toString(s.charAt(6));
            String ss2 = Character.toString(s.charAt(7));

            if ((ss + ss1 + ss2).equals("AES")) {

                StringBuilder stringBuilder = new StringBuilder(s);
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(0);
                stringBuilder.deleteCharAt(0);
                Message messageinfo = new Message(userid, phoneNo, stringBuilder.toString());
                FirebaseDatabase.getInstance().getReference().child("Inbox").child(FirebaseAuth.getInstance().getUid())
                        .child(userid)
                        .setValue(messageinfo)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });



               /* Intent intent1 = new Intent(context , MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("Phone Number" , msgs[i].getOriginatingAddress());
                intent1.putExtra("Message" , msgs[i].getMessageBody());
                context.startActivity(intent1);*/

            }
        }
    }

}
