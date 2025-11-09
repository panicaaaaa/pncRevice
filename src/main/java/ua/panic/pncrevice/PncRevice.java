package ua.panic.pncrevice;

import com.mojang.brigadier.CommandDispatcher;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import ua.panic.pncrevice.command.BrigadierBinder;
import ua.panic.pncrevice.command.ReviceCommands;
import ua.panic.pncrevice.config.ConfigManager;
import ua.panic.pncrevice.logic.ActionBarTask;
import ua.panic.pncrevice.logic.CheckManager;
import ua.panic.pncrevice.listener.*;

public final class PncRevice extends JavaPlugin {

    private BukkitAudiences audiences;
    private ConfigManager configManager;
    private CheckManager checkManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.audiences = BukkitAudiences.create(this);
        this.configManager = new ConfigManager(this, audiences);
        this.checkManager = new CheckManager(this, configManager, audiences);

        CommandDispatcher<CommandSender> dispatcher = new CommandDispatcher<>();
        new ReviceCommands(dispatcher, checkManager, configManager).register();
        new BrigadierBinder(this, dispatcher).bind("revice");

        Bukkit.getPluginManager().registerEvents(new FreezeListener(checkManager), this);
        Bukkit.getPluginManager().registerEvents(new ReviewChatListener(checkManager, configManager), this);
        Bukkit.getPluginManager().registerEvents(new CommandBlockListener(checkManager), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(checkManager, configManager), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(checkManager), this);
        Bukkit.getPluginManager().registerEvents(new GuiListener(checkManager), this);

        new ActionBarTask(checkManager, configManager).runTaskTimer(this, 20L, 20L);
    }

    @Override
    public void onDisable() {
        if (checkManager != null) checkManager.stopAllAndReturn();
        if (audiences != null) audiences.close();
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
