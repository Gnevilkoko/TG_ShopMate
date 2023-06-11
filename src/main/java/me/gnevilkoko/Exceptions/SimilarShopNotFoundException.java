package me.gnevilkoko.Exceptions;

public class SimilarShopNotFoundException extends Exception {
    public SimilarShopNotFoundException(String shopName) {
        super("Not found similar shops for \""+shopName+"\"");
    }
}
