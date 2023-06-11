package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Enums.ChatStatus;
import me.gnevilkoko.Starter;
import me.gnevilkoko.Utils.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetUserQuery implements Query {
    private long userId;
    private User user = new User();

    public GetUserQuery(long userId) {
        this.userId = userId;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM accounts WHERE account_id = ? LIMIT 1");
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                user.setUserId(resultSet.getLong("account_id"));
                user.setLanguage(resultSet.getString("language"));
                user.setChatStatus(ChatStatus.valueOf(resultSet.getString("chat_status")));
                user.setTempData(resultSet.getString("temp"));
            }

            statement.close();

        } catch (SQLException e){
            Starter.log("Error while getting user account");
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }
}
