package com.example.sendsms;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ReadSMS extends AppCompatActivity {

    ListView lViewSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_sms);

        lViewSMS = findViewById(R.id.listViewSMS);

        if(fetchInbox()!=null) {

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, fetchInbox());
            lViewSMS.setAdapter(adapter);
        }
    }

    public ArrayList fetchInbox() {
        ArrayList sms = new ArrayList();
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(uriSms, new String[]{"_id", "address", "date", "body"},null,null,null);
        cursor.moveToFirst();
        while  (cursor.moveToNext()) {

            String address = cursor.getString(1);
            String body = cursor.getString(3);
            System.out.println("======&gt; Mobile number =&gt; "+address);
            System.out.println("=====&gt; SMS Text =&gt; "+body);
            sms.add("Address=&gt; "+address+"n SMS =&gt; "+body);
        }
        return sms;

    }
}