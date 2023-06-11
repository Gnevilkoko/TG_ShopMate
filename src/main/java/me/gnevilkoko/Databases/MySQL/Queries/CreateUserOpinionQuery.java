package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateUserOpinionQuery implements Query {
    private long shopId;
    private long userId;
    private String text;
    private double rating;

    public CreateUserOpinionQuery(long shopId, long userId, String text, double rating) {
        this.shopId = shopId;
        this.userId = userId;
        this.text = text;
        this.rating = rating;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO opinions(shop_id, user_id, text, created, rating) VALUES (?, ?, ?, ?, ?)");
            statement.setLong(1, shopId);
            statement.setLong(2, userId);
            statement.setString(3, text);
            statement.setLong(4, System.currentTimeMillis()/1000);
            statement.setDouble(5, rating);
            statement.executeUpdate();
            statement.close();

            Starter.log("Created opinion from user ID: "+userId+" for shop with ID: "+shopId);
        } catch (SQLException e){
            Starter.log("Error while creating user opinion");
            e.printStackTrace();
        }
    }
}
