package eu.metacloudservice.velocity.listener;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.metacloudservice.Driver;
import eu.metacloudservice.bungee.BungeeBootstrap;
import eu.metacloudservice.config.Configuration;
import eu.metacloudservice.configuration.ConfigDriver;
import eu.metacloudservice.events.entrys.ICloudListener;
import eu.metacloudservice.events.entrys.Subscribe;
import eu.metacloudservice.events.listeners.*;
import net.kyori.text.TextComponent;

import java.util.function.Consumer;

public class CloudListener implements ICloudListener {

    private final ProxyServer proxyServer;

    public CloudListener(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void handle(CloudProxyDisconnectedEvent event){

        Configuration configuration = (Configuration) new ConfigDriver().convert(BungeeBootstrap.getInstance().getRestDriver().get("/module/notify/configuration"), Configuration.class);

        proxyServer.getAllPlayers().forEach(player -> {
            if (player.hasPermission("metacloud.notify")){
                player.sendMessage(TextComponent.builder(Driver.getInstance().getMessageStorage().base64ToUTF8(configuration.getProxiedServiceDiconnected()).replace("&", "§").replace("%service_name%", event.getName())).build());
            }
        });

    }

    @Subscribe
    public void handle(CloudProxyConnectedEvent event){
        Configuration configuration = (Configuration) new ConfigDriver().convert(BungeeBootstrap.getInstance().getRestDriver().get("/module/notify/configuration"), Configuration.class);
        proxyServer.getAllPlayers().forEach(player -> {
            if (player.hasPermission("metacloud.notify")){
                player.sendMessage(TextComponent.builder(Driver.getInstance().getMessageStorage().base64ToUTF8(configuration.getProxiedServiceConnected()).replace("&", "§")
                        .replace("%service_name%", event.getName()).replace("%node_name%", event.getNode())).build());
            }
        });
    }

    @Subscribe
    public void handle(CloudProxyPreparedEvent event){
        Configuration configuration = (Configuration) new ConfigDriver().convert(BungeeBootstrap.getInstance().getRestDriver().get("/module/notify/configuration"), Configuration.class);
        proxyServer.getAllPlayers().forEach(player -> {
            if (player.hasPermission("metacloud.notify")){
                player.sendMessage(TextComponent.builder(Driver.getInstance().getMessageStorage().base64ToUTF8(configuration.getProxiedServicePrepared()).replace("&", "§")
                        .replace("%service_name%", event.getName()).replace("%node_name%", event.getNode())).build());
            }
        });

    }

    @Subscribe
    public void handle(CloudServiceConnectedEvent event){
        Configuration configuration = (Configuration) new ConfigDriver().convert(BungeeBootstrap.getInstance().getRestDriver().get("/module/notify/configuration"), Configuration.class);

        proxyServer.getAllPlayers().forEach(player -> {
            if (player.hasPermission("metacloud.notify")){
                player.sendMessage(TextComponent.builder(Driver.getInstance().getMessageStorage().base64ToUTF8(configuration.getServiceConnected()).replace("&", "§")
                        .replace("%service_name%", event.getName()).replace("%node_name%", event.getNode())).build());
            }
        });

    }

    @Subscribe
    public void handle(CloudServiceDisconnectedEvent event){
        Configuration configuration = (Configuration) new ConfigDriver().convert(BungeeBootstrap.getInstance().getRestDriver().get("/module/notify/configuration"), Configuration.class);


        proxyServer.getAllPlayers().forEach(player -> {
            if (player.hasPermission("metacloud.notify")){
                player.sendMessage(TextComponent.builder(Driver.getInstance().getMessageStorage().base64ToUTF8(configuration.getServiceDiconnected()).replace("&", "§").replace("%service_name%", event.getName())).build());
            }
        });


    }

    @Subscribe
    public void handle(CloudServicePreparedEvent event){
        Configuration configuration = (Configuration) new ConfigDriver().convert(BungeeBootstrap.getInstance().getRestDriver().get("/module/notify/configuration"), Configuration.class);

        proxyServer.getAllPlayers().forEach(player -> {
            if (player.hasPermission("metacloud.notify")){
                player.sendMessage(TextComponent.builder(Driver.getInstance().getMessageStorage().base64ToUTF8(configuration.getServicePrepared()).replace("&", "§")
                        .replace("%service_name%", event.getName()).replace("%node_name%", event.getNode())).build());
            }
        });
    }

}