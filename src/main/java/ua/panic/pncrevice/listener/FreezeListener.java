package ua.panic.pncrevice.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import ua.panic.pncrevice.logic.CheckManager;

public class FreezeListener implements Listener {
    private final CheckManager checks;
    public FreezeListener(CheckManager checks){ this.checks = checks; }

    @EventHandler public void onMove(PlayerMoveEvent e){
        if (!checks.isFrozen(e.getPlayer())) return;
        if (e.getFrom().distanceSquared(e.getTo()) > 0){
            e.setTo(e.getFrom());
        }
    }
    @EventHandler public void onInteract(PlayerInteractEvent e){
        if (checks.isFrozen(e.getPlayer())) e.setCancelled(true);
    }
    @EventHandler public void onBreak(BlockBreakEvent e){
        if (checks.isFrozen(e.getPlayer())) e.setCancelled(true);
    }
    @EventHandler public void onPlace(BlockPlaceEvent e){
        if (checks.isFrozen(e.getPlayer())) e.setCancelled(true);
    }
    @EventHandler public void onInv(InventoryClickEvent e){
        if (e.getWhoClicked() != null && e.getWhoClicked() instanceof org.bukkit.entity.Player p){
            if (checks.isFrozen(p)) e.setCancelled(true);
        }
    }
    @EventHandler public void onHit(EntityDamageByEntityEvent e){
        if (e.getDamager() instanceof org.bukkit.entity.Player p && checks.isFrozen(p)) e.setCancelled(true);
        if (e.getEntity() instanceof org.bukkit.entity.Player p && checks.isFrozen(p)) e.setCancelled(true);
    }
}
