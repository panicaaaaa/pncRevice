package ua.panic.pncrevice.logic;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ua.panic.pncrevice.config.ConfigManager;
import ua.panic.pncrevice.ui.GuiMenu;

import java.util.*;

public class CheckManager {
    private final JavaPlugin plugin;
    private final ConfigManager cfg;
    private final BukkitAudiences audiences;
    private final Map<UUID, CheckSession> bySuspect = new HashMap<>();
    private final Map<UUID, CheckSession> byModer = new HashMap<>();

    public CheckManager(JavaPlugin plugin, ConfigManager cfg, BukkitAudiences audiences){
        this.plugin = plugin; this.cfg = cfg; this.audiences = audiences;
    }

    public Collection<CheckSession> sessions(){
        return Collections.unmodifiableCollection(bySuspect.values());
    }

    public int startOrOpen(CommandSender src, Player suspect, String rawNick){
        if (!(src instanceof Player)) { src.sendMessage("§cТолько для игроков."); return 0; }
        Player moder = (Player) src;
        CheckSession existing = byModer.get(moder.getUniqueId());
        if (existing != null) {
            GuiMenu.open(moder, this);
            return 1;
        }
        if (suspect == null){
            moder.sendMessage("§cИгрок оффлайн: §f" + rawNick);
            return 0;
        }
        if (bySuspect.containsKey(suspect.getUniqueId())) {
            moder.sendMessage("§eЭтот игрок уже на проверке.");
            return 0;
        }

        long secs = cfg.defaultSeconds();
        CheckSession s = new CheckSession(moder, suspect, suspect.getLocation().clone(), secs);
        bySuspect.put(suspect.getUniqueId(), s);
        byModer.put(moder.getUniqueId(), s);

        suspect.teleport(cfg.checkpoint());
        s.frozen = true;

        for (String line : cfg.getMessageList("messages.start.chat")) {
            audiences.player(suspect).sendMessage(cfg.mm(line));
        }
        sendTitleSound(suspect, "messages.start");

        GuiMenu.open(moder, this);
        moder.sendMessage("§aПроверка начата: §f" + suspect.getName());
        return 1;
    }

    public void setCheckpointHere(CommandSender src){
        if (!(src instanceof Player)){ src.sendMessage("§cТолько для игроков."); return; }
        Player p = (Player) src;
        cfg.saveCheckpoint(p.getLocation());
        p.sendMessage("§aТочка проверки сохранена.");
    }

    public void setSpawnHere(CommandSender src){
        if (!(src instanceof Player)){
            src.sendMessage("§cТолько для игроков.");
            return;
        }
        Player p = (Player) src;
        cfg.saveSpawn(p.getLocation());
        p.sendMessage("§aТочка спавна сохранена.");
    }


    public int stop(CommandSender src){
        CheckSession s = resolveSession(src);
        if (s == null){ src.sendMessage("§cНет активной проверки."); return 0; }
        finish(s, true);
        src.sendMessage("§aПроверка завершена.");
        return 1;
    }

    public void banNow(CommandSender src){
        CheckSession s = resolveSession(src);
        if (s == null){ src.sendMessage("§cНет активной проверки."); return; }
        String cmd = cfg.manualBanCommand().replace("%player%", s.suspect.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        finish(s, false);
        src.sendMessage("§cИгрок забанен и проверка завершена.");
    }

    public int addTime(CommandSender src, int seconds){
        CheckSession s = resolveSession(src);
        if (s == null){ src.sendMessage("§cНет активной проверки."); return 0; }
        s.endsAtMillis += seconds * 1000L;
        src.sendMessage("§aДобавлено времени: §f"+seconds+"§a сек.");
        return 1;
    }

    public void pauseTimer(CommandSender src){
        CheckSession s = resolveSession(src);
        if (s == null){ src.sendMessage("§cНет активной проверки."); return; }
        s.timerPaused = !s.timerPaused;
        src.sendMessage(s.timerPaused ? "§eТаймер поставлен на паузу." : "§aТаймер возобновлён.");
        if (s.timerPaused && s.suspect.isOnline()){
            cfg.audience().player(s.suspect).sendActionBar(cfg.mm(cfg.actionbarPausedChat()));
        }
    }

    public void tpAgain(CommandSender src){
        CheckSession s = resolveSession(src);
        if (s == null){ src.sendMessage("§cНет активной проверки."); return; }
        s.suspect.teleport(cfg.checkpoint());
        src.sendMessage("§aПодозреваемый перемещён на точку проверки.");
    }

    public boolean isFrozen(Player p){
        CheckSession s = bySuspect.get(p.getUniqueId());
        return s != null && s.frozen;
    }

    public boolean hasSession(Player p){
        return bySuspect.containsKey(p.getUniqueId()) || byModer.containsKey(p.getUniqueId());
    }

    public Optional<CheckSession> fromAny(Player p){
        if (p == null) return Optional.empty();
        CheckSession s = bySuspect.get(p.getUniqueId());
        if (s != null) return Optional.of(s);
        s = byModer.get(p.getUniqueId());
        return Optional.ofNullable(s);
    }

    public void markQuit(Player p){
        CheckSession s = bySuspect.get(p.getUniqueId());
        if (s == null) return;
        if (!cfg.punishOnQuit()) return;
        s.graceUntil = System.currentTimeMillis() + cfg.punishGraceSeconds() * 1000L;
    }

    public void checkPunishTimeouts(){
        long now = System.currentTimeMillis();
        List<CheckSession> punish = new ArrayList<>();
        for (CheckSession s : bySuspect.values()){
            if (s.graceUntil > 0 && now > s.graceUntil){
                punish.add(s);
            }
        }
        for (CheckSession s : punish){
            String cmd = cfg.punishCommand().replace("%player%", s.suspect.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            finish(s, false);
        }
    }

    public void onJoin(Player p){
        CheckSession s = bySuspect.get(p.getUniqueId());
        if (s == null) return;
        s.graceUntil = 0;
        p.teleport(cfg.checkpoint());
        p.sendMessage("§eВы вернулись в режим проверки.");
    }

    public void stopAllAndReturn(){
        for (CheckSession s : new ArrayList<>(bySuspect.values())){
            finish(s, false);
        }
    }

    private void finish(CheckSession s, boolean playMessages){
        s.frozen = false;
        s.timerPaused = true;
        if (s.suspect.isOnline()) {
            s.suspect.teleport(cfg.spawn());
            if (playMessages){
                for (String line : cfg.getMessageList("messages.stop.chat")) {
                    cfg.audience().player(s.suspect).sendMessage(cfg.mm(line));
                }
                sendTitleSound(s.suspect, "messages.stop");
            }
        }
        bySuspect.remove(s.suspect.getUniqueId());
        byModer.remove(s.moder.getUniqueId());
    }

    private void sendTitleSound(Player p, String basePath){
        var t = cfg.raw().getConfigurationSection(basePath + ".title");
        if (t != null){
            var title = cfg.mm(t.getString("title"));
            var sub = cfg.mm(t.getString("subtitle"));
            int fi = t.getInt("fadein", 10), st = t.getInt("stay", 40), fo = t.getInt("fadeout", 10);
            cfg.audience().player(p).showTitle(net.kyori.adventure.title.Title.title(title, sub,
                    net.kyori.adventure.title.Title.Times.times(
                            java.time.Duration.ofMillis(fi*50L),
                            java.time.Duration.ofMillis(st*50L),
                            java.time.Duration.ofMillis(fo*50L)
                    )));
        }
        String soundRaw = cfg.raw().getString(basePath + ".sound", "");
        if (!soundRaw.isEmpty()){
            String[] s = soundRaw.split(",");
            try {
                Sound sound = Sound.valueOf(s[0].trim());
                float vol = s.length>1 ? Float.parseFloat(s[1].trim()) : 1.0f;
                float pit = s.length>2 ? Float.parseFloat(s[2].trim()) : 1.0f;
                p.playSound(p.getLocation(), sound, vol, pit);
            } catch (Exception ignored) {}
        }
    }

    private CheckSession resolveSession(CommandSender src){
        if (src instanceof Player){
            Player p = (Player) src;
            CheckSession s = byModer.get(p.getUniqueId());
            if (s != null) return s;
            return bySuspect.get(p.getUniqueId());
        }
        return null;
    }
}
