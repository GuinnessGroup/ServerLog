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
    private final FileConfiguration langConfig;
    private final SimpleDateFormat dateFileFormat;
    private final SimpleDateFormat dateFullFormat;
    private final SimpleDateFormat dateNormalFormat;

    public ServerLogUtils(ServerLog serverLog) {
        this.serverLog = serverLog;
        this.langConfig = loadLangConfig();
        this.dateFileFormat = new SimpleDateFormat(Objects.requireNonNull(langConfig.getString("time.file")));
        this.dateFullFormat = new SimpleDateFormat(Objects.requireNonNull(langConfig.getString("time.full")));
        this.dateNormalFormat = new SimpleDateFormat(Objects.requireNonNull(langConfig.getString("time.normal")));
    }

    private FileConfiguration loadLangConfig() {
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

    public String template(Message message) {
        String value = langConfig.getString(message.getLangKey());
        if (value == null) throw new IllegalStateException("Missing lang key: " + message.getLangKey());
        return value;
    }

    public boolean isFileEnabled() {
        return serverLog.getConfig().getStringList("output").contains("file");
    }

    public boolean isDatabaseEnabled() {
        return serverLog.getConfig().getStringList("output").contains("database");
    }

    public void appendString(String path, String configString) {
        if (!isFileEnabled()) return;
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
            serverLog.getLogger().log(Level.SEVERE, "Failed to create the file [" + path + "].", e);
            return;
        }

        try (
                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8);
                BufferedWriter bufferedWriter = new BufferedWriter(osw)
        ) {
            bufferedWriter.append(configString
                    .replace("[time.normal]", "[" + dateNormalFormat.format(now) + "]")
                    .replace("[time.full]", "[" + dateFullFormat.format(now) + "]")
            );
            bufferedWriter.newLine();
        } catch (IOException e) {
            serverLog.getLogger().log(Level.SEVERE, "Failed to write to the file [" + path + "].", e);
        }
    }

    public String toPlainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
