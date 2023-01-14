package eu.themetacloudservice.manager;

import eu.themetacloudservice.Driver;
import eu.themetacloudservice.configuration.ConfigDriver;
import eu.themetacloudservice.configuration.dummys.authenticator.AuthenticatorKey;
import eu.themetacloudservice.configuration.dummys.managerconfig.ManagerConfig;
import eu.themetacloudservice.manager.cloudtasks.TaskDriver;
import eu.themetacloudservice.manager.cloudtasks.dummy.TaskGroup;
import eu.themetacloudservice.manager.cloudtasks.dummy.TaskService;
import eu.themetacloudservice.manager.commands.ClearCommand;
import eu.themetacloudservice.manager.commands.GroupCommand;
import eu.themetacloudservice.manager.commands.HelpCommand;
import eu.themetacloudservice.manager.commands.StopCommand;
import eu.themetacloudservice.manager.networking.ManagerNetworkChannel;
import eu.themetacloudservice.network.autentic.PackageAuthenticByManager;
import eu.themetacloudservice.network.autentic.PackageAuthenticRequestFromManager;
import eu.themetacloudservice.network.autentic.PackageCallBackAuthenticByManager;
import eu.themetacloudservice.network.tasks.PackageLaunchTask;
import eu.themetacloudservice.network.tasks.PackageStopNodes;
import eu.themetacloudservice.network.tasks.PackageStopTask;
import eu.themetacloudservice.networking.NettyDriver;
import eu.themetacloudservice.networking.server.NettyServer;
import eu.themetacloudservice.terminal.enums.Type;

import java.io.File;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CloudManager {

    public static TaskDriver taskDriver;


    public CloudManager(){

        ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);
        System.setProperty("log4j.configurationFile", "log4j2.properties");
        initNetty(config);
        taskDriver = new TaskDriver();
        if (!new File("./connection.key").exists()){
            AuthenticatorKey key = new AuthenticatorKey();
            String  k = Driver.getInstance().getMessageStorage().utf8ToUBase64(UUID.randomUUID().toString() + UUID.randomUUID().toString()+ UUID.randomUUID().toString()+ UUID.randomUUID().toString()+ UUID.randomUUID().toString()+ UUID.randomUUID().toString()+ UUID.randomUUID().toString()+ UUID.randomUUID().toString()+ UUID.randomUUID().toString());
            key.setKey(k);
            new ConfigDriver("./connection.key").save(key);
        }
        if (!new File("./local/server-icon.png").exists()){
            Driver.getInstance().getMessageStorage().packetLoader.loadLogo();
        }
        AuthenticatorKey authConfig = (AuthenticatorKey) new ConfigDriver("./connection.key").read(AuthenticatorKey.class);
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new HelpCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new GroupCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new ClearCommand());
        Driver.getInstance().getTerminalDriver().getCommandDriver().registerCommand(new StopCommand());
        new File("./modules/").mkdirs();
        new File("./local/GLOBAL/").mkdirs();
        new File("./local/groups/").mkdirs();
        new File("./local/templates/").mkdirs();
        Driver.getInstance().getModuleDriver().loadAllModules();
        Driver.getInstance().createQueue();


        Driver.getInstance().getTerminalDriver().logSpeed(Type.INFORMATION, "die Cloud erfolgreich gestartet ist, können Sie sie von nun an mit '§fhelp§r' nutzen.",
                "the cloud is successfully started, you can use it from now on with '§fhelp§r'.");


        Driver.getInstance().getGroupDriver().getByNode("InternalNode").stream().collect(Collectors.toList()).forEach(group -> {
            for (int i = 0; i != group.getMinimalOnline() ; i++) {
                taskDriver.launch(group.getGroup());
            }
        });
    }


    public void initNetty(ManagerConfig config){
        new NettyDriver();
        Driver.getInstance().getTerminalDriver().logSpeed(Type.NETWORK, "der Netty-Server wird vorbereitet und dann gestartet", "the Netty server is prepared and then started");
        NettyDriver.getInstance().nettyServer = new NettyServer();
        NettyDriver.getInstance().nettyServer.bind(config.getNetworkingCommunication()).start();

        //PACKETS
        NettyDriver.getInstance().packetDriver
                .handelPacket(PackageLaunchTask.class)
                .handelPacket(PackageStopTask.class)
                .handelPacket(PackageAuthenticByManager.class)
                .handelPacket(PackageCallBackAuthenticByManager.class)
                .handelPacket(PackageAuthenticRequestFromManager.class)
                .handelPacket(PackageStopNodes.class)
                .handelListener(new ManagerNetworkChannel());
        Driver.getInstance().getTerminalDriver().logSpeed(Type.NETWORK, "der '§fNetty-Server§r' wurde erfolgreich an Port '§f"+config.getNetworkingCommunication()+"§r' angebunden", "the '§fNetty-server§r' was successfully bound on port '§f"+config.getNetworkingCommunication()+"§r'");

    }


    public static void shutdownHook(){

        taskDriver.getRegisteredGroups().forEach(taskGroup -> taskGroup.getTasks().stream().filter(taskService -> taskService.getCloudProcess().getGroup().getStorage().getRunningNode().equals("InternalNode")).forEach(tss -> {
            tss.getCloudProcess().shutdown();
        }));


        NettyDriver.getInstance().nettyServer.sendToAllPackets(new PackageStopNodes());


        System.exit(0);
    }
}
