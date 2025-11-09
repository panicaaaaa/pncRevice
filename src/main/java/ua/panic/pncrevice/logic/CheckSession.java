package ua.panic.pncrevice.logic;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CheckSession {
    public final Player moder;
    public final Player suspect;

    public final Location suspectPrevLoc;
    public boolean frozen = true;
    public boolean timerPaused = false;
    public long endsAtMillis;
    public long graceUntil = 0L;

    public CheckSession(Player moder, Player suspect, Location suspectPrevLoc, long durationSeconds){
        this.moder = moder;
        this.suspect = suspect;
        this.suspectPrevLoc = suspectPrevLoc;
        this.endsAtMillis = System.currentTimeMillis() + durationSeconds * 1000L;
    }

    public long remainingSeconds(){
        long now = System.currentTimeMillis();
        long left = (endsAtMillis - now) / 1000L;
        return Math.max(0, left);
    }
}
