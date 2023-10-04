/*
 * this class is by RauchigesEtwas
 */

package eu.metacloudservice.serverside.bukkit.commands;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.Driver;
import eu.metacloudservice.config.SignLocation;
import eu.metacloudservice.groups.dummy.Group;
import eu.metacloudservice.serverside.bukkit.SignBootstrap;
import eu.metacloudservice.serverside.bukkit.entry.CloudSign;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SignCommand implements CommandExecutor, TabCompleter {

    private static final List<String> subCommands = new ArrayList<>();

    static {
        subCommands.add("create");
        subCommands.add("delete");
        subCommands.add("claenup");
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof  Player player){
            if (player.hasPermission("metacloud.command.cloudsign")){
                String prefix = CloudAPI.getInstance().getMessages().getPrefix().replace("&", "§");
                if (args.length == 0){
                    sendHelp(player);
                }else if  (args[0].equalsIgnoreCase("cleanup")) {
                            SignBootstrap.signDriver.getSignCache().forEach(cloudSign -> {
                        if (cloudSign.getSignPosition().getBlock().getState() instanceof Sign){

                        }else {
                            SignBootstrap.signDriver.unregisterSign(cloudSign.getSignUUID());
                        }
                    });
                    player.sendMessage(prefix + "all signs that are not correct have been deleted.");

                } else if (args[0].equalsIgnoreCase("create")){
                    if (args.length == 2){
                        Block targetBlock = player.getTargetBlock(null, 4);

                        if (targetBlock.getState() instanceof Sign){
                            Group targetGroup  = CloudAPI.getInstance().getGroupPool().getGroup(args[1]);
                            if (targetGroup != null){
                                if (!targetGroup.getGroupType().equalsIgnoreCase("PROXY")){
                                    CloudSign cloudSign =  SignBootstrap.signDriver.getSignFromLocation(targetBlock.getLocation());
                                    if (cloudSign == null){
                                        UUID uuid = UUID.randomUUID();
                                        SignBootstrap.signDriver.registerSign(new CloudSign(uuid, targetGroup.getGroup(), targetBlock.getLocation()));
                                        SignBootstrap.signsAPI.createSign(new SignLocation(uuid.toString(), targetBlock.getLocation().getX(), targetBlock.getLocation().getY(), targetBlock.getLocation().getZ(), targetBlock.getLocation().getWorld().getName(), targetGroup.getGroup()));
                                        player.sendMessage(prefix + "The sign was added.");
                                    }else {
                                        player.sendMessage(prefix + "There is already a sign registered on this location.");
                                        return true;
                                    }
                                }else {
                                    player.sendMessage(prefix + "Cannot create a sign for proxy groups.");
                                    return true;
                                }
                            }else {
                                player.sendMessage(prefix + "The group not found.");
                                return true;
                            }
                        }else {
                            player.sendMessage(prefix + "You must look at a sign.");
                            return true;
                        }
                    }else {
                        sendHelp(player);
                    }
                }else if (args[0].equalsIgnoreCase("delete")){
                    Block targetBlock = player.getTargetBlock(null, 4);
                    CloudSign cloudSign = SignBootstrap.signDriver.getSignFromLocation(targetBlock.getLocation());
                    if (cloudSign == null){
                        player.sendMessage(prefix + "The sign is not found");
                        return true;
                    }

                    SignBootstrap.signDriver.unregisterSign(cloudSign.getSignUUID());
                   SignBootstrap.signsAPI.removeSign(cloudSign.getSignUUID().toString());
                    player.sendMessage(prefix + "The sign was removed");
                    return true;
                }
            }else {
                player.sendMessage("§8▷ §7The network uses §bMetacloud§8 [§a"+Driver.getInstance().getMessageStorage().version+"§8]");
                player.sendMessage("§8▷ §fhttps://metacloudservice.eu/");
            }
        }
        return false;
    }

    public void sendHelp(Player player){
        String prefix = CloudAPI.getInstance().getMessages().getPrefix().replace("&", "§");
        player.sendMessage(prefix + "/cloudsigns create [group]");
        player.sendMessage(prefix + "/cloudsigns delete");
        player.sendMessage(prefix + "/cloudsigns claenup");


    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String subCmd : subCommands) {
                if (subCmd.startsWith(args[0].toLowerCase())) {
                    completions.add(subCmd);
                }
            }
            return completions;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            return CloudAPI.getInstance().getGroupPool().getGroups().stream().map(Group::getGroup).filter(group -> group.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());

        }
        return null;
    }
}