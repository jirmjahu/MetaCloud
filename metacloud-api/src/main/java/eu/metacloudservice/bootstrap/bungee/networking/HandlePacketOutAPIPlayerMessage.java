package eu.metacloudservice.bootstrap.bungee.networking;

import eu.metacloudservice.networking.packet.packets.out.service.playerbased.apibased.PacketOutAPIPlayerMessage;
import io.netty.channel.Channel;
import net.md_5.bungee.api.ProxyServer;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;

public class HandlePacketOutAPIPlayerMessage implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (!(packet instanceof PacketOutAPIPlayerMessage)) {
            return;
        }
        var player = ProxyServer.getInstance().getPlayer(((PacketOutAPIPlayerMessage) packet).getUsername());
        if (!player.isConnected()) {
            return;
        }
        player.sendMessage(((PacketOutAPIPlayerMessage) packet).getMessage());
    }
}
