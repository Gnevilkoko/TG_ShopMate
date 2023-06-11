package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Enums.ChatStatus;
import me.gnevilkoko.Starter;
import me.gnevilkoko.Utils.Shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GetShopListQuery implements Query {
    private ArrayList<Shop> shops = new ArrayList<>();

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM shops");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                shops.add(new Shop(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("avg_rating")
                ));
            }

            statement.close();
        } catch (SQLException e){
            Starter.log("Error while getting shop list");
            e.printStackTrace();
        }
    }

    public ArrayList<Shop> getShops() {
        return shops;
    }

}
