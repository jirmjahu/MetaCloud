package eu.metacloudservice.bootstrap.bungee.utils;

import eu.metacloudservice.bootstrap.bungee.BungeeBootstrap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ServerConfig {

    private static File file;
    private static Configuration bungeeConfig;
    private static boolean locked;

    static {
        setupConfig();
        if (locked) {
            ProxyServer.getInstance().getScheduler().schedule(BungeeBootstrap.getInstance(), ServerConfig::setupConfig, 5L, TimeUnit.SECONDS);
        }
    }

    public static void addToConfig(ServerInfo serverInfo) {
        if (locked) {
            return;
        }
        bungeeConfig.set("servers." + serverInfo.getName() + ".motd", serverInfo.getMotd().replace(ChatColor.COLOR_CHAR, '&'));
        bungeeConfig.set("servers." + serverInfo.getName() + ".address", serverInfo.getSocketAddress().toString());
        bungeeConfig.set("servers." + serverInfo.getName() + ".restricted", false);
        saveConfig();
    }


    public static void addToConfigLobby(ServerInfo serverInfo) {
        if (locked) {
            return;
        }

        List<String> b = bungeeConfig.getStringList("listeners.priorities");
        b.add(serverInfo.getName());
        bungeeConfig.set("listeners.priorities", b);

        bungeeConfig.set("servers." + serverInfo.getName() + ".motd", serverInfo.getMotd().replace(ChatColor.COLOR_CHAR, '&'));
        bungeeConfig.set("servers." + serverInfo.getName() + ".address", serverInfo.getSocketAddress().toString());
        bungeeConfig.set("servers." + serverInfo.getName() + ".restricted", false);
        saveConfig();
    }

    public static void remove(String lobby) {
        if (locked) {
            return;
        }
        List<String> b = bungeeConfig.getStringList("listeners.priorities");
        if (b.contains(lobby)) {
            b.remove(lobby);
        }
        bungeeConfig.set("listeners.priorities", b);

        bungeeConfig.set("servers." + lobby, null);
        saveConfig();

    }

    public static void removeFromConfig(ServerInfo serverInfo) {
        removeFromConfig(serverInfo.getName());
    }

    public static void removeFromConfig(String name) {
        if (locked) {
            return;
        }

        bungeeConfig.set("servers." + name, null);
        saveConfig();
    }

    private static void saveConfig() {
        if (locked) {
            return;
        }
        try {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(bungeeConfig, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setupConfig() {
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            file = new File(ProxyServer.getInstance().getPluginsFolder().getParentFile(), "config.yml");
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.ISO_8859_1);
            bungeeConfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }

                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (IOException ignored) {

            }
        }
        locked = bungeeConfig == null;
    }
}
