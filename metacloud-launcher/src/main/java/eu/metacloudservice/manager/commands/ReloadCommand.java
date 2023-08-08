package eu.metacloudservice.manager.commands;

import eu.metacloudservice.Driver;
import eu.metacloudservice.configuration.ConfigDriver;
import eu.metacloudservice.configuration.dummys.managerconfig.ManagerConfig;
import eu.metacloudservice.configuration.dummys.managerconfig.ManagerConfigNodes;
import eu.metacloudservice.configuration.dummys.message.Messages;
import eu.metacloudservice.manager.CloudManager;
import eu.metacloudservice.terminal.commands.CommandAdapter;
import eu.metacloudservice.terminal.commands.CommandInfo;
import eu.metacloudservice.terminal.enums.Type;
import eu.metacloudservice.terminal.utils.TerminalStorageLine;
import eu.metacloudservice.webserver.dummys.Addresses;
import eu.metacloudservice.webserver.dummys.GroupList;
import eu.metacloudservice.webserver.dummys.WhiteList;
import eu.metacloudservice.webserver.entry.RouteEntry;

import java.util.ArrayList;
import java.util.stream.Collectors;

@CommandInfo(command = "reload", DEdescription = "lade alle module sowie weiter daten neu", ENdescription = "reload all modules and further data", aliases = {"rl"})
public class ReloadCommand extends CommandAdapter {


    @Override
    public void performCommand(CommandAdapter command, String[] args) {


        if (args.length == 0){
            sendHelp();
        }else if (args[0].equalsIgnoreCase("all")){
            Driver.getInstance().getModuleDriver().reload();
            Messages msg = (Messages) new ConfigDriver("./local/messages.json").read(Messages.class);



            WhiteList whitelistConfig = new WhiteList();
            CloudManager.config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);

            whitelistConfig.setWhitelist(CloudManager.config.getWhitelist());
            Driver.getInstance().getWebServer().updateRoute("/default/whitelist", new ConfigDriver().convert(whitelistConfig));
            Addresses AddressesConfig = new Addresses();

            ArrayList<String> addresses = CloudManager.config.getNodes().stream().map(ManagerConfigNodes::getAddress).collect(Collectors.toCollection(ArrayList::new));
            AddressesConfig.setWhitelist(addresses);
            Driver.getInstance().getWebServer().updateRoute("/default/addresses", new ConfigDriver().convert(AddressesConfig));
            GroupList groupList = new GroupList();
            groupList.setGroups(Driver.getInstance().getGroupDriver().getAllStrings());
            Driver.getInstance().getWebServer().updateRoute("/cloudgroup/general", new ConfigDriver().convert(groupList));

            Driver.getInstance().getWebServer().updateRoute("/message/default", new ConfigDriver().convert(msg));
            try {
                Driver.getInstance().getGroupDriver().getAll().forEach(group -> {
                    if (Driver.getInstance().getWebServer().getRoute("/cloudgroup/" +group.getGroup()) == null){
                        Driver.getInstance().getWebServer().addRoute(new RouteEntry("/cloudgroup/" + group.getGroup(), new ConfigDriver().convert(group)));

                    }else {
                        Driver.getInstance().getWebServer().updateRoute("/cloudgroup/"  + group.getGroup(), new ConfigDriver().convert(group));
                    }
                });
            }catch (Exception ignored){}
            Driver.getInstance().getTerminalDriver().logSpeed(Type.COMMAND, "Die Cloud wurde mit Erfolg neu geladen", "The cloud was reloaded with success");

        }else if (args[0].equalsIgnoreCase("config")){



            Messages msg = (Messages) new ConfigDriver("./local/messages.json").read(Messages.class);



            WhiteList whitelistConfig = new WhiteList();
            CloudManager.config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);

            whitelistConfig.setWhitelist(CloudManager.config.getWhitelist());
            Driver.getInstance().getWebServer().updateRoute("/default/whitelist", new ConfigDriver().convert(whitelistConfig));
            Addresses AddressesConfig = new Addresses();

            ArrayList<String> addresses = CloudManager.config.getNodes().stream().map(ManagerConfigNodes::getAddress).collect(Collectors.toCollection(ArrayList::new));
            AddressesConfig.setWhitelist(addresses);
            Driver.getInstance().getWebServer().updateRoute("/default/addresses", new ConfigDriver().convert(AddressesConfig));
            GroupList groupList = new GroupList();
            groupList.setGroups(Driver.getInstance().getGroupDriver().getAllStrings());
            Driver.getInstance().getWebServer().updateRoute("/cloudgroup/general", new ConfigDriver().convert(groupList));

            Driver.getInstance().getWebServer().updateRoute("/message/default", new ConfigDriver().convert(msg));
            try {
                Driver.getInstance().getGroupDriver().getAll().forEach(group -> {
                    if (Driver.getInstance().getWebServer().getRoute("/cloudgroup/" +group.getGroup()) == null){
                        Driver.getInstance().getWebServer().addRoute(new RouteEntry("/cloudgroup/" + group.getGroup(), new ConfigDriver().convert(group)));

                    }else {
                        Driver.getInstance().getWebServer().updateRoute("/cloudgroup/"  + group.getGroup(), new ConfigDriver().convert(group));
                    }
                });
            }catch (Exception ignored){}
            Driver.getInstance().getTerminalDriver().logSpeed(Type.COMMAND, "Die Cloud wurde mit Erfolg neu geladen", "The cloud was reloaded with success");

        }else if (args[0].equalsIgnoreCase("modules")){
            Driver.getInstance().getModuleDriver().reload();
            Driver.getInstance().getTerminalDriver().logSpeed(Type.COMMAND, "Die Cloud wurde mit Erfolg neu geladen", "The cloud was reloaded with success");

        }
        else {
            sendHelp();
        }


    }

    @Override
    public ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args) {
        ArrayList<String> returns = new ArrayList<>();
        returns.add("all");
        returns.add("config");
        returns.add("modules");
        return returns;
    }

    public void sendHelp(){
        Driver.getInstance().getTerminalDriver().logSpeed(Type.COMMAND,
                " >> §freload all §7~ Um alles zu neu zuladen",
                " >> §freload all §7~ To reload everything");
        Driver.getInstance().getTerminalDriver().logSpeed(Type.COMMAND,
                " >> §freload config §7~ Um alle Konfigurationen neu zu laden",
                " >> §freload config §7~ To reload all configurations");
        Driver.getInstance().getTerminalDriver().logSpeed(Type.COMMAND,
                " >> §freload modules §7~ Um alle Module neu zu laden",
                " >> §freload modules §7~ To reload all modules");
    }
}
