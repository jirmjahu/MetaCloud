package eu.metacloudservice.subcommands;

import com.velocitypowered.api.proxy.Player;
import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.Driver;
import eu.metacloudservice.api.PluginCommand;
import eu.metacloudservice.api.PluginCommandInfo;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

@PluginCommandInfo(command = "version", description = "/cloud version")
public class VersionCommand extends PluginCommand {

    @Override
    public void performCommand(PluginCommand command, ProxiedPlayer proxiedPlayer, Player veloPlayer, org.bukkit.entity.Player bukkitPlayer, String[] args) {
        var messages = CloudAPI.getInstance().getMessages();
        var prefix = messages.getMessages().get("prefix").replace("&", "§");
        if (proxiedPlayer != null) {
            proxiedPlayer.sendMessage(prefix + "The cloud is currently running on version §8⯮ §f" + Driver.getInstance().getMessageStorage().version);
            return;
        }
        veloPlayer.sendMessage(Component.text(prefix + "The cloud is currently running on version §8⯮ §f" + Driver.getInstance().getMessageStorage().version));
    }


    @Override
    public List<String> tabComplete(String[] args) {
        return new ArrayList<>();
    }
}
