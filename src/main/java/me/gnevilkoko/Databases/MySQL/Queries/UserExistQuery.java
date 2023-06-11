package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;
import me.gnevilkoko.Utils.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserExistQuery implements Query {
    private long userId;
    private boolean exist = false;

    public UserExistQuery(long userId) {
        this.userId = userId;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM accounts WHERE account_id = ? LIMIT 1");
            statement.setLong(1, userId);
            exist = statement.executeQuery().next();
            statement.close();

        } catch (SQLException e){
            Starter.log("Error while checking is user account exist");
            e.printStackTrace();
        }
    }

    public long getUserId() {
        return userId;
    }

    public boolean isExist() {
        return exist;
    }
}
