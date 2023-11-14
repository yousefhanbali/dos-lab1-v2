package org.example;

import java.io.IOException;

import static spark.Spark.*;
// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {

    public static void main(String[] args) throws IOException {
        final CatalogService catalogService = new CatalogServiceImp();
        port(4567);
        put("/purchase/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            try{
                int test = catalogService.purchaseBook(id);
                res.status(test);
                return "";
            }catch(IOException ex){
                res.status(400);
                return ex.getMessage();
            }
        });
    }
}