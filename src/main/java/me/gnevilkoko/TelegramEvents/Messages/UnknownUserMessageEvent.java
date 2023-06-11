package me.gnevilkoko.TelegramEvents.Messages;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import me.gnevilkoko.BotNode;
import me.gnevilkoko.Databases.MySQL.ConnectorMySQL;
import me.gnevilkoko.Databases.MySQL.Queries.GetUserQuery;
import me.gnevilkoko.Databases.MySQL.Queries.UpdateUserTempDataQuery;
import me.gnevilkoko.Enums.ClickedButton;
import me.gnevilkoko.Starter;
import me.gnevilkoko.TelegramEvents.Event;
import me.gnevilkoko.Utils.LocaleFile;
import me.gnevilkoko.Utils.User;

public class UnknownUserMessageEvent extends Event {
    private Message message;
    private static TelegramBot BOT = BotNode.BOT;
    private static ConnectorMySQL DATABASE = BotNode.DATABASE;

    /**
     * Trigger when user write unknown message, which not processing by bot
     * @param message - received message
     */
    public UnknownUserMessageEvent(Message message) {
        this.message = message;

        onTrigger();
    }

    @Override
    protected void onTrigger() {
        long chatId = message.chat().id();
        long userId = message.from().id();

        GetUserQuery getUserQuery = new GetUserQuery(userId);
        DATABASE.executeQuery(getUserQuery);
        User user = getUserQuery.getUser();

        SendMessage sendMessage = new SendMessage(chatId, LocaleFile.getString(user.getLanguage(), "DefaultMessage"))
                .replyMarkup(new InlineKeyboardMarkup(
                        new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "SelectShopButton"))
                                .callbackData(ClickedButton.SELECT_SHOP.name()))
                        .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "ChangeLanguageButton"))
                                .callbackData(ClickedButton.CHANGE_LANGUAGE.name()))
                        .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "StatisticButton"))
                                .callbackData(ClickedButton.STATISTIC_BUTTON.name())));
        BOT.execute(sendMessage);
        DATABASE.executeQuery(new UpdateUserTempDataQuery(user.getUserId(), ""));

        Starter.log("Get unknown message from user ID: "+userId);
    }
}
