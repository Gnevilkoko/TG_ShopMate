package me.gnevilkoko.TelegramEvents.Messages;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import me.gnevilkoko.BotNode;
import me.gnevilkoko.Databases.MySQL.ConnectorMySQL;
import me.gnevilkoko.Databases.MySQL.Queries.CreateUserOpinionQuery;
import me.gnevilkoko.Databases.MySQL.Queries.GetUserQuery;
import me.gnevilkoko.Databases.MySQL.Queries.UpdateRatingForShopQuery;
import me.gnevilkoko.Enums.Back;
import me.gnevilkoko.Enums.ClickedButton;
import me.gnevilkoko.Exceptions.TooBigOpinionMessageException;
import me.gnevilkoko.TelegramEvents.Event;
import me.gnevilkoko.Utils.LocaleFile;
import me.gnevilkoko.Utils.Storage;
import me.gnevilkoko.Utils.User;

public class UserMakingOpinionEvent extends Event {
    private static TelegramBot BOT = BotNode.BOT;
    private static ConnectorMySQL DATABASE = BotNode.DATABASE;
    private Message message;

    /**
     * Trigger when user enter his opinion
     * @param message - user message (shop name)
     */
    public UserMakingOpinionEvent(Message message) throws TooBigOpinionMessageException {
        this.message = message;

        onTrigger();
    }
    @Override
    protected void onTrigger() throws TooBigOpinionMessageException {
        if(message.text().toCharArray().length > 512)
            throw new TooBigOpinionMessageException();

        GetUserQuery getUserQuery = new GetUserQuery(message.from().id());
        DATABASE.executeQuery(getUserQuery);
        User user = getUserQuery.getUser();

        long shopId = Storage.usersBuyList.get(user.getUserId()).getShopId();
        double rating = Double.parseDouble(user.getTempData());
        DATABASE.executeQuery(new CreateUserOpinionQuery(shopId, user.getUserId(), message.text(), rating));
        DATABASE.executeQuery(new UpdateRatingForShopQuery(shopId));

        SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "MakingOpinionMessageSaved"))
                .replyMarkup(new InlineKeyboardMarkup(
                        new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoToMenu"))
                                .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU))
                );
        BOT.execute(sendMessage);
    }
}
