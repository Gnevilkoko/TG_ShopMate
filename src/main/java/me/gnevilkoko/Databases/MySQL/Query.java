package me.gnevilkoko.Databases.MySQL;

import java.sql.Connection;
import java.sql.SQLException;

public interface Query {
    void execute(Connection connection);
}
