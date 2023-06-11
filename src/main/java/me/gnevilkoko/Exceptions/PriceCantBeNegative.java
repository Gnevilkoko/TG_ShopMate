package me.gnevilkoko.Exceptions;

public class PriceCantBeNegative extends Exception{

    public PriceCantBeNegative() {
        super("Price can't be negative");
    }
}
