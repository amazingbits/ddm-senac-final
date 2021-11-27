package com.example.restaurante.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.restaurante.R;
import com.example.restaurante.constants.Config;
import com.example.restaurante.helpers.DatabaseHelper;
import com.example.restaurante.helpers.FileHelper;
import com.example.restaurante.helpers.SlugHelper;
import com.example.restaurante.model.FoodModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private Button btnLoadJson;
    private Button btnLoadXml;
    private Button btnLoadFoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoadJson = findViewById(R.id.btnLoadJson);
        btnLoadXml = findViewById(R.id.btnLoadXml);
        btnLoadFoodList = findViewById(R.id.btnLoadFoodList);

        // evento para carregar lista JSON
        btnLoadJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoadJsonTask jsonTask = new LoadJsonTask();
                jsonTask.execute(Config.API_URL_JSON);

            }
        });

        // evento para carregar lista XML
        btnLoadXml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoadXmlTask xmlTask = new LoadXmlTask();
                xmlTask.execute(Config.API_URL_XML);

            }
        });

        // evento de clique para carregar pratos
        btnLoadFoodList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FoodListActivity.class);
                startActivity(intent);
            }
        });
    }

    //AsyncTask CarregarJSON
    class LoadJsonTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnLoadJson.setEnabled(false);
            btnLoadXml.setEnabled(false);
            btnLoadFoodList.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String apiUrl = strings[0];

            InputStream inputStream = null;
            StringBuffer buffer = null;

            try {

                URL url = new URL(apiUrl);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setConnectTimeout(5000);

                inputStream = conexao.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                JSONArray json = new JSONArray(buffer.toString());

                DatabaseHelper db = new DatabaseHelper();
                db.createDatabase(getApplicationContext());
                db.deleteAll(getApplicationContext());

                FileHelper fileHelper = new FileHelper();
                for(int i = 0; i < json.length(); i++) {
                    JSONObject jObj = (JSONObject) json.get(i);

                    //Recuperando os dados do JSON
                    String name = jObj.getString("name");
                    String description = jObj.getString("description");
                    String price = "a confirmar";
                    String hasGluten = jObj.getString("hasGluten");
                    String calories = jObj.getString("calories");
                    String imgURL = jObj.getString("picture");
                    byte[] imgByte = fileHelper.getFileFromURL(getApplicationContext(), imgURL);

                    // Montando objeto para inserir no banco
                    FoodModel foodObj = new FoodModel();
                    foodObj.setName(name);
                    foodObj.setDescription(description);
                    foodObj.setPrice(price);
                    foodObj.setHasGluten(hasGluten);
                    foodObj.setCalories(calories);
                    foodObj.setImgUrl(imgURL);
                    foodObj.setPictureBlob(imgByte);

                    // Inserindo dado no banco
                    db.insert(getApplicationContext(), foodObj);

                }

            } catch (IOException | JSONException e) {
                return "Não foi possível carregar a lista JSON";
            }

            return "Lista carregada com sucesso!";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            btnLoadJson.setEnabled(true);
            btnLoadXml.setEnabled(true);
            btnLoadFoodList.setEnabled(true);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }
    }

    //AsyncTask CarregarXML
    class LoadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnLoadJson.setEnabled(false);
            btnLoadXml.setEnabled(false);
            btnLoadFoodList.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String apiUrl = strings[0];

            InputStream inputStream = null;
            StringBuffer buffer = null;

            URL url = null;
            try {
                url = new URL(apiUrl);

                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setConnectTimeout(5000);

                inputStream = conexao.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = dbFac.newDocumentBuilder();
                Document doc = documentBuilder.parse( new InputSource( new StringReader(buffer.toString())));
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("food-item");


                DatabaseHelper db = new DatabaseHelper();
                db.createDatabase(getApplicationContext());
                db.deleteAll(getApplicationContext());

                FileHelper fileHelper = new FileHelper();
                for(int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if(node.getNodeType() == Node.ELEMENT_NODE) {

                        Element element = (Element) node;

                        // Recuperando dados XML
                        String name = element.getElementsByTagName("name").item(0).getTextContent();
                        String description = element.getElementsByTagName("description").item(0).getTextContent();
                        String price = "a confirmar";
                        String hasGluten = element.getElementsByTagName("hasGluten").item(0).getTextContent();
                        String calories = element.getElementsByTagName("calories").item(0).getTextContent();
                        String imgURL = element.getElementsByTagName("picture").item(0).getTextContent();
                        byte[] imgByte = fileHelper.getFileFromURL(getApplicationContext(), imgURL);

                        // Montando objeto para inserir no banco
                        FoodModel foodObj = new FoodModel();
                        foodObj.setName(name);
                        foodObj.setDescription(description);
                        foodObj.setPrice(price);
                        foodObj.setHasGluten(hasGluten);
                        foodObj.setCalories(calories);
                        foodObj.setImgUrl(imgURL);
                        foodObj.setPictureBlob(imgByte);

                        // Inserindo dado no banco
                        db.insert(getApplicationContext(), foodObj);

                    }
                }

            } catch (IOException | ParserConfigurationException | SAXException e) {
                return "Não foi possível carregar a lista XML";
            }

            return "Lista XML carregada com sucesso!";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            btnLoadJson.setEnabled(true);
            btnLoadXml.setEnabled(true);
            btnLoadFoodList.setEnabled(true);
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }
    }

}