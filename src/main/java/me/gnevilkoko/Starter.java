package me.gnevilkoko;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Starter {
    public static void main(String[] args) {

        //Launching bot listener
        new BotNode().botProcessor();
    }

    public static void log(String message){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("["+dtf.format(now)+"] "+message);
    }
}