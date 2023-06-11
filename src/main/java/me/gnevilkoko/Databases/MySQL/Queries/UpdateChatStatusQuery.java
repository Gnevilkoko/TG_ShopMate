package me.gnevilkoko.Databases.MySQL.Queries;

import me.gnevilkoko.Databases.MySQL.Query;
import me.gnevilkoko.Enums.ChatStatus;
import me.gnevilkoko.Starter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateChatStatusQuery implements Query {
    private ChatStatus chatStatus;
    private long userId;

    public UpdateChatStatusQuery(ChatStatus chatStatus, long userId) {
        this.chatStatus = chatStatus;
        this.userId = userId;
    }

    @Override
    public void execute(Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE accounts SET chat_status = ? WHERE account_id = ? LIMIT 1");
            statement.setString(1, chatStatus.name());
            statement.setLong(2, userId);
            statement.executeUpdate();
            statement.close();

            Starter.log("Updated account with ID: "+userId+" chat_status = "+chatStatus.name());
        } catch (SQLException e){
            Starter.log("Error while updating account user account");
            e.printStackTrace();
        }
    }
}
