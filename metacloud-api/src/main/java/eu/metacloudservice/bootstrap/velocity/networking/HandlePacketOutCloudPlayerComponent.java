package eu.metacloudservice.bootstrap.velocity.networking;

import eu.metacloudservice.bootstrap.velocity.VelocityBootstrap;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;
import eu.metacloudservice.networking.packet.packets.out.service.playerbased.apibased.PacketOutCloudPlayerComponent;
import io.netty.channel.Channel;

public class HandlePacketOutCloudPlayerComponent implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (!(packet instanceof PacketOutCloudPlayerComponent)) {
            return;
        }
        var player = VelocityBootstrap.getProxyServer().getPlayer(((PacketOutCloudPlayerComponent) packet).getPlayer());
        if (player.isEmpty()) {
            return;
        }
        player.get().sendMessage(((PacketOutCloudPlayerComponent) packet).getComponent());
    }
}
