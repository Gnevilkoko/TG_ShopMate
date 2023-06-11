package me.gnevilkoko.Utils;

import me.gnevilkoko.Enums.ProductStatus;

public class Product {
    private String productName;
    private ProductStatus status;
    private double price = -1.0D;

    public Product(String productName, ProductStatus status) {
        this.productName = productName;
        this.status = status;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public String getProductName() {
        return productName;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productName='" + productName + '\'' +
                ", status=" + status +
                ", price=" + price +
                '}';
    }
}
