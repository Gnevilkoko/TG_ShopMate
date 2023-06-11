package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateUserLanguageQuery implements Query {
    private long userId;
    private String lang;

    public UpdateUserLanguageQuery(long userId, String lang) {
        this.userId = userId;
        this.lang = lang;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE accounts SET language = ? WHERE account_id = ? LIMIT 1");
            statement.setString(1, lang);
            statement.setLong(2, userId);
            statement.executeUpdate();
            statement.close();

            Starter.log("Updated account with ID: "+userId+" language = "+lang);
        } catch (SQLException e){
            Starter.log("Error while updating user account | language");
            e.printStackTrace();
        }
    }
}
