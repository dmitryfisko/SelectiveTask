package com.fisko.selectivetask;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getSupportActionBar().setTitle("Foo Fighters Tour Dates");

        list = (ListView) findViewById(R.id.listView);

        new ArtistDataDownloadTask().execute("http://api.bandsintown.com/artists/FooFighters/ " +
                "events.json?api_version=2.0&app_id=SelectiveTask");
    }

    private void updateList(JSONArray artists) {
        ListViewAdapter adapter = new ListViewAdapter(this, artists);
        list.setAdapter(adapter);
    }

    private class ArtistDataDownloadTask extends AsyncTask<String, Void, JSONArray>   {

        @Override
        protected JSONArray doInBackground(String... urls) {
            try {
                return downloadAndParseData(urls[0]);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray res) {
            if(res != null) updateList(res);
            else {
                Toast.makeText(MainActivity.this, "Sorry. Cannot load " +
                        "Foo Fighters tour dates.", Toast.LENGTH_LONG).show();
            }
        }

        private JSONArray downloadAndParseData(String requestdUrl) throws IOException, JSONException {
            InputStream is = null;

            try {
                URL url = new URL(requestdUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();

                return parseInputStreamToJSON(is);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        private JSONArray parseInputStreamToJSON(InputStream is) throws IOException, JSONException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder res = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                res.append(line);
            }

            Log.e("downloader_result", res.toString());
            return new JSONArray(res.toString());
        }
    }

}
