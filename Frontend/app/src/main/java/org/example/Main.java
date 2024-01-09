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
        int cacheSize = Integer.parseInt(enviromentVariables.get("CACHE_SIZE"));
        LRUCache<StandardResponse> cache = new LRUCache<>(cacheSize);
        String categoryKey = enviromentVariables.get("CATEGORY_SERVER");
        String purchaseKey = enviromentVariables.get("PURCHASE_SERVER");
        int categoryPort = 8000;
        int purchasePort = 4567;
        boolean cacheEnable = Boolean.parseBoolean(enviromentVariables.get("ENABLE_CACHING"));

        try{
            categoryPort = Integer.parseInt(enviromentVariables.get("CATEGORY_PORT"));
            purchasePort = Integer.parseInt(enviromentVariables.get("PURCHASE_PORT"));
        }catch (Exception ex){
            ex.printStackTrace();
        }
        String portKey = enviromentVariables.get("PORT");
        port = portKey == null ? 3000 : Integer.parseInt(portKey);
        port(port);
        int finalCategoryPort = categoryPort;
        get("*", (req, res) -> {
            String pathInfo = req.pathInfo();
            String[] pathParts = pathInfo.split("/");
            String lastPathComponent = (pathParts[pathParts.length - 2]+"/"+pathParts[pathParts.length - 1]).toLowerCase();
            StandardResponse cachedResponse = cacheEnable ? cache.get(lastPathComponent) : null;
            if(cachedResponse == null){
                // Load balancing
                categoryServer = "http://"+categoryKey+":"+ finalCategoryPort;
                // END
                String requestUrl = ProxyUtil.formatUrl(categoryServer, req.pathInfo());
                res.header("Content-Type","application/json");
                try{
                    StandardResponse response = ProxyUtil.Get(requestUrl);
                    res.status(response.status);
                    if(cacheEnable){
                        cache.store(lastPathComponent, response);
                    }
                    return response.response;
                }catch(Exception ex){
                    res.status(404);
                    return "";
                }
            }else{
                res.status(cachedResponse.status);
                return cachedResponse.response;
            }
        });

        delete("/invalidate/:id", (req, res) -> {
            String id = "book/"+req.params(":id");
            cache.invalidate(id);
            res.status(204);
            return "";
        });


        int finalPurchasePort = purchasePort;
        put("*", (req, res) ->{
            purchaseServer = "http://"+purchaseKey+":"+finalPurchasePort;
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