package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;
import me.gnevilkoko.Utils.Shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetShopByIdQuery implements Query {
    private Shop shop;
    private long id;

    public GetShopByIdQuery(long id) {
        this.id = id;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM shops WHERE id = ? LIMIT 1");
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                shop = new Shop(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("avg_rating")
                );
            }
            statement.close();
        } catch (SQLException e){
            Starter.log("Error while getting shop information");
            e.printStackTrace();
        }
    }

    public Shop getShop() {
        return shop;
    }
}
