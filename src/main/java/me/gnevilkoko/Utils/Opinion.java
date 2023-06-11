package me.gnevilkoko.Utils;

public class Opinion {
    private long id;
    private long shopId;
    private long userId;
    private String text;
    private double rating;
    private long created;

    public Opinion(long id, long shopId, long userId, String text, double rating, long created) {
        this.id = id;
        this.shopId = shopId;
        this.userId = userId;
        this.text = text;
        this.rating = rating;
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }
}
