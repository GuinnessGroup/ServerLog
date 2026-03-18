package kr.guinnessgroup.serverLog.utils;

import kr.guinnessgroup.serverLog.ServerLog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;

public class ServerLogUtils {
    private final ServerLog serverLog;

    public ServerLogUtils(ServerLog serverLog) {
        this.serverLog = serverLog;
    }

    public void appendString(String path, String configString) {
        SimpleDateFormat dateFileFormat = new SimpleDateFormat(Objects.requireNonNull(getConfigFile().getString("time.file")));
        SimpleDateFormat dateFullFormat = new SimpleDateFormat(Objects.requireNonNull(getConfigFile().getString("time.full")));
        SimpleDateFormat dateNormalFormat = new SimpleDateFormat(Objects.requireNonNull(getConfigFile().getString("time.normal")));
        Date now = new Date();

        File folder = new File(serverLog.getDataFolder() + path);
        if (folder.mkdirs()) {
            serverLog.getLogger().info("Folder created: " + folder.getPath());
        }

        File file = new File(folder, dateFileFormat.format(now) + ".txt");
        try {
            if (file.createNewFile()) {
                serverLog.getLogger().info("File created: " + file.getPath());
            }
        } catch (IOException e) {
            serverLog.getLogger().log(Level.SEVERE, "Failed to " + "create the file [" + path + "].", e.getStackTrace());
        }

        try (
                FileWriter fileWriter = new FileWriter(file, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
        ) {
            bufferedWriter.append(configString
                    .replace("[time.normal]", "[" + dateNormalFormat.format(now) + "]")
                    .replace("[time.full]", "[" + dateFullFormat.format(now) + "]")
            );
            bufferedWriter.newLine();
        } catch (IOException e) {
            serverLog.getLogger().log(Level.SEVERE, "Failed to " + "write to the file [" + path + "].", e.getStackTrace());
        }
    }

    public FileConfiguration getConfigFile() {
        String lang = serverLog.getConfig().getString("lang");
        try (InputStream is = serverLog.getResource("lang/" + lang + ".yml")) {
            InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8);
            return YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            serverLog.getLogger().log(Level.SEVERE, "The language file does not exist.");
            Bukkit.getPluginManager().disablePlugin(serverLog);
            throw new IllegalStateException("The language file does not exist");
        }
    }

    public String toPlainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
