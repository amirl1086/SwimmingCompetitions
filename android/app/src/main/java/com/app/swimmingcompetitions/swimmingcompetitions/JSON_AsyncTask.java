package com.app.swimmingcompetitions.swimmingcompetitions;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class JSON_AsyncTask extends AsyncTask<String, Void, String> {
    public HttpAsyncResponse delegate;
    private HttpURLConnection urlConnection;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private ResponseBody responseBody;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected String doInBackground(String... params) {

        StringBuilder result = new StringBuilder();

        //setup the connection
        try {
            /*JSONObject data = new JSONObject(params[0]);
            OkHttpClient client = new OkHttpClient();


            String urlAddress = "https://us-central1-firebase-swimmingcompetitions.cloudfunctions.net" + data.getString("urlSuffix");
            data.remove("urlSuffix");
            data.remove("httpMethod");
            RequestBody requestBody = RequestBody.create(JSON, data.toString());


            Request request = new Request.Builder()
                    .url(urlAddress)
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            this.responseBody = response.body();

            if(this.responseBody != null) {
                return this.responseBody.string();
            }
            return null;*/

            JSONObject data = new JSONObject(params[0]);

            //set up the url
            String urlAddress = "https://us-central1-firebase-swimmingcompetitions.cloudfunctions.net" + data.getString("urlSuffix");
            URL url = new URL(urlAddress);
            data.remove("urlSuffix");

            //initialize the connection
            this.urlConnection = (HttpURLConnection) url.openConnection();
            this.urlConnection.setReadTimeout(10000);
            this.urlConnection.setConnectTimeout(10000);
            this.urlConnection.setRequestMethod(data.getString("httpMethod"));
            data.remove("httpMethod");

            this.urlConnection.setDoOutput(true);    //enable output (body extra)
            this.urlConnection.setRequestProperty("Content-Type", "application/json");   //set header
            this.urlConnection.connect();

            //get output stream for writing
            this.outputStream = urlConnection.getOutputStream();
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.outputStream));

            //write data into server
            this.bufferedWriter.write(data.toString());
            this.bufferedWriter.flush();

            //read response from server
            this.inputStream = this.urlConnection.getInputStream();
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.inputStream));
            String line;
            while ((line = this.bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }

            return result.toString();
        }
        catch(Exception e) {
            System.out.println("JSON_AsyncTask doInBackground try Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if(this.urlConnection != null) {
                    this.urlConnection.disconnect();
                }
                if(this.outputStream != null) {
                    this.outputStream.close();
                }
                if(this.inputStream != null) {
                    this.inputStream.close();
                }
                if(this.bufferedReader != null) {
                    this.bufferedReader.close();
                }
                if(this.bufferedWriter != null) {
                    this.bufferedWriter.close();
                }
                if(this.responseBody != null) {
                    this.responseBody.close();
                }
            }
            catch(Exception e) {
                System.out.println("JSON_AsyncTask doInBackground finally Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}