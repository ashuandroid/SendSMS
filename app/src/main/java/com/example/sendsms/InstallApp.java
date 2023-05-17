package com.example.sendsms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class InstallApp extends AppCompatActivity {

    ListView listView;
    ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_app);

        listView = findViewById(R.id.listView);
        List<ApplicationInfo> installedApps = getInstalledApps();
        arrayAdapter = new ArrayAdapter(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, installedApps);
        listView.setAdapter(arrayAdapter);

    }


    private List<ApplicationInfo> getInstalledApps() {
        List<ApplicationInfo> installedApps = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        List<ApplicationInfo> allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : allApps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                installedApps.add(appInfo);
            }
        }

        return installedApps;
    }
}
