package eu.metacloudservice.bootstrap.bungee.listener;


import eu.metacloudservice.bootstrap.bungee.utils.ServerDriver;
import eu.metacloudservice.events.entrys.ICloudListener;
import eu.metacloudservice.events.entrys.Priority;
import eu.metacloudservice.events.entrys.Subscribe;
import eu.metacloudservice.events.listeners.services.CloudServiceConnectedEvent;
import eu.metacloudservice.events.listeners.services.CloudServiceDisconnectedEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

public class CloudEvents implements ICloudListener {


    @Subscribe(priority = Priority.HIGHEST)
    public void handle(CloudServiceConnectedEvent event){
        var info = ProxyServer.getInstance().constructServerInfo(event.getName(), new InetSocketAddress(event.getHost(), event.getPort()), "metacloud-service", false);
        new ServerDriver().addServer(info);

    }

    @Subscribe(priority = Priority.HIGHEST)
    public void handle(CloudServiceDisconnectedEvent event){
        new ServerDriver().removeServer(event.getName());
    }

}
