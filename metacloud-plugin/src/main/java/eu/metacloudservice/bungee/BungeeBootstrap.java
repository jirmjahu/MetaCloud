package eu.metacloudservice.bungee;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.Driver;
import eu.metacloudservice.api.PluginDriver;
import eu.metacloudservice.bungee.command.CloudCommand;
import eu.metacloudservice.bungee.listener.CloudConnectListener;
import eu.metacloudservice.configuration.ConfigDriver;
import eu.metacloudservice.configuration.dummys.serviceconfig.LiveService;
import eu.metacloudservice.networking.NettyDriver;
import eu.metacloudservice.pool.service.entrys.CloudService;
import eu.metacloudservice.process.ServiceState;
import eu.metacloudservice.subcommands.*;
import eu.metacloudservice.timebaser.TimerBase;
import eu.metacloudservice.timebaser.utils.TimeUtil;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.*;

public class BungeeBootstrap extends Plugin {

    @Getter
    private static BungeeBootstrap instance;

    @Override
    public void onEnable() {
        instance = this;
        var service = (LiveService) new ConfigDriver("./CLOUDSERVICE.json").read(LiveService.class);
        new Driver();
        new PluginDriver();

        CloudAPI.getInstance().setState(ServiceState.LOBBY, service.getService());

        //register listeners
        ProxyServer.getInstance().getPluginManager().registerListener(this, new CloudConnectListener());

        //register commands
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CloudCommand("cloud"));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CloudCommand("metacloud"));
        PluginDriver.getInstance().register(new ExitCommand());
        PluginDriver.getInstance().register(new VersionCommand());
        PluginDriver.getInstance().register(new ReloadCommand());
        PluginDriver.getInstance().register(new ServiceCommand());
        PluginDriver.getInstance().register(new GroupCommand());
        PluginDriver.getInstance().register(new PlayerCommand());

        new TimerBase().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!CloudAPI.getInstance().getGroupPool().getGroup(service.getGroup()).isMaintenance()) {
                    return;
                }
                ProxyServer.getInstance().getPlayers().forEach(player -> {
                    if (player.hasPermission("metacloud.connection.maintenance") || CloudAPI.getInstance().getWhitelist().contains(player.getName())) {
                        return;
                    }
                    player.disconnect(Driver.getInstance().getMessageStorage().base64ToUTF8(CloudAPI.getInstance().getMessages().getMessages().get("kickNetworkIsMaintenance")).replace("&", "§"));
                });

                if (!NettyDriver.getInstance().nettyClient.getChannel().isActive()) {
                    System.exit(0);
                }
            }
        }, 2, 2, TimeUtil.SECONDS);
    }

    public CloudService getLobby(ProxiedPlayer player) {
        var services = CloudAPI.getInstance().getServicePool().getServices();
        if (services.isEmpty()) {
            return null;
        }

        if (services.stream().noneMatch(service -> service.isTypeLobby() && service.getState() == ServiceState.LOBBY)) {
            return null;
        }

        List<CloudService> cloudServices = services.stream()
                .filter(CloudService::isTypeLobby)
                .filter(service -> !service.getGroup().isMaintenance())
                .filter(service -> service.getState() == ServiceState.LOBBY).toList()
                .stream().filter(service -> {
                    if (!service.getGroup().getPermission().equalsIgnoreCase("")) {
                        return player.hasPermission(service.getGroup().getPermission());
                    }
                    return true;
                }).toList();

        if (cloudServices.isEmpty()) {
            return null;
        }

        List<Integer> priority = new ArrayList<>();

        cloudServices.forEach(service -> priority.add(service.getGroup().getPriority()));
        priority.sort(Collections.reverseOrder());
        List<CloudService> lobbys = cloudServices.stream().filter(service -> service.getGroup().getPriority().equals(priority.get(0))).toList();

        if (lobbys.isEmpty()) {
            return null;
        }

        return lobbys.get(new Random().nextInt(lobbys.size()));
    }

    public CloudService getLobby(ProxiedPlayer player, String kicked) {
        var services = CloudAPI.getInstance().getServicePool().getServices();
        if (services.isEmpty()) {
            return null;
        }

        if (services.stream().noneMatch(service -> service.getGroup().getGroupType().equals("LOBBY") && service.getState() == ServiceState.LOBBY)) {
            return null;
        }

        List<CloudService> cloudServices = CloudAPI.getInstance().getServicePool().getServices().stream()
                .filter(CloudService::isTypeLobby)
                .filter(service -> !service.getGroup().isMaintenance())
                .filter(service -> !service.getName().equals(kicked))
                .filter(service -> service.getState() == ServiceState.LOBBY)
                .filter(service -> service.getGroup().getPermission().equals("") || player.hasPermission(service.getGroup().getPermission())).toList();

        if (cloudServices.isEmpty()) {
            return null;
        }

        List<Integer> priority = new ArrayList<>();
        cloudServices.forEach(service -> priority.add(service.getGroup().getPriority()));
        priority.sort(Collections.reverseOrder());
        var lobbys = cloudServices.stream().filter(service -> service.getGroup().getPriority().equals(priority.get(0))).toList();

        if (lobbys.isEmpty()) {
            return null;
        }

        return lobbys.get(new Random().nextInt(lobbys.size()));
    }
}
