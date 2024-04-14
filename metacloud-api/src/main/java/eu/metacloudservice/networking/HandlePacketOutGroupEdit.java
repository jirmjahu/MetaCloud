package eu.metacloudservice.networking;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.events.listeners.group.CloudGroupUpdateEditEvent;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;
import eu.metacloudservice.networking.packet.packets.out.service.group.PacketOutGroupEdit;
import io.netty.channel.Channel;

public class HandlePacketOutGroupEdit implements NettyAdaptor {

    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutGroupEdit) {
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudGroupUpdateEditEvent(((PacketOutGroupEdit) packet).getGroup()));
        }
    }
}
