package com.example.mt.menitest;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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

/**
 * Created by mladen.todorovic on 9/27/2017.
 */

public class tabNijeUtovareno extends Fragment implements LoadJSONTask.Listener, LoadJsonObject.Listener, AdapterView.OnItemClickListener {


    private tabNijeUtovareno.customAdapter adapter;
    private ListView lvTaskovi;
    private List<Task> mTaskMapList = new ArrayList<>();
    private ProgressBar bar = null;
    private Map<Integer, String> vozaci = new HashMap<Integer, String>();
    private Map<Integer, String> vozila = new HashMap<Integer, String>();

    public tabNijeUtovareno(){

        mTaskMapList = new ArrayList<>();
        vozaci = new HashMap<Integer, String>();
        vozila = new HashMap<Integer, String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_nije_utovareno, container, false);

        bar = (ProgressBar) rootView.findViewById(R.id.progresbar2);
        bar.setVisibility(View.VISIBLE);

        lvTaskovi = (ListView)rootView.findViewById(R.id.ListViewNijeUtovareno);
        lvTaskovi.setOnItemClickListener(this);
        registerForContextMenu(lvTaskovi);

        mTaskMapList.clear();
        vozaci.clear();
        lvTaskovi.setAdapter(null);

        SharedPreferences preferences =  this.getActivity().getSharedPreferences("GMTEL", Context.MODE_PRIVATE);
        String token = preferences.getString("Token", "");


        new LoadJSONTask(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "Vozaci/VratiAktivneVozaceOsim?IdVozac=0");
        new LoadJSONTask(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "Vozilo/VratiAktivnaVozilaOsim?IdVozilo=0");
        new LoadJSONTask(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "api/Tasks?token="+token);


        return rootView;
    }

    public void PrezaduziVozaca(int IdVozac, int IdDnevnik, int IdVozilo )
    {
        new LoadJsonObject(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "Vozaci/PromjeniVozaca?IdVozac=" + IdVozac + "&IdDnevnik=" + IdDnevnik  + "&IdVozilo=" + IdVozilo);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       // Toast.makeText(getActivity(),"Odabrani id je " + view.getTag(), Toast.LENGTH_SHORT).show();


        Task clickedTask = mTaskMapList.get(position);

        Intent detalji = new Intent(getActivity(), TaskDetalji.class);
        detalji.putExtra("task", clickedTask);
        this.startActivity(detalji);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater mi = this.getActivity().getMenuInflater();
        mi.inflate(R.menu.task_menu_utovar, menu);

        menu.setHeaderTitle("Postavi status prevoza");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int index = info.position;
        int IdTask = 666;//mTaskMapList.get(index).getIdTask();

        switch (item.getItemId()) {
            case R.id.menu_utovar1: {

                //Toast.makeText(getActivity(), "menu_utovar !" + IdTask, Toast.LENGTH_SHORT).show();
                Intent detalji = new Intent(getActivity(), TaskDetail.class);
                Task clickedTask = mTaskMapList.get(index);
                detalji.putExtra("task", clickedTask);
                detalji.putExtra("status","Nije_utovareno");
                this.startActivity(detalji);
                break;
            }
            case R.id.menu_transport1: {
                Intent detalji = new Intent(getActivity(), TaskDetail.class);
                Task clickedTask = mTaskMapList.get(index);
                detalji.putExtra("task", clickedTask);
                detalji.putExtra("status","U_transportu");
                this.startActivity(detalji);
                break;
            }
            case R.id.menu_troskovi1: {
                Intent trosak = new Intent(getActivity(), TroskoviNoviActivity.class);
                Task clickedTask = mTaskMapList.get(index);
                trosak.putExtra("IdDnevnik", clickedTask.getIdTask());
                this.startActivity(trosak);
                break;
            }
            case R.id.menu_istovar1: {
                final Intent detalji = new Intent(getActivity(), TaskDetail.class);
                Task clickedTask = mTaskMapList.get(index);
                detalji.putExtra("task", clickedTask);
                detalji.putExtra("status","Dostavljeno");
                List <String> PodStatusi = new ArrayList<String>();
                PodStatusi.add("Dostavljeno na krajnju lokaciju");
                PodStatusi.add("Dostavljeno u skladište");
                PodStatusi.add("Dostavljeno brzoj pošti");

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Gdje je roba dostavljena?");
                alertDialog.setItems(PodStatusi.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        int PodStatus = which;
                        if (PodStatus == 2)
                            PodStatus = 7;

                        detalji.putExtra("podStatus",PodStatus);
                        getActivity().startActivity(detalji);
                    }

                });
                alertDialog.show();

                break;
            }
            case R.id.menu_promjeni_vozaca1: {
                //Task clickedTask = mTaskMapList.get(index);
                final int IdDnevnik = mTaskMapList.get(index).getIdTask();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                //AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create(); //Read Update
                alertDialog.setTitle("Prezaduži vožnju");
                alertDialog.setItems(vozaci.values().toArray(new String[0]), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                        final int idVozaca = vozaci.keySet().toArray(new Integer [0])[which];


                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Odaberi vozilo");
                        alertDialog.setItems(vozila.values().toArray(new String[0]), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                final int idVozilo = vozila.keySet().toArray(new Integer [0])[which];


                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Prezaduži transport na " + vozaci.get(idVozaca) + " u vozilo " + vozila.get(idVozilo))
                                        .setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                PrezaduziVozaca(idVozaca, IdDnevnik, idVozilo);
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

                });
                alertDialog.show();
                break;
            }
            default:
                return super.onContextItemSelected(item);
        }

        return true;
    }

    @Override
    public void onLoaded(JSONArray arr) {

        String ObjectName = "";

        for (int i =0; i<arr.length(); i++)
        {
            try {

                ObjectName = arr.getJSONObject(i).has("type") ? "Vozilo" : (arr.getJSONObject(i).has("key") ? "Vozaci" : "Prevozi");

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

                    if (Status.equals("NIJE UTOVARENO"))
                        mTaskMapList.add(new Task(IdTask, SerijskiBroj, Vozilo, Istovar, Roba, Status, DatumAzuriranja, Utovar, UvoznaSpedicija, IzvoznaSpedicija, Uvoznik, Izvoznik, Napomena, RefBroj, Pregledano));

                }

                if (ObjectName.equals("Vozaci")) {
                    int Key = arr.getJSONObject(i).getInt("key");
                    String Vozac = arr.getJSONObject(i).getString("value");

                    vozaci.put(Key, Vozac);
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

        loadListView();

        bar.setVisibility(View.INVISIBLE);
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
            Toast.makeText(getActivity(), "Transport prezadužen", Toast.LENGTH_SHORT).show();
          //  Intent i = new Intent(this, TroskoviActivity.class);
          //  this.startActivity(i);
        }
        else {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError() {
        Toast.makeText(getActivity(), "Error !", Toast.LENGTH_SHORT).show();
    }


    private void loadListView() {

        //lvTaskovi = (ListView)rootView.findViewById(R.id.ListViewNijeUtovareno);
        //lvTaskovi = (ListView)findViewById(R.id.ListViewTaskovi1);
        adapter = new tabNijeUtovareno.customAdapter(getActivity(),mTaskMapList);
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

            View v = View.inflate(mContext,R.layout.list_view_item_small,null);
            TextView SerijskiBroj = (TextView)v.findViewById(R.id.SerijskiBroj);
           // TextView Vozilo = (TextView)v.findViewById(R.id.Vozilo);
            TextView Istovar = (TextView)v.findViewById(R.id.Istovar);
            TextView Status = (TextView)v.findViewById(R.id.Status);
           // TextView Roba = (TextView)v.findViewById(R.id.Roba);
           // TextView DatumAzuriranja = (TextView)v.findViewById(R.id.DatumAzuriranja);
            TextView Utovar = (TextView)v.findViewById(R.id.Utovar);
            TextView Prevoznik = (TextView)v.findViewById(R.id.Prevoznik);


            SerijskiBroj.setText(mTaskList.get(position).getSerijskiBroj());
           // Vozilo.setText(mTaskList.get(position).getVozilo());
            Istovar.setText(mTaskList.get(position).getIstovar());
            Status.setText(mTaskList.get(position).getStatus());
          //  Roba.setText(mTaskList.get(position).getRoba());
          //  DatumAzuriranja.setText(mTaskList.get(position).getDatumAzuriranja());
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
