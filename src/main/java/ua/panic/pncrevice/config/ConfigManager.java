package ua.panic.pncrevice.config;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    private final JavaPlugin plugin;
    private final BukkitAudiences audiences;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ConfigManager(JavaPlugin plugin, BukkitAudiences audiences){
        this.plugin = plugin; this.audiences = audiences;
    }

    public FileConfiguration raw() { return plugin.getConfig(); }
    public BukkitAudiences audience(){ return audiences; }
    public MiniMessage mmEngine(){ return mm; }
    public Component mm(String raw){ return mm.deserialize(raw == null ? "" : raw); }

    public Location checkpoint() { return readLoc("checkpoint"); }
    public Location spawn() { return readLoc("spawn"); }

    public int defaultSeconds() { return raw().getInt("timer.default-seconds", 600); }
    public int addTimeSeconds() { return raw().getInt("timer.dop_time", 300); }
    public String actionbarFormatRaw() { return raw().getString("timer.actionbar.format", "<gray>%mm:ss%</gray>"); }
    public String actionbarPausedChat() { return raw().getString("timer.actionbar.paused-chat", "<yellow>Проверка активна.</yellow>"); }

    public boolean punishOnQuit() { return raw().getBoolean("punish.on-quit.enabled", true); }
    public int punishGraceSeconds() { return raw().getInt("punish.on-quit.grace-seconds", 60); }
    public String punishCommand() { return raw().getString("punish.on-quit.command", "ban %player% 7d Покинул проверку"); }
    public String manualBanCommand() { return raw().getString("punish-gui.manual-ban.command", "ban %player% 7d Читы (revice)"); }

    public String chatPrefixRaw() { return raw().getString("review-chat.prefix"); }
    public String roleSuspectRaw() { return raw().getString("review-chat.role.suspect"); }
    public String roleModerRaw() { return raw().getString("review-chat.role.moder"); }
    public String chatFormatRaw() { return raw().getString("review-chat.format"); }

    public String perm(String key) { return raw().getString("permissions."+key); }

    public String cmdUsage(){ return raw().getString("messages.command.usage", "<yellow>Использование: </yellow>/revice <player> или /revice setposs"); }
    public String cmdInvalid(){ return raw().getString("messages.command.invalid", "<red>Неверная команда или синтаксис.</red>"); }
    public String cmdNoPerm(){ return raw().getString("messages.command.no-permission", "<red>Недостаточно прав.</red>"); }
    public String reloadMessage() { return raw().getString("messages.reload", "<green>Config reloaded.</green>"); }

    public List<String> getMessageList(String path) { return raw().getStringList(path); }

    public void saveCheckpoint(Location loc){
        raw().set("checkpoint.world", loc.getWorld().getName());
        raw().set("checkpoint.x", loc.getX());
        raw().set("checkpoint.y", loc.getY());
        raw().set("checkpoint.z", loc.getZ());
        raw().set("checkpoint.yaw", loc.getYaw());
        raw().set("checkpoint.pitch", loc.getPitch());
        plugin.saveConfig();
    }

    public void saveSpawn(Location loc){
        raw().set("spawn.world", loc.getWorld().getName());
        raw().set("spawn.x", loc.getX());
        raw().set("spawn.y", loc.getY());
        raw().set("spawn.z", loc.getZ());
        raw().set("spawn.yaw", loc.getYaw());
        raw().set("spawn.pitch", loc.getPitch());
        plugin.saveConfig();
    }

    private Location readLoc(String path) {
        var w = plugin.getServer().getWorld(raw().getString(path+".world", "world"));
        double x = raw().getDouble(path+".x"), y = raw().getDouble(path+".y"), z = raw().getDouble(path+".z");
        float yaw = (float)raw().getDouble(path+".yaw"), pitch = (float)raw().getDouble(path+".pitch");
        return new Location(w, x, y, z, yaw, pitch);
    }

    public String timeOne() { return raw().getString("formating_time.time-one"); }
    public String timeTwo() { return raw().getString("formating_time.time-two"); }
    public String timeThree() { return raw().getString("formating_time.time-three"); }
    public String timeFour() { return raw().getString("formating_time.time-four"); }

    public int guiSize(){
        int s = raw().getInt("GUI.size", 9);
        if (s < 9) s = 9;
        if (s > 54) s = 54;
        if (s % 9 != 0) s = ((s / 9) + 1) * 9;
        return s;
    }
    public String guiTitle(){ return raw().getString("GUI.title", "Revice • Panel"); }

    public List<String> guiItemKeys(){
        ConfigurationSection sec = raw().getConfigurationSection("GUI.items");
        if (sec == null) return List.of();
        return new ArrayList<>(sec.getKeys(false));
    }

    public String guiItemMaterial(String key){ return raw().getString("GUI.items."+key+".item", "GRAY_STAINED_GLASS_PANE"); }
    public String guiItemName(String key){ return raw().getString("GUI.items."+key+".display_name", ""); }
    public List<String> guiItemLore(String key){ return raw().getStringList("GUI.items."+key+".lore"); }
    public List<?> guiItemSlotsRaw(String key){ return raw().getList("GUI.items."+key+".slots"); }
}
