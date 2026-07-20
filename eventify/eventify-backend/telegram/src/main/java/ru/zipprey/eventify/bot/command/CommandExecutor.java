package ru.zipprey.eventify.bot.command;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CommandExecutor {

    private final Map<String, TelegramCommand> commands = new HashMap<>();

    public CommandExecutor(List<TelegramCommand> commandList) {
        commandList.forEach(c -> commands.put(c.getKey(), c));
    }

    public Optional<TelegramCommand> find(String text) {
        var key = text.strip().toLowerCase();
        var space = key.indexOf(' ');
        if (space != -1) {
            key = key.substring(0, space);
        }
        return Optional.ofNullable(commands.get(key));
    }

    public boolean tryExecute(long chatId, String text) {
        var parts = text.strip().split("\\s+");
        if (parts.length == 0) {
            return false;
        }
        var args = parts.length > 1
                ? Arrays.copyOfRange(parts, 1, parts.length)
                : new String[0];

        var command = find(parts[0]);
        if (command.isPresent()) {
            command.get().execute(chatId, args);
            return true;
        }
        return false;
    }
}
