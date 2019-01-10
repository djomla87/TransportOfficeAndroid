package com.example.mt.menitest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

public class TroskoviRazmjenaAcitivty extends AppCompatActivity implements LoadJsonObject.Listener {


    Troskovi objTrosak;

    public Spinner ValutaSppiner1;
    public Spinner ValutaSppiner2;
    public ArrayAdapter<CharSequence> adapter;
    ArrayList<String> spinnerArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troskovi_razmjena_acitivty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        objTrosak = (Troskovi) getIntent().getSerializableExtra("ObjTrosak");

        spinnerArray.add("");
        spinnerArray.add("BAM");
        spinnerArray.add("EUR");
        spinnerArray.add("USD");
        spinnerArray.add("RSD");

        ValutaSppiner1 = (Spinner) findViewById(R.id.ValutaSpinner1);
        ValutaSppiner2 = (Spinner) findViewById(R.id.ValutaSpinner2);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ValutaSppiner1.setAdapter(spinnerArrayAdapter);
        ValutaSppiner2.setAdapter(spinnerArrayAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void onSacuvaj(View view) {

        String valuta1 = ValutaSppiner1.getSelectedItem().toString();
        String valuta2 = ValutaSppiner2.getSelectedItem().toString();
        TextView et = (TextView)findViewById(R.id.datumIvrijeme);

        EditText iznos1 = (EditText)findViewById(R.id.Iznos1);
        EditText iznos2 = (EditText)findViewById(R.id.Iznos2);


        boolean validacija = true;

        if (objTrosak.getIznos().endsWith(valuta1) && !valuta1.equals("")) {
            Snackbar.make(view, "Valuta koja je razmjenjena mora biti različita od mjenjane valute", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            validacija = false;
        }

        else if(iznos1.getText().toString().equals(""))
        {
            Snackbar.make(view, "Morate unijeti iznos nakon razmjene", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            validacija = false;
        }

        else if(et.getText().toString().equals("")) {
            Snackbar.make(view, "Morate odabrati datum razmjene", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            validacija = false;
        }
        else if(!iznos2.getText().toString().equals(""))
        {
            if(valuta2.equals("")) {
                Snackbar.make(view, "Niste odabrali valutu za unešeni iznos", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                validacija = false;
            }
        }
        else if(!iznos1.getText().toString().equals(""))
        {
            if(valuta1.equals("")) {
                Snackbar.make(view, "Niste odabrali valutu za unešeni iznos", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                validacija = false;
            }
        }

        if (iznos2.getText().toString().equals(""))
            iznos2.setText("0");

        if (validacija) {
            Button btn = (Button)findViewById(R.id.button3);
            btn.setEnabled(false);


            try {
                String query1 = URLEncoder.encode(et.getText().toString(), "utf-8");

                if (!isOnline() )
                    throw new Exception("Internet nije dostupan");


            new LoadJsonObject(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "DnevnikPrevoza/RazmjeniNovac?id=" + objTrosak.getId() +
                    "&iznos1=" + iznos1.getText() + "&iznos2=" + iznos2.getText() + "&valuta1=" + valuta1 + "&valuta2=" + valuta2 + "&date=" + query1);

            } catch (Exception e) {

                View parentLayout = findViewById(android.R.id.content);
                Snackbar.make(parentLayout, "Greška u snimanju razmjene: " + e.getMessage(), Snackbar.LENGTH_INDEFINITE)
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
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

        Toast.makeText(this, "Došlo je do greške! Provjerite internet konekciju!", Toast.LENGTH_SHORT).show();

        Button btn = (Button)findViewById(R.id.button3);
        btn.setEnabled(true);

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
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

            DialogFragment newFragment = new TimePickerFragment();
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

            TextView et = (TextView)getActivity().findViewById(R.id.datumIvrijeme);

            String hour = "0"+hourOfDay;
            hour = hour.substring(hour.length()-2);


            String min = "0"+minute;
            min = min.substring(min.length()-2);

            et.setText(et.getText() + " "+  hour+":"+min);
        }
    }

}
