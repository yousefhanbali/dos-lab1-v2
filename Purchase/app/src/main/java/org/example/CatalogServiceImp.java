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

    public CatalogServiceImp(){
        Map<String, String> enviromentVariables = System.getenv();
        String categoryKey = enviromentVariables.get("CATEGORY_SERVER");
        this.url = "http://"+ (categoryKey == null ? "category:8000" : categoryKey);
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
        Book book = this.getBookById(id);
        if (book == null)
            return 404; // Book not found

        if (book.quantity == 0)
            return 400; // Book out of stock

        book.quantity--;

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String jsonString = gson.toJson(book);

        URL obj = new URL(url + bookEndpoint + id);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");

        // For POST only - START
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(jsonString.getBytes());
        os.flush();
        os.close();
        // For POST only - END

        return con.getResponseCode();
    }
}
