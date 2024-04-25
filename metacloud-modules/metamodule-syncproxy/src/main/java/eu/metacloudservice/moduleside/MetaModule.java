package eu.metacloudservice.moduleside;

import com.google.common.primitives.Bytes;
import eu.metacloudservice.config.*;
import eu.metacloudservice.Driver;
import eu.metacloudservice.config.migration.MigrationConfiguration;
import eu.metacloudservice.configuration.ConfigDriver;
import eu.metacloudservice.configuration.dummys.restapi.GeneralConfig;
import eu.metacloudservice.configuration.interfaces.IConfigAdapter;
import eu.metacloudservice.module.extention.IModule;
import eu.metacloudservice.moduleside.converter.IconConverter;
import eu.metacloudservice.moduleside.events.SyncEvents;
import eu.metacloudservice.terminal.animation.AnimationDriver;
import eu.metacloudservice.terminal.enums.Type;
import eu.metacloudservice.timebaser.TimerBase;
import eu.metacloudservice.timebaser.utils.TimeUtil;
import eu.metacloudservice.webserver.entry.RouteEntry;
import lombok.SneakyThrows;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimerTask;

public class MetaModule implements IModule {
    @Override
    public void load() {
        create();
        set();
        Driver.getInstance().getMessageStorage().eventDriver.registerListener(new SyncEvents());
    }

    @Override
    public void unload() {
        Driver.getInstance().getWebServer().removeRoute("/module/syncproxy/general");
        Driver.getInstance().getWebServer().removeRoute("/module/syncproxy/configuration");
    }

    @Override
    public void reload() {
        create();
        update();
    }

    private static void create(){
            if (!new File("./modules/syncproxy/config.json").exists()) {
                new File("./modules/syncproxy/icons/").mkdirs();
                Configuration configuration = new Configuration();
                ArrayList<DesignConfig> configs = new ArrayList<>();
                Driver.getInstance().getGroupDriver().getAll().stream().filter(group -> group.getGroupType().equalsIgnoreCase("PROXY")).forEach(group -> {
                    DesignConfig config = new DesignConfig();
                    config.setMotdEnabled(true);
                    config.setTabEnabled(true);
                    config.setTargetGroup(group.getGroup());
                    ArrayList<Motd> maintenance = new ArrayList<>();

                    Motd maintenanceLayout = new Motd();
                    ArrayList<String> maintenancePlayerInfo = new ArrayList<>();
                    maintenanceLayout.setProtocol("§8▷ §c§lMaintenance");
                    maintenanceLayout.setFirstline("§8► §b§lMeta§3§lCloud §8▷ §7Ready §ffor the §7Future §8• §f§l1.16-1.20.✘");
                    maintenanceLayout.setSecondline("§8➥ ✎ §7We are in §f§lMAINTENANCE §8| §8◣ §b§l%proxy_name% §8◥");

                    maintenancePlayerInfo.add("");
                    maintenancePlayerInfo.add("§8► §b§lMeta§3§lCloud §8▷ §7Ready §ffor the §7Future");
                    maintenancePlayerInfo.add("");
                    maintenancePlayerInfo.add("  Our Discord §8▷ §b4kKEcaP9WC");
                    maintenancePlayerInfo.add("  Developed by §8▷ §b§lRauchigesEtwas");
                    maintenancePlayerInfo.add("");
                    maintenanceLayout.setPlayerinfos(maintenancePlayerInfo);

                    Motd maintenanceLayout02 = new Motd();

                    ArrayList<String> maintenancePlayerInfo02 = new ArrayList<>();
                    maintenanceLayout02.setProtocol("§8▷ §c§lMaintenance");
                    maintenanceLayout02.setFirstline("§8► §b§lMeta§3§lCloud §8▷ §7Ready §ffor the §7Future §8• §f§l1.16-1.20.✘");
                    maintenanceLayout02.setSecondline("§8➥ ✂ §7Download this §fCloudsystem §7on §b§lSPIGOT§3§lMC");

                    maintenancePlayerInfo02.add("");
                    maintenancePlayerInfo02.add("§8► §b§lMeta§3§lCloud §8▷ §7Ready §ffor the §7Future");
                    maintenancePlayerInfo02.add("");
                    maintenancePlayerInfo02.add("  Our Discord §8▷ §b4kKEcaP9WC");
                    maintenancePlayerInfo02.add("  Developed by §8▷ §b§lRauchigesEtwas");
                    maintenancePlayerInfo02.add("");
                    maintenanceLayout02.setPlayerinfos(maintenancePlayerInfo02);

                    maintenance.add(maintenanceLayout);
                    maintenance.add(maintenanceLayout02);

                    ArrayList<Motd> defaults = new ArrayList<>();

                    Motd defaultsLayout = new Motd();
                    ArrayList<String> defaultsPlayerInfo = new ArrayList<>();
                    defaultsLayout.setProtocol("§8▷ §b§l%online_players%§8/§b§l%max_players%");
                    defaultsLayout.setFirstline("§8► §b§lMeta§3§lCloud §8▷ §7Ready §ffor the §7Future §8• §f§l1.16-1.20.✘");
                    defaultsLayout.setSecondline("§8➥ ✂ §7Download this §fCloudsystem §7on §b§lSPIGOT§3§lMC");
                    defaultsPlayerInfo.add("");
                    defaultsPlayerInfo.add("§8► §b§lMeta§3§lCloud §8▷ §7Ready §ffor the §7Future");
                    defaultsPlayerInfo.add("");
                    defaultsPlayerInfo.add("  Our Discord §8▷ §b§l4kKEcaP9WC");
                    defaultsPlayerInfo.add("  Developed by §8▷ §b§lRauchigesEtwas");
                    defaultsPlayerInfo.add("");
                    defaultsLayout.setPlayerinfos(defaultsPlayerInfo);
                    Motd defaultsLayout02 = new Motd();

                    ArrayList<String> defaultsPlayerInfo02 = new ArrayList<>();
                    defaultsLayout02.setProtocol("§8▷ §b§l%online_players%§8/§b§l%max_players%");
                    defaultsLayout02.setFirstline("§8► §b§lMeta§3§lCloud §8▷ §7Ready §ffor the §7Future §8• §f§l1.16-1.20.✘");
                    defaultsLayout02.setSecondline("§8➥ ✎ §7The network now §f§lONLINE §8| §8◣ §b§l%proxy_name% §8◥");
                    defaultsPlayerInfo02.add("");
                    defaultsPlayerInfo02.add("§8► §b§lMeta§3§lCloud §8▷ §7Ready §ffor the §7Futuree");
                    defaultsPlayerInfo02.add("");
                    defaultsPlayerInfo02.add("  Our Discord §8▷ §b§l4kKEcaP9WC");
                    defaultsPlayerInfo02.add("  Developed by §8▷ §b§lRauchigesEtwas");
                    defaultsPlayerInfo02.add("");
                    defaultsLayout02.setPlayerinfos(defaultsPlayerInfo02);

                    defaults.add(defaultsLayout);
                    defaults.add(defaultsLayout02);

                    ArrayList<Tablist> tablist = new ArrayList<>();

                    Tablist tablayout01 = new Tablist();
                    tablayout01.setHeader("\n      §8► §b§lMeta§3§lCloud §8▷ §7Ready §ffor the §7Future §8• §b§l%online_players% §7/ §3§l%max_players% §8◄      \n   §8► §7Your current server §8▷ §b§l%service_name% §8◄   \n");

                    tablayout01.setFooter("\n        §8► §7Discord §8▷ §b§ldiscord.gg/§3§l4kKEcaP9WC §8◄       \n§8► §7Developer §8▷ §b§lReauchiges§3§lEtwas §8◄\n");

                    tablist.add(tablayout01);

                    Tablist tablayout02 = new Tablist();
                    tablayout02.setHeader("\n      §8► §b§lMeta§3§lCloud §8▷ §7Ready §ffor the §7Future §8• §b§l%online_players% §7/ §3§l%max_players% §8◄      \n   §8► §7Your current proxy §8▷ §b§l%proxy_name% §8◄   \n");
                    tablayout02.setFooter("\n        §8► §7Discord §8▷ §b§ldiscord.gg/§3§l4kKEcaP9WC §8◄       \n§8► §7Website §8▷ §b§lmetacloud§3§lservice.eu §8◄\n");

                    tablist.add(tablayout02);
                    config.setMaintenancen(maintenance);
                    config.setTablist(tablist);
                    config.setDefaults(defaults);
                    config.setServerIcon(new ServerIcon("maintenance.png", "default.png")); configs.add(config);
                    loader();
                });


                configuration.setConfiguration(configs);
                Driver.getInstance().getWebServer().addRoute(new RouteEntry("/module/syncproxy/configuration", new ConfigDriver().convert(configuration)));
                Driver.getInstance().getWebServer().updateRoute("/module/syncproxy/configuration", new ConfigDriver().convert(configuration));

                HashMap<String, String> icons = new HashMap<>();
                getIcons().forEach(s -> {
                    try {
                        icons.put(s, new IconConverter().convertToBase64("./modules/syncproxy/icons/" + s + ".png"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                Driver.getInstance().getWebServer().addRoute(new RouteEntry("/module/syncproxy/icons", new ConfigDriver().convert( new IconBase(icons))));


                new ConfigDriver("./modules/syncproxy/config.json").save(configuration);
                set();
                update();
            }else if (new File("./local/server-icon.png").exists()){
                new File("./local/server-icon.png").deleteOnExit();
                MigrationConfiguration migrationConfiguration = (MigrationConfiguration) new ConfigDriver("./modules/syncproxy/config.json").read(MigrationConfiguration.class);
                Configuration configuration = new Configuration();
                ArrayList<DesignConfig> configs = new ArrayList<>();
                migrationConfiguration.getConfiguration().forEach(designConfig -> {
                    DesignConfig config = new DesignConfig();
                    config.setTablist(designConfig.getTablist());
                    config.setDefaults(designConfig.getDefaults());
                    config.setMaintenancen(designConfig.getMaintenancen());
                    config.setTargetGroup(designConfig.getTargetGroup());
                    config.setServerIcon(new ServerIcon("maintenance.png", "default.png"));
                    configs.add(config);
                });
                configuration.setConfiguration(configs);
                Driver.getInstance().getWebServer().addRoute(new RouteEntry("/module/syncproxy/configuration", new ConfigDriver().convert(configuration)));
                Driver.getInstance().getWebServer().updateRoute("/module/syncproxy/configuration", new ConfigDriver().convert(configuration));

                new File("./modules/syncproxy/icons/").mkdirs();
                new File("./modules/syncproxy/config.json").delete();
                new ConfigDriver("./modules/syncproxy/config.json").save(configuration);
                loader();

                HashMap<String, String> icons = new HashMap<>();
                getIcons().forEach(s -> {
                    try {
                        icons.put(s, new IconConverter().convertToBase64("./modules/syncproxy/icons/" + s + ".png"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                Driver.getInstance().getWebServer().addRoute(new RouteEntry("/module/syncproxy/icons", new ConfigDriver().convert( new IconBase(icons))));

            }

        new TimerBase().scheduleAsync(new TimerTask() {
            @Override
            public void run() {
                set();
                update();
            }
        }, 5, TimeUtil.SECONDS);

    }
    
    public static void set(){
        if (Driver.getInstance().getWebServer().getRoute("/module/syncproxy/configuration") != null) return;
        Configuration config = (Configuration) new ConfigDriver("./modules/syncproxy/config.json").read(Configuration.class);
        Driver.getInstance().getWebServer().addRoute(new RouteEntry("/module/syncproxy/configuration", new ConfigDriver().convert(config)));
        HashMap<String, String> icons = new HashMap<>();
        getIcons().forEach(s -> {
            try {
                icons.put(s, new IconConverter().convertToBase64("./modules/syncproxy/icons/" + s + ".png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Driver.getInstance().getWebServer().addRoute(new RouteEntry("/module/syncproxy/icons", new ConfigDriver().convert( new IconBase(icons))));

    }

    public static void update(){

        try {

            Configuration config = (Configuration) new ConfigDriver("./modules/syncproxy/config.json").read(Configuration.class);

            Driver.getInstance().getWebServer().updateRoute("/module/syncproxy/configuration", new ConfigDriver().convert(config));
        }catch (Exception e){
            set();
            update();
        }

        HashMap<String, String> icons = new HashMap<>();
        getIcons().forEach(s -> {
            try {
                icons.put(s, new IconConverter().convertToBase64("./modules/syncproxy/icons/" + s + ".png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Driver.getInstance().getWebServer().updateRoute("/module/syncproxy/icons", new ConfigDriver().convert(new IconBase(icons)));


    }


    @SneakyThrows
    private static void loader(){
        try (InputStream inputStream = new URL("https://metacloudservice.eu/rest/?type=general").openStream()) {
            final StringBuilder builder = new StringBuilder();
            BufferedReader bufferedReader;

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            int counter;
            while ((counter = bufferedReader.read()) != -1) {
                builder.append((char) counter);
            }
            final String rawJson = builder.toString();
            GeneralConfig updateConfig = (GeneralConfig) new ConfigDriver().convert(rawJson, GeneralConfig.class);

            try (BufferedInputStream in = new BufferedInputStream(new URL(updateConfig.getLogo()).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream("./modules/syncproxy/icons/default.png")) {
                byte[] dataBuffer = new byte[1024];

                int bytesRead;

                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try (BufferedInputStream in = new BufferedInputStream(new URL("https://metacloudservice.eu/download/general/maintenance.png").openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream("./modules/syncproxy/icons/maintenance.png")) {
                byte[] dataBuffer = new byte[1024];

                int bytesRead;

                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    private  static ArrayList<String> getIcons() {
        File file = new File("./modules/syncproxy/icons/");
        File[] files = file.listFiles();
        ArrayList<String> modules = new ArrayList<>();
        for (int i = 0; i != Objects.requireNonNull(files).length; i++) {
            String FirstFilter = files[i].getName();
            if (FirstFilter.contains(".png")) {
                String group = FirstFilter.split(".png")[0];
                modules.add(group);
            }

        }
        return modules;
    }
}
