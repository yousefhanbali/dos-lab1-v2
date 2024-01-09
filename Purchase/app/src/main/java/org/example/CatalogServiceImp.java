package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CatalogServiceImp implements CatalogService {
    private final String url;
    private final String bookEndpoint = "/book/";

    private final String purchaseEndpoint = "/purchase/";

    public CatalogServiceImp() {
        Map<String, String> enviromentVariables = System.getenv();
        String categoryKey = enviromentVariables.get("CATEGORY_SERVER");
        String categoryPort = enviromentVariables.get("CATEGORY_PORT");
        this.url = "http://"
                + (categoryKey == null || categoryPort == null ? "catalog:8000" : categoryKey + ":" + categoryPort);
    }

    public Book getBookById(int id) throws IOException {
        URL obj = new URL(url + bookEndpoint + id);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            Book result = gson.fromJson(response.toString(), Book.class);
            return result;
        } else {
            return null;
        }
    }

    public int purchaseBook(int id) throws IOException {
        URL obj = new URL(url + purchaseEndpoint + id);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("PUT");

        return con.getResponseCode();
    }
}
