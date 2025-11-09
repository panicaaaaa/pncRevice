package ua.panic.pncrevice.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ua.panic.pncrevice.config.ConfigManager;

import java.util.List;
import java.util.stream.Collectors;

public class BrigadierBinder {
    private final JavaPlugin plugin;
    private final CommandDispatcher<CommandSender> dispatcher;

    public BrigadierBinder(JavaPlugin plugin, CommandDispatcher<CommandSender> dispatcher) {
        this.plugin = plugin;
        this.dispatcher = dispatcher;
    }

    public void bind(String rootCmd) {
        PluginCommand cmd = plugin.getCommand(rootCmd);
        if (cmd == null) throw new IllegalStateException("Command not found in plugin.yml: " + rootCmd);

        cmd.setExecutor((sender, command, label, args) -> {
            ConfigManager cfg = ((ua.panic.pncrevice.PncRevice) plugin).getConfigManager();

            if (sender instanceof Player p && !p.hasPermission("pncRevice.use")) {
                cfg.audience().player(p).sendMessage(cfg.mm(cfg.cmdNoPerm()));
                return true;
            }

            if (args.length == 0) {
                sendUsage(sender, cfg);
                return true;
            }

            String input = label + " " + String.join(" ", args);
            try {
                dispatcher.execute(input, sender);
            } catch (CommandSyntaxException ex) {
                sendInvalid(sender, cfg);
            } catch (Exception ex) {
                sender.sendMessage("§cПроизошла ошибка при выполнении команды.");
                ex.printStackTrace();
            }
            return true;
        });

        cmd.setTabCompleter((sender, command, alias, args) -> {
            String input = alias + (args.length > 0 ? " " + String.join(" ", args) : "");
            try {
                var parse = dispatcher.parse(input, sender);
                var suggestions = dispatcher.getCompletionSuggestions(parse);
                return suggestions.get().getList().stream().map(s -> s.getText()).collect(Collectors.toList());
            } catch (Exception e) {
                return List.of();
            }
        });
    }

    private void sendUsage(CommandSender sender, ConfigManager cfg){
        if (sender instanceof Player p) cfg.audience().player(p).sendMessage(cfg.mm(cfg.cmdUsage()));
        else sender.sendMessage(cfg.mm(cfg.cmdUsage()).toString());
    }

    private void sendInvalid(CommandSender sender, ConfigManager cfg){
        if (sender instanceof Player p) cfg.audience().player(p).sendMessage(cfg.mm(cfg.cmdInvalid()));
        else sender.sendMessage(cfg.mm(cfg.cmdInvalid()).toString());
    }
}
