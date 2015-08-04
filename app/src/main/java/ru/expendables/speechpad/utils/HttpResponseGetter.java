package ru.expendables.speechpad.utils;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpResponseGetter  {

    public static String streamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(is),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        is.close();
        return sb.toString();
    }

    public static JSONObject getResponseByUrl(String url) {
        String downloads = null;
        JSONObject json = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            json = new JSONObject(streamToString(in));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return json;
    }



}
