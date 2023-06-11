package me.gnevilkoko.TelegramEvents.Messages;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import me.gnevilkoko.BotNode;
import me.gnevilkoko.Databases.MySQL.ConnectorMySQL;
import me.gnevilkoko.Databases.MySQL.Queries.GetShopByIdQuery;
import me.gnevilkoko.Databases.MySQL.Queries.GetUserQuery;
import me.gnevilkoko.Databases.MySQL.Queries.UpdateChatStatusQuery;
import me.gnevilkoko.Enums.Back;
import me.gnevilkoko.Enums.ChatStatus;
import me.gnevilkoko.Enums.ClickedButton;
import me.gnevilkoko.Exceptions.PriceCantBeNegative;
import me.gnevilkoko.Exceptions.TooBigOpinionMessageException;
import me.gnevilkoko.Starter;
import me.gnevilkoko.TelegramEvents.Event;
import me.gnevilkoko.Utils.LocaleFile;
import me.gnevilkoko.Utils.Shop;
import me.gnevilkoko.Utils.User;

public class MessageReceivedEvent extends Event {
    private Message message;
    private static TelegramBot BOT = BotNode.BOT;
    private static ConnectorMySQL DATABASE = BotNode.DATABASE;

    /**
     * Trigger when bot receiving any message from user
     * @param message - received message
     */
    public MessageReceivedEvent(Message message) {
        this.message = message;

        onTrigger();
    }

    @Override
    protected void onTrigger() {
        GetUserQuery getUserQuery = new GetUserQuery(message.from().id());
        DATABASE.executeQuery(getUserQuery);
        User user = getUserQuery.getUser();
        ChatStatus chatStatus = user.getChatStatus();

        switch (chatStatus){
            case ENTERING_SHOP_NAME -> {
                new UserFindingShopEvent(message);
            }
            case ENTERING_BUY_LIST -> {
                new UserCreatingBuyListEvent(message);
            }
            case ENTERING_DISCOUNT -> {
                try {
                    new UserEnterDiscountEvent(message);
                } catch (NumberFormatException e){
                    GetShopByIdQuery getShopByIdQuery = new GetShopByIdQuery(Long.parseLong(user.getTempData().split(" ")[0]));
                    DATABASE.executeQuery(getShopByIdQuery);
                    Shop selectedShop = getShopByIdQuery.getShop();

                    SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "DiscountSetForShopError"))
                            .replyMarkup(new InlineKeyboardMarkup(
                                    new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                            .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.SHOP_INFO + " "+selectedShop.getId())))
                            .parseMode(ParseMode.HTML);
                    BOT.execute(sendMessage);
                    return;
                }
            }
            case ENTERING_PRODUCT_PRICE -> {
                try {
                    new UserEnterProductPriceEvent(message);
                } catch (NumberFormatException e){
                    SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "EnterPriceForProductError"))
                            .parseMode(ParseMode.HTML);
                    BOT.execute(sendMessage);
                    return;
                } catch (PriceCantBeNegative e){
                    SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "EnterPriceForProductNegativeError"))
                            .parseMode(ParseMode.HTML);
                    BOT.execute(sendMessage);
                    return;
                }
            }

            case MAKE_OPINION -> {
                try {
                    new UserMakingOpinionEvent(message);
                } catch (TooBigOpinionMessageException e) {
                    SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "MakingOpinionMessageTooBig"));
                    BOT.execute(sendMessage);
                    return;
                }
            }
            default -> {
                new UnknownUserMessageEvent(message);
            }
        }

        if(user.getChatStatus() != ChatStatus.NONE && user.getChatStatus() != ChatStatus.ENTERING_BUY_LIST){
            DATABASE.executeQuery(new UpdateChatStatusQuery(ChatStatus.NONE, user.getUserId()));
        }
        Starter.log("User with ID: "+user.getUserId()+" wrote message \""+message.text()+"\" with chat status "+chatStatus.name());
    }
}
