package eu.metacloudservice.api;

import com.velocitypowered.api.proxy.Player;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

@Getter
public abstract class PluginCommand {

    private final String command;

    private final String description;

    public PluginCommand() {
        var annotation = getClass().getAnnotation(PluginCommandInfo.class);
        this.command = annotation.command();
        this.description = annotation.description();
    }

    public abstract void performCommand(PluginCommand command, ProxiedPlayer proxiedPlayer, Player veloPlayer, org.bukkit.entity.Player bukkitPlayer, String[] args);

    public abstract List<String> tabComplete(String[] args);

}
