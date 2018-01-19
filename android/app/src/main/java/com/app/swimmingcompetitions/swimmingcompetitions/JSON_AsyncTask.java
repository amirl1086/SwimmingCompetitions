package com.app.swimmingcompetitions.swimmingcompetitions;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSON_AsyncTask extends AsyncTask<String, Void, String> {

    //delegate the response from the server to the caller requesting function
    public AsyncResponse delegate;

    @Override
    protected String doInBackground(String... params) {

        StringBuilder result = new StringBuilder();
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;

        //setup the connection
        try {
            JSONObject data = new JSONObject(params[0]);

            //set up the url
            String urlAddress = "https://us-central1-firebase-swimmingcompetitions.cloudfunctions.net" + data.getString("urlSuffix");
            URL url = new URL(urlAddress);
            //URL url = new URL("http://localhost:5000/firebase-swimmingcompetitions/us-central1" + data.getString("urlSuffix"));
            data.remove("urlSuffix");

            //initialize the connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod(data.getString("httpMethod"));
            data.remove("httpMethod");

            urlConnection.setDoOutput(true);    //enable output (body extra)
            urlConnection.setRequestProperty("Content-Type", "application/json");   //set header
            urlConnection.connect();

            //get output stream for writing
            OutputStream outputStream = urlConnection.getOutputStream();
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

            //write data into server
            bufferedWriter.write(data.toString());
            bufferedWriter.flush();

            //read response from server
            InputStream inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }
            return result.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("post finished");
        processFinished(result);
    }

    //@Override
    protected void onGetExcecute(String result) {
        System.out.println("get finished");
        processFinished(result);
    }

    public void processFinished(String result) {
        System.out.println("result " + result);
        delegate.processFinish(result);
    }
}