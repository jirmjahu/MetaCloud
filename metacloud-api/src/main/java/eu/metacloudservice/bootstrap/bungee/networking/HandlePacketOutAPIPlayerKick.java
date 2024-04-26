package eu.metacloudservice.bootstrap.bungee.networking;

import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;
import eu.metacloudservice.networking.packet.packets.out.service.playerbased.apibased.PacketOutAPIPlayerKick;
import io.netty.channel.Channel;
import net.md_5.bungee.api.ProxyServer;


public class HandlePacketOutAPIPlayerKick implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (!(packet instanceof PacketOutAPIPlayerKick)) {
            return;
        }
        var player = ProxyServer.getInstance().getPlayer(((PacketOutAPIPlayerKick) packet).getUsername());
        if (!player.isConnected()) {
            return;
        }
        player.disconnect(((PacketOutAPIPlayerKick) packet).getMessage());
    }
}
