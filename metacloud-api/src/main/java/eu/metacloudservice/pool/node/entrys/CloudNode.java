package eu.metacloudservice.pool.node.entrys;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.pool.player.entrys.CloudPlayer;
import eu.metacloudservice.pool.service.entrys.CloudService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CloudNode {

    private final String nodeName;
    private final String address;

    public CloudNode(String nodeName, String address) {
        this.nodeName = nodeName;
        this.address = address;
    }

    public List<CloudService> getService() {
        return CloudAPI.getInstance().getServicePool().getServices().stream().filter(cloudService -> cloudService.getGroup().getStorage().getRunningNode().equals(getNodeName())).toList();
    }

    public List<CloudPlayer> getPlayersServiceSide() {
        List<CloudPlayer> players = new ArrayList<>();
        CloudAPI.getInstance().getGroupPool().getGroups().stream().filter(group -> group.getStorage().getRunningNode().equals(getNodeName())).toList().forEach(group -> {
            players.addAll(CloudAPI.getInstance().getPlayerPool().getPlayersByServiceGroup(group.getGroup()));
        });
        return players;
    }

    public List<CloudPlayer> getPlayersProxySide() {
        List<CloudPlayer> players = new ArrayList<>();
        CloudAPI.getInstance().getGroupPool().getGroups().stream().filter(group -> group.getStorage().getRunningNode().equals(getNodeName())).toList().forEach(group -> {
            players.addAll(CloudAPI.getInstance().getPlayerPool().getPlayersByProxyGroup(group.getGroup()));
        });
        return players;
    }
}
