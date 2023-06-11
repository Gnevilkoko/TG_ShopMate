package me.gnevilkoko.TelegramEvents.Messages;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import me.gnevilkoko.BotNode;
import me.gnevilkoko.Databases.MySQL.ConnectorMySQL;
import me.gnevilkoko.Databases.MySQL.Queries.GetShopByIdQuery;
import me.gnevilkoko.Databases.MySQL.Queries.GetUserQuery;
import me.gnevilkoko.Databases.MySQL.Queries.UpdateChatStatusQuery;
import me.gnevilkoko.Databases.MySQL.Queries.UpdateUserTempDataQuery;
import me.gnevilkoko.Enums.Back;
import me.gnevilkoko.Enums.ChatStatus;
import me.gnevilkoko.Enums.ClickedButton;
import me.gnevilkoko.Enums.ProductStatus;
import me.gnevilkoko.TelegramEvents.Event;
import me.gnevilkoko.Utils.*;

import java.util.ArrayList;
import java.util.Arrays;

public class UserCreatingBuyListEvent extends Event {

    private static TelegramBot BOT = BotNode.BOT;
    private static ConnectorMySQL DATABASE = BotNode.DATABASE;
    private Message message;

    /**
     * Trigger when user want create buy list
     * @param message - user message
     */
    public UserCreatingBuyListEvent(Message message) {
        this.message = message;

        onTrigger();
    }

    @Override
    protected void onTrigger() {
        String msg = message.text();
        GetUserQuery getUserQuery = new GetUserQuery(message.from().id());
        DATABASE.executeQuery(getUserQuery);
        User user = getUserQuery.getUser();

        GetShopByIdQuery getShopByIdQuery = new GetShopByIdQuery(Long.parseLong(user.getTempData().split(" ")[0]));
        DATABASE.executeQuery(getShopByIdQuery);
        Shop selectedShop = getShopByIdQuery.getShop();

        if(msg.toCharArray().length > 2048){
            SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "CreateBuyListError")
                    .replace("{shop_name}", selectedShop.getName())
                    .replace("{rating_stars}", MainMethods.getStarsFromRating(selectedShop.getAvgRating()))
                    .replace("{rating_numbers}", ""+selectedShop.getAvgRating()))
                    .replyMarkup(new InlineKeyboardMarkup(
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "CreateBuyListButton"))
                                    .callbackData(ClickedButton.CREATE_BUY_LIST.name()+" "+selectedShop.getId()))
                            .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                    .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.SHOP_INFO + " "+selectedShop.getId())))
                    .parseMode(ParseMode.HTML);
            BOT.execute(sendMessage);
        }

        String[] tempData = user.getTempData().split(" ");
        String[] products = message.text().split(",");
        ArrayList<String> productListString = new ArrayList<>(Arrays.asList(products));
        ArrayList<Product> productList = new ArrayList<>();
        productListString.forEach(product -> productList.add(new Product(product, ProductStatus.UNCHECKED)));

        long shopId = Long.parseLong(tempData[0]);
        boolean considerPrices = Boolean.parseBoolean(tempData[1]);

        BuyList buyList = new BuyList(user.getUserId(), productList, considerPrices, shopId);
        Storage.usersBuyList.put(user.getUserId(), buyList);

        if(considerPrices){
            DATABASE.executeQuery(new UpdateChatStatusQuery(ChatStatus.ENTERING_DISCOUNT, user.getUserId()));
            SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "DiscountSetForShop"))
                    .replyMarkup(new InlineKeyboardMarkup(
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                    .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.SHOP_INFO + " "+selectedShop.getId())))
                    .parseMode(ParseMode.HTML);
            BOT.execute(sendMessage);
        } else {
            SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "StartShoppingMessage"))
                    .replyMarkup(new InlineKeyboardMarkup(
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "StartShoppingButton"))
                                    .callbackData(ClickedButton.START_SHOPPING.name()))
                            .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                    .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.SHOP_INFO + " "+selectedShop.getId())))
                    .parseMode(ParseMode.HTML);
            BOT.execute(sendMessage);

            DATABASE.executeQuery(new UpdateUserTempDataQuery(user.getUserId(), ""));
            DATABASE.executeQuery(new UpdateChatStatusQuery(ChatStatus.NONE, user.getUserId()));
        }
    }
}
