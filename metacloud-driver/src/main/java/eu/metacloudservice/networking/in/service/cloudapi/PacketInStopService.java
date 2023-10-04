package eu.metacloudservice.networking.in.service.cloudapi;

import eu.metacloudservice.networking.packet.NettyBuffer;
import eu.metacloudservice.networking.packet.Packet;
import lombok.Getter;

public class PacketInStopService extends Packet {
    @Getter
    private String service;

    public PacketInStopService() {
        setPacketUUID(1278782187);
    }

    public PacketInStopService(String service) {
        setPacketUUID(1278782187);
        this.service = service;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        service = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeString(service);
    }
}
