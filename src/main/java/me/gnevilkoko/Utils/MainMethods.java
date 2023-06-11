package me.gnevilkoko.Utils;

import info.debatty.java.stringsimilarity.Damerau;
import me.gnevilkoko.Exceptions.SimilarShopNotFoundException;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainMethods {
    public static Shop getSimilarShop(String shopName, ArrayList<Shop> shops) throws SimilarShopNotFoundException {
        Damerau similar = new Damerau();

        Shop mostSimilarShop = null;
        double minDistance = Double.MAX_VALUE;
        double minimalSimilarDistance = shopName.length()/2;

        for(Shop allShops : shops){
            double distance = similar.distance(shopName, allShops.getName());
            if(distance < minDistance && distance < minimalSimilarDistance){
                minDistance = distance;
                mostSimilarShop = allShops;
            }
        }

        if(mostSimilarShop == null)
            throw new SimilarShopNotFoundException(shopName);

        System.out.println("Distance: "+minDistance+" | "+minimalSimilarDistance);
        return mostSimilarShop;
    }

    public static String getStarsFromRating(double rating){
        StringBuilder stars = new StringBuilder();
        int fullStars = (int) rating;
        int emptyStars = 5 - fullStars;

        for(int i = 0; i < fullStars; i++){
            stars.append("⭐");
        }

        for(int i = 0; i < emptyStars; i++){
            stars.append("☆");
        }

        return stars.toString();
    }

    public static String convertUnixTimeToTextTime(long time, String lang){
        long years = TimeUnit.SECONDS.toDays(time) / 365;
        time -= TimeUnit.DAYS.toSeconds(years * 365);
        long days = TimeUnit.SECONDS.toDays(time);
        time -= TimeUnit.DAYS.toSeconds(days);
        long hours = TimeUnit.SECONDS.toHours(time);
        time -= TimeUnit.HOURS.toSeconds(hours);
        long minutes = TimeUnit.SECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toSeconds(minutes);
        long seconds = time;

        StringBuilder result = new StringBuilder();
        if (years > 0) {
            result.append(years).append(getNoun(years, LocaleFile.getString(lang, "Year"),
                    LocaleFile.getString(lang, "Years"),
                    LocaleFile.getString(lang, "OfYear"))).append(", ");
        }
        if (days > 0) {
            result.append(days).append(getNoun(days, LocaleFile.getString(lang, "Day"),
                    LocaleFile.getString(lang, "Days"),
                    LocaleFile.getString(lang, "OfDays"))).append(", ");
        }
        if (hours > 0) {
            result.append(hours).append(getNoun(hours, LocaleFile.getString(lang, "Hour"),
                    LocaleFile.getString(lang, "Hours"),
                    LocaleFile.getString(lang, "OfHours"))).append(", ");
        }
        if (minutes > 0) {
            result.append(minutes).append(getNoun(minutes, LocaleFile.getString(lang, "Minute"),
                    LocaleFile.getString(lang, "Minutes"),
                    LocaleFile.getString(lang, "OfMinutes"))).append(", ");
        }
        result.append(seconds).append(getNoun(seconds, LocaleFile.getString(lang, "Second"),
                LocaleFile.getString(lang, "Seconds"),
                LocaleFile.getString(lang, "OfSeconds")));
        return result.toString();
    }
    public static String getElapsedTime(long startTime, long stopTime, String lang){
        long elapsedTimeInSeconds = stopTime - startTime;

        long years = TimeUnit.SECONDS.toDays(elapsedTimeInSeconds) / 365;
        elapsedTimeInSeconds -= TimeUnit.DAYS.toSeconds(years * 365);
        long days = TimeUnit.SECONDS.toDays(elapsedTimeInSeconds);
        elapsedTimeInSeconds -= TimeUnit.DAYS.toSeconds(days);
        long hours = TimeUnit.SECONDS.toHours(elapsedTimeInSeconds);
        elapsedTimeInSeconds -= TimeUnit.HOURS.toSeconds(hours);
        long minutes = TimeUnit.SECONDS.toMinutes(elapsedTimeInSeconds);
        elapsedTimeInSeconds -= TimeUnit.MINUTES.toSeconds(minutes);
        long seconds = elapsedTimeInSeconds;

        StringBuilder result = new StringBuilder();
        if (years > 0) {
            result.append(years).append(getNoun(years, LocaleFile.getString(lang, "Year"),
                    LocaleFile.getString(lang, "Years"),
                    LocaleFile.getString(lang, "OfYear"))).append(", ");
        }
        if (days > 0) {
            result.append(days).append(getNoun(days, LocaleFile.getString(lang, "Day"),
                    LocaleFile.getString(lang, "Days"),
                    LocaleFile.getString(lang, "OfDays"))).append(", ");
        }
        if (hours > 0) {
            result.append(hours).append(getNoun(hours, LocaleFile.getString(lang, "Hour"),
                    LocaleFile.getString(lang, "Hours"),
                    LocaleFile.getString(lang, "OfHours"))).append(", ");
        }
        if (minutes > 0) {
            result.append(minutes).append(getNoun(minutes, LocaleFile.getString(lang, "Minute"),
                    LocaleFile.getString(lang, "Minutes"),
                    LocaleFile.getString(lang, "OfMinutes"))).append(", ");
        }
        result.append(seconds).append(getNoun(seconds, LocaleFile.getString(lang, "Second"),
                LocaleFile.getString(lang, "Seconds"),
                LocaleFile.getString(lang, "OfSeconds")));
        return result.toString();
    }

    private static String getNoun(long value, String form1, String form2, String form5) {
        value = Math.abs(value) % 100;
        long remainder = value % 10;
        if (value > 10 && value < 20) {
            return form5;
        }
        if (remainder > 1 && remainder < 5) {
            return form2;
        }
        if (remainder == 1) {
            return form1;
        }
        return form5;
    }
}
