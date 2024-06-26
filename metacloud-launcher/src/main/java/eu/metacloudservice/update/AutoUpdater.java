/*
 * this class is by RauchigesEtwas
 */

package eu.metacloudservice.update;

import eu.metacloudservice.Driver;
import eu.metacloudservice.storage.ModuleLoader;
import eu.metacloudservice.terminal.animation.AnimationDriver;
import eu.metacloudservice.terminal.enums.Type;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AutoUpdater {

    public AutoUpdater() {

        Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLang().getMessage("update-check-if-update-found"));
        if (Driver.getInstance().getMessageStorage().checkAvailableUpdate()){
            Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLang().getMessage("update-is-found")
                    .replace("%current_version%", Driver.getInstance().getMessageStorage().version)
                    .replace("%new_version%", Driver.getInstance().getMessageStorage().getNewVersionName()));
            try {
                URLConnection urlConnection = new URL("https://metacloudservice.eu/download/UPDATE.jar").openConnection();
                urlConnection.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                urlConnection.connect();
                Files.copy(urlConnection.getInputStream(), Paths.get("./UPDATE.jar"));
            } catch (IOException ignored) {
            }
            new AnimationDriver().play();
            new File("./local/GLOBAL/EVERY/plugins/metacloud-api.jar").deleteOnExit();
            new File("./local/GLOBAL/EVERY/plugins/metacloud-plugin.jar").deleteOnExit();
            new File("./dependency/runnable-manager.jar").deleteOnExit();
            new File("./dependency/runnable-node.jar").deleteOnExit();
            Driver.getInstance().getTerminalDriver().log(Type.INFO, "Update §fmetacloud-plugin.jar...");
            try {
                URLConnection urlConnection = new URL("https://metacloudservice.eu/download/metacloud-plugin.jar").openConnection();
                urlConnection.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                urlConnection.connect();
                Files.copy(urlConnection.getInputStream(), Paths.get("./local/GLOBAL/EVERY/plugins/metacloud-plugin.jar"));
            } catch (IOException ignored) {
            }
            new AnimationDriver().play();
            Driver.getInstance().getTerminalDriver().log(Type.INFO, "Update §fmetacloud-api.jar...");
            try {
                URLConnection urlConnection = new URL("https://metacloudservice.eu/download/metacloud-api.jar").openConnection();
                urlConnection.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                urlConnection.connect();
                Files.copy(urlConnection.getInputStream(), Paths.get("./local/GLOBAL/EVERY/plugins/metacloud-api.jar"));
            } catch (IOException ignored) {
            }
            new AnimationDriver().play();
            new File("./Launcher.jar").renameTo(new File("./OLD.jar"));
            new File("./UPDATE.jar").renameTo(new File("./Launcher.jar"));
            if (new File("./modules/").exists()){
                new ModuleLoader().updateAllModules();
            }
            Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLang().getMessage("update-is-finished"));
            new File("./local/storage/messages.storage").deleteOnExit();
            System.exit(0);
        }else {
            Driver.getInstance().getTerminalDriver().log(Type.INFO, Driver.getInstance().getLanguageDriver().getLang().getMessage("update-no-update-was-found"));
        }

    }
}
