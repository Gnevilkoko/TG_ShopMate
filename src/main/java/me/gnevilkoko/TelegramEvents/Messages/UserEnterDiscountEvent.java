package me.gnevilkoko.TelegramEvents.Messages;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import me.gnevilkoko.BotNode;
import me.gnevilkoko.Databases.MySQL.ConnectorMySQL;
import me.gnevilkoko.Databases.MySQL.Queries.GetShopByIdQuery;
import me.gnevilkoko.Databases.MySQL.Queries.GetUserQuery;
import me.gnevilkoko.Databases.MySQL.Queries.UpdateUserTempDataQuery;
import me.gnevilkoko.Enums.Back;
import me.gnevilkoko.Enums.ClickedButton;
import me.gnevilkoko.TelegramEvents.Event;
import me.gnevilkoko.Utils.*;

public class UserEnterDiscountEvent extends Event {
    private static TelegramBot BOT = BotNode.BOT;
    private static ConnectorMySQL DATABASE = BotNode.DATABASE;
    private Message message;

    /**
     * Trigger when user enter discount for buy list
     * @param message - user message
     */
    public UserEnterDiscountEvent(Message message) {
        this.message = message;

        onTrigger();
    }
    @Override
    protected void onTrigger() throws NumberFormatException{
        GetUserQuery getUserQuery = new GetUserQuery(message.from().id());
        DATABASE.executeQuery(getUserQuery);
        User user = getUserQuery.getUser();

        double discount = Double.parseDouble(message.text());
        BuyList list = Storage.usersBuyList.get(user.getUserId());

        list.setConsiderPrices(true);
        list.setDiscount(discount);
        Storage.usersBuyList.put(user.getUserId(), list);

        DATABASE.executeQuery(new UpdateUserTempDataQuery(user.getUserId(), ""));

        SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "StartShoppingMessage"))
                .replyMarkup(new InlineKeyboardMarkup(
                        new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "StartShoppingButton"))
                                .callbackData(ClickedButton.START_SHOPPING.name()))
                        .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.SHOP_INFO + " "+ list.getShopId())))
                .parseMode(ParseMode.HTML);
        BOT.execute(sendMessage);
    }
}
