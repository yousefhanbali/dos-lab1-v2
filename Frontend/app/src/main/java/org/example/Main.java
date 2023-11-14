package org.example;

import java.io.IOException;
import java.util.Map;

import static spark.Spark.*;
// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    private static String purchaseServer;
    private static String categoryServer;

    private static int port;
    public static void main(String[] args) throws IOException {
        Map<String, String> enviromentVariables = System.getenv();
        String categoryKey = enviromentVariables.get("CATEGORY_SERVER");
        String purchaseKey = enviromentVariables.get("PURCHASE_SERVER");
        String portKey = enviromentVariables.get("PORT");
        categoryServer = "http://"+ (categoryKey == null ? "category:8000" : categoryKey);
        purchaseServer = "http://"+ (purchaseKey == null ? "purchase:4567" : purchaseKey);
        port = portKey == null ? 3000 : Integer.parseInt(portKey);
        port(port);
        get("*", (req, res) -> {
            String requestUrl = ProxyUtil.formatUrl(categoryServer, req.pathInfo());
            res.header("Content-Type","application/json");
            try{
                StandardResponse response = ProxyUtil.Get(requestUrl);
                res.status(response.status);
                return response.response;
            }catch(Exception ex){
                res.status(404);
                return "";
            }
        });

        put("*", (req, res) ->{
            String requestUrl = ProxyUtil.formatUrl(purchaseServer, req.pathInfo());
            res.header("Content-Type","application/json");
            try{
                StandardResponse response = ProxyUtil.Put(requestUrl);
                res.status(response.status);
                return response.response;
            }catch(Exception ex){
                res.status(400);
                return "";
            }
        });
    }
}