package me.gnevilkoko.TelegramEvents.Messages;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import me.gnevilkoko.BotNode;
import me.gnevilkoko.Databases.MySQL.ConnectorMySQL;
import me.gnevilkoko.Databases.MySQL.Queries.GetUserQuery;
import me.gnevilkoko.Exceptions.PriceCantBeNegative;
import me.gnevilkoko.TelegramEvents.Event;
import me.gnevilkoko.Utils.*;

import java.util.ArrayList;

public class UserEnterProductPriceEvent extends Event {
    private static TelegramBot BOT = BotNode.BOT;
    private static ConnectorMySQL DATABASE = BotNode.DATABASE;
    private Message message;

    /**
     * Trigger when user enter price for product from buy list
     * @param message - user message
     */
    public UserEnterProductPriceEvent(Message message) throws PriceCantBeNegative {
        this.message = message;

        onTrigger();
    }
    @Override
    protected void onTrigger() throws NumberFormatException, PriceCantBeNegative {
        GetUserQuery getUserQuery = new GetUserQuery(message.from().id());
        DATABASE.executeQuery(getUserQuery);
        User user = getUserQuery.getUser();

        int productId = Integer.parseInt(user.getTempData());
        double price = Double.parseDouble(message.text());

        if(price < 0){
            throw new PriceCantBeNegative();
        }

        BuyList buyList = Storage.usersBuyList.get(user.getUserId());
        ArrayList<Product> products = buyList.getProductList();
        Product product = products.get(productId);
        product.setPrice(price);
        products.set(productId, product);
        buyList.setProductList(products);
        Storage.usersBuyList.put(user.getUserId(), buyList);

        SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "ShoppingMessage"))
                .parseMode(ParseMode.HTML)
                .replyMarkup(Storage.usersBackToBuy.get(user.getUserId()));
        BOT.execute(sendMessage);
    }
}
