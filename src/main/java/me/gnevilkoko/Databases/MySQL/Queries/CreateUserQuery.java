package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;
import me.gnevilkoko.Utils.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateUserQuery implements Query {
    private User user;

    public CreateUserQuery(User user) {
        this.user = user;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO accounts(account_id, language) VALUES (?, ?)");
            statement.setLong(1, user.getUserId());
            statement.setString(2, user.getLanguage());
            statement.executeUpdate();
            statement.close();

            Starter.log("Created account with ID: "+user.getUserId());
        } catch (SQLException e){
            Starter.log("Error while creating user account");
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }
}
