package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;
import me.gnevilkoko.Utils.Opinion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GetOpinionsQuery implements Query {

    private ArrayList<Opinion> opinions = new ArrayList<>();
    private int amountOfOpinions;
    private long startFromId;
    private long shopId;

    public GetOpinionsQuery(int amountOfOpinions, long startFromId, long shopId) {
        this.amountOfOpinions = amountOfOpinions;
        this.startFromId = startFromId;
        this.shopId = shopId;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM opinions WHERE id < ? AND shop_id = ? ORDER BY id DESC LIMIT ?;");
            statement.setLong(1, startFromId);
            statement.setLong(2, shopId);
            statement.setInt(3, amountOfOpinions);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                opinions.add(new Opinion(
                        resultSet.getLong("id"),
                        resultSet.getLong("shop_id"),
                        resultSet.getLong("user_id"),
                        resultSet.getString("text"),
                        resultSet.getDouble("rating"),
                        resultSet.getLong("created")
                ));
            }

            statement.close();
        } catch (SQLException e){
            Starter.log("Error while getting opinion list");
            e.printStackTrace();
        }
    }

    public ArrayList<Opinion> getOpinions() {
        return opinions;
    }
}
