package eu.metacloudservice.service;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.networking.packet.packets.in.service.cloudapi.PacketInLaunchService;
import eu.metacloudservice.networking.packet.packets.in.service.cloudapi.PacketInStopService;
import eu.metacloudservice.networking.packet.packets.in.service.cloudapi.PacketLaunchServiceWithCustomTemplate;
import eu.metacloudservice.process.ServiceState;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServicePool {

    private final ArrayList<CloudService> connectedServices;

    public ServicePool() {
        connectedServices = new ArrayList<>();
    }

    public List<CloudService> getServices() {
        return connectedServices;
    }

    public CloudService getService(@NonNull String name) {
        return connectedServices.stream().filter(service -> service.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean serviceNotNull(@NonNull String name) {
        return connectedServices.stream().anyMatch(service -> service.getName().equalsIgnoreCase(name));
    }

    public List<CloudService> getServicesByGroup(@NonNull String group) {
        return connectedServices.stream().filter(cloudService -> cloudService.getGroup() == null ? false : cloudService.getGroup().getGroup().equals(group)).collect(Collectors.toList());
    }

    public List<CloudService> getServicesByState(@NonNull ServiceState state) {
        return connectedServices.stream().filter(cloudService -> cloudService.getState() == state).collect(Collectors.toList());
    }

    public List<CloudService> getServicesByGroupAndState(@NonNull String group, @NonNull ServiceState state) {
        return getServicesByGroup(group).stream().filter(cloudService -> cloudService.getState() == state).toList();
    }

    public boolean registerService(CloudService cloudService) {
        if (connectedServices.stream().noneMatch(service -> cloudService.getName().equals(cloudService.getName()))) {
            connectedServices.add(cloudService);
            return true;
        } else {
            return false;
        }
    }

    public boolean unregisterService(String service) {
        if (connectedServices.stream().anyMatch(service1 -> service1.getName().equals(service))) {
            connectedServices.removeIf(service1 -> service1.getName().equals(service));
            return true;
        } else {
            return false;
        }
    }

    public void launchService(String group) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInLaunchService(group));
    }

    public void launchServices(String group, int count) {
        for (int i = 0; i != count - 1; i++) {
            launchService(group);
        }
    }

    public void launchService(String group, String template) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketLaunchServiceWithCustomTemplate(group, template));
    }

    public void launchServices(String group, int count, String template) {
        for (int i = 0; i != count - 1; i++) {
            launchService(group, template);
        }
    }

    public void stopService(String service) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInStopService(service));
    }
}
