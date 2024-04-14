package eu.metacloudservice;

import eu.metacloudservice.async.pool.group.AsyncGroupPool;
import eu.metacloudservice.async.pool.offlineplayer.AsyncOfflinePlayerPool;
import eu.metacloudservice.async.pool.player.AsyncPlayerPool;
import eu.metacloudservice.async.pool.player.entrys.AsyncCloudPlayer;
import eu.metacloudservice.async.pool.service.AsyncServicePool;
import eu.metacloudservice.bootstrap.bungee.listener.CloudEvents;
import eu.metacloudservice.bootstrap.bungee.networking.*;
import eu.metacloudservice.configuration.ConfigDriver;
import eu.metacloudservice.configuration.dummys.message.Messages;
import eu.metacloudservice.configuration.dummys.serviceconfig.LiveService;
import eu.metacloudservice.events.EventDriver;
import eu.metacloudservice.events.entrys.ICloudListener;
import eu.metacloudservice.groups.dummy.Group;
import eu.metacloudservice.networking.*;
import eu.metacloudservice.networking.client.NettyClient;
import eu.metacloudservice.networking.packet.Packet;
import eu.metacloudservice.networking.packet.packets.in.service.PacketInServiceConnect;
import eu.metacloudservice.networking.packet.packets.in.service.cloudapi.PacketInChangeState;
import eu.metacloudservice.networking.packet.packets.in.service.cloudapi.PacketInDispatchMainCommand;
import eu.metacloudservice.networking.packet.packets.in.service.command.PacketInCommandWhitelist;
import eu.metacloudservice.networking.packet.packets.out.service.*;
import eu.metacloudservice.networking.packet.packets.out.service.events.PacketOutCloudProxyCouldNotStartEvent;
import eu.metacloudservice.networking.packet.packets.out.service.events.PacketOutCloudServiceCouldNotStartEvent;
import eu.metacloudservice.networking.packet.packets.out.service.group.PacketOutGroupCreate;
import eu.metacloudservice.networking.packet.packets.out.service.group.PacketOutGroupDelete;
import eu.metacloudservice.networking.packet.packets.out.service.group.PacketOutGroupEdit;
import eu.metacloudservice.networking.packet.packets.out.service.playerbased.PacketOutPlayerConnect;
import eu.metacloudservice.networking.packet.packets.out.service.playerbased.PacketOutPlayerDisconnect;
import eu.metacloudservice.networking.packet.packets.out.service.playerbased.PacketOutPlayerSwitchService;
import eu.metacloudservice.networking.packet.packets.out.service.playerbased.apibased.*;
import eu.metacloudservice.pool.group.GroupPool;
import eu.metacloudservice.pool.offlineplayer.OfflinePlayerPool;
import eu.metacloudservice.pool.player.PlayerPool;
import eu.metacloudservice.pool.player.entrys.CloudPlayer;
import eu.metacloudservice.pool.service.ServicePool;
import eu.metacloudservice.process.ServiceState;
import eu.metacloudservice.storage.UUIDDriver;
import eu.metacloudservice.timebaser.TimerBase;
import eu.metacloudservice.timebaser.utils.TimeUtil;
import eu.metacloudservice.webserver.RestDriver;
import eu.metacloudservice.webserver.dummys.PlayerGeneral;
import eu.metacloudservice.webserver.dummys.WhiteList;
import eu.metacloudservice.webserver.dummys.liveservice.LiveServiceList;
import eu.metacloudservice.webserver.dummys.liveservice.LiveServices;
import lombok.Getter;
import lombok.NonNull;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

@Getter
public class CloudAPI {

    @Getter
    private static CloudAPI instance;
    private final LiveService service;
    private final PlayerPool playerPool;
    private final OfflinePlayerPool offlinePlayerPool;
    private final AsyncOfflinePlayerPool asyncOfflinePlayerPool;
    private final GroupPool groupPool;
    private final ServicePool servicePool;
    private final RestDriver restDriver;
    private final EventDriver eventDriver;

    private final AsyncPlayerPool asyncPlayerPool;
    private final AsyncServicePool asyncServicePool;
    private final AsyncGroupPool asyncGroupPool;

    public CloudAPI(boolean isVelocity) {
        instance = this;
        new Driver();
        service = (LiveService) new ConfigDriver("./CLOUDSERVICE.json").read(LiveService.class);
        new NettyDriver();
        this.offlinePlayerPool = new OfflinePlayerPool();
        this.asyncOfflinePlayerPool = new AsyncOfflinePlayerPool();
        this.playerPool = new PlayerPool();
        this.servicePool = new ServicePool();
        this.groupPool = new GroupPool();
        this.asyncGroupPool = new AsyncGroupPool();
        this.asyncServicePool = new AsyncServicePool();
        this.asyncPlayerPool = new AsyncPlayerPool();

        restDriver = new RestDriver(service.getManagerAddress(), service.getRestPort());
        NettyDriver.getInstance().nettyClient = new NettyClient();
        NettyDriver.getInstance().nettyClient.bind(service.getManagerAddress(), service.getNetworkPort()).connect();

        NettyDriver.getInstance().getPacketDriver()
                .registerHandler(new PacketOutCloudServiceCouldNotStartEvent().getPacketUUID(), new HandlePacketOutCloudServiceCouldNotStartEvent(), PacketOutCloudServiceCouldNotStartEvent.class)
                .registerHandler(new PacketOutCloudProxyCouldNotStartEvent().getPacketUUID(), new HandlePacketOutCloudProxyCouldNotStartEvent(), PacketOutCloudProxyCouldNotStartEvent.class)
                .registerHandler(new PacketOutServicePrepared().getPacketUUID(), new HandlePacketOutServicePrepared(), PacketOutServicePrepared.class)
                .registerHandler(new PacketOutServiceConnected().getPacketUUID(), new HandlePacketOutServiceConnected(), PacketOutServiceConnected.class)
                .registerHandler(new PacketOutServiceDisconnected().getPacketUUID(), new HandlePacketOutServiceDisconnected(), PacketOutServiceDisconnected.class)
                .registerHandler(new PacketOutPlayerConnect().getPacketUUID(), new HandlePacketOutPlayerConnect(), PacketOutPlayerConnect.class)
                .registerHandler(new PacketOutPlayerDisconnect().getPacketUUID(), new HandlePacketOutPlayerDisconnect(), PacketOutPlayerDisconnect.class)
                .registerHandler(new PacketOutPlayerSwitchService().getPacketUUID(), new HandlePacketOutPlayerSwitchService(), PacketOutPlayerSwitchService.class)
                .registerHandler(new PacketOutServiceLaunch().getPacketUUID(), new HandlePacketOutServiceLaunch(), PacketOutServiceLaunch.class)
                .registerHandler(new PacketOutGroupCreate().getPacketUUID(), new HandlePacketOutGroupCreate(), PacketOutGroupCreate.class)
                .registerHandler(new PacketOutGroupDelete().getPacketUUID(), new HandlePacketOutGroupDelete(), PacketOutGroupDelete.class)
                .registerHandler(new PacketOutGroupEdit().getPacketUUID(), new HandlePacketOutGroupEdit(), PacketOutGroupEdit.class)
                .registerHandler(new PacketOutResAPItReload().getPacketUUID(), new HandlePacketOutResAPItReload(), PacketOutResAPItReload.class)
                .registerHandler(new PacketOutCloudServiceChangeState().getPacketUUID(), new HandlePacketOutCloudServiceChangeState(), PacketOutCloudServiceChangeState.class)
                .registerHandler(new PacketOutCloudProxyChangeState().getPacketUUID(), new HandlePacketOutCloudProxyChangeState(), PacketOutCloudProxyChangeState.class)
                .registerHandler(new PacketOutRestAPIPut().getPacketUUID(), new HandlePacketOutRestAPIPut(), PacketOutRestAPIPut.class);

        this.eventDriver = new EventDriver();

        var group = (Group) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudgroup/" + service.getGroup()), Group.class);
        var players = (PlayerGeneral) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudplayer/genernal"), PlayerGeneral.class);
        players.getCloudplayers().forEach(s -> {
            if (!CloudAPI.getInstance().getPlayerPool().playerIsNotNull(s)) {
                getAsyncPlayerPool().registerPlayer(new AsyncCloudPlayer(s, Objects.requireNonNull(UUIDDriver.getUUID(s))));
                getPlayerPool().registerPlayer(new CloudPlayer(s, Objects.requireNonNull(UUIDDriver.getUUID(s))));
            }
        });

        if (!group.getGroupType().equals("PROXY")) {
            return;
        }

        if (isVelocity) {
            registerVelocityHandlers();
        } else {
            registerBungeeHandlers();
        }

        NettyDriver.getInstance().nettyClient.sendPacketSynchronized(new PacketInServiceConnect(service.getService()));

        new TimerBase().schedule(new TimerTask() {
            @Override
            public void run() {
                var service = CloudAPI.getInstance().getServicePool().getService(CloudAPI.getInstance().getCurrentService().getService());
                var list = (LiveServiceList) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudservice/general"), LiveServiceList.class);
                var general = (PlayerGeneral) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudplayer/genernal"), PlayerGeneral.class);
                general.getCloudplayers().forEach(it -> {
                    if (!CloudAPI.getInstance().getPlayerPool().playerIsNotNull(it)) {
                        getAsyncPlayerPool().registerPlayer(new AsyncCloudPlayer(it, Objects.requireNonNull(UUIDDriver.getUUID(it))));
                        getPlayerPool().registerPlayer(new CloudPlayer(it, Objects.requireNonNull(UUIDDriver.getUUID(it))));
                    }
                });
                playerPool.getPlayers().stream().filter(cloudPlayer -> general.getCloudplayers().stream().noneMatch(s -> s.equalsIgnoreCase(cloudPlayer.getUniqueId().toString()))).toList().forEach(cloudPlayer -> {
                    getPlayerPool().unregisterPlayer(cloudPlayer.getUniqueId());
                    getAsyncPlayerPool().unregisterPlayer(cloudPlayer.getUniqueId());
                });

                servicePool.getServices().stream().filter(cloudService -> list.getCloudServices().stream().noneMatch(s -> s.equalsIgnoreCase(cloudService.getName()))).toList().forEach(cloudService -> {
                    getServicePool().unregisterService(cloudService.getName());
                    getAsyncServicePool().unregisterService(cloudService.getName());
                });

                if (!NettyDriver.getInstance().nettyClient.getChannel().isOpen()) {
                    System.exit(0);
                }

                var liveServices = (LiveServices) new ConfigDriver().convert(restDriver.get("/cloudservice/" + service.getName().replace(list.getCloudServiceSplitter(), "~")), LiveServices.class);
                liveServices.setLastReaction(System.currentTimeMillis());
                getRestDriver().update("/cloudservice/" + service.getName().replace(list.getCloudServiceSplitter(), "~"), new ConfigDriver().convert(liveServices));

            }
        }, 30, 30, TimeUtil.SECONDS);
    }

    private void registerVelocityHandlers() {
        eventDriver.registerListener(new eu.metacloudservice.bootstrap.velocity.listener.CloudEvents());
        NettyDriver.getInstance().getPacketDriver()
                .registerHandler(new PacketOutAPIPlayerConnect().getPacketUUID(), new eu.metacloudservice.bootstrap.velocity.networking.HandlePacketOutAPIPlayerConnect(), PacketOutAPIPlayerConnect.class)
                .registerHandler(new PacketOutAPIPlayerMessage().getPacketUUID(), new eu.metacloudservice.bootstrap.velocity.networking.HandlePacketOutAPIPlayerMessage(), PacketOutAPIPlayerMessage.class)
                .registerHandler(new PacketOutAPIPlayerTitle().getPacketUUID(), new eu.metacloudservice.bootstrap.velocity.networking.HandlePacketOutAPIPlayerTitle(), PacketOutAPIPlayerTitle.class)
                .registerHandler(new PacketOutAPIPlayerActionBar().getPacketUUID(), new eu.metacloudservice.bootstrap.velocity.networking.HandlePacketOutAPIPlayerActionBar(), PacketOutAPIPlayerActionBar.class)
                .registerHandler(new PacketOutAPIPlayerDispactchCommand().getPacketUUID(), new eu.metacloudservice.bootstrap.velocity.networking.HandlePacketOutAPIPlayerDispactchCommand(), PacketOutAPIPlayerDispactchCommand.class)
                .registerHandler(new PacketOutAPIPlayerKick().getPacketUUID(), new eu.metacloudservice.bootstrap.velocity.networking.HandlePacketOutAPIPlayerKick(), PacketOutAPIPlayerKick.class)
                .registerHandler(new PacketOutCloudPlayerComponent().getPacketUUID(), new eu.metacloudservice.bootstrap.velocity.networking.HandlePacketOutCloudPlayerComponent(), PacketOutCloudPlayerComponent.class)
                .registerHandler(new PacketOutAPIPlayerTab().getPacketUUID(), new eu.metacloudservice.bootstrap.velocity.networking.HandlePacketOutAPIPlayerTab(), PacketOutAPIPlayerTab.class);
    }

    private void registerBungeeHandlers() {
        eventDriver.registerListener(new CloudEvents());
        NettyDriver.getInstance().getPacketDriver()
                .registerHandler(new PacketOutAPIPlayerConnect().getPacketUUID(), new HandlePacketOutAPIPlayerConnect(), PacketOutAPIPlayerConnect.class)
                .registerHandler(new PacketOutAPIPlayerMessage().getPacketUUID(), new HandlePacketOutAPIPlayerMessage(), PacketOutAPIPlayerMessage.class)
                .registerHandler(new PacketOutAPIPlayerTitle().getPacketUUID(), new HandlePacketOutAPIPlayerTitle(), PacketOutAPIPlayerTitle.class)
                .registerHandler(new PacketOutAPIPlayerActionBar().getPacketUUID(), new HandlePacketOutAPIPlayerActionBar(), PacketOutAPIPlayerActionBar.class)
                .registerHandler(new PacketOutAPIPlayerKick().getPacketUUID(), new HandlePacketOutAPIPlayerKick(), PacketOutAPIPlayerKick.class)
                .registerHandler(new PacketOutCloudPlayerComponent().getPacketUUID(), new HandlePacketOutCloudPlayerComponent(), PacketOutCloudPlayerComponent.class)
                .registerHandler(new PacketOutAPIPlayerDispactchCommand().getPacketUUID(), new HandlePacketOutAPIPlayerDispactchCommand(), PacketOutAPIPlayerDispactchCommand.class)
                .registerHandler(new PacketOutAPIPlayerTab().getPacketUUID(), new HandlePacketOutAPIPlayerTab(), PacketOutAPIPlayerTab.class);
    }

    /**
     * Sends a command for execution to a cloud service.
     *
     * @param command The command to execute.
     */
    public void dispatchCommand(String command) {
        sendPacketSynchronized(new PacketInDispatchMainCommand(command));
    }

    /**
     * Retrieves the current live service.
     *
     * @return The current live service.
     */
    public LiveService getCurrentService() {
        return service;
    }

    /**
     * Loads the message configuration
     *
     * @return The loaded message configuration.
     */
    public Messages getMessages() {
        return (Messages) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/message/default"), Messages.class);
    }

    /**
     * Retrieves the whitelist of the cloud.
     *
     * @return The whitelist of the cloud.
     */
    public List<String> getWhitelist() {
        var whiteList = (WhiteList) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/default/whitelist"), WhiteList.class);
        return whiteList.getWhitelist();
    }

    /**
     * Adds a user to the whitelist.
     *
     * @param username The username to add to the whitelist.
     */
    public void addWhiteList(String username) {
        if (getWhitelist().stream().noneMatch(it -> it.equals(username))) {
            CloudAPI.getInstance().sendPacketSynchronized(new PacketInCommandWhitelist(username));
        }
    }

    /**
     * Removes a user from the whitelist.
     *
     * @param username The username to remove from the whitelist.
     */
    public void removeWhiteList(String username) {
        if (getWhitelist().stream().anyMatch(it -> it.equals(username))) {
            if (this.playerPool.getPlayer(username) != null) {
                this.playerPool.getPlayer(username).disconnect(getMessages().getMessages().get("kickNetworkIsMaintenance"));
            }
            sendPacketSynchronized(new PacketInCommandWhitelist(username));
        }
    }

    /**
     * Registers an event listener
     *
     * @param eventListener The event listener to register.
     */
    public void registerListener(ICloudListener eventListener) {
        this.eventDriver.registerListener(eventListener);
    }

    /**
     * Sets the state of a cloud service.
     *
     * @param state The state to set.
     * @param name  The name of the service.
     */
    public void setState(@NonNull ServiceState state, String name) {
        sendPacketSynchronized(new PacketInChangeState(name, state.toString()));
    }

    /**
     * Sets the state of a cloud service.
     *
     * @param state The state to set.
     */
    public void setState(ServiceState state) {
        setState(state, getCurrentService().getService());
    }

    /**
     * Retrieves the amount of used memory by the Cloud.
     *
     * @return The amount of used memory in megabytes.
     */
    public double getUsedMemory() {
        return (double) ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576;
    }

    /**
     * Retrieves the maximum amount of memory that the Cloud can use.
     *
     * @return The maximum amount of memory in megabytes.
     */
    public double getMaxMemory() {
        return (double) ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1048576;
    }

    /**
     * Sends a packet to the cloud service synchronously.
     *
     * @param packet The packet to send.
     */
    public void sendPacketSynchronized(Packet packet) {
        NettyDriver.getInstance().nettyClient.sendPacketSynchronized(packet);
    }

    /**
     * Sends a packet to the cloud service asynchronously.
     *
     * @param packet The packet to send.
     */
    public void sendPacketAsynchronous(Packet packet) {
        NettyDriver.getInstance().nettyClient.sendPacketsAsynchronous(packet);
    }
}
