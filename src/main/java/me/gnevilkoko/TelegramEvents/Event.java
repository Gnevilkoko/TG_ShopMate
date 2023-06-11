package me.gnevilkoko.TelegramEvents;

import me.gnevilkoko.Exceptions.PriceCantBeNegative;
import me.gnevilkoko.Exceptions.TooBigOpinionMessageException;

public abstract class Event {

    protected abstract void onTrigger() throws PriceCantBeNegative, TooBigOpinionMessageException;
}
