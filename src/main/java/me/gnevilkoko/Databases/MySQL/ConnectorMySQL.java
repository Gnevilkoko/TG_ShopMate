package me.gnevilkoko.Databases.MySQL;

import me.gnevilkoko.BotNode;
import me.gnevilkoko.Files.ConfigFile;
import me.gnevilkoko.Starter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectorMySQL {
    private Connection connection;

    public ConnectorMySQL() {

    }

    private void openConnection(){
        ConfigFile config = new ConfigFile();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://"+config.getSQL_HOST()+":"+config.getSQL_PORT()+"/"+config.getSQL_DATABASE(),
                    config.getSQL_USER(),config.getSQL_PASS());
        } catch (SQLException e) {
            System.out.println("Error in MySQL connection | ConnectorMySQL");
            throw new RuntimeException(e);
        }
    }

    private void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            Starter.log("Can't close connection to MySQL");
        }
    }

    public void executeQuery(Query query) {
        openConnection();

        if(connection == null){
            Starter.log("Connection to MySQL is null");
            return;
        }

        Connection connection = this.connection;
        query.execute(connection);
        closeConnection();
    }
}
