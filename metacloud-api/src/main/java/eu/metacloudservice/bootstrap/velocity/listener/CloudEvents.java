package eu.metacloudservice.bootstrap.velocity.listener;

import com.velocitypowered.api.proxy.server.ServerInfo;
import eu.metacloudservice.bootstrap.velocity.VelocityBootstrap;
import eu.metacloudservice.events.entrys.ICloudListener;
import eu.metacloudservice.events.entrys.Priority;
import eu.metacloudservice.events.entrys.Subscribe;
import eu.metacloudservice.events.listeners.services.CloudServiceConnectedEvent;
import eu.metacloudservice.events.listeners.services.CloudServiceDisconnectedEvent;

import java.net.InetSocketAddress;

public class CloudEvents implements ICloudListener {

    @Subscribe(priority = Priority.HIGHEST)
    public void handle(CloudServiceConnectedEvent event) {
        VelocityBootstrap.getProxyServer().registerServer(new ServerInfo(event.getName(), new InetSocketAddress(event.getHost(), event.getPort())));
    }

    @Subscribe
    public void handle(CloudServiceDisconnectedEvent event) {
        if (VelocityBootstrap.getProxyServer().getServer(event.getName()).isEmpty()) {
            return;
        }
        VelocityBootstrap.getProxyServer().unregisterServer(VelocityBootstrap.getProxyServer().getServer(event.getName()).get().getServerInfo());
    }
}
