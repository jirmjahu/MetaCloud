package eu.metacloudservice.bootstrap.bungee.networking;

import eu.metacloudservice.networking.packet.packets.out.service.playerbased.apibased.PacketOutAPIPlayerTitle;
import io.netty.channel.Channel;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;

public class HandlePacketOutAPIPlayerTitle implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (!(packet instanceof PacketOutAPIPlayerTitle titlePacket)) {
            return;
        }
        var player = ProxyServer.getInstance().getPlayer(((PacketOutAPIPlayerTitle) packet).getUsername());
        if (!player.isConnected()) {
            return;
        }
        var title = ProxyServer.getInstance().createTitle();
        title.title(new TextComponent(titlePacket.getTitle()));
        title.subTitle(new TextComponent(titlePacket.getSubTitle()));
        title.fadeIn(titlePacket.getFadeIn());
        title.stay(titlePacket.getStay());
        title.fadeOut(titlePacket.getFadeOut());
        player.sendTitle(title);
    }
}
