package com.example.mt.menitest;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search extends AppCompatActivity implements LoadJSONTask.Listener, LoadJsonObject.Listener, AdapterView.OnItemClickListener {


    private ListView searchDnevnik;
    private Search.customAdapter adapter;
    private List<Task> AllTask = new ArrayList<Task>();
    private List<Task> SearchList = new ArrayList<Task>();
    private int IdVozac = 0;
    private Map<Integer, String> vozila = new HashMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        searchDnevnik = (ListView) findViewById(R.id.search_dnevnik);
        searchDnevnik.setOnItemClickListener(this);

        SharedPreferences preferences =  this.getSharedPreferences("GMTEL", Context.MODE_PRIVATE);
        String token = preferences.getString("Token", "");
        IdVozac = preferences.getInt("IdVozac", 0);

        new LoadJSONTask(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "Vozilo/VratiAktivnaVozilaOsim?IdVozilo=0");
        new LoadJSONTask(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "DnevnikPrevoza/SearchDnevnik?token="+token);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchtask_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_dnevnik_menu);
        SearchView searchView = (SearchView)menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchList.clear();
                for (Task s : AllTask) {
                    if (s.getSerijskiBroj().toLowerCase().contains(query.toLowerCase())) {
                        SearchList.add(s);
                    }
                }

                adapter = new Search.customAdapter(Search.this,SearchList);
                searchDnevnik.setAdapter(adapter);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchList.clear();
                for (Task s : AllTask) {
                    if (s.getSerijskiBroj().toLowerCase().contains(newText.toLowerCase())) {
                        SearchList.add(s);
                    }
                }

                adapter = new Search.customAdapter(Search.this,SearchList);
                searchDnevnik.setAdapter(adapter);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public void onLoaded(JSONArray arr) {

        AllTask.clear();
        String ObjectName = "";

        for (int i = 0; i < arr.length(); i++) {
            try {

                ObjectName = arr.getJSONObject(i).has("type") ? "Vozilo" : "Prevozi";

                if (ObjectName.equals("Prevozi")) {
                    int IdTask = arr.getJSONObject(i).getInt("IdTask");
                    String SerijskiBroj = arr.getJSONObject(i).getString("SerijskiBroj");
                    String Vozilo = arr.getJSONObject(i).getString("Vozilo");
                    String Istovar = arr.getJSONObject(i).getString("Istovar");
                    String Roba = arr.getJSONObject(i).getString("Roba");
                    String Status = arr.getJSONObject(i).getString("Status");
                    String DatumAzuriranja = arr.getJSONObject(i).getString("DatumAzuriranja");
                    String Utovar = arr.getJSONObject(i).getString("Utovar");
                    String UvoznaSpedicija = arr.getJSONObject(i).getString("UvoznaSpedicija");
                    String IzvoznaSpedicija = arr.getJSONObject(i).getString("IzvoznaSpedicija");
                    String Uvoznik = arr.getJSONObject(i).getString("Uvoznik");
                    String Izvoznik = arr.getJSONObject(i).getString("Izvoznik");
                    String Napomena = arr.getJSONObject(i).getString("Napomena");
                    String RefBroj = arr.getJSONObject(i).getString("RefBroj");
                    String Pregledano = arr.getJSONObject(i).getString("Pregledano");

                    SerijskiBroj = SerijskiBroj.replace("#", " ");
                    //  String Vozac = SerijskiBroj.split("#")[1];

                    AllTask.add(new Task(IdTask, SerijskiBroj, Vozilo, Istovar, Roba, Status, DatumAzuriranja, Utovar, UvoznaSpedicija, IzvoznaSpedicija, Uvoznik, Izvoznik, Napomena, RefBroj, Pregledano));
                    SearchList.add(new Task(IdTask, SerijskiBroj, Vozilo, Istovar, Roba, Status, DatumAzuriranja, Utovar, UvoznaSpedicija, IzvoznaSpedicija, Uvoznik, Izvoznik, Napomena, RefBroj, Pregledano));
                }

                if (ObjectName.equals("Vozilo")) {
                    int Key = arr.getJSONObject(i).getInt("key");
                    String Vozac = arr.getJSONObject(i).getString("value");

                    vozila.put(Key, Vozac);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        /*
    adapter = new ArrayAdapter<String>(
            Search.this,
            android.R.layout.simple_list_item_1,
            lst
    );
    */
        adapter = new Search.customAdapter(this,AllTask);
        searchDnevnik.setAdapter(adapter);

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
            Toast.makeText(this, "Transport prezadužen", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, TaskPrevozi.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError() {

    }


    public void PrezaduziVozaca(int IdVozac, int IdDnevnik, int IdVozilo )
    {
        new LoadJsonObject(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "Vozaci/PromjeniVozaca?IdVozac=" + IdVozac + "&IdDnevnik=" + IdDnevnik  + "&IdVozilo=" + IdVozilo);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final int IdDnevnik  = SearchList.get(position).getIdTask();


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Odaberi vozilo");
        alertDialog.setItems(vozila.values().toArray(new String[0]), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                final int idVozilo = vozila.keySet().toArray(new Integer [0])[which];

                AlertDialog.Builder builder = new AlertDialog.Builder(Search.this);
                builder.setMessage("Prezaduži transport " + SearchList.get(position).getSerijskiBroj() + " na sebe u vozilo " + vozila.get(idVozilo))
                        .setPositiveButton("Preuzmi", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PrezaduziVozaca(IdVozac, IdDnevnik, idVozilo);
                            }
                        })
                        .setNegativeButton("Odustani", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

                builder.show();

            }
        });
        alertDialog.show();






    }




    class customAdapter extends BaseAdapter {

        private Context mContext;
        private List<Task> mTaskList;

        public customAdapter(Context mContext, List<Task> mTaskList) {
            this.mContext = mContext;
            this.mTaskList = mTaskList;
        }

        @Override
        public int getCount() {
            return mTaskList.size(); //taskovi.length();
        }

        @Override
        public Object getItem(int position) {
            return mTaskList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mTaskList.get(position).getIdTask() ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //View v = View.inflate(mContext,R.layout.list_view_item,null);

            View v = View.inflate(mContext,R.layout.list_view_item_small,null);

            TextView SerijskiBroj = (TextView)v.findViewById(R.id.SerijskiBroj);
            // TextView Vozilo = (TextView)v.findViewById(R.id.Vozilo);
            TextView Istovar = (TextView)v.findViewById(R.id.Istovar);
            TextView Status = (TextView)v.findViewById(R.id.Status);
            //   TextView Roba = (TextView)v.findViewById(R.id.Roba);
            //  TextView DatumAzuriranja = (TextView)v.findViewById(R.id.DatumAzuriranja);
            TextView Utovar = (TextView)v.findViewById(R.id.Utovar);
            TextView Prevoznik = (TextView)v.findViewById(R.id.Prevoznik);

            SerijskiBroj.setText(mTaskList.get(position).getSerijskiBroj());
            //    Vozilo.setText(mTaskList.get(position).getVozilo());
            Istovar.setText(mTaskList.get(position).getIstovar());
            Status.setText(mTaskList.get(position).getStatus());
            //    Roba.setText(mTaskList.get(position).getRoba());
            //   DatumAzuriranja.setText(mTaskList.get(position).getDatumAzuriranja());
            Utovar.setText(mTaskList.get(position).getUtovar());


            if (mTaskList.get(position).getPregledano().equals(""))
            {
                Prevoznik.setVisibility(TextView.INVISIBLE);
                TableRow row = (TableRow)v.findViewById(R.id.Red);
                TableLayout tableLayout = (TableLayout)v.findViewById(R.id.Tabela);
                tableLayout.removeView(row);
            }
            else
            {
                Prevoznik.setVisibility(TextView.VISIBLE);
                Prevoznik.setText("*Drugi Prevoznik");
                Prevoznik.setTextColor(getResources().getColor(R.color.colorAccent));
            }

            v.setTag(mTaskList.get(position).getIdTask());

            return v;
                /*
                convertView = getLayoutInflater().inflate(R.layout.tasl_list_item, null);
                setContentView(R.layout.tasl_list_item);
                TextView Serijskibroj = (TextView)findViewById(R.id.textView4);
                TextView Podaci = (TextView)findViewById(R.id.textView5);

                String [] Sb = {"2017-0001","2017-0002","2017-0003","2017-0004"};
                String [] TaskData = {"aaaaa","bbbbb","cccc","ddddd"};

                Serijskibroj.setText(Sb[position]);
                Podaci.setText(TaskData[position]);

                setContentView(R.layout.activity_task);

                return  convertView;
                */
        }
    }


}
