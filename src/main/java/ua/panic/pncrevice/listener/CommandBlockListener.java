package ua.panic.pncrevice.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import ua.panic.pncrevice.logic.CheckManager;

public class CommandBlockListener implements Listener {
    private final CheckManager checks;
    public CommandBlockListener(CheckManager checks){ this.checks = checks; }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent e){
        if (checks.isFrozen(e.getPlayer())){
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cКоманды запрещены во время проверки.");
        }
    }
}
