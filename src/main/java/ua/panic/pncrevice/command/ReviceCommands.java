package ua.panic.pncrevice.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ua.panic.pncrevice.PncRevice;
import ua.panic.pncrevice.config.ConfigManager;
import ua.panic.pncrevice.logic.CheckManager;

public class ReviceCommands {
    private final CommandDispatcher<CommandSender> d;
    private final CheckManager checks;
    private final ConfigManager cfg;
    private final PncRevice plugin;

    public ReviceCommands(CommandDispatcher<CommandSender> d, CheckManager checks, ConfigManager cfg) {
        this.d = d;
        this.checks = checks;
        this.cfg = cfg;
        this.plugin = (PncRevice) Bukkit.getPluginManager().getPlugin("pncRevice");
    }

    public void register() {
        LiteralArgumentBuilder<CommandSender> root = LiteralArgumentBuilder.<CommandSender>literal("revice")

                .then(LiteralArgumentBuilder.<CommandSender>literal("setposs")
                        .requires(s -> s.hasPermission(cfg.perm("setposs")))
                        .executes(ctx -> {
                            checks.setCheckpointHere(ctx.getSource());
                            return 1;
                        }))

                .then(LiteralArgumentBuilder.<CommandSender>literal("reload")
                        .requires(s -> s.hasPermission(cfg.perm("reload")))
                        .executes(ctx -> {
                            plugin.reloadConfig();
                            ctx.getSource().sendMessage(cfg.mm(cfg.raw().getString("messages.reload", "<green>Config reloaded.</green>")));
                            return 1;
                        }))

                .then(LiteralArgumentBuilder.<CommandSender>literal("setspawn")
                        .requires(s -> s.hasPermission(cfg.perm("setspawn")))
                        .executes(ctx -> {
                            checks.setSpawnHere(ctx.getSource());
                            return 1;
                        }))

                .then(RequiredArgumentBuilder.<CommandSender, String>argument("nick", StringArgumentType.string())
                        .requires(s -> s.hasPermission(cfg.perm("start")))
                        .executes(ctx -> {
                            String nick = StringArgumentType.getString(ctx, "nick");
                            Player suspect = Bukkit.getPlayerExact(nick);
                            return checks.startOrOpen(ctx.getSource(), suspect, nick);
                        }));


        d.register(root);
    }
}
