package eu.metacloudservice.bukkit.command.impl;

import com.velocitypowered.api.proxy.Player;
import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.api.PluginCommand;
import eu.metacloudservice.api.PluginCommandInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

@PluginCommandInfo(command = "shutdown", description = "/service shutdown")
public class ShutdownCommand extends PluginCommand {

    @Override
    public void performCommand(PluginCommand command, ProxiedPlayer proxiedPlayer, Player veloPlayer, org.bukkit.entity.Player bukkitPlayer, String[] args) {
        CloudAPI.getInstance().getServicePool().getService(CloudAPI.getInstance().getCurrentService().getService()).shutdown();
    }

    @Override
    public List<String> tabComplete(String[] args) {
        return new ArrayList<>();
    }
}
