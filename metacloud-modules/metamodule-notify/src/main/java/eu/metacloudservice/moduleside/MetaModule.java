package eu.metacloudservice.moduleside;

import eu.metacloudservice.config.Configuration;
import eu.metacloudservice.Driver;
import eu.metacloudservice.config.General;
import eu.metacloudservice.configuration.ConfigDriver;
import eu.metacloudservice.module.extention.IModule;
import eu.metacloudservice.webserver.entry.RouteEntry;

import java.io.File;

public class MetaModule implements IModule {
    @Override
    public void load() {
        create();
        set();
    }

    @Override
    public void unload() {
        Driver.getInstance().getWebServer().removeRoute("/module/notify/general");
        Driver.getInstance().getWebServer().removeRoute("/module/notify/configuration");
    }

    @Override
    public void reload() {
        create();
        update();

    }

    public void create(){
        if (!new File("./modules/notify/config.json").exists()){
            new File("./modules/notify/").mkdirs();
            try {
                Configuration configuration = new Configuration();
                configuration.setProxiedServiceConnected("§8[§a▷§8] §7service: §a%service_name% §7was §aconnected §8| §b%node_name%");
                configuration.setProxiedServiceDiconnected("§8[§c◁§8] §7service: §c%service_name% §7was §cdisconnected");
                configuration.setProxiedServicePrepared("§8[§e▷§8] §7service: §e%service_name% §7was §ePrepared §8| §b%node_name%");
                configuration.setServiceConnected("§8[§a▷§8] §7service: §a%service_name% §7was §aconnected §8| §b%node_name%");
                configuration.setServiceDiconnected("§8[§c◁§8] §7service: §c%service_name% §7was §cdisconnected");
                configuration.setServicePrepared("§8[§e▷§8] §7service: §e%service_name% §7was §ePrepared §8| §b%node_name%");
                new ConfigDriver("./modules/notify/config.json").save(configuration);

            }catch (Exception e){
                create();
                set();
            }
        }
    }

    public void set(){


        General general = new General("notify", "1.0.0", "RauchigesEtwas");
        Driver.getInstance().getWebServer().addRoute(new RouteEntry("/module/notify/general", new ConfigDriver().convert(general)));

        try {
            Configuration configuration = (Configuration) new ConfigDriver("./modules/notify/config.json").read(Configuration.class);
            Configuration rest = new Configuration();
            rest.setProxiedServiceConnected(Driver.getInstance().getMessageStorage().utf8ToUBase64(configuration.getProxiedServiceConnected()));
            rest.setProxiedServiceDiconnected(Driver.getInstance().getMessageStorage().utf8ToUBase64(configuration.getProxiedServiceDiconnected()));
            rest.setProxiedServicePrepared(Driver.getInstance().getMessageStorage().utf8ToUBase64(configuration.getProxiedServicePrepared()));
            rest.setServiceConnected(Driver.getInstance().getMessageStorage().utf8ToUBase64(configuration.getServiceConnected()));
            rest.setServiceDiconnected(Driver.getInstance().getMessageStorage().utf8ToUBase64(configuration.getServiceDiconnected()));
            rest.setServicePrepared(Driver.getInstance().getMessageStorage().utf8ToUBase64(configuration.getServicePrepared()));
            Driver.getInstance().getWebServer().addRoute(new RouteEntry("/module/notify/configuration", new ConfigDriver().convert(rest)));
        }catch (Exception e){
            new File("./modules/notify/config.json").deleteOnExit();
            create();
            set();
        }
    }

    public void update(){
        try {
            Configuration configuration = (Configuration) new ConfigDriver("./modules/notify/config.json").read(Configuration.class);
            Configuration rest = new Configuration();
            rest.setProxiedServiceConnected(Driver.getInstance().getMessageStorage().utf8ToUBase64(configuration.getProxiedServiceConnected()));
            rest.setProxiedServiceDiconnected(Driver.getInstance().getMessageStorage().utf8ToUBase64(configuration.getProxiedServiceDiconnected()));
            rest.setProxiedServicePrepared(Driver.getInstance().getMessageStorage().utf8ToUBase64(configuration.getProxiedServicePrepared()));
            rest.setServiceConnected(Driver.getInstance().getMessageStorage().utf8ToUBase64(configuration.getServiceConnected()));
            rest.setServiceDiconnected(Driver.getInstance().getMessageStorage().utf8ToUBase64(configuration.getServiceDiconnected()));
            rest.setServicePrepared(Driver.getInstance().getMessageStorage().utf8ToUBase64(configuration.getServicePrepared()));
            Driver.getInstance().getWebServer().updateRoute("/module/notify/configuration", new ConfigDriver().convert(rest));
        }catch (Exception e){
            new File("./modules/notify/config.json").deleteOnExit();
            create();
            update();
        }
    }
}
