package eu.metacloudservice.bootstrap.velocity.networking;

import eu.metacloudservice.bootstrap.velocity.VelocityBootstrap;
import eu.metacloudservice.networking.packet.packets.out.service.playerbased.apibased.PacketOutAPIPlayerTab;
import io.netty.channel.Channel;
import net.kyori.adventure.text.Component;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;

public class HandlePacketOutAPIPlayerTab implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (!(packet instanceof PacketOutAPIPlayerTab)) {
            return;
        }
        var player = VelocityBootstrap.getProxyServer().getPlayer(((PacketOutAPIPlayerTab) packet).getUsername());
        if (player.isEmpty()) {
            return;
        }
        player.get().getTabList().setHeaderAndFooter(Component.text(((PacketOutAPIPlayerTab) packet).getHeader()), Component.text(((PacketOutAPIPlayerTab) packet).getFooter()));

    }
}
