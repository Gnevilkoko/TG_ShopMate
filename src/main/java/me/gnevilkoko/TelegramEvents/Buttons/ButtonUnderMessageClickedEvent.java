package me.gnevilkoko.TelegramEvents.Buttons;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import me.gnevilkoko.BotNode;
import me.gnevilkoko.Databases.MySQL.ConnectorMySQL;
import me.gnevilkoko.Databases.MySQL.Queries.*;
import me.gnevilkoko.Enums.Back;
import me.gnevilkoko.Enums.ChatStatus;
import me.gnevilkoko.Enums.ClickedButton;
import me.gnevilkoko.Enums.ProductStatus;
import me.gnevilkoko.Starter;
import me.gnevilkoko.TelegramEvents.Event;
import me.gnevilkoko.Utils.*;

import java.util.ArrayList;

public class ButtonUnderMessageClickedEvent extends Event {
    private CallbackQuery callbackQuery;
    private static TelegramBot BOT = BotNode.BOT;
    private static ConnectorMySQL DATABASE = BotNode.DATABASE;

    /**
     * Trigger when user click on any button under message
     * @param callbackQuery - callback info about message & button
     */
    public ButtonUnderMessageClickedEvent(CallbackQuery callbackQuery) {
        this.callbackQuery = callbackQuery;

        onTrigger();
    }

    @Override
    protected void onTrigger() {
        String[] data = callbackQuery.data().split(" ");
        ClickedButton clickedButton = ClickedButton.valueOf(data[0]);
        //data[0] - always is info about clicked button

        Message message = callbackQuery.message();

        GetUserQuery getUserQuery = new GetUserQuery(callbackQuery.from().id());
        DATABASE.executeQuery(getUserQuery);
        User user = getUserQuery.getUser();

        switch (clickedButton){
            case BACK_BUTTON -> {
                Back backTo = Back.valueOf(data[1]);
                //data[1] here is always back to

                switch (backTo){
                    case MAIN_MENU -> {
                        EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "DefaultMessage"))
                                .replyMarkup(new InlineKeyboardMarkup(
                                        new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "SelectShopButton"))
                                                .callbackData(ClickedButton.SELECT_SHOP.name()))
                                        .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "ChangeLanguageButton"))
                                                .callbackData(ClickedButton.CHANGE_LANGUAGE.name()))
                                        .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "StatisticButton"))
                                                .callbackData(ClickedButton.STATISTIC_BUTTON.name())));
                        BOT.execute(editMessageText);
                    }
                    case SHOP_INFO -> {
                        long shopId = Long.parseLong(data[2]);
                        GetShopByIdQuery getShopByIdQuery = new GetShopByIdQuery(shopId);
                        DATABASE.executeQuery(getShopByIdQuery);
                        Shop selectedShop = getShopByIdQuery.getShop();
                        //data[1] is always selected shop ID

                        EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "FoundedShop")
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
                        BOT.execute(editMessageText);
                    }
                }
            }
            case SELECT_SHOP -> {
                EditMessageText editMessageText = new EditMessageText(
                        message.chat().id(),
                        message.messageId(),
                        LocaleFile.getString(user.getLanguage(), "SelectShopMessage")
                ).parseMode(ParseMode.HTML)
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                        .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU.name())
                        ));
                BOT.execute(editMessageText);

                DATABASE.executeQuery(new UpdateChatStatusQuery(ChatStatus.ENTERING_SHOP_NAME, user.getUserId()));
            }
            case SELECT_SIMILAR_SHOP -> {
                String temp = data[1];
                GetShopByNameQuery getShopByNameQuery = new GetShopByNameQuery(temp);
                DATABASE.executeQuery(getShopByNameQuery);
                Shop selectedShop = getShopByNameQuery.getShop();
                //data[1] is always selected shop NAME

                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "FoundedShop")
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
                BOT.execute(editMessageText);
            }
            case SELECT_SHOP_AS_DEFINED -> {
                String shopName = data[1];

                DATABASE.executeQuery(new CreateShopQuery(shopName));
                GetShopByNameQuery getShopByNameQuery = new GetShopByNameQuery(shopName);
                DATABASE.executeQuery(getShopByNameQuery);
                Shop selectedShop = getShopByNameQuery.getShop();

                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "FoundedShop")
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
                BOT.execute(editMessageText);

                DATABASE.executeQuery(new UpdateChatStatusQuery(ChatStatus.ENTERING_SHOP_NAME, user.getUserId()));
            }

            case SHOW_SHOP_OPINIONS -> {
                long shopId = Long.parseLong(data[1]);
                //data[1] is always selected shop ID

                GetMaxOpinionIdQuery getMaxOpinionIdQuery = new GetMaxOpinionIdQuery();
                DATABASE.executeQuery(getMaxOpinionIdQuery);
                long maxId = getMaxOpinionIdQuery.getMaxId();

                GetOpinionsQuery getOpinionsQuery = new GetOpinionsQuery(5, maxId+1, shopId);
                DATABASE.executeQuery(getOpinionsQuery);
                ArrayList<Opinion> opinions = getOpinionsQuery.getOpinions();

                if(opinions.size() != 0){
                    StringBuilder sendOpinions = new StringBuilder();
                    for(Opinion opinion : opinions){
                        sendOpinions.append("☆ <b>Оценка: ").append(opinion.getRating()).append("</b>\n")
                                .append("‣ Отзыв: ").append(opinion.getText())
                                .append("\n\n");
                    }

                    EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "FoundedOpinions")
                            .replace("{opinions}", sendOpinions.toString()))
                            .replyMarkup(new InlineKeyboardMarkup(
                                    new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "MoreShopOpinions"))
                                            .callbackData(ClickedButton.MORE_OPINIONS.name()+" "+shopId+" "+opinions.get(opinions.size()-1).getId()))
                                    .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                            .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.SHOP_INFO + " "+shopId))
                            ).parseMode(ParseMode.HTML);
                    BOT.execute(editMessageText);
                } else {
                    EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "NotFoundedOpinions"))
                            .replyMarkup(new InlineKeyboardMarkup(
                                    new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                            .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.SHOP_INFO + " "+shopId))
                            ).parseMode(ParseMode.HTML);
                    BOT.execute(editMessageText);
                }
            }
            case MORE_OPINIONS -> {
                long shopId = Long.parseLong(data[1]);
                long lastOpinionId = Long.parseLong(data[2]);
                //data[1] is always selected shop ID

                GetOpinionsQuery getOpinionsQuery = new GetOpinionsQuery(5, lastOpinionId, shopId);
                DATABASE.executeQuery(getOpinionsQuery);
                ArrayList<Opinion> opinions = getOpinionsQuery.getOpinions();

                if(opinions.size() != 0){
                    StringBuilder sendOpinions = new StringBuilder();
                    for(Opinion opinion : opinions){
                        sendOpinions.append("☆ <b>Оценка: ").append(opinion.getRating()).append("</b>\n")
                                .append("‣ Отзыв: ").append(opinion.getText())
                                .append("\n\n");
                    }

                    EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "FoundedOpinions")
                            .replace("{opinions}", sendOpinions.toString()))
                            .replyMarkup(new InlineKeyboardMarkup(
                                    new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "MoreShopOpinions"))
                                            .callbackData(ClickedButton.MORE_OPINIONS.name()+" "+shopId+" "+opinions.get(opinions.size()-1).getId()))
                                    .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                            .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.SHOP_INFO + " "+shopId))
                            ).parseMode(ParseMode.HTML);
                    BOT.execute(editMessageText);
                } else {
                    EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "OpinionsEnded"))
                            .replyMarkup(new InlineKeyboardMarkup(
                                    new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                            .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.SHOP_INFO + " "+shopId))
                            ).parseMode(ParseMode.HTML);
                    BOT.execute(editMessageText);
                }
            }

            case CREATE_BUY_LIST -> {
                long shopId = Long.parseLong(data[1]);

                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "CreateBuyList"))
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "CountPricesButton")+" ⬜")
                                        .callbackData(ClickedButton.COUNT_PRICES.name()+" "+shopId+" false"))
                                .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                        .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.SHOP_INFO + " "+shopId))
                        ).parseMode(ParseMode.HTML);
                BOT.execute(editMessageText);

                DATABASE.executeQuery(new UpdateChatStatusQuery(ChatStatus.ENTERING_BUY_LIST, user.getUserId()));
                DATABASE.executeQuery(new UpdateUserTempDataQuery(user.getUserId(), shopId+" false"));
            }
            case COUNT_PRICES -> {
                long shopId = Long.parseLong(data[1]);
                boolean currentState = Boolean.parseBoolean(data[2]);
                String emoji = "";

                if(currentState){
                    emoji = "⬜";
                } else {
                    emoji = "✅";
                }

                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "CreateBuyList"))
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "CountPricesButton")+" "+emoji)
                                        .callbackData(ClickedButton.COUNT_PRICES.name()+" "+shopId+" "+!currentState))
                                .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                        .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.SHOP_INFO + " "+shopId))
                        ).parseMode(ParseMode.HTML);
                BOT.execute(editMessageText);

                DATABASE.executeQuery(new UpdateChatStatusQuery(ChatStatus.ENTERING_BUY_LIST, user.getUserId()));
                DATABASE.executeQuery(new UpdateUserTempDataQuery(user.getUserId(), shopId+" "+!currentState));
            }
            case START_SHOPPING -> {
                BuyList buyList = Storage.usersBuyList.get(user.getUserId());
                buyList.startShopping();
                Storage.usersBuyList.put(user.getUserId(), buyList);

                ArrayList<Product> products = buyList.getProductList();

                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "ShoppingMessage"))
                        .parseMode(ParseMode.HTML);

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                if(products.size() > 10){
                    for(int i = 0; i < 10; i++){
                        String action = "";
                        if(products.get(i).getStatus() == ProductStatus.CHECKED){
                            action = "✅";
                        } else if(products.get(i).getStatus() == ProductStatus.DELETED){
                            action = "❌";
                        }
                        inlineKeyboardMarkup.addRow(new InlineKeyboardButton(products.get(i).getProductName()+" "+action)
                                .callbackData(ClickedButton.PRODUCT_MARKING.name()+" "+ products.get(i).getStatus()+" "+i));
                    }
                    inlineKeyboardMarkup.addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "EndBuyingButton"))
                                    .callbackData(ClickedButton.END_BUYING.name()),
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoNextListButton"))
                                    .callbackData(ClickedButton.GO_NEXT_LIST.name()+" "+10));
                } else {
                    for(int i = 0; i<products.size(); i++){
                        String action = "";
                        if(products.get(i).getStatus() == ProductStatus.CHECKED){
                            action = "✅";
                        } else if(products.get(i).getStatus() == ProductStatus.DELETED){
                            action = "❌";
                        }
                        inlineKeyboardMarkup.addRow(new InlineKeyboardButton(products.get(i).getProductName()+" "+action)
                                .callbackData(ClickedButton.PRODUCT_MARKING.name()+" "+ products.get(i).getStatus()+" "+i));
                    }
                    inlineKeyboardMarkup.addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "EndBuyingButton"))
                                    .callbackData(ClickedButton.END_BUYING.name())
                    );
                }

                editMessageText.replyMarkup(inlineKeyboardMarkup);
                BOT.execute(editMessageText);
            }
            case GO_NEXT_LIST -> {
                BuyList buyList = Storage.usersBuyList.get(user.getUserId());
                ArrayList<Product> products = buyList.getProductList();
                int startPoint = Integer.parseInt(data[1]);
                int endPoint = startPoint+10; //including

                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "ShoppingMessage"))
                        .parseMode(ParseMode.HTML);

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                if(endPoint < products.size()){
                    for(int i = startPoint; i < endPoint; i++){
                        String action = "";
                        if(products.get(i).getStatus() == ProductStatus.CHECKED){
                            action = "✅";
                        } else if(products.get(i).getStatus() == ProductStatus.DELETED){
                            action = "❌";
                        }
                        inlineKeyboardMarkup.addRow(new InlineKeyboardButton(products.get(i).getProductName()+" "+action)
                                .callbackData(ClickedButton.PRODUCT_MARKING.name()+" "+ products.get(i).getStatus()+" "+i));
                    }

                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoBackListButton"))
                                    .callbackData(ClickedButton.GO_BACK_LIST.name()+" "+(startPoint-10)),
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "EndBuyingButton"))
                                    .callbackData(ClickedButton.END_BUYING.name()),
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoNextListButton"))
                                    .callbackData(ClickedButton.GO_NEXT_LIST.name()+" "+(startPoint+10))
                    );
                } else {
                    for(int i = startPoint; i < products.size(); i++){
                        String action = "";
                        if(products.get(i).getStatus() == ProductStatus.CHECKED){
                            action = "✅";
                        } else if(products.get(i).getStatus() == ProductStatus.DELETED){
                            action = "❌";
                        }
                        inlineKeyboardMarkup.addRow(new InlineKeyboardButton(products.get(i).getProductName()+" "+action)
                                .callbackData(ClickedButton.PRODUCT_MARKING.name()+" "+ products.get(i).getStatus()+" "+i));
                    }

                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoBackListButton"))
                                    .callbackData(ClickedButton.GO_BACK_LIST.name()+" "+(startPoint-10)),
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "EndBuyingButton"))
                                    .callbackData(ClickedButton.END_BUYING.name())
                    );
                }

                editMessageText.replyMarkup(inlineKeyboardMarkup);
                BOT.execute(editMessageText);
            }
            case GO_BACK_LIST -> {
                BuyList buyList = Storage.usersBuyList.get(user.getUserId());
                ArrayList<Product> products = buyList.getProductList();
                int startPoint = Integer.parseInt(data[1]);
                int endPoint = startPoint+10; //including

                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "ShoppingMessage"))
                        .parseMode(ParseMode.HTML);

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                if(startPoint > 0){
                    for(int i = startPoint; i < endPoint; i++){
                        String action = "";
                        if(products.get(i).getStatus() == ProductStatus.CHECKED){
                            action = "✅";
                        } else if(products.get(i).getStatus() == ProductStatus.DELETED){
                            action = "❌";
                        }
                        inlineKeyboardMarkup.addRow(new InlineKeyboardButton(products.get(i).getProductName()+" "+action)
                                .callbackData(ClickedButton.PRODUCT_MARKING.name()+" "+ products.get(i).getStatus()+" "+i));
                    }

                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoBackListButton"))
                                    .callbackData(ClickedButton.GO_BACK_LIST.name()+" "+(startPoint-10)),
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "EndBuyingButton"))
                                    .callbackData(ClickedButton.END_BUYING.name()),
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoNextListButton"))
                                    .callbackData(ClickedButton.GO_NEXT_LIST.name()+" "+(startPoint+10))
                    );
                } else {
                    for(int i = startPoint; i < endPoint; i++){
                        String action = "";
                        if(products.get(i).getStatus() == ProductStatus.CHECKED){
                            action = "✅";
                        } else if(products.get(i).getStatus() == ProductStatus.DELETED){
                            action = "❌";
                        }
                        inlineKeyboardMarkup.addRow(new InlineKeyboardButton(products.get(i).getProductName()+" "+action)
                                .callbackData(ClickedButton.PRODUCT_MARKING.name()+" "+ products.get(i).getStatus()+" "+i));
                    }

                    inlineKeyboardMarkup.addRow(
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "EndBuyingButton"))
                                    .callbackData(ClickedButton.END_BUYING.name()),
                            new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoNextListButton"))
                                    .callbackData(ClickedButton.GO_NEXT_LIST.name()+" "+(startPoint+10))
                    );
                }

                editMessageText.replyMarkup(inlineKeyboardMarkup);
                BOT.execute(editMessageText);
            }
            case PRODUCT_MARKING -> {
                ProductStatus oldProductStatus = ProductStatus.valueOf(data[1]);
                ProductStatus newProductStatus;
                String action = "";

                if(oldProductStatus == ProductStatus.UNCHECKED){
                    newProductStatus = ProductStatus.CHECKED;
                    action = "✅";
                } else if(oldProductStatus == ProductStatus.CHECKED){
                    newProductStatus = ProductStatus.DELETED;
                    action = "❌";
                } else {
                    newProductStatus = ProductStatus.UNCHECKED;
                    action = "";
                }

                int productId = Integer.parseInt(data[2]);
                InlineKeyboardButton[][] keyboard = message.replyMarkup().inlineKeyboard();

                BuyList buyList = Storage.usersBuyList.get(user.getUserId());
                ArrayList<Product> products = buyList.getProductList();
                Product product = products.get(productId);
                product.setStatus(newProductStatus);
                products.set(productId, product);
                buyList.setProductList(products);
                Storage.usersBuyList.put(user.getUserId(), buyList);

                for(int i = 0; i < keyboard.length; i++) {
                    for(int j = 0; j < keyboard[i].length; j++){
                        InlineKeyboardButton button = keyboard[i][j];
                        String[] buttonData = button.callbackData().split(" ");
                        if(buttonData.length == 3){
                            int findProductId = Integer.parseInt(buttonData[2]);
                            if(findProductId == productId){
                                String text = button.text();
                                if(oldProductStatus == ProductStatus.CHECKED){
                                    text = text.substring(0, text.length()-2);
                                } else if(oldProductStatus == ProductStatus.DELETED){
                                    text = text.substring(0, text.length()-3);
                                }
                                InlineKeyboardButton newButton = new InlineKeyboardButton(text+" "+action)
                                        .callbackData(ClickedButton.PRODUCT_MARKING.name()+" "+newProductStatus.name()+" "+productId);
                                keyboard[i][j] = newButton;
                                continue;
                            }
                        }
                        keyboard[i][j] = button;
                    }
                }

                if(oldProductStatus == ProductStatus.UNCHECKED && Storage.usersBuyList.get(user.getUserId()).isConsiderPrices()){
                    Storage.usersBackToBuy.put(user.getUserId(), new InlineKeyboardMarkup(keyboard));
                    SendMessage sendMessage = new SendMessage(message.chat().id(), LocaleFile.getString(user.getLanguage(), "EnterPriceForProduct"));
                    BOT.execute(sendMessage);
                    DATABASE.executeQuery(new UpdateChatStatusQuery(ChatStatus.ENTERING_PRODUCT_PRICE, user.getUserId()));
                    DATABASE.executeQuery(new UpdateUserTempDataQuery(user.getUserId(), productId+""));
                } else {
                    EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), message.text())
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(new InlineKeyboardMarkup(keyboard));
                    BOT.execute(editMessageText);
                }
            }

            case END_BUYING -> {
                TransactionExistQuery transactionExistQuery = new TransactionExistQuery(Storage.usersBuyList.get(user.getUserId()));
                DATABASE.executeQuery(transactionExistQuery);
                if(!transactionExistQuery.isExist()) {
                    DATABASE.executeQuery(new CreateTransactionQuery(Storage.usersBuyList.get(user.getUserId())));
                }

                UserOpinionExistQuery opinionExistQuery = new UserOpinionExistQuery(user.getUserId(), Storage.usersBuyList.get(user.getUserId()).getShopId());
                DATABASE.executeQuery(opinionExistQuery);
                String messageText = LocaleFile.getString(user.getLanguage(), "ShoppingEndedMessage")
                        .replace("{time_spended}", MainMethods.getElapsedTime(Storage.usersBuyList.get(user.getUserId()).getStarted(), System.currentTimeMillis()/1000, user.getLanguage()))
                        .replace("{money_spended}", String.valueOf(Storage.usersBuyList.get(user.getUserId()).getSpendedMoney()));
                if(opinionExistQuery.isExist()){
                    EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), messageText)
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(new InlineKeyboardMarkup(
                                    new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoToMenu"))
                                            .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU))
                            );
                    BOT.execute(editMessageText);
                } else {
                    messageText += "\n\n";
                    messageText += LocaleFile.getString(user.getLanguage(), "OpinionAllowedInformation");

                    EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), messageText)
                            .replyMarkup(new InlineKeyboardMarkup(
                                    new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "MakeOpinion"))
                                            .callbackData(ClickedButton.MAKE_RATING.name()))
                                    .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoToMenu"))
                                            .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU)));
                    BOT.execute(editMessageText);
                }
            }

            case MAKE_RATING -> {
                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "MakeRatingMessage"))
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton("1").callbackData(ClickedButton.RATING_NUMBER.name()+" 1"),
                                new InlineKeyboardButton("2").callbackData(ClickedButton.RATING_NUMBER.name()+" 2"),
                                new InlineKeyboardButton("3").callbackData(ClickedButton.RATING_NUMBER.name()+" 3"),
                                new InlineKeyboardButton("4").callbackData(ClickedButton.RATING_NUMBER.name()+" 4"),
                                new InlineKeyboardButton("5").callbackData(ClickedButton.RATING_NUMBER.name()+" 5"))
                                .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoToMenu"))
                                        .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU)));
                BOT.execute(editMessageText);
            }
            case RATING_NUMBER -> {
                double rating = Double.parseDouble(data[1]);
                DATABASE.executeQuery(new UpdateChatStatusQuery(ChatStatus.MAKE_OPINION, user.getUserId()));
                DATABASE.executeQuery(new UpdateUserTempDataQuery(user.getUserId(), ""+rating));

                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "MakingOpinionMessage"))
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoToMenu"))
                                        .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU))
                        );
                BOT.execute(editMessageText);
            }
            case STATISTIC_BUTTON -> {
                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "SelectPeriodForStatisticMessage"))
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "ForTodayButton"))
                                        .callbackData(ClickedButton.GET_STATS.name()+" 1"),
                                new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "ForWeekButton"))
                                        .callbackData(ClickedButton.GET_STATS.name()+" 7"),
                                new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "ForMonthButton"))
                                        .callbackData(ClickedButton.GET_STATS.name()+" 31"))
                                .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "ForAllTimeButton"))
                                        .callbackData(ClickedButton.GET_STATS.name()+" -1"))
                                .addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoToMenu"))
                                        .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU))
                        );
                BOT.execute(editMessageText);
            }

            case GET_STATS -> {
                int days = Integer.parseInt(data[1]);

                GetUserStatisticQuery getUserStatisticQuery = new GetUserStatisticQuery(days, user.getUserId());
                DATABASE.executeQuery(getUserStatisticQuery);
                ArrayList<BuyStatistic> buyStatistics = getUserStatisticQuery.getStatistic();

                long spendedTime = 0;
                double spendedMoney = 0.0D;
                for(BuyStatistic stat : buyStatistics){
                    spendedTime = spendedTime + (stat.getEnded()-stat.getStarted());
                    spendedMoney = spendedMoney + stat.getSpendedMoney();
                }

                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "StatisticMessage")
                        .replace("{spended_time}", MainMethods.convertUnixTimeToTextTime(spendedTime, user.getLanguage()))
                        .replace("{spended_money}", spendedMoney+""))
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "GoToMenu"))
                                        .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU))
                        );
                BOT.execute(editMessageText);
            }
            case CHANGE_LANGUAGE -> {
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                for(String lang : LocaleFile.getLocales()){
                    markup.addRow(new InlineKeyboardButton(LocaleFile.getString(lang, "LanguageName"))
                            .callbackData(ClickedButton.SELECT_LANGUAGE.name()+" "+lang));
                }
                markup.addRow(new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                        .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU));

                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(user.getLanguage(), "ChangeLanguageMessage"))
                        .replyMarkup(markup)
                        .parseMode(ParseMode.HTML);
                BOT.execute(editMessageText);
            }

            case SELECT_LANGUAGE -> {
                String newLanguage = data[1];
                String oldLanguage = user.getLanguage();

                DATABASE.executeQuery(new UpdateUserLanguageQuery(user.getUserId(), newLanguage));
                EditMessageText editMessageText = new EditMessageText(message.chat().id(), message.messageId(), LocaleFile.getString(newLanguage, "ChangeLanguageOkayMessage")
                        .replace("{old_language}", LocaleFile.getString(oldLanguage, "LanguageName"))
                        .replace("{new_language}",  LocaleFile.getString(newLanguage, "LanguageName")))
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(new InlineKeyboardMarkup(
                                new InlineKeyboardButton(LocaleFile.getString(user.getLanguage(), "BackButton"))
                                        .callbackData(ClickedButton.BACK_BUTTON.name()+" "+ Back.MAIN_MENU))
                        );
                BOT.execute(editMessageText);
            }
        }

        if(user.getChatStatus() != ChatStatus.NONE && clickedButton != ClickedButton.COUNT_PRICES && clickedButton != ClickedButton.CREATE_BUY_LIST){
            DATABASE.executeQuery(new UpdateChatStatusQuery(ChatStatus.NONE, user.getUserId()));
        }

        Starter.log("User with ID: "+user.getUserId()+" click on button under message with name \""+clickedButton.name()+"\"");
    }
}
