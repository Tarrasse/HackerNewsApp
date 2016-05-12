package com.example.mahmoud.finalhackernews;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter adapter ;
    private ListView listView ;
    private ArrayList<String> titles  ;
    private ArrayList<String> urls;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Hcker News");

        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        titles = new ArrayList<>();
        urls = new ArrayList<>();

        dialog = new ProgressDialog(this);
        dialog.setMessage("loading ...");
        dialog.setCancelable(false);

        GetDataTask get = new GetDataTask();
        get.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent I = new Intent(getApplicationContext(), NewsActivety.class);
                I.putExtra("url", urls.get(position));
                startActivity(I);
            }
        });

    }

    private class GetDataTask extends AsyncTask <String, String, String> {
        //StringBuilder string = new StringBuilder();
        String string = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStreamReader input = new InputStreamReader(connection.getInputStream());
                int data = input.read();
                while(data != -1 ){
                    string +=(char) data;
                    data =input.read();
                }
                System.out.println(string);

                JSONArray array = new JSONArray(string);

                for (int i = 0; i < 20 ; i++) {
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/"+array.get(i)+".json?print=pretty");
                    connection = (HttpURLConnection) url.openConnection();
                    input = new InputStreamReader(connection.getInputStream());
                    data = input.read();
                    string = "";
                    while(data != -1 ){
                        string +=(char) data;
                        data =input.read();
                    }
                    System.out.println(string);
                    JSONObject root = new JSONObject(string);
                    try {
                        String articletitle = root.getString("title");
                        String articleUrl = root.getString("url");

                        titles.add(articletitle);
                        urls.add(articleUrl);

                    }catch (Exception e){
                        System.out.println(e);
                    }

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            adapter.addAll(titles);
            adapter.notifyDataSetChanged();
            dialog.hide();
            super.onPostExecute(s);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            GetDataTask getDataTask = new GetDataTask();
            getDataTask.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
