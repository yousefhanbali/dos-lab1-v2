package org.example;

import java.io.IOException;

public interface CatalogService {
    public Book getBookById(int id) throws IOException;
    public int purchaseBook(int id) throws IOException;
}
