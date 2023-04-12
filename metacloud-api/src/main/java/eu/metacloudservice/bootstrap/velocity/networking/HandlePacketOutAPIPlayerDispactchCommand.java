package eu.metacloudservice.bootstrap.bungee.networking;

import eu.metacloudservice.networking.in.service.playerbased.apibased.PacketOutAPIPlayerDispactchCommand;
import eu.metacloudservice.networking.out.service.playerbased.apibased.PacketOutAPIPlayerConnect;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;
import io.netty.channel.Channel;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class HandlePacketOutAPIPlayerDispactchCommand implements NettyAdaptor {
    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutAPIPlayerDispactchCommand) {
            if (ProxyServer.getInstance().getPlayer(((PacketOutAPIPlayerDispactchCommand) packet).getUserName()).isConnected()){
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(((PacketOutAPIPlayerDispactchCommand) packet).getUserName());
                ProxyServer.getInstance().getPluginManager().dispatchCommand(player, ((PacketOutAPIPlayerDispactchCommand) packet).getCommand());
            }
        }
    }
}
