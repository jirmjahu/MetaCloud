package eu.metacloudservice.bootstrap.bungee.networking;

import eu.metacloudservice.bootstrap.bungee.BungeeBootstrap;
import eu.metacloudservice.networking.packet.packets.out.service.playerbased.apibased.PacketOutCloudPlayerComponent;
import io.netty.channel.Channel;
import net.md_5.bungee.api.ProxyServer;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;

public class HandlePacketOutCloudPlayerComponent implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutCloudPlayerComponent) {
            var player = ProxyServer.getInstance().getPlayer(((PacketOutCloudPlayerComponent) packet).getPlayer());
            if (!ProxyServer.getInstance().getPlayer(((PacketOutCloudPlayerComponent) packet).getPlayer()).isConnected()) {
                return;
            }
            BungeeBootstrap.getInstance().getAudiences().player(player).sendMessage(((PacketOutCloudPlayerComponent) packet).getComponent());
        }
    }
}
