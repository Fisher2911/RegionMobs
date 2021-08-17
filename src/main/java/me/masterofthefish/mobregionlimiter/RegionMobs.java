package me.masterofthefish.mobregionlimiter;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.*;

public class RegionMobs {

    private final MobRegionLimiter plugin;
    private final String world;
    private final ProtectedRegion region;
    private final Set<String> mobsToBeChecked;
    private final Map<UUID, Location> mobLocations = new HashMap<>();
    private final Set<UUID> deadMobs = new HashSet<>();

    public RegionMobs(final MobRegionLimiter plugin, final String world, final ProtectedRegion region, final Set<String> mobsToBeChecked) {
        this.plugin = plugin;
        this.world = world;
        this.region = region;
        this.mobsToBeChecked = mobsToBeChecked;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public void checkMob(final ActiveMob mob) {
        checkMob(mob, mobLocations.get(mob.getUniqueId()));
    }

    public void checkMob(final ActiveMob mob, final Location previousLocation) {
        final UUID uuid = mob.getUniqueId();
        final String type = mob.getType().getInternalName();
        if(mob.isDead()) {
            deadMobs.add(uuid);
        }
        if(mobsToBeChecked.contains(type)) {
            final Entity entity = mob.getEntity().getBukkitEntity();
            final Location currentLocation = entity.getLocation();
            if(contains(currentLocation)) {
                mobLocations.put(uuid, currentLocation);
                return;
            }
            if(!contains(currentLocation)) {
                teleportMob(entity, previousLocation);
            }
        }
    }
    
    public void checkAllMobs() {
        for(final Map.Entry<UUID, Location> entry : mobLocations.entrySet()) {
            final UUID uuid = entry.getKey();
            final Location location = entry.getValue();
            final MobManager mobManager = plugin.getMythicMobs().getMobManager();
            if(!mobManager.isActiveMob(uuid)) {
                continue;
            }
            final Optional<ActiveMob> activeMob = mobManager.getActiveMob(uuid);
            activeMob.ifPresent(mob -> checkMob(mob, location));
        }
        deadMobs.forEach(mobLocations::remove);
        deadMobs.clear();
    }

    public boolean contains(final Location location) {
        if(region == null || location == null) {
            return false;
        }
        return region.contains((int) location.getX(), (int) location.getY(), (int) location.getZ());
    }

    private void teleportMob(final Entity entity, final Location location) {
        final Config.TeleportSetting setting = plugin.getConfigSettings().getTeleportSetting();
        switch(setting) {
            case CENTER:
                entity.teleport(getCenter());
                break;
            case EDGE:
                entity.teleport(location);
                break;
            case DESPAWN:
                entity.remove();
        }
    }

    public Location getCenter() {
        final World world = Bukkit.getWorld(this.world);
        if(world == null) {
            plugin.getLogger().severe("Error getting world: " + this.world + " in region " + region.getId());
            return null;
        }
        final BlockVector3 min = region.getMinimumPoint();
        final BlockVector3 max = region.getMaximumPoint();
        final int minX = min.getX();
        final int minY = min.getY();
        final int minZ = min.getZ();
        final int maxX = max.getX();
        final int maxY = max.getY();
        final int maxZ = max.getZ();
        return new Location(world, getMiddle(minX, maxX), getMiddle(minY, maxY), getMiddle(minZ, maxZ));
    }

    private int getMiddle(final int first, final int second) {
        return (first + second) / 2;
    }

    public boolean containsMob(final String string) {
        return mobsToBeChecked.contains(string);
    }
}