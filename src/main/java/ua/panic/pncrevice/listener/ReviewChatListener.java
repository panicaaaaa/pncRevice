package ua.panic.pncrevice.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ua.panic.pncrevice.config.ConfigManager;
import ua.panic.pncrevice.logic.CheckManager;

public class ReviewChatListener implements Listener {
    private final CheckManager checks;
    private final ConfigManager cfg;

    public ReviewChatListener(CheckManager checks, ConfigManager cfg){
        this.checks = checks; this.cfg = cfg;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        var p = e.getPlayer();
        var opt = checks.fromAny(p);
        if (opt.isEmpty()) return;

        e.setCancelled(true);
        var s = opt.get();

        String role = p.getUniqueId().equals(s.suspect.getUniqueId()) ? cfg.roleSuspectRaw() : cfg.roleModerRaw();

        String msg = cfg.chatFormatRaw()
                .replace("%prefix%", cfg.chatPrefixRaw())
                .replace("%role%", role)
                .replace("%name%", p.getName())
                .replace("%message%", e.getMessage());

        Component comp = cfg.mm(msg);

        if (s.suspect.isOnline()) cfg.audience().player(s.suspect).sendMessage(comp);
        if (s.moder.isOnline()) cfg.audience().player(s.moder).sendMessage(comp);
    }
}
