package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Enums.ProductStatus;
import me.gnevilkoko.Starter;
import me.gnevilkoko.Utils.BuyList;
import me.gnevilkoko.Utils.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class CreateTransactionQuery implements Query {
    private BuyList buyList;

    public CreateTransactionQuery(BuyList buyList) {
        this.buyList = buyList;
    }

    @Override
    public void execute(Connection connection) {
        ArrayList<Product> productsList = buyList.getProductList();
        StringBuilder products = new StringBuilder();
        for(int i = 0; i < productsList.size(); i++){
            if(productsList.get(i).getStatus() != ProductStatus.CHECKED) continue;
            if(products.length() != 0){
                products.append(",");
            }
            products.append(productsList.get(i).getProductName());
        }
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO transactions(user_id, shop_id, buy_products, started, ended, spended_money) VALUES (?, ?, ?, ?, ?, ?)");
            statement.setLong(1, buyList.getUserId());
            statement.setLong(2, buyList.getShopId());
            statement.setString(3, products.toString());
            statement.setLong(4, buyList.getStarted());
            statement.setLong(5, System.currentTimeMillis()/1000);
            statement.setDouble(6, buyList.getSpendedMoney());
            statement.executeUpdate();
            statement.close();

            Starter.log("Created new transaction for user with ID: "+buyList.getUserId());
        } catch (SQLException e){
            Starter.log("Error while creating transaction");
            e.printStackTrace();
        }
    }
}
