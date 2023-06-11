package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateShopQuery implements Query {
    private String shopName;

    public CreateShopQuery(String shopName) {
        this.shopName = shopName;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO shops(name) VALUES (?)");
            statement.setString(1, shopName);
            statement.executeUpdate();
            statement.close();

            Starter.log("Created shop with name: "+shopName);
        } catch (SQLException e){
            Starter.log("Error while creating user account");
            e.printStackTrace();
        }
    }
}
