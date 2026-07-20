package ru.zipprey.eventify.bot.message.caller;

import ru.zipprey.eventify.bot.model.dto.EventInfo;

import java.util.List;

public interface EventServiceCaller {

    List<EventInfo> findByIds(List<Long> ids);
}
