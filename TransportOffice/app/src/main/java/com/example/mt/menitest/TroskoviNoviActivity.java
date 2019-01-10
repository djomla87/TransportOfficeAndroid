package com.example.mt.menitest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.mt.menitest.R.id.Status;

public class TroskoviNoviActivity extends AppCompatActivity implements LoadJSONTask.Listener, LoadJsonObject.Listener {

    Troskovi objTrosak;

    public Spinner vrsteTroskova;
    public Spinner vrsteValuta;

    public ArrayAdapter<CharSequence> adapter;
    public ArrayAdapter<CharSequence> adapter1;

    ArrayList<String> spinnerArray = new ArrayList<String>();
    ArrayList<String> spinnerArray1 = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troskovi_novi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            objTrosak = (Troskovi) getIntent().getSerializableExtra("ObjTrosak");
        }
        catch (Exception ex){}

        vrsteTroskova = (Spinner) findViewById(R.id.VrstaTroskaSppiner);
        vrsteValuta = (Spinner) findViewById(R.id.ValutaSpinner);

        spinnerArray1.add("");
        spinnerArray1.add("BAM");
        spinnerArray1.add("EUR");
        spinnerArray1.add("USD");
        spinnerArray1.add("RSD");

        ArrayAdapter<String> spinnerArrayAdapter1 = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        spinnerArray1); //selected item will look like a spinner set from XML
        spinnerArrayAdapter1.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        vrsteValuta.setAdapter(spinnerArrayAdapter1);


        if (objTrosak != null)
        {
            RadioButton rb1 = (RadioButton)findViewById(R.id.radioRashod);
            RadioButton rb2 = (RadioButton)findViewById(R.id.radioZaduzenje);
            EditText iznosText = (EditText)findViewById(R.id.IznosDecimal);
            CheckBox cbKartica = (CheckBox)findViewById(R.id.Kartica);

            String [] niz = objTrosak.getIznos().split(" ");

            Spinner sp = (Spinner)findViewById(R.id.ValutaSpinner);
            Spinner sp1 = (Spinner)findViewById(R.id.VrstaTroskaSppiner);

            if( objTrosak.getKartica() == 1 )
                cbKartica.setChecked(true);
            else
                cbKartica.setChecked(false);

            if(objTrosak.getTip().equals("RASHOD"))
            {
                rb1.setChecked(true);
                rb2.setChecked(false);
            }
            else
            {
                rb2.setChecked(true);
                rb1.setChecked(false);
            }

            iznosText.setText(niz[0].replace(",","."));
            sp.setSelection(spinnerArray1.indexOf(niz[1]));



        }

        try {
            if (!isOnline())
                throw new Exception("Internet nije dostupan");

            new LoadJSONTask(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "DnevnikPrevoza/ListaVrsteTroskova");
        }
        catch (Exception e){
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Greška u učitavanju: " + e.getMessage(), Snackbar.LENGTH_INDEFINITE)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();

        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public void onLoaded(JSONArray arr) {

        spinnerArray.add("");
        for (int i =0; i<arr.length(); i++)
        {
            try {
                int     Id       = arr.getJSONObject(i).getInt("IdVrstaTroska");
                String Naziv     = arr.getJSONObject(i).getString("Naziv");

                spinnerArray.add(Naziv);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        spinnerArray); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        vrsteTroskova.setAdapter(spinnerArrayAdapter);

        if (objTrosak!=null)
            vrsteTroskova.setSelection(spinnerArray.indexOf(objTrosak.getVrsta()));

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
            Toast.makeText(this, "Podaci uspješno sačuvani", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, TroskoviActivity.class);
            this.startActivity(i);
        }
        else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError() {

    }

    public void OnButtonClick(View view)
    {
        SharedPreferences preferences =  this.getSharedPreferences("GMTEL", Context.MODE_PRIVATE);
        String token = preferences.getString("Token", "");
        RadioButton rb1 = (RadioButton)findViewById(R.id.radioRashod);
        RadioButton rb2 = (RadioButton)findViewById(R.id.radioZaduzenje);
        EditText iznosText = (EditText)findViewById(R.id.IznosDecimal);
        Spinner sp = (Spinner)findViewById(R.id.ValutaSpinner);
        Spinner sp1 = (Spinner)findViewById(R.id.VrstaTroskaSppiner);
        CheckBox cbKartica = (CheckBox)findViewById(R.id.Kartica);

        TextView et = (TextView)findViewById(R.id.datumIvrijeme);

        String tip = rb1.isChecked() ? "RASHOD" : "ZADUŽENJE";
        String iznos = iznosText.getText().toString();
        String valuta = sp.getSelectedItem().toString();
        String vrstatroska = sp1.getSelectedItem().toString();
        String Kartica = cbKartica.isChecked() ? "1" : "0";

        int IdTrosak = objTrosak == null ? 0 : objTrosak.getId();


        boolean validacija = true;

        if (iznos.equals("") || valuta.equals("")) {
            Snackbar.make(view, "Iznos i valuta moraju biti popunjeni", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            validacija = false;
        }
        else if (tip.equals("RASHOD") && vrstatroska.equals("")) {
            Snackbar.make(view, "Pri unosu rashoda morate odabrati i vrstu rashoda", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            validacija = false;
        }
        else if (et.getText().equals(""))
        {
            Snackbar.make(view, "Niste odabrali datum!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            validacija = false;
        }

        if(validacija) {

            try {

                String query1 = URLEncoder.encode(vrstatroska, "utf-8");
                String query2 = URLEncoder.encode(et.getText().toString(), "utf-8");


                if (!isOnline())
                    throw new Exception("Internet nije dostupan");

            new LoadJsonObject(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "DnevnikPrevoza/NapraviVozacevTrosak?token=" + token + "&IdTrosak=" + IdTrosak + "&iznos=" + iznos + "&valuta=" +
                    valuta + "&tip=" + tip + "&vrstatroska=" + query1 + "&napomena=&date=" + query2 + "&Kartica=" + Kartica);

            } catch (Exception e) {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Greška u snimanju troška: " + e.getMessage(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("CLOSE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                        .show();
            }
            }

    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new TroskoviRazmjenaAcitivty.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }


        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            TextView et = (TextView)getActivity().findViewById(R.id.datumIvrijeme);
            et.setText(dayOfMonth + "." + (month + 1) + "." + year);

            DialogFragment newFragment = new TroskoviRazmjenaAcitivty.TimePickerFragment();
            newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            String hour = "0"+hourOfDay;
            hour = hour.substring(hour.length()-2);

            String min = "0"+minute;
            min = min.substring(min.length()-2);

            TextView et = (TextView)getActivity().findViewById(R.id.datumIvrijeme);
            et.setText(et.getText() + " "+  hour+":"+min);
        }
    }

}
