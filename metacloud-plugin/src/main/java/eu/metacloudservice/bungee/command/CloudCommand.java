package eu.metacloudservice.bungee.command;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.Driver;
import eu.metacloudservice.api.PluginDriver;
import lombok.SneakyThrows;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CloudCommand extends Command implements TabExecutor {
    public CloudCommand(String name) {
        super(name);
    }

    @SneakyThrows
    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }
        var player = (ProxiedPlayer) commandSender;
        var messages = CloudAPI.getInstance().getMessages();
        var prefix = messages.getMessages().get("prefix").replace("&", "§");
        if (!player.hasPermission("metacloud.command.use") && !player.hasPermission("metacloud.command.*")) {
            player.sendMessage(prefix + "§8▷ §7The network uses §bMetacloud§8 [§a" + Driver.getInstance().getMessageStorage().version + "§8]");
            player.sendMessage(prefix + "§8▷ §fhttps://metacloudservice.eu/");
            return;
        }

        if (args.length == 0) {
            sendHelp(player);
            return;
        }

        var pluginCommand = PluginDriver.getInstance().getCommand(args[0]);
        if (pluginCommand != null) {
            String[] refreshedArguments = Arrays.copyOfRange(args, 1, args.length);
            pluginCommand.performCommand(pluginCommand, player, null, null, refreshedArguments);
            return;
        }
        sendHelp(player);
    }

    public void sendHelp(ProxiedPlayer player) {
        PluginDriver.getInstance().getCommands().forEach(proxyCommand -> {
            player.sendMessage(TextComponent.fromLegacyText(CloudAPI.getInstance().getMessages().getMessages().get("prefix").replace("&", "§") + proxyCommand.getDescription()));
        });
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            PluginDriver.getInstance().getCommands().forEach(proxyCommand -> {
                suggestions.add(proxyCommand.getCommand());
            });
            return suggestions;
        }

        if (PluginDriver.getInstance().getCommand(args[0]) == null) {
            return null;
        }

        if (args.length == 2) {
            suggestions.addAll(PluginDriver.getInstance().getCommand(args[0]).tabComplete(new String[]{}));
            return suggestions;
        }
        var refreshedArguments = Arrays.copyOfRange(args, 1, args.length);
        suggestions.addAll(PluginDriver.getInstance().getCommand(args[0]).tabComplete(refreshedArguments));
        var prefix = args[args.length - 1].toLowerCase();
        return suggestions;
        //return suggestions.stream().filter(suggestion -> suggestion.toLowerCase().startsWith(prefix)).collect(Collectors.toList());
    }
}
