package org.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class ProxyUtil {
    public static String formatUrl(String host, String path){
        return host+path;
    }


    public static StandardResponse Get(String url) throws IOException{
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        return getStandardResponse(con);
    }

    public static StandardResponse Put(String url) throws IOException{
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("PUT");
        return getStandardResponse(con);
    }

    private static StandardResponse getStandardResponse(HttpURLConnection con) throws IOException {
        int responseCode = con.getResponseCode();
        StandardResponse response = new StandardResponse();
        response.status = responseCode;
        if (responseCode == HttpURLConnection.HTTP_OK){
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder responseBody = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                responseBody.append(inputLine);
            }
            in.close();
            response.response = responseBody.toString();
        }
        return response;
    }
}
