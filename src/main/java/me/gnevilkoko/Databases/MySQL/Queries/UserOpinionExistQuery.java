package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserOpinionExistQuery implements Query {
    private boolean exist;
    private long userId;
    private long shop_id;

    public UserOpinionExistQuery(long userId, long shop_id) {
        this.userId = userId;
        this.shop_id = shop_id;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * " +
                    "FROM opinions " +
                    "WHERE user_id = ? " +
                    "  AND shop_id = ? LIMIT 1;");
            statement.setLong(1, userId);
            statement.setLong(2, shop_id);
            exist = statement.executeQuery().next();
            statement.close();

        } catch (
                SQLException e){
            Starter.log("Error while checking is transaction exist");
            e.printStackTrace();
        }
    }

    public boolean isExist() {
        return exist;
    }
}
