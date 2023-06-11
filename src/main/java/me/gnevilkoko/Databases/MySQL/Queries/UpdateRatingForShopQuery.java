package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateRatingForShopQuery implements Query {
    private long shopId;

    public UpdateRatingForShopQuery(long shopId) {
        this.shopId = shopId;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE shops SET avg_rating = (SELECT AVG(rating) FROM opinions WHERE shop_id = ?) WHERE id = ?;");
            statement.setLong(1, shopId);
            statement.setLong(2, shopId);
            statement.executeUpdate();
            statement.close();

            Starter.log("Updated shop with ID: "+shopId);
        } catch (SQLException e){
            Starter.log("Error while updating account user account");
            e.printStackTrace();
        }
    }
}
