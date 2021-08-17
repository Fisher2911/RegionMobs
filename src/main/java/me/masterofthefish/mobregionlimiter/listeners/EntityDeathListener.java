package me.masterofthefish.mobregionlimiter.listeners;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import me.masterofthefish.mobregionlimiter.MobManager;
import me.masterofthefish.mobregionlimiter.MobRegionLimiter;
import me.masterofthefish.mobregionlimiter.RegionMobs;
import me.masterofthefish.mobregionlimiter.WorldRegions;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EntityDeathListener implements Listener {

    private final MobRegionLimiter plugin;
    private final MobManager mobManager;
    private final MythicMobs mythicMobs;

    public EntityDeathListener(final MobRegionLimiter plugin) {
        this.plugin = plugin;
        this.mobManager = plugin.getMobManager();
        this.mythicMobs = plugin.getMythicMobs();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(final MythicMobDeathEvent event) {
        final Entity entity = event.getEntity();
        final Location location = entity.getLocation();
        final World world = location.getWorld();
        if(world == null) {
            return;
        }

        final ActiveMob mob = event.getMob();
        final WorldRegions worldRegions = mobManager.getWorldRegions(world.getName());
        if(worldRegions == null) {
            return;
        }
        final RegionMobs regionMobs = worldRegions.getFromLocation(location);
        if(regionMobs == null) {
            return;
        }
        regionMobs.checkMob(mob);
    }

}
