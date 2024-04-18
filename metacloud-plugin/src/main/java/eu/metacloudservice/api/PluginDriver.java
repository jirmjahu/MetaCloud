package eu.metacloudservice.api;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PluginDriver {

    private final List<PluginCommand> commands;

    @Getter
    private static PluginDriver instance;

    public PluginDriver() {
        instance = this;
        commands = new ArrayList<>();
    }

    public void register(PluginCommand command) {
        commands.add(command);
    }

    public PluginCommand getCommand(String name) {
        return getCommands().stream().filter(command -> command.getCommand().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
