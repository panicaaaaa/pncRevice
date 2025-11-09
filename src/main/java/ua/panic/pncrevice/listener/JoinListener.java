package ua.panic.pncrevice.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ua.panic.pncrevice.logic.CheckManager;

public class JoinListener implements Listener {
    private final CheckManager checks;
    public JoinListener(CheckManager checks){ this.checks = checks; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        checks.onJoin(e.getPlayer());
    }
}