package eu.metacloudservice.bukkit.command;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.Driver;
import eu.metacloudservice.api.PluginCommand;
import eu.metacloudservice.api.PluginDriver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("metacloud.command.service") || player.hasPermission("metacloud.command.*")) {
                player.sendMessage("§8▷ §7The network uses §bMetaCloud§8 [§a" + Driver.getInstance().getMessageStorage().version + "§8]");
                player.sendMessage("§8▷ §fhttps://metacloudservice.eu/");
                return false;
            }

            if (args.length == 0) {
                sendHelp(player);
                return false;
            }

            var pluginCommand = PluginDriver.getInstance().getCommand(args[0]);
            if (pluginCommand == null) {
                sendHelp(player);
                return false;
            }
            var argsUpdate = Arrays.copyOfRange(args, 1, args.length);
            pluginCommand.performCommand(pluginCommand, null, null, player, argsUpdate);
            return true;
        }
        return false;
    }

    private void sendHelp(Player player) {
        PluginDriver.getInstance().getCommands().forEach(pluginCommand -> {
            player.sendMessage(CloudAPI.getInstance().getMessages().getMessages().get("prefix") + pluginCommand.getDescription());
        });
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            PluginDriver.getInstance().getCommands().forEach(pluginCommand -> {
                suggestions.add(pluginCommand.getCommand());
            });
            return suggestions;
        }

        if (args.length >= 2) {
            PluginCommand pluginCommand = PluginDriver.getInstance().getCommand(args[0]);
            if (pluginCommand != null) {
                String[] refreshedArguments = Arrays.copyOfRange(args, 1, args.length);
                suggestions.addAll(pluginCommand.tabComplete(refreshedArguments));
                return suggestions;
            }
        }

        return suggestions;
    }
}

