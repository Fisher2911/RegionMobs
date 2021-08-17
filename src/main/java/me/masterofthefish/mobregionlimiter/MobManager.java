package me.masterofthefish.mobregionlimiter;

import com.sk89q.worldguard.WorldGuard;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MobManager {

    private final MobRegionLimiter plugin;
    private final Config config;
    private final WorldGuard worldGuard;
    private final MythicMobs mythicMobs;
    private final Map<String, WorldRegions> worldRegionsMap = new HashMap<>();


    public MobManager(final MobRegionLimiter plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigSettings();
        this.worldGuard = plugin.getWorldGuard();
        this.mythicMobs = plugin.getMythicMobs();
    }

    public void onEnable() {
        for(final String worldName : config.getEnabledWorlds()) {
            final WorldRegions worldRegions = getWorldRegions(worldName);
            if(worldRegions == null) {
                continue;
            }
            final World world = Bukkit.getWorld(worldName);
            if(world == null) {
                plugin.getLogger().severe("Error loading world: " + worldName + " when loading mobs.");
                continue;
            }
            for(final Entity entity : world.getEntities()) {
                final io.lumine.xikage.mythicmobs.mobs.MobManager mobManager = mythicMobs.getMobManager();
                if(!mobManager.isActiveMob(entity.getUniqueId())) {
                    continue;
                }
                final ActiveMob activeMob = mobManager.getMythicMobInstance(entity);
                final Location location = entity.getLocation();
                final RegionMobs regionMobs = worldRegions.getFromLocation(location);
                if(regionMobs == null) {
                    continue;
                }
                regionMobs.checkMob(activeMob);
            }
        }
    }

    public void setWorldRegions(final String world, final WorldRegions worldRegions) {
        this.worldRegionsMap.put(world, worldRegions);
    }

    public WorldRegions getWorldRegions(final String world) {
        return worldRegionsMap.get(world);
    }

    public boolean containsCheckedMob(@NotNull final World world, final String regionId, final String mob) {
        final WorldRegions worldRegions = worldRegionsMap.get(world.getName());
        return worldRegions.containsCheckedMob(regionId, mob);
    }
}
