package me.gnevilkoko.Utils;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.HashMap;

public class Storage {
    public static HashMap<Long, BuyList> usersBuyList = new HashMap<>();
    public static HashMap<Long, InlineKeyboardMarkup> usersBackToBuy = new HashMap<>();
}
