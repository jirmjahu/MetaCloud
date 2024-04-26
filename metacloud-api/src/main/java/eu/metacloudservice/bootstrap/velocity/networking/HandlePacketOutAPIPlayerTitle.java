package eu.metacloudservice.bootstrap.velocity.networking;

import eu.metacloudservice.bootstrap.velocity.VelocityBootstrap;
import eu.metacloudservice.networking.packet.packets.out.service.playerbased.apibased.PacketOutAPIPlayerTitle;
import io.netty.channel.Channel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.time.Duration;

import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;

public class HandlePacketOutAPIPlayerTitle implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (!(packet instanceof PacketOutAPIPlayerTitle)) {
            return;
        }
        var player = VelocityBootstrap.getProxyServer().getPlayer(((PacketOutAPIPlayerTitle) packet).getUsername());
        if (player.isEmpty()) {
            return;
        }
        var title = Title.title(Component.text(((PacketOutAPIPlayerTitle) packet).getTitle()), Component.text(((PacketOutAPIPlayerTitle) packet).getSubTitle()), Title.Times.times(Duration.ofSeconds(Long.getLong(String.valueOf(((PacketOutAPIPlayerTitle) packet).getFadeIn()))), Duration.ofSeconds(Long.getLong(String.valueOf(((PacketOutAPIPlayerTitle) packet).getStay()))), Duration.ofSeconds(Long.getLong(String.valueOf(((PacketOutAPIPlayerTitle) packet).getFadeOut())))));
        player.get().getCurrentServer().get().getServer().showTitle(title);
    }
}
