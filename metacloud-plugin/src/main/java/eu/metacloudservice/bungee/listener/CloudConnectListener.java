package eu.metacloudservice.bungee.listener;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.bungee.BungeeBootstrap;
import eu.metacloudservice.configuration.ConfigDriver;
import eu.metacloudservice.configuration.dummys.serviceconfig.LiveService;
import eu.metacloudservice.networking.packet.packets.in.service.playerbased.PacketInPlayerConnect;
import eu.metacloudservice.networking.packet.packets.in.service.playerbased.PacketInPlayerDisconnect;
import eu.metacloudservice.networking.packet.packets.in.service.playerbased.PacketInPlayerSwitchService;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.UUID;

public class CloudConnectListener implements Listener {

    private final ArrayList<UUID> connected = new ArrayList<>();

    public ServerInfo target;

    @EventHandler
    public void handle(final ServerConnectEvent event) {
        if (!this.connected.contains(event.getPlayer().getUniqueId())) {
            return;
        }

        if (event.getPlayer().getServer() != null) {
            return;
        }

        target = ProxyServer.getInstance().getServerInfo(BungeeBootstrap.getInstance().getLobby(event.getPlayer()).getName());
        if (target == null) {
            event.setCancelled(true);
            return;
        }
        event.setTarget(target);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(PostLoginEvent event) {
        var service = (LiveService) (new ConfigDriver("./CLOUDSERVICE.json")).read(LiveService.class);
        var group = CloudAPI.getInstance().getGroupPool().getGroup(service.getGroup());

        if (CloudAPI.getInstance().getPlayerPool().getPlayers().stream().anyMatch(cloudPlayer -> cloudPlayer.getUsername().equalsIgnoreCase(event.getPlayer().getName()))) {
            event.getPlayer().disconnect(CloudAPI.getInstance().getMessages().getMessages().get("kickAlreadyOnNetwork").replace("&", "§"));
        }

        this.connected.add(event.getPlayer().getUniqueId());
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketInPlayerConnect(event.getPlayer().getName(), service.getService()));

        //check if the group is maintenance and the player is not whitelisted
        if (group.isMaintenance() &&
                ProxyServer.getInstance().getPlayer(event.getPlayer().getUniqueId()) != null &&
                !ProxyServer.getInstance().getPlayer(event.getPlayer().getUniqueId()).hasPermission("metacloud.bypass.connection.maintenance") &&
                !CloudAPI.getInstance().getWhitelist().contains(ProxyServer.getInstance().getPlayer(event.getPlayer().getUniqueId()).getName())) {
            event.getPlayer().disconnect(CloudAPI.getInstance().getMessages().getMessages().get("kickNetworkIsMaintenance").replace("&", "§"));
            return;
        }

        //check if the group is full and the player is not whitelisted
        if (CloudAPI.getInstance().getPlayerPool().getPlayers().size() >= group.getMaxPlayers() &&
                !ProxyServer.getInstance().getPlayer(event.getPlayer().getUniqueId()).hasPermission("metacloud.bypass.connection.full") &&
                !CloudAPI.getInstance().getWhitelist().contains(ProxyServer.getInstance().getPlayer(event.getPlayer().getUniqueId()).getName())) {
            event.getPlayer().disconnect(CloudAPI.getInstance().getMessages().getMessages().get("kickNetworkIsFull").replace("&", "§"));
            return;
        }

        //check if there is no fallback server available
        if (ProxyServer.getInstance().getPlayer(event.getPlayer().getUniqueId()) != null && BungeeBootstrap.getInstance().getLobby(ProxyServer.getInstance().getPlayer(event.getPlayer().getUniqueId())) == null) {
            event.getPlayer().disconnect(CloudAPI.getInstance().getMessages().getMessages().get("kickNoFallback").replace("&", "§"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(final PlayerDisconnectEvent event) {
        if (!this.connected.contains(event.getPlayer().getUniqueId())) {
            return;
        }
        this.connected.remove(event.getPlayer().getUniqueId());
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketInPlayerDisconnect(event.getPlayer().getName()));
    }

    @EventHandler
    public void handle(ServerSwitchEvent event) {
        if (!this.connected.contains(event.getPlayer().getUniqueId())) {
            return;
        }
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketInPlayerSwitchService(event.getPlayer().getName(), event.getPlayer().getServer().getInfo().getName()));
    }

    @EventHandler
    public void handle(ServerKickEvent event) {
        if (!this.connected.contains(event.getPlayer().getUniqueId())) {
            return;
        }

        target = ProxyServer.getInstance().getServerInfo(BungeeBootstrap.getInstance().getLobby(event.getPlayer(), event.getKickedFrom().getName()).getName());
        if (target == null) {
            event.setCancelled(false);
            event.setCancelServer(null);
            event.getPlayer().disconnect(CloudAPI.getInstance().getMessages().getMessages().get("kickNoFallback").replace("&", "§"));
            return;
        }

        event.setCancelServer(target);
        event.setCancelled(true);
    }
}
