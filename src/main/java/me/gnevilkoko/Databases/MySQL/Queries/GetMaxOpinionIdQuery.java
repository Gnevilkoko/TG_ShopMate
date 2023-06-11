package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;
import me.gnevilkoko.Utils.Opinion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetMaxOpinionIdQuery implements Query {
    private long maxId = 0;

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT MAX(id) AS max_id FROM opinions");
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                maxId = resultSet.getLong("max_id");
            }

            statement.close();
        } catch (SQLException e){
            Starter.log("Error while getting max id from opinions");
            e.printStackTrace();
        }
    }

    public long getMaxId() {
        return maxId;
    }
}
