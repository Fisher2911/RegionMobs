package me.masterofthefish.mobregionlimiter;

import com.sk89q.worldguard.WorldGuard;
import io.lumine.xikage.mythicmobs.MythicMobs;
import me.masterofthefish.mobregionlimiter.listeners.EntityDeathListener;
import me.masterofthefish.mobregionlimiter.listeners.EntitySpawnListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MobRegionLimiter extends JavaPlugin {

    private Config config;
    private WorldGuard worldGuard;
    private MythicMobs mythicMobs;
    private MobManager mobManager;

    @Override
    public void onEnable() {
        load();
        startScheduler();
        registerListeners();
    }

    private void load() {
        this.worldGuard = WorldGuard.getInstance();
        this.mythicMobs = MythicMobs.inst();
        this.config = new Config(this);
        this.mobManager = new MobManager(this);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            config.load();
            mobManager.onEnable();
        }, 20);
    }

    private void registerListeners() {
        List.of(new EntitySpawnListener(this),
                new EntityDeathListener(this)).forEach(listener ->
                getServer().getPluginManager().registerEvents(listener, this));
    }

    private void startScheduler() {
        Bukkit.getScheduler().runTaskTimer(this, new MobScheduler(this), 21, config.getTimerInterval());
    }

    public Config getConfigSettings() {
        return config;
    }

    public WorldGuard getWorldGuard() {
        return worldGuard;
    }

    public MythicMobs getMythicMobs() {
        return mythicMobs;
    }

    public MobManager getMobManager() {
        return mobManager;
    }
}