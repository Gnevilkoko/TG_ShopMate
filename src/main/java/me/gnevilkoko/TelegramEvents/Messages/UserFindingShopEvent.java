package me.gnevilkoko.TelegramEvents.Messages;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import info.debatty.java.stringsimilarity.Damerau;
import me.gnevilkoko.BotNode;
import me.gnevilkoko.Databases.MySQL.ConnectorMySQL;
import me.gnevilkoko.Databases.MySQL.Queries.GetShopListQuery;
import me.gnevilkoko.Databases.MySQL.Queries.GetUserQuery;
import me.gnevilkoko.Enums.Back;
import me.gnevilkoko.Enums.ClickedButton;
import me.gnevilkoko.Exceptions.SimilarShopNotFoundException;
import me.gnevilkoko.Starter;
import me.gnevilkoko.TelegramEvents.Event;
import me.gnevilkoko.Utils.LocaleFile;
import me.gnevilkoko.Utils.MainMethods;
import me.gnevilkoko.Utils.Shop;
import me.gnevilkoko.Utils.User;

import java.util.ArrayList;

public class UserFindingShopEvent extends Event {
    private static TelegramBot BOT = BotNode.BOT;
    private static ConnectorMySQL DATABASE = BotNode.DATABASE;
    private Message message;

    /**
     * Trigger when user enter shop name and trying to
     * find his shop from database
     * @param message - user message (shop name)
     */
    public UserFindingShopEvent(Message message) {
        this.message = message;

        onTrigger();
    }

    @Override
    protected void onTrigger() {
        GetShopListQuery getShopListQuery = new GetShopListQuery();
        DATABASE.executeQuery(getShopListQuery);

        GetUserQuery getUserQuery = new GetUserQuery(message.from().id());
        DATABASE.executeQuery(getUserQuery);
        User user = getUserQuery.getUser();

        ArrayList<Shop> shops = getShopListQuery.getShops();
        String findShopName = message.text();

        if(findShopName.equalsIgnoreCase("")){
            SendMessage sendMessage = new SendMessage(user.getUserId(), LocaleFile.getString(user.getLanguage(), "ErrorWhileShopFinding"))
                    .replyMarkup(new InlineKeyboardMarkup(
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "SelectShopButton"))
                                    .callbackData(ClickedButton.SELECT_SHOP.name()))
                            .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "ChangeLanguageButton"))
                                    .callbackData(ClickedButton.CHANGE_LANGUAGE.name()))
                            .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "StatisticButton"))
                                    .callbackData(ClickedButton.STATISTIC_BUTTON.name())));
            BOT.execute(sendMessage);
            return;
        }

        Shop selectedShop = getShopByName(findShopName, shops);
        if(selectedShop != null){
            //100% find shop
            SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "FoundedShop")
                    .replace("{shop_name}", selectedShop.getName())
                    .replace("{rating_stars}", MainMethods.getStarsFromRating(selectedShop.getAvgRating()))
                    .replace("{rating_numbers}", ""+selectedShop.getAvgRating()))
                    .replyMarkup(new InlineKeyboardMarkup(
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "ShopOpinionsButton"))
                                    .callbackData(ClickedButton.SHOW_SHOP_OPINIONS.name()+" "+selectedShop.getId()))
                            .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "CreateBuyListButton"))
                                    .callbackData(ClickedButton.CREATE_BUY_LIST.name()+" "+selectedShop.getId()))
                            .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                    .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU)))
                    .parseMode(ParseMode.HTML);
            BOT.execute(sendMessage);
            Starter.log("User with ID: "+user.getUserId()+" founded 100% shop "+selectedShop.getName());
        } else {
            //if similar to any -> offer this shop or offer create
            try {
                Shop similarShop = MainMethods.getSimilarShop(findShopName, shops);

                SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "FoundedSimilarShop")
                        .replace("{user_shop}", findShopName)
                        .replace("{similar_shop}", similarShop.getName()))
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "SelectSimilarShopButton"))
                                        .callbackData(ClickedButton.SELECT_SIMILAR_SHOP.name()+" "+similarShop.getName()))
                                .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "ContinueAsDefinedShopButton"))
                                        .callbackData(ClickedButton.SELECT_SHOP_AS_DEFINED.name()+" "+findShopName))
                                .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                        .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU)))
                        .parseMode(ParseMode.HTML);
                BOT.execute(sendMessage);
                Starter.log("User with ID: "+user.getUserId()+"not founded shop, but offered similar -> "+similarShop.getName());
            } catch (SimilarShopNotFoundException e) {
                SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "NotFoundedShop")
                        .replace("{user_shop}", findShopName))
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "ContinueAsDefinedShopButton"))
                                        .callbackData(ClickedButton.SELECT_SHOP_AS_DEFINED.name()+" "+findShopName))
                                .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                        .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU)))
                        .parseMode(ParseMode.HTML);
                BOT.execute(sendMessage);
                Starter.log("User with ID: "+user.getUserId()+"not founded any shop and offered to create -> "+findShopName);
            }
            //if not found -> offer create
        }
    }


    private Shop getShopByName(String shopName, ArrayList<Shop> shops){
        for(Shop shop : shops){
            if(shop.getName().equalsIgnoreCase(shopName))
                return shop;
        }
        return null;
    }
}
