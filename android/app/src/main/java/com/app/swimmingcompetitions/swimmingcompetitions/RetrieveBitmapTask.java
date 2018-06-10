package com.app.swimmingcompetitions.swimmingcompetitions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


class RetrieveBitmapTask extends AsyncTask<String, Void, Bitmap> {

    public bitmapAsyncResponse delegate;
    private URL url;
    private HttpURLConnection urlConnection;
    private InputStream inputStream;

    protected Bitmap doInBackground(String... urls) {
        try {
            this.url = new URL(urls[0]);
            this.urlConnection = (HttpURLConnection) url.openConnection();
            this.urlConnection.setDoInput(true);
            this.urlConnection.connect();
            this.inputStream = this.urlConnection.getInputStream();
            return BitmapFactory.decodeStream(this.inputStream);
        }
        catch (Exception e) {
            System.out.println("RetrieveBitmapTask doInBackground try Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
            e.printStackTrace();
        }
        finally {
            if(this.urlConnection != null) {
                this.urlConnection.disconnect();
            }
            try {
                if(this.inputStream != null) {
                    this.inputStream.close();
                }
            }
            catch(Exception e) {
                System.out.println("RetrieveBitmapTask doInBackground finally Exception \nMessage: " + e.getMessage() + "\nStack Trace:\n");
                e.printStackTrace();
            }
        }
        return null;
    }

    protected void onPostExecute(Bitmap bitmap) {
        processFinished(bitmap);
    }

    private void processFinished(Bitmap bitmap) {
        delegate.processFinish(bitmap);
    }
}
