package com.example.aesmessaging;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Favorites extends AppCompatActivity {

    ListView listView;
    List<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        listView = (ListView) findViewById(R.id.listview);
        getSupportActionBar().setTitle("FAVORITES");
        FirebaseDatabase.getInstance().getReference().child("Fav").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (messageList.size() > 0)
                    messageList.clear();
                if (dataSnapshot != null) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Message order = dataSnapshot1.getValue(Message.class);
                        messageList.add(order);
                    }
                    ListAdapter adapter = new ListAdapter(Favorites.this, messageList);

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
