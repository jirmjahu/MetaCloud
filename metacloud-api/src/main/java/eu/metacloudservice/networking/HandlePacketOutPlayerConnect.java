package eu.metacloudservice.networking;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.player.async.AsyncCloudPlayer;
import eu.metacloudservice.events.listeners.player.CloudPlayerConnectedEvent;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;
import eu.metacloudservice.networking.packet.packets.out.service.playerbased.PacketOutPlayerConnect;
import eu.metacloudservice.player.CloudPlayer;
import eu.metacloudservice.storage.UUIDDriver;
import io.netty.channel.Channel;

public class HandlePacketOutPlayerConnect implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutPlayerConnect) {
            if (CloudAPI.getInstance().getPlayerPool().playerIsNotNull(((PacketOutPlayerConnect) packet).getName())) {
                return;
            }
            var uniqueId = UUIDDriver.getUUID(((PacketOutPlayerConnect) packet).getName());
            CloudAPI.getInstance().getAsyncPlayerPool().registerPlayer(new AsyncCloudPlayer(((PacketOutPlayerConnect) packet).getName(), uniqueId));
            CloudAPI.getInstance().getPlayerPool().registerPlayer(new CloudPlayer(((PacketOutPlayerConnect) packet).getName(), uniqueId));
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudPlayerConnectedEvent(((PacketOutPlayerConnect) packet).getName(),
                    ((PacketOutPlayerConnect) packet).getProxy(), UUIDDriver.getUUID(((PacketOutPlayerConnect) packet).getName())));
        }
    }
}
