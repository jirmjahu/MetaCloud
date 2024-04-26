package eu.metacloudservice.bootstrap.velocity.networking;

import eu.metacloudservice.bootstrap.velocity.VelocityBootstrap;
import eu.metacloudservice.networking.packet.packets.in.service.playerbased.apibased.PacketOutAPIPlayerDispactchCommand;
import io.netty.channel.Channel;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;

import java.util.concurrent.ExecutionException;

public class HandlePacketOutAPIPlayerDispactchCommand implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (!(packet instanceof PacketOutAPIPlayerDispactchCommand)) {
            return;
        }
        var player = VelocityBootstrap.getProxyServer().getPlayer(((PacketOutAPIPlayerDispactchCommand) packet).getUserName());
        if (player.isEmpty()) {
            return;
        }
        try {
            VelocityBootstrap.getProxyServer().getCommandManager().executeAsync(player.get(),
                    ((PacketOutAPIPlayerDispactchCommand) packet).getCommand()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
