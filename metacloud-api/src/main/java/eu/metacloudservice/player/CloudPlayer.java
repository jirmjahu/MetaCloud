package eu.metacloudservice.player;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.cloudplayer.CloudPlayerRestCache;
import eu.metacloudservice.cloudplayer.codec.title.Title;
import eu.metacloudservice.configuration.ConfigDriver;
import eu.metacloudservice.networking.packet.packets.in.service.playerbased.apibased.*;
import eu.metacloudservice.service.CloudService;
import eu.metacloudservice.process.ServiceState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public class CloudPlayer {

    private final String username;
    private final UUID uniqueId;

    public void performMore(Consumer<CloudPlayer> cloudPlayerConsumer) {
        cloudPlayerConsumer.accept(this);
    }

    public CloudService getProxyServer() {
        CloudPlayerRestCache cech = (CloudPlayerRestCache) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudplayer/" + getUniqueId()), CloudPlayerRestCache.class);
        return CloudAPI.getInstance().getServicePool().getService(cech.getCloudplayerproxy());
    }

    public void connectRanked(String group) {
        CloudAPI.getInstance()
                .getServicePool()
                .getServicesByGroup(group)
                .stream()
                .filter(cloudService -> (cloudService.getState() == ServiceState.LOBBY))
                .min(Comparator.comparingInt(CloudService::getPlayercount))
                .ifPresent(service -> CloudAPI.getInstance().sendPacketSynchronized(new PacketInAPIPlayerConnect(this.username, service.getName())));
    }

    public long getCurrentPlayTime() {
        var cache = (CloudPlayerRestCache) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudplayer/" + getUniqueId()), CloudPlayerRestCache.class);
        return cache.getCloudplayerconnect();
    }

    public CloudService getServer() {
        var cache = (CloudPlayerRestCache) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudplayer/" + getUniqueId()), CloudPlayerRestCache.class);
        return CloudAPI.getInstance().getServicePool().getService(cache.getCloudplayerservice());
    }

    public void connect(@NonNull CloudService cloudService) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInAPIPlayerConnect(username, cloudService.getName()));
    }

    public void connectRandom(String group) {
        var cloudService = CloudAPI.getInstance().getServicePool().getServicesByGroup(group).get(new Random().nextInt(CloudAPI.getInstance().getServicePool().getServicesByGroup(group).size()));
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInAPIPlayerConnect(username, cloudService.getName()));
    }

    public void dispatchCommand(@NonNull String command) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketOutAPIPlayerDispactchCommand(username, command));
    }

    public String getSkinValue() {
        String urlString = "https://minecraft-api.com/api/uuid/" + username + "/json";
        try {
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            JSONObject json = new JSONObject(builder.toString());
            return json.getJSONObject("properties").getString("value");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getSkinSignature() {
        String urlString = "https://minecraft-api.com/api/uuid/" + username + "/json";
        try {
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            JSONObject json = new JSONObject(builder.toString());
            return json.getJSONObject("properties").getString("signature");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void connect(CloudPlayer cloudPlayer) {
        CloudAPI.getInstance().sendPacketAsynchronous(new PacketInAPIPlayerConnect(username, cloudPlayer.getServer().getName()));
    }

    public void disconnect(@NonNull String message) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInAPIPlayerKick(username, message));
    }

    public void sendTitle(@NonNull Title title) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInAPIPlayerTitle(title.getTitle(), title.getSubtitle(), title.getFadeIn(), title.getStay(), title.getFadeOut(), username));
    }

    public void sendTabList(String header, String footer) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInAPIPlayerTab(username, header, footer));
    }

    public void sendActionBar(String message) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInAPIPlayerActionBar(username, message));
    }

    public void sendMessage(@NonNull String message) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInAPIPlayerMessage(username, message));
    }

    public void sendMessage(@NonNull String... message) {
        for (String msg : message) {
            sendMessage(msg);
        }
    }

    public void sendComponent(Component component) {
        CloudAPI.getInstance().sendPacketSynchronized(new PacketInCloudPlayerComponent(component, username));
    }

    public boolean isConnectedOnFallback() {
        return getServer().isTypeLobby();
    }

    public String toString() {
        return "username='" + username + "', uniqueId='" + uniqueId + "', proxy='" + getProxyServer().getName() + "', service='" + getServer().getName() + "', skinValue='" + getSkinValue() + "', skinSignature='" + getSkinSignature() + "', isConnectedOnFallback='" + isConnectedOnFallback() + "', currentPlayTime='" + getCurrentPlayTime() + "'";
    }
}
