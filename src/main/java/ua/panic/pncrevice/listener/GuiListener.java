package ua.panic.pncrevice.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import ua.panic.pncrevice.logic.CheckManager;
import ua.panic.pncrevice.ui.GuiMenu;

public class GuiListener implements Listener {
    private final CheckManager manager;
    public GuiListener(CheckManager manager){ this.manager = manager; }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        GuiMenu.handleClick(e, manager);
    }
}
