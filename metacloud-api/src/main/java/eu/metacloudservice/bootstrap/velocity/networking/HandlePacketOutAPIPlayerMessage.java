package eu.metacloudservice.bootstrap.velocity.networking;

import eu.metacloudservice.bootstrap.velocity.VelocityBootstrap;
import eu.metacloudservice.networking.packet.packets.out.service.playerbased.apibased.PacketOutAPIPlayerMessage;
import io.netty.channel.Channel;
import net.kyori.adventure.text.Component;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;

public class HandlePacketOutAPIPlayerMessage implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerMessage) {
            var player = VelocityBootstrap.proxyServer.getPlayer(((PacketOutAPIPlayerMessage) packet).getUsername());
            if (player.isEmpty()) {
                return;
            }
            player.get().sendMessage(Component.text(((PacketOutAPIPlayerMessage) packet).getMessage()));
        }
    }
}
