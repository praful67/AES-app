package com.example.aesmessaging;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;

import java.util.List;

public class ListAdapter extends BaseAdapter {

    Activity activity;
    List<Message> messages;

    public ListAdapter(Activity activity, List<Message> messages) {
        this.activity = activity;
        this.messages = messages;
    }

    LayoutInflater inflater;

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int i, View itemView, ViewGroup viewGroup) {

        inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = inflater.inflate(R.layout.messageitem, null);

        FirebaseApp.initializeApp(activity.getBaseContext());
        final TextView id = (TextView) itemView.findViewById(R.id.id);


        final TextView message = (TextView) itemView.findViewById(R.id.message);
        final TextView numebr = (TextView) itemView.findViewById(R.id.number);
        LinearLayout linearLayout = (LinearLayout) itemView.findViewById(R.id.layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MessageDecrytActivity.class);
                intent.putExtra("id", messages.get(i).getId());
                intent.putExtra("number", messages.get(i).getNumber());
                intent.putExtra("message", messages.get(i).getMessage());
                activity.startActivity(intent);
            }
        });
        id.setText("ID : " + messages.get(i).getId());
        numebr.setText("Number : " + messages.get(i).getNumber());
        message.setText("Message : " + messages.get(i).getMessage());

        return itemView;
    }
}
