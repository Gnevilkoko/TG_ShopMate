package me.gnevilkoko.Utils;

import java.util.ArrayList;

public class BuyStatistic {
    private long transactionId;
    private long userId;
    private long shopId;
    private ArrayList<String> products;
    private long started;
    private long ended;
    private double spendedMoney;

    public BuyStatistic(long transactionId, long userId, long shopId, ArrayList<String> products, long started, long ended, double spendedMoney) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.shopId = shopId;
        this.products = products;
        this.started = started;
        this.ended = ended;
        this.spendedMoney = spendedMoney;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public long getUserId() {
        return userId;
    }

    public long getShopId() {
        return shopId;
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public long getStarted() {
        return started;
    }

    public long getEnded() {
        return ended;
    }

    public double getSpendedMoney() {
        return spendedMoney;
    }
}
