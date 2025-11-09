package ua.panic.pncrevice.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import ua.panic.pncrevice.config.ConfigManager;
import ua.panic.pncrevice.logic.CheckManager;

public class QuitListener implements Listener {
    private final CheckManager checks;
    private final ConfigManager cfg;
    public QuitListener(CheckManager checks, ConfigManager cfg){ this.checks = checks; this.cfg = cfg; }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        if (checks.hasSession(e.getPlayer())){
            checks.markQuit(e.getPlayer());
        }
    }
}