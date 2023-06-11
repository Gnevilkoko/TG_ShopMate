package me.gnevilkoko.TelegramEvents.Messages;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import me.gnevilkoko.BotNode;
import me.gnevilkoko.Databases.MySQL.Queries.CreateUserQuery;
import me.gnevilkoko.Enums.ChatStatus;
import me.gnevilkoko.Enums.ClickedButton;
import me.gnevilkoko.TelegramEvents.Event;
import me.gnevilkoko.Utils.LocaleFile;
import me.gnevilkoko.Utils.User;

public class UnknownUserWriteMessageEvent extends Event {
    private Message message;
    private static TelegramBot BOT = BotNode.BOT;

    /**
     * Trigger when unknown user trying to use the bot
     * @param message - received message
     */
    public UnknownUserWriteMessageEvent(Message message) {
        this.message = message;

        onTrigger();
    }

    @Override
    protected void onTrigger() {
        com.pengrad.telegrambot.model.User from = message.from();

        String lang = from.languageCode();
        if(!LocaleFile.getLocales().contains(lang)){
            lang = "en";
        }

        User user = new User(
                from.id(),
                lang,
                ChatStatus.NONE,
                ""
        );

        BotNode.DATABASE.executeQuery(new CreateUserQuery(user));

        SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "FirstHelloMessage"))
                .replyMarkup(new InlineKeyboardMarkup(
                        new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "SelectShopButton"))
                                .callbackData(ClickedButton.SELECT_SHOP.name()))
                        .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "ChangeLanguageButton"))
                                .callbackData(ClickedButton.CHANGE_LANGUAGE.name()))
                        .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "StatisticButton"))
                                .callbackData(ClickedButton.STATISTIC_BUTTON.name())));
        BOT.execute(sendMessage);
    }
}
