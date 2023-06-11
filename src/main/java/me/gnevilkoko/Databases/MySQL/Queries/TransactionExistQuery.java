package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;
import me.gnevilkoko.Utils.BuyList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionExistQuery implements Query {
    private BuyList buyList;
    private boolean exist;

    public TransactionExistQuery(BuyList buyList) {
        this.buyList = buyList;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * " +
                    "FROM transactions " +
                    "WHERE user_id = ? " +
                    "  AND shop_id = ? " +
                    "  AND started = ? LIMIT 1;");
            statement.setLong(1, buyList.getUserId());
            statement.setLong(2, buyList.getShopId());
            statement.setLong(3, buyList.getStarted());
            exist = statement.executeQuery().next();
            statement.close();

        } catch (SQLException e){
            Starter.log("Error while checking is transaction exist");
            e.printStackTrace();
        }
    }

    public boolean isExist() {
        return exist;
    }
}
