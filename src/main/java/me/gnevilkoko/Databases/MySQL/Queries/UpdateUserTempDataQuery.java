package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateUserTempDataQuery implements Query {
    private long userId;
    private String data;

    public UpdateUserTempDataQuery(long userId, String data) {
        this.userId = userId;
        this.data = data;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE accounts SET temp = ? WHERE account_id = ? LIMIT 1");
            statement.setString(1, data);
            statement.setLong(2, userId);
            statement.executeUpdate();
            statement.close();

            Starter.log("Updated account with ID: "+userId+" temp data = "+data);
        } catch (SQLException e){
            Starter.log("Error while updating account user account");
            e.printStackTrace();
        }
    }
}
