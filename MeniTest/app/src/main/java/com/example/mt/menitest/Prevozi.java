package com.example.mt.menitest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Prevozi extends AppCompatActivity implements LoadJSONTask.Listener, AdapterView.OnItemClickListener {



    private Prevozi.customAdapter adapter;
    private ListView lvTaskovi;
    private List<Task> mTaskMapList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prevozi);

        SharedPreferences preferences = getSharedPreferences("GMTEL", Context.MODE_PRIVATE);
        String token = preferences.getString("Token", "");

        lvTaskovi = (ListView)findViewById(R.id.ListViewTaskovi1);
        lvTaskovi.setOnItemClickListener(this);

        registerForContextMenu(lvTaskovi);

        new LoadJSONTask(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "api/Tasks?token="+token);

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.task_menu, menu);

        menu.setHeaderTitle("Postavi status prevoza");


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int index = info.position;
        int IdTask = mTaskMapList.get(index).getIdTask();

        switch (item.getItemId()) {
            case R.id.menu_utovar: {
                Toast.makeText(this, "menu_utovar !" + IdTask, Toast.LENGTH_SHORT).show();
                mTaskMapList.get(index).setStatus("DJOMLA STATUS");
                return true;
            }
            case R.id.menu_transport: {
                Toast.makeText(this, "menu_transport !" + IdTask, Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.menu_istovar: {
                Toast.makeText(this, "menu_istovar !" + IdTask, Toast.LENGTH_SHORT).show();
                return true;
            }default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),"Odabrani id je " + view.getTag(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaded(JSONArray arr) {

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

        loadListView();
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
    }


    private void loadListView() {

        lvTaskovi = (ListView)findViewById(R.id.ListViewTaskovi1);
        adapter = new Prevozi.customAdapter(getApplicationContext(),mTaskMapList);
        lvTaskovi.setAdapter(adapter);

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

            View v = View.inflate(mContext,R.layout.list_view_item,null);
            TextView SerijskiBroj = (TextView)v.findViewById(R.id.SerijskiBroj);
            TextView Vozilo = (TextView)v.findViewById(R.id.Vozilo);
            TextView Istovar = (TextView)v.findViewById(R.id.Istovar);
            TextView Status = (TextView)v.findViewById(R.id.Status);
            TextView Roba = (TextView)v.findViewById(R.id.Roba);
            TextView DatumAzuriranja = (TextView)v.findViewById(R.id.DatumAzuriranja);
            TextView Utovar = (TextView)v.findViewById(R.id.Utovar);

            SerijskiBroj.setText(mTaskList.get(position).getSerijskiBroj());
            Vozilo.setText(mTaskList.get(position).getVozilo());
            Istovar.setText(mTaskList.get(position).getIstovar());
            Status.setText(mTaskList.get(position).getStatus());
            Roba.setText(mTaskList.get(position).getRoba());
            DatumAzuriranja.setText(mTaskList.get(position).getDatumAzuriranja());
            Utovar.setText(mTaskList.get(position).getUtovar());

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
