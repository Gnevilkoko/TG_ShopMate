package me.gnevilkoko;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import me.gnevilkoko.Databases.MySQL.ConnectorMySQL;
import me.gnevilkoko.Databases.MySQL.Queries.UserExistQuery;
import me.gnevilkoko.Files.ConfigFile;
import me.gnevilkoko.TelegramEvents.Buttons.ButtonUnderMessageClickedEvent;
import me.gnevilkoko.TelegramEvents.Messages.MessageReceivedEvent;
import me.gnevilkoko.TelegramEvents.Messages.UnknownUserMessageEvent;
import me.gnevilkoko.TelegramEvents.Messages.UnknownUserWriteMessageEvent;

public class BotNode {
    public static final TelegramBot BOT = new TelegramBot(new ConfigFile().getBotApiToken()); // Creating bot
    public static final ConnectorMySQL DATABASE = new ConnectorMySQL();
    public void botProcessor() {
        //Listening for all events and preparing it to processing data
        BOT.setUpdatesListener(updates -> {
            try {
                updates.forEach(this::process);
            } catch (Exception e){
                e.printStackTrace();
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void process(Update update) {

        //Getting what was received
       if(update.message() != null){
           Message message = update.message();

           //Checking is user exist or not
           UserExistQuery query = new UserExistQuery(message.from().id());
           DATABASE.executeQuery(query);

           //It is mean, that user write for first time
           if(!query.isExist()){
               new UnknownUserWriteMessageEvent(message);
           } else {
               //Trigger when user write any message
               new MessageReceivedEvent(message);
           }

       } else if(update.callbackQuery() != null){
           //When user click any button under message
           CallbackQuery callbackQuery = update.callbackQuery();
           new ButtonUnderMessageClickedEvent(callbackQuery);
       }

    }
}
