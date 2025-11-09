package ua.panic.pncrevice.logic;

import net.kyori.adventure.text.Component;
import org.bukkit.scheduler.BukkitRunnable;
import ua.panic.pncrevice.config.ConfigManager;
import ua.panic.pncrevice.util.TextCache;

public class ActionBarTask extends BukkitRunnable {
    private final CheckManager checks;
    private final ConfigManager cfg;
    private final TextCache cache;

    public ActionBarTask(CheckManager checks, ConfigManager cfg){
        this.checks = checks;
        this.cfg = cfg;
        this.cache = new TextCache(cfg.mmEngine());
    }

    @Override
    public void run() {
        checks.checkPunishTimeouts();

        String rawMask = cfg.actionbarFormatRaw();
        Component pausedComp = cache.toComponent(cfg.actionbarPausedChat());

        for (CheckSession s : checks.sessions()){
            if (s.suspect == null) continue;

            if (s.timerPaused) {
                if (s.suspect.isOnline()) cfg.audience().player(s.suspect).sendActionBar(pausedComp);
                if (s.moder.isOnline())   cfg.audience().player(s.moder).sendActionBar(pausedComp);
                continue;
            }

            long left = s.remainingSeconds();
            String formattedTime = ua.panic.pncrevice.util.TimeFmt.format(left, cfg);
            String mm = rawMask.replace("%mm:ss%", formattedTime);
            Component comp = cache.toComponent(mm);

            if (s.suspect.isOnline()) cfg.audience().player(s.suspect).sendActionBar(comp);
            if (s.moder.isOnline())   cfg.audience().player(s.moder).sendActionBar(comp);
        }
    }
}
