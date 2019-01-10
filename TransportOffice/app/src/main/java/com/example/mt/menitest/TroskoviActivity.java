package com.example.mt.menitest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class TroskoviActivity extends AppCompatActivity implements LoadJSONTask.Listener, AdapterView.OnItemClickListener {

    private ListView lvTroskovi;
    private List<Troskovi> mTroskoviMapList = new ArrayList<>();
    private ProgressBar bar = null;
    private TroskoviActivity.customAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troskovi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */

                Intent novi = new Intent(view.getContext(), TroskoviNoviActivity.class);
                startActivity(novi);
            }
        });


        bar = (ProgressBar) findViewById(R.id.progresbar);
        bar.setVisibility(View.VISIBLE);

        lvTroskovi = (ListView)findViewById(R.id.ListViewTroskovi);
        lvTroskovi.setOnItemClickListener(this);
        registerForContextMenu(lvTroskovi);

        mTroskoviMapList.clear();
        lvTroskovi.setAdapter(null);

        SharedPreferences preferences =  getSharedPreferences("GMTEL", Context.MODE_PRIVATE);
        String token = preferences.getString("Token", "");




        try {

            if (!isOnline())
                throw new Exception("Internet nije dostupan");
            new LoadJSONTask(this).execute(getResources().getString(R.string.ProdukcijaSajt) + "DnevnikPrevoza/ListaVozacevihTroskova?token=" + token);
        }
        catch (Exception ex)
        {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Greška u listanju troškova: " + ex.getMessage(), Snackbar.LENGTH_INDEFINITE)
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Troskovi clickedTask = mTroskoviMapList.get(position);

        Intent detalji = new Intent(this, TroskoviNoviActivity.class);
        detalji.putExtra("ObjTrosak", clickedTask);
        this.startActivity(detalji);


        /*
         Toast.makeText(this,"Odabrani id je " + view.getTag(), Toast.LENGTH_SHORT).show();
           */
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.troskovi_menu, menu);

        menu.setHeaderTitle("Ažuriranje podataka");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int index = info.position;
        //   int IdTask = mTaskMapList.get(index).getIdTask();

        switch (item.getItemId()) {
            case R.id.menu_trosak_izmjeni: {

                Intent detalji = new Intent(this, TroskoviNoviActivity.class);
                Troskovi clickedTrosak = mTroskoviMapList.get(index);
                detalji.putExtra("ObjTrosak", clickedTrosak);
                this.startActivity(detalji);
                break;
            }
            case R.id.menu_trosak_razmjeni: {

                Intent detalji = new Intent(this, TroskoviRazmjenaAcitivty.class);
                Troskovi clickedTrosak = mTroskoviMapList.get(index);
                detalji.putExtra("ObjTrosak", clickedTrosak);
                this.startActivity(detalji);
                break;
            }
            default:
                return super.onContextItemSelected(item);
        }

        return true;
    }



    @Override
    public void onLoaded(JSONArray arr) {
        for (int i =0; i<arr.length(); i++)
        {
            try {
                int     Id       = arr.getJSONObject(i).getInt("Id");
                String Vrsta     = arr.getJSONObject(i).getString("Vrsta");
                String Tip       = arr.getJSONObject(i).getString("Tip");
                String Iznos     = arr.getJSONObject(i).getString("Iznos");
                String Datum     = arr.getJSONObject(i).getString("Datum");
                int Kartica      = arr.getJSONObject(i).getInt("Kartica");
                mTroskoviMapList.add(new Troskovi(Id, Vrsta,Iznos, Datum, Tip, Kartica  ));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        loadListView();
        bar.setVisibility(View.INVISIBLE);
    }

    private void loadListView() {

        //lvTaskovi = (ListView)rootView.findViewById(R.id.ListViewNijeUtovareno);
        //lvTaskovi = (ListView)findViewById(R.id.ListViewTaskovi1);
        adapter = new TroskoviActivity.customAdapter(getApplicationContext(), mTroskoviMapList);
        lvTroskovi.setAdapter(adapter);

    }


    @Override
    public void onError() {
        Toast.makeText(this, "Error !", Toast.LENGTH_SHORT).show();
    }



    class customAdapter extends BaseAdapter {

        private Context mContext;
        private List<Troskovi> mTrosakList;

        public customAdapter(Context mContext, List<Troskovi> mTrosakList) {
            this.mContext = mContext;
            this.mTrosakList = mTrosakList;
        }

        @Override
        public int getCount() {
            return mTrosakList.size(); //taskovi.length();
        }

        @Override
        public Object getItem(int position) {
            return mTrosakList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mTrosakList.get(position).getId() ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //View v = View.inflate(mContext,R.layout.list_view_item,null);

            View v = View.inflate(mContext,R.layout.list_view_item_troskovi,null);

            TextView IznosTroska = (TextView)v.findViewById(R.id.IznosTroska);
            TextView DatumTroska = (TextView)v.findViewById(R.id.DatumTroska);
            TextView TipTroska = (TextView)v.findViewById(R.id.TipTroska);
            TextView VrstaTroska = (TextView)v.findViewById(R.id.VrstaTroska);
            ImageView slika = (ImageView) v.findViewById(R.id.imageViewTrosak2);

            IznosTroska.setText(mTrosakList.get(position).getIznos());
            DatumTroska.setText(mTrosakList.get(position).getDatum());
            TipTroska.setText(mTrosakList.get(position).getTip());
            VrstaTroska.setText(mTrosakList.get(position).getVrsta());

            if(mTrosakList.get(position).getTip().equals("RASHOD"))
                TipTroska.setTextColor(getResources().getColor(R.color.colorAccent));
            else
                TipTroska.setTextColor(getResources().getColor(R.color.colorGreen));

            if(mTrosakList.get(position).getKartica() == 1)
            {
                TipTroska.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                slika.setImageResource(R.drawable.ic_baseline_credit_card_24px);
            }

            v.setTag(mTrosakList.get(position).getId());

            return v;

        }
    }

}
