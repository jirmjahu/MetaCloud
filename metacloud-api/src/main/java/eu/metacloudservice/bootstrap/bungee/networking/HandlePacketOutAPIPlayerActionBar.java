package eu.metacloudservice.bootstrap.bungee.networking;

import eu.metacloudservice.networking.packet.packets.out.service.playerbased.apibased.PacketOutAPIPlayerActionBar;
import io.netty.channel.Channel;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;

public class HandlePacketOutAPIPlayerActionBar implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerActionBar) {
            var player = ProxyServer.getInstance().getPlayer(((PacketOutAPIPlayerActionBar) packet).getUsername());
            if (!player.isConnected()) {
                return;
            }
            player.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(((PacketOutAPIPlayerActionBar) packet).getMessage()));
        }
    }
}
