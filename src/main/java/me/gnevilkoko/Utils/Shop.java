package me.gnevilkoko.Utils;

import info.debatty.java.stringsimilarity.Damerau;

import java.util.ArrayList;

public class Shop {
    private long id;
    private String name;
    private double avgRating;

    public Shop(long id, String name, double avgRating) {
        this.id = id;
        this.name = name;
        this.avgRating = avgRating;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }
}
