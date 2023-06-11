package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Starter;
import me.gnevilkoko.Utils.BuyList;
import me.gnevilkoko.Utils.BuyStatistic;
import me.gnevilkoko.Utils.Shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class GetUserStatisticQuery implements Query {
    private ArrayList<BuyStatistic> statistic = new ArrayList<>();
    private int days;
    private long userId;

    public GetUserStatisticQuery(int days, long userId) {
        this.days = days;
        this.userId = userId;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM transactions WHERE user_id = ?");
            statement.setLong(1, userId);
            if(days != -1){
                long finding = (System.currentTimeMillis()/1000)-(60L * 60L * 24L * days);
                statement = connection.prepareStatement("SELECT * FROM transactions WHERE user_id = ? AND ended >= ?");
                statement.setLong(1, userId);
                statement.setLong(2, finding);
            }
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                ArrayList<String> products = new ArrayList<>(Arrays.asList(resultSet.getString("buy_products").split(",")));

                statistic.add(new BuyStatistic(
                        resultSet.getLong("transaction_id"),
                        resultSet.getLong("user_id"),
                        resultSet.getLong("shop_id"),
                        products,
                        resultSet.getLong("started"),
                        resultSet.getLong("ended"),
                        resultSet.getDouble("spended_money")
                ));
            }

            statement.close();
        } catch (SQLException e){
            Starter.log("Error while getting shop list");
            e.printStackTrace();
        }
    }

    public ArrayList<BuyStatistic> getStatistic() {
        return statistic;
    }
}
