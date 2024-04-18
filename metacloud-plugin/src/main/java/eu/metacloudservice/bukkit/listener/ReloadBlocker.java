package eu.metacloudservice.bukkit.listener;

import eu.metacloudservice.CloudAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ReloadBlocker implements Listener {


    @EventHandler
    public void handle(PlayerCommandPreprocessEvent event) {
        var player = event.getPlayer();
        var message = event.getMessage().toLowerCase();
        var prefix = CloudAPI.getInstance().getMessages().getMessages().get("prefix");

        if (!message.equals("/rl") && !message.equals("/reload") && !message.equals("/rl confirm") && !message.equals("/reload confirm")) {
            return;
        }

        if (!player.hasPermission("bukkit.command.reload")) {
            player.sendMessage(prefix + "§cYou dont have permission to do that!");
            return;
        }

        event.setCancelled(true);
        player.sendMessage(prefix + "§cCloud-Servers cannot be reloaded!");
    }
}

