package eu.metacloudservice.service.async;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.networking.packet.packets.in.service.cloudapi.PacketInStopService;
import eu.metacloudservice.networking.packet.packets.in.service.cloudapi.PacketLaunchServiceWithCustomTemplate;
import eu.metacloudservice.process.ServiceState;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncServicePool {

    private final ArrayList<AsyncCloudService> connectedServices;
    public AsyncServicePool() {
        connectedServices = new ArrayList<>();
    }
    public CompletableFuture<List<AsyncCloudService>> getServices(){
        return CompletableFuture.supplyAsync(() -> connectedServices);
    }
    public CompletableFuture<AsyncCloudService> getService(@NonNull String name){
        return CompletableFuture.supplyAsync(() -> connectedServices.stream().filter(asyncCloudService -> asyncCloudService.getName().equalsIgnoreCase(name)).findFirst().orElse(null));
    }
    public CompletableFuture<List<AsyncCloudService>> getServicesByGroup(@NonNull String group){
        return CompletableFuture.supplyAsync(() -> connectedServices.stream().filter(asyncCloudService -> asyncCloudService.getGroup()== null ? false : asyncCloudService.getGroup().getGroup().equals(group)).collect(Collectors.toList()));
    }
    public CompletableFuture<List<AsyncCloudService>> getServicesByState(@NonNull  ServiceState state){
        return CompletableFuture.supplyAsync( () ->connectedServices.stream().filter(asyncCloudService -> asyncCloudService.getState() == state).collect(Collectors.toList()));
    }
    public CompletableFuture<List<AsyncCloudService>> getServicesByGroupAndState(@NonNull String group, @NonNull ServiceState state) throws InterruptedException {
        return CompletableFuture.supplyAsync(() -> connectedServices.stream().filter(asyncCloudService -> asyncCloudService.getGroup().getGroup().equalsIgnoreCase(group)).filter(asyncCloudService -> asyncCloudService.getState() == state).toList());
    }
    public boolean serviceNotNull(@NonNull String name){
        return connectedServices.parallelStream().anyMatch(service -> service.getName().equalsIgnoreCase(name));
    }
    public boolean registerService(AsyncCloudService service){
        if (connectedServices.parallelStream().noneMatch(service1 -> service1.getName().equals(service.getName()))){
            connectedServices.add(service);
            return true;
        }else {
            return false;
        }
    }

    public boolean unregisterService(String service){
        if (connectedServices.parallelStream().anyMatch(service1 -> service1.getName().equals(service))){
            connectedServices.removeIf(service1 -> service1.getName().equals(service));
            return true;
        }else {
            return false;
        }
    }

    public void launchService(String group, String template){
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketLaunchServiceWithCustomTemplate(group, template));
    }
    public void launchServices(String group, int count, String template){
        for (int i = 0; i != count-1; i++) {
            launchService(group, template);
        }
    }

    public void stopService(String service){
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketInStopService(service));
    }


}
