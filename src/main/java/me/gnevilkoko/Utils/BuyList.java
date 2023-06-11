package me.gnevilkoko.Utils;

import java.util.ArrayList;

public class BuyList {
    private long userId;
    private ArrayList<Product> productList;
    private boolean considerPrices;
    private double discount = 0.0D;
    private long started;
    private long shopId;

    public BuyList(long userId, ArrayList<Product> productList, boolean considerPrices, long shopId) {
        this.userId = userId;
        this.productList = productList;
        this.considerPrices = considerPrices;
        this.shopId = shopId;
    }

    public long getUserId() {
        return userId;
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public boolean isConsiderPrices() {
        return considerPrices;
    }

    public double getDiscount() {
        return discount;
    }

    public long getStarted() {
        return started;
    }

    public void setConsiderPrices(boolean considerPrices) {
        this.considerPrices = considerPrices;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
    public void startShopping(){
        started = System.currentTimeMillis()/1000;
    }

    public long getShopId() {
        return shopId;
    }

    public void setProductList(ArrayList<Product> productList) {
        this.productList = productList;
    }

    public double getSpendedMoney(){
        double spendedMoney = 0.0D;
        if(considerPrices) {
            for (Product product : productList) {
                if (product.getPrice() != -1) {
                    spendedMoney += product.getPrice();
                }
            }
            if(discount != 0){
                spendedMoney = spendedMoney * (1 - (discount/100));
            }
        }

        return spendedMoney;
    }

    @Override
    public String toString() {
        return "BuyList{" +
                "userId=" + userId +
                ", productList=" + productList +
                ", considerPrices=" + considerPrices +
                ", discount=" + discount +
                ", started=" + started +
                ", shopId=" + shopId +
                '}';
    }
}
