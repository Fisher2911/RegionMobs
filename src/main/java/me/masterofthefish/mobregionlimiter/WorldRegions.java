package me.masterofthefish.mobregionlimiter;

import org.bukkit.Location;

import java.util.Map;

public class WorldRegions {

    private final String world;
    private final Map<String, RegionMobs> regionIdToRegionMobs;

    public WorldRegions(final String world, final Map<String, RegionMobs> regionIdToRegionMobs) {
        this.world = world;
        this.regionIdToRegionMobs = regionIdToRegionMobs;
    }

    public boolean containsCheckedMob(final String regionId, final String mob) {
        return regionIdToRegionMobs.get(regionId).containsMob(mob);
    }

    public void checkAllRegionMobs() {
        for(final RegionMobs regionMobs : regionIdToRegionMobs.values()) {
            regionMobs.checkAllMobs();
        }
    }

    public RegionMobs getFromLocation(final Location location) {
        for(Map.Entry<String, RegionMobs> entry : regionIdToRegionMobs.entrySet()) {
            final RegionMobs regionMobs = entry.getValue();
            if(regionMobs.contains(location)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "WorldRegions{" +
                "world='" + world + '\'' +
                '}';
    }
}
