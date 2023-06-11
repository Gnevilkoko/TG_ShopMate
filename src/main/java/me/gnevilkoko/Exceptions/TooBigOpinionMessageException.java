package me.gnevilkoko.Exceptions;

public class TooBigOpinionMessageException extends Exception {
    public TooBigOpinionMessageException() {
        super("User entered too big opinion");
    }
}
