package com.example.sendsms;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SEND_SMS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TODO = "Permission Check";
    Button readSMS, callIntent;
    Button allAppList, getLocation;
    Button sendSMS;
    private ArrayList permissionsToRequest;
    private final ArrayList permissionsRejected = new ArrayList();
    private final ArrayList permissions = new ArrayList();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    TextView tvLocation;
    TextView tvDeviceID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readSMS = findViewById(R.id.btnReadSms);
        callIntent = findViewById(R.id.btnCall);
        allAppList = findViewById(R.id.btnInstalledApp);
        sendSMS = findViewById(R.id.btnSendSMS);
        getLocation = findViewById(R.id.btnLocation);
        tvLocation = findViewById(R.id.textview1);

        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        tvDeviceID = findViewById(R.id.textDeviceId);
        tvDeviceID.setText("Device ID- " + android_id);

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(READ_SMS);
        permissions.add(CALL_PHONE);
        permissions.add(SEND_SMS);
        permissions.add(READ_PHONE_STATE);


        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        readSMS.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ReadSMS.class)));


        callIntent.setOnClickListener(v -> {

            Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:8527833551"));
            try {
                startActivity(in);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this, "Could not find an activity to place the call.", Toast.LENGTH_SHORT).show();
            }

        });


        allAppList.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, InstallApp.class)));


        getLocation.setOnClickListener(v -> {

            locationTrack = new LocationTrack(MainActivity.this);

            if (locationTrack.canGetLocation()) {

                double longitude = locationTrack.getLongitude();
                double latitude = locationTrack.getLatitude();

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                //String city = addresses.get(0).getLocality();
                //String state = addresses.get(0).getAdminArea();
                //String country = addresses.get(0).getCountryName();
                //String postalCode = addresses.get(0).getPostalCode();
                //String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                tvLocation.setText("Longitude: " + longitude + "\nLatitude: " + latitude + "\n\nAddress: " + address);


                //Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();

            } else {

                locationTrack.showSettingsAlert();
            }

        });

        sendSMS.setOnClickListener(v -> {
            try {
                SmsManager sms = SmsManager.getDefault();
                String message = "Hello";
                String userInput = "9873720753,9069593916";
                String numbers[] = userInput.split(", *");
                for (String number : numbers) {
                    sms.sendTextMessage(number, null, message, null, null);
                }

                Toast.makeText(MainActivity.this, "Message Send", Toast.LENGTH_LONG).show();

            } catch (Exception e) {

                Toast.makeText(MainActivity.this, "SMS sending failed!", Toast.LENGTH_SHORT).show();
            }
        });


        String phoneNumber = getPhoneNumber();
        Toast.makeText(this, "Phone number: " + phoneNumber, Toast.LENGTH_SHORT).show();


    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();
        for (Object perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(Object permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission((String) permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    (dialog, which) -> {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;

        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }


    private String getPhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO:Consider calling
                //  ActivityCompat#requestPermissions
                //  here to request the missing permissions, and then overriding
                //  public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //  int[] grantResults)
                //  to handle the case where the user grants the permission. See the documentation
                //  for ActivityCompat#requestPermissions for more details.
                return TODO;
            }
            return telephonyManager.getLine1Number();
        }
        return null;
    }
}


