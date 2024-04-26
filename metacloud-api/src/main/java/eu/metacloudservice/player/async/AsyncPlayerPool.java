package eu.metacloudservice.player.async;

import eu.metacloudservice.process.ServiceState;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncPlayerPool {

    private final ArrayList<AsyncCloudPlayer> connectedPlayers;

    public AsyncPlayerPool() {
        this.connectedPlayers = new ArrayList<>();
    }

    public CompletableFuture<List<AsyncCloudPlayer>> getPlayers() {
        return CompletableFuture.supplyAsync(() -> connectedPlayers);
    }

    public CompletableFuture<AsyncCloudPlayer> getPlayer(@NonNull String username) {
        return CompletableFuture.supplyAsync(() -> connectedPlayers.stream().filter(cloudPlayer -> cloudPlayer.getUsername().equals(username)).findFirst().orElse(null));
    }

    public CompletableFuture<AsyncCloudPlayer> getPlayer(@NonNull UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> connectedPlayers.stream().filter(cloudPlayer -> cloudPlayer.getUniqueId().equals(uniqueId.toString().replace("-", ""))).findFirst().orElse(null));
    }

    public CompletableFuture<List<AsyncCloudPlayer>> getPlayersFromService(@NonNull String service) {
        return CompletableFuture.supplyAsync(() -> connectedPlayers.stream().filter(cloudPlayer -> cloudPlayer.getServer() == null ? false : cloudPlayer.getServer().getName().equals(service)).collect(Collectors.toList()));
    }

    public CompletableFuture<List<AsyncCloudPlayer>> getPlayersFromServiceGroupByState(@NonNull String group, ServiceState state) {
        return CompletableFuture.supplyAsync(() -> connectedPlayers.stream().filter(cloudPlayer -> cloudPlayer.getServer() == null ? false : cloudPlayer.getServer().getGroup().getGroup().equals(group))
                .filter(cloudPlayer -> cloudPlayer.getServer().getState() == state)
                .collect(Collectors.toList()));
    }

    public CompletableFuture<List<AsyncCloudPlayer>> getPlayersFromProxyGroupByState(@NonNull String group, ServiceState state) {
        return CompletableFuture.supplyAsync(() -> connectedPlayers.stream().filter(cloudPlayer -> cloudPlayer.getProxyServer() == null ? false : cloudPlayer.getProxyServer().getGroup().getGroup().equals(group))
                .filter(cloudPlayer -> cloudPlayer.getProxyServer().getState() == state)
                .collect(Collectors.toList()));
    }

    public CompletableFuture<List<AsyncCloudPlayer>> getPlayersFromProxy(@NonNull String Proxy) {
        return CompletableFuture.supplyAsync(() -> connectedPlayers.stream().filter(cloudPlayer -> cloudPlayer.getProxyServer() == null ? false : cloudPlayer.getProxyServer().getName().equals(Proxy)).collect(Collectors.toList()));
    }

    public CompletableFuture<List<AsyncCloudPlayer>> getPlayersByProxyGroup(@NonNull String group) {
        return CompletableFuture.supplyAsync(() -> connectedPlayers.stream().filter(cloudPlayer -> cloudPlayer.getProxyServer() == null ? false : cloudPlayer.getProxyServer().getGroup().getGroup().equals(group)).collect(Collectors.toList()));
    }

    public CompletableFuture<List<AsyncCloudPlayer>> getPlayersByServiceGroup(@NonNull String group) {
        return CompletableFuture.supplyAsync(() -> connectedPlayers.stream().filter(cloudPlayer -> cloudPlayer.getServer() == null ? false : cloudPlayer.getServer().getGroup().getGroup().equals(group)).collect(Collectors.toList()));
    }

    public boolean playerIsNotNull(@NonNull UUID uniqueId) {
        return connectedPlayers.stream().anyMatch(cloudPlayer -> cloudPlayer.getUniqueId().equals(uniqueId));
    }

    public boolean playerIsNotNull(@NonNull String username) {
        return connectedPlayers.stream().anyMatch(cloudPlayer -> cloudPlayer.getUsername().equals(username));
    }

    public void registerPlayer(@NonNull AsyncCloudPlayer asyncCloudPlayer) {
        if (connectedPlayers.stream().noneMatch(it -> it.getUniqueId().equals(asyncCloudPlayer.getUniqueId()))) {
            connectedPlayers.add(asyncCloudPlayer);
        }
    }

    public void unregisterPlayer(@NonNull String username) {
        connectedPlayers.removeIf(cloudPlayer -> cloudPlayer.getUsername().equalsIgnoreCase(username));
    }

    public boolean unregisterPlayer(@NonNull UUID uniqueId) {
        if (connectedPlayers.stream().anyMatch(cloudPlayer -> cloudPlayer.getUniqueId().equals(uniqueId.toString().replace("-", "")))) {
            connectedPlayers.removeIf(cloudPlayer -> cloudPlayer.getUniqueId().equals(uniqueId.toString().replace("-", "")));
            return true;
        } else {
            return false;
        }
    }
}
