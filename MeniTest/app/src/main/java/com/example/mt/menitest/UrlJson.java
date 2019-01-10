package com.example.mt.menitest;

import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mladen.todorovic on 14.09.2017..
 */

public class UrlJson {

    private String url;

    public String GetUrl() { return this.url; }
    public void SetUrl(String url) { this.url = url; }

    private  String getJSON() {
        HttpURLConnection con = null;

        URL u = null;
        try {
            u = new URL(url);

            con = (HttpURLConnection) u.openConnection();

            con.connect();


            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            return sb.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected JSONObject GetJson(){
        JSONObject obj = null;
        String ret = "";
        try {
            obj = new JSONObject(getJSON());

            if (obj != null) {

                return obj;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    protected JSONArray GetJsonArray(){
        JSONArray obj = null;
        String ret = "";
        try {
            obj = new JSONArray(getJSON());

            if (obj != null) {

                return obj;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }






}

