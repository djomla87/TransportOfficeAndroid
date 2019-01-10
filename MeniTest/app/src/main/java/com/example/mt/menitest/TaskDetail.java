package com.example.mt.menitest;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskDetail extends AppCompatActivity implements LoadJsonObject.Listener, LocationListener {

    private Task task;
    private String Status;
    private int PodStatus;
    private LocationManager locationManager;
    private TextView TVlokacija;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        task = (Task)getIntent().getSerializableExtra("task");
        Status = getIntent().getExtras().getString("status");
        PodStatus = getIntent().getExtras().getInt("podStatus");

        String StatusOpis = Status;

        if(PodStatus == 1)
            StatusOpis = StatusOpis + " (U Skladište)";
        if(PodStatus == 7)
            StatusOpis = StatusOpis + " (U Brzu Poštu)";


        TextView text = (TextView)findViewById(R.id.TaskPromjenaStatusa);
        text.setText("Promjeni status prevoza " + "\nSerijski Broj: " + task.getSerijskiBroj() + "\nRelacija: " + task.getUtovar() + "\n" + task.getIstovar() + "\n u " + StatusOpis + " ?");


        ActivityCompat.requestPermissions(TaskDetail.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        TVlokacija = (TextView) findViewById(R.id.textView4);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS nije aktivan");
            builder.setMessage("Molimo vas da omogućite Lokaciju / GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        Location location2 = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);


    }


    public void bntZavrsi(View view)
    {

        Location loc = VratiLokaciju();
        String LAT = "";
        String LONG = "";

        if (loc != null) {
            LAT = loc.getLatitude() + "";
            LONG = loc.getLongitude() + "";
        }

        SharedPreferences preferences =  this.getSharedPreferences("GMTEL", Context.MODE_PRIVATE);
        String token = preferences.getString("Token", "");

        CheckBox cb = (CheckBox)findViewById(R.id.sendEmailGmtel);
        int mail = cb.isChecked() ? 1 : 0;

        CheckBox cb2 = (CheckBox)findViewById(R.id.sendEmailNarucioc);
        int mail2 = cb.isChecked() ? 1 : 0;

        new LoadJsonObject(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "DnevnikPrevoza/GetUpdateStatus?id="+ task.getIdTask() + "&status="+ Status + "&PodStatus="+ PodStatus +"&token="+token+"&mail="+mail+"&mail2="+mail2+"&LONG="+LONG+"&LAT="+LAT);
    }

     @Override
     public void onLoaded(JSONObject obj) {

         String response = null;
         String message = null;

         try {
             response = obj.getString("response");
             message = obj.getString("message");
         } catch (JSONException e) {
             e.printStackTrace();
         }
         if (response.equals("OK")) {
             Toast.makeText(this, "Status uspješno ažuriran", Toast.LENGTH_SHORT).show();
             Intent i = new Intent(this, TaskPrevozi.class);
             this.startActivity(i);
         }
         else {
             Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
         }


     }

     @Override
     public void onError() {

         Toast.makeText(this, "Greska", Toast.LENGTH_SHORT).show();
     }


     public Location VratiLokaciju()
     {

         if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                 !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
             // Build the alert dialog
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
             builder.setTitle("GPS nije aktivan");
             builder.setMessage("Molimo vas da omogućite Lokaciju / GPS");
             builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialogInterface, int i) {
                     // Show location settings when the user acknowledges the alert dialog
                     Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                     startActivity(intent);
                 }
             });
             Dialog alertDialog = builder.create();
             alertDialog.setCanceledOnTouchOutside(false);
             alertDialog.show();
         }

         if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                 != PackageManager.PERMISSION_GRANTED
                 && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                 != PackageManager.PERMISSION_GRANTED) {
             return null;
         }

         Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
         Location location2 = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);


         if (location2 != null)
             return location2;
         else
             return location;

     }


     @Override
     public void onLocationChanged(Location location) {
         if (location == null)
             TVlokacija.setText("NA");
         else
        TVlokacija.setText(location.getLatitude() + "\n " + location.getLongitude());

     }

     @Override
     public void onStatusChanged(String provider, int status, Bundle extras) {

     }

     @Override
     public void onProviderEnabled(String provider) {

     }

     @Override
     public void onProviderDisabled(String provider) {

     }
 }
