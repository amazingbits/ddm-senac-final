package com.example.restaurante.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.restaurante.R;
import com.example.restaurante.adapter.FoodListAdapter;
import com.example.restaurante.constants.Config;
import com.example.restaurante.helpers.DatabaseHelper;
import com.example.restaurante.helpers.FileHelper;
import com.example.restaurante.helpers.SlugHelper;
import com.example.restaurante.model.FoodModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public class FoodListActivity extends AppCompatActivity {

    private RecyclerView recyclerFoodList;
    private List<FoodModel> foodList = new ArrayList<FoodModel>();
    private FoodListAdapter adapter;
    private RelativeLayout loadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        recyclerFoodList = findViewById(R.id.recyclerFoodList);
        loadingPanel = findViewById(R.id.loadingPanel);
        loadingPanel.setVisibility(View.VISIBLE);

        LoadJsonTask task = new LoadJsonTask();
        task.execute(Config.API_URL_JSON);

        foodList = getListFromInternalFiles();

        //configurar recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerFoodList.setLayoutManager(layoutManager);
        recyclerFoodList.setHasFixedSize(true);
        adapter = new FoodListAdapter(foodList);
        recyclerFoodList.setAdapter(adapter);

    }

    public ArrayList<FoodModel> getListFromInternalFiles() {

        DatabaseHelper db = new DatabaseHelper();
        db.createDatabase(getApplicationContext());
        return db.getAll(getApplicationContext());

    }

    // AsyncTask para carregar lista via API JSON
    class LoadJsonTask extends AsyncTask<String, Void, ArrayList<FoodModel>> {


        @Override
        protected void onPostExecute(ArrayList<FoodModel> foodModels) {
            super.onPostExecute(foodModels);
            if(foodModels.size() > 0) {
                adapter.swapData(foodModels);
            }
            loadingPanel.setVisibility(View.GONE);
        }

        @Override
        protected ArrayList<FoodModel> doInBackground(String... strings) {
            String apiUrl = strings[0];
            ArrayList<FoodModel> newList = new ArrayList<FoodModel>();

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                InputStream is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(isr);
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                JSONArray json = new JSONArray(buffer.toString());

                FileHelper fileHelper = new FileHelper();
                for(int i = 0; i < json.length(); i++) {
                    JSONObject jObj = (JSONObject) json.get(i);
                    FoodModel foodModel = new FoodModel();

                    foodModel.setName(jObj.getString("name"));
                    foodModel.setDescription(jObj.getString("description"));
                    foodModel.setPrice(jObj.getString("price"));
                    foodModel.setHasGluten(jObj.getString("hasGluten"));
                    foodModel.setCalories(jObj.getString("calories"));
                    foodModel.setImgUrl(jObj.getString("picture"));
                    foodModel.setPictureBlob(fileHelper.getFileFromURL(getApplicationContext(), jObj.getString("picture")));
                    newList.add(foodModel);
                }
                return newList;

            } catch (IOException | JSONException e) {
                Log.d("ERRO", "doInBackground: " + e.getMessage());
                return newList;
            }
        }
    }
}