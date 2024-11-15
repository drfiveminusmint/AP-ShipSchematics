package com.github.drfiveminusmint.apshipschematics.impound;

import net.countercraft.movecraft.util.ComponentPaginator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ImpoundManager extends BukkitRunnable {
    private final ConcurrentLinkedQueue<ImpoundTask> tasks = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<Player, ComponentPaginator> cachedSearchResults = new ConcurrentHashMap<>();
    private boolean taskRunning;
    private int cachePurgeCountdown = 1200;
    public void run() {
        if (tasks.isEmpty()) {
            if (--cachePurgeCountdown == 0) {
                cachedSearchResults.clear();
                cachePurgeCountdown = 1200;
            }
            return;
        }
        if (taskRunning) return;
        tasks.poll().run();
        cachePurgeCountdown = 1200;
    }

    public void setTaskRunning(boolean running) {taskRunning = running;}

    public void addTask(ImpoundTask t) {
        tasks.add(t);
    }

    public boolean isTaskRunning() {return taskRunning;}

    public ComponentPaginator getSearchResults(Player p) {
        //refresh the countdown
        cachePurgeCountdown = 1200;
        return cachedSearchResults.get(p);
    }

    public void addSearchResult (Player p, ComponentPaginator results) {
        if (cachedSearchResults.containsKey(p))
            cachedSearchResults.remove(p);
        cachedSearchResults.put(p,results);
    }
}
