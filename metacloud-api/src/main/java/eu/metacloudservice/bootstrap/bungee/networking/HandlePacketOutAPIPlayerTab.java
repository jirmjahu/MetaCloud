package eu.metacloudservice.bootstrap.bungee.networking;

import eu.metacloudservice.networking.packet.packets.out.service.playerbased.apibased.PacketOutAPIPlayerTab;
import io.netty.channel.Channel;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;

public class HandlePacketOutAPIPlayerTab implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerTab) {
            var player = ProxyServer.getInstance().getPlayer(((PacketOutAPIPlayerTab) packet).getUsername());
            if (!player.isConnected()) {
                return;
            }
            player.setTabHeader(TextComponent.fromLegacyText(((PacketOutAPIPlayerTab) packet).getHeader()), TextComponent.fromLegacyText(((PacketOutAPIPlayerTab) packet).getFooter()));
        }
    }
}
