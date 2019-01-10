package com.example.mt.menitest;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoadJSONTask.Listener {


    private List<Task> mTaskMapList = new ArrayList<>();
    private NotificationCompat.Builder notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        SharedPreferences preferences = getSharedPreferences("GMTEL", Context.MODE_PRIVATE);
        if (preferences.getString("Korisnik", "").equals("")) {
            Intent intent = new Intent(this, Logovanje.class);
            startActivity(intent);
        } else {
            NavigationView n = (NavigationView) findViewById(R.id.nav_view);
            n.getMenu().findItem(R.id.nav_user).setTitle(preferences.getString("Korisnik", ""));
        }


        try {
            Task task = (Task) getIntent().getSerializableExtra("task");

            if (task != null) {
                Intent intent = new Intent(this, TaskPrevozi.class);
                startActivity(intent);
            }
        } catch (Exception e) {
        }


        new CheckInternetConn().execute();



        String token = preferences.getString("Token", "");

        if (!isOnline())
        {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Internet nije dostupan !!!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
        }
        else {
            //  new LoadJSONTask(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "DnevnikPrevoza/NoviTaskovi?token="+ token);
            int radi = BackgroundService.getStarted();

            if (radi != 1)
                startService(new Intent(this, BackgroundService.class));


            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
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
        }

    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void onLoaded(JSONArray arr){

        mTaskMapList.clear();

        for (int i =0; i<arr.length(); i++)
        {
            try {
                int     IdTask          = arr.getJSONObject(i).getInt("IdTask");
                String SerijskiBroj     = arr.getJSONObject(i).getString("SerijskiBroj");
                String Vozilo           = arr.getJSONObject(i).getString("Vozilo");
                String Istovar          = arr.getJSONObject(i).getString("Istovar");
                String Roba             = arr.getJSONObject(i).getString("Roba");
                String Status           = arr.getJSONObject(i).getString("Status");
                String DatumAzuriranja  = arr.getJSONObject(i).getString("DatumAzuriranja");
                String Utovar           = arr.getJSONObject(i).getString("Utovar");
                String UvoznaSpedicija = arr.getJSONObject(i).getString("UvoznaSpedicija");
                String IzvoznaSpedicija = arr.getJSONObject(i).getString("IzvoznaSpedicija");
                String Uvoznik = arr.getJSONObject(i).getString("Uvoznik");
                String Izvoznik = arr.getJSONObject(i).getString("Izvoznik");
                String Napomena = arr.getJSONObject(i).getString("Napomena");
                String RefBroj = arr.getJSONObject(i).getString("RefBroj");
                String Pregledano = arr.getJSONObject(i).getString("Pregledano");

                mTaskMapList.add(new Task(IdTask, SerijskiBroj, Vozilo, Istovar, Roba, Status, DatumAzuriranja, Utovar, UvoznaSpedicija, IzvoznaSpedicija, Uvoznik, Izvoznik, Napomena, RefBroj, Pregledano));



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (mTaskMapList.size() > 0)
        {
            Task findTask = mTaskMapList.get(0);

            Intent detalji = new Intent(this, TaskDetalji.class);
            detalji.putExtra("task", findTask);
            this.startActivity(detalji);
        }

    }

    @Override
    public void onError(){}


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {

            SharedPreferences preferences = getSharedPreferences("GMTEL", Context.MODE_PRIVATE);

            preferences.edit().remove("Korisnik").commit();
            preferences.edit().remove("Token").commit();

            Intent intent = new Intent(this, MainActivity.class);
            stopService(new Intent(this, BackgroundService.class));
            startActivity(intent);

        } else if (id == R.id.nav_exit) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

        if (!isOnline())
        {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Internet nije dostupan !!!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))

                    .show();
        }
        else {

            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_task) {
                Intent intent = new Intent(this, TaskPrevozi.class);
                startActivity(intent);

            } else if (id == R.id.nav_user) {
            } else if (id == R.id.nav_pretraga) {

                Intent intent = new Intent(this, Search.class);
                startActivity(intent);

            } else if (id == R.id.nav_barcode) {

                try {

                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

                    startActivityForResult(intent, 0);

                } catch (Exception e) {

                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);

                }

            } else if (id == R.id.nav_logout) {

                SharedPreferences preferences = getSharedPreferences("GMTEL", Context.MODE_PRIVATE);

                preferences.edit().remove("Korisnik").commit();
                preferences.edit().remove("Token").commit();

                Intent intent = new Intent(this, MainActivity.class);
                stopService(new Intent(this, BackgroundService.class));
                startActivity(intent);

            } else if (id == R.id.nav_exit) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            } else if (id == R.id.nav_trosak) {
                Intent intent = new Intent(this, TroskoviActivity.class);
                startActivity(intent);
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                contents = contents.replace("www.gmtel-office.com/DnevnikPrevoza/GuestDetail?s=","");
                //Toast.makeText(this, contents , Toast.LENGTH_SHORT).show();

                new LoadJSONTask(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "DnevnikPrevoza/FindByGuestCode?code="+ contents);

            }
            if(resultCode == RESULT_CANCELED){
               // Toast.makeText(this, "greska" , Toast.LENGTH_SHORT).show();
            }
        }
    }



    class DbUserData extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {

            String url = params[0];
            UrlJson a = new UrlJson();
            a.SetUrl(url);   //"http://gmtel-office.com/api/Login?username=mario.kuzmanovic&password=admin"
            JSONObject obj = a.GetJson();

            return obj;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            NavigationView n = (NavigationView) findViewById(R.id.nav_view);
            try {
                n.getMenu().findItem(R.id.nav_user).setTitle(result.getString("Korisnik"));

                SharedPreferences preferences = getSharedPreferences("GMTEL", Context.MODE_PRIVATE);
                preferences.edit().putString("Korisnik",result.getString("Korisnik")).commit();
                preferences.edit().putString("Token",result.getString("Code")).commit();

            } catch (JSONException e) {
                n.getMenu().findItem(R.id.nav_user).setTitle("");
            }

        }
    }


    private class CheckInternetConn extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //do stuff and return the value you want
            try {
                //make a URL to a known source
                URL url = new URL("http://www.google.com");

                //open a connection to that source
                HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

                //trying to retrieve data from the source. If there
                //is no connection, this line will fail
                Object objData = urlConnect.getContent();

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            // Call activity method with results
            ProvjeriInternet(result);
        }
    }

    private void ProvjeriInternet(Boolean result) {

        if(!result) {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Internet nije dostupan !!!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();
        }
    }
}



