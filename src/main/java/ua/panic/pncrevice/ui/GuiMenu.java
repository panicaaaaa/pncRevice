package ua.panic.pncrevice.ui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import ua.panic.pncrevice.config.ConfigManager;
import ua.panic.pncrevice.logic.CheckManager;
import ua.panic.pncrevice.logic.CheckSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GuiMenu {
    private enum Action { BAN, ADDTIME, PAUSE, TP, STOP, NONE }
    private static final Map<UUID, Map<Integer, Action>> ACTIONS = new ConcurrentHashMap<>();

    public static void open(Player moder, CheckManager manager){
        Optional<CheckSession> opt = manager.fromAny(moder);
        if (opt.isEmpty()){ moder.sendMessage("§cНет активной проверки для GUI."); return; }

        ConfigManager cfg = getCfg(manager);
        int size = cfg.guiSize();
        String title = color(cfg.guiTitle());
        Inventory inv = Bukkit.createInventory(moder, size, title);

        Map<Integer, Action> map = new HashMap<>();
        placeItems(inv, cfg, map);
        ACTIONS.put(moder.getUniqueId(), map);

        moder.openInventory(inv);
    }

    public static boolean handleClick(InventoryClickEvent e, CheckManager manager){
        if (e.getView() == null) return false;
        String openTitle = e.getView().getTitle();
        if (openTitle == null) return false;

        ConfigManager cfg = getCfg(manager);
        if (!openTitle.equals(color(cfg.guiTitle()))) return false;

        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return true;
        Player moder = (Player) e.getWhoClicked();

        Map<Integer, Action> map = ACTIONS.getOrDefault(moder.getUniqueId(), Map.of());
        Action act = map.getOrDefault(e.getRawSlot(), Action.NONE);

        switch (act){
            case BAN -> { manager.banNow(moder); moder.closeInventory(); }
            case ADDTIME -> { manager.addTime(moder, cfg.addTimeSeconds()); }
            case PAUSE -> manager.pauseTimer(moder);
            case TP -> manager.tpAgain(moder);
            case STOP -> { manager.stop(moder); moder.closeInventory(); }
            default -> {}
        }
        return true;
    }

    private static void placeItems(Inventory inv, ConfigManager cfg, Map<Integer, Action> map){
        List<String> keys = cfg.guiItemKeys();
        int size = inv.getSize();
        for (String key : keys){
            Material mat = parseMat(cfg.guiItemMaterial(key));
            ItemStack it = new ItemStack(mat);
            ItemMeta im = it.getItemMeta();
            String name = cfg.guiItemName(key);
            if (name != null && !name.isEmpty()) im.setDisplayName(color(name));
            List<String> lore = cfg.guiItemLore(key);
            if (lore != null && !lore.isEmpty()){
                List<String> colored = new ArrayList<>(lore.size());
                for (String l : lore) colored.add(color(l));
                im.setLore(colored);
            }
            it.setItemMeta(im);

            Set<Integer> slots = parseSlots(cfg.guiItemSlotsRaw(key), size);
            for (int slot : slots){
                if (slot >= 0 && slot < size){
                    inv.setItem(slot, it);
                    map.put(slot, actionOfKey(key));
                }
            }
        }
    }

    private static Action actionOfKey(String key){
        String k = key.toLowerCase(Locale.ROOT);
        return switch (k) {
            case "ban" -> Action.BAN;
            case "addtime", "add", "add30" -> Action.ADDTIME;
            case "pause", "started" -> Action.PAUSE;
            case "tp", "tpagain" -> Action.TP;
            case "stop", "finish" -> Action.STOP;
            default -> Action.NONE;
        };
    }

    private static Material parseMat(String s){
        if (s == null) return Material.GRAY_STAINED_GLASS_PANE;
        Material m = Material.matchMaterial(s.toUpperCase(Locale.ROOT));
        return m != null ? m : Material.GRAY_STAINED_GLASS_PANE;
    }

    private static String color(String s){
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private static Set<Integer> parseSlots(List<?> raw, int max){
        Set<Integer> out = new HashSet<>();
        if (raw == null) return out;
        for (Object o : raw){
            if (o instanceof Number){
                int v = ((Number) o).intValue();
                if (v >= 0 && v < max) out.add(v);
            } else if (o instanceof String){
                String str = ((String) o).replace(" ", "");
                if (str.isEmpty()) continue;
                for (String part : str.split(",")){
                    if (part.isEmpty()) continue;
                    if (part.contains("-")){
                        String[] ab = part.split("-");
                        if (ab.length == 2){
                            try {
                                int a = Integer.parseInt(ab[0]);
                                int b = Integer.parseInt(ab[1]);
                                if (a > b){ int t = a; a = b; b = t; }
                                for (int i=a;i<=b;i++) if (i>=0 && i<max) out.add(i);
                            } catch (NumberFormatException ignored){}
                        }
                    } else {
                        try {
                            int v = Integer.parseInt(part);
                            if (v >= 0 && v < max) out.add(v);
                        } catch (NumberFormatException ignored){}
                    }
                }
            }
        }
        return out;
    }

    private static ConfigManager getCfg(CheckManager manager){
        try {
            var f = CheckManager.class.getDeclaredField("cfg");
            f.setAccessible(true);
            return (ConfigManager) f.get(manager);
        } catch (Exception e){
            throw new IllegalStateException("ConfigManager access error");
        }
    }
}
