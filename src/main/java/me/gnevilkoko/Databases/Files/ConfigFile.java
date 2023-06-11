package me.gnevilkoko.Files;

import me.gnevilkoko.Starter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class ConfigFile {
    private String botApiToken;

    //SQL DATA
    private String SQL_HOST;
    private String SQL_USER;
    private String SQL_PASS;
    private String SQL_PORT;
    private String SQL_DATABASE;

    //SERVER IP -----
    private static final String serverIp;

    static {
        try {
            serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    // ---------------


    /**
     * Constructor which get all data from config file.
     * To get data - use getters
     */
    public ConfigFile() {
        Properties prop;

        //Trying to load config file data
        try {
            FileInputStream configFile = new FileInputStream("config.properties");
            prop = new Properties();
            prop.load(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Getting data from config file
        this.botApiToken = prop.getProperty("BOT_API_KEY");
        this.SQL_HOST = prop.getProperty("SQL_HOST");
        this.SQL_USER = prop.getProperty("SQL_USER");
        this.SQL_PASS = prop.getProperty("SQL_PASS");
        this.SQL_PORT = prop.getProperty("SQL_PORT");
        this.SQL_DATABASE = prop.getProperty("SQL_DATABASE");
    }



    //Getters to get data from class

    public String getBotApiToken() {
        return botApiToken;
    }

    public String getSQL_HOST() {
        return SQL_HOST;
    }

    public String getSQL_USER() {
        return SQL_USER;
    }

    public String getSQL_PASS() {
        return SQL_PASS;
    }

    public String getSQL_PORT() {
        return SQL_PORT;
    }

    public String getSQL_DATABASE() {
        return SQL_DATABASE;
    }
}
