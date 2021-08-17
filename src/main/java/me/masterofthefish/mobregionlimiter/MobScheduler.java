package me.masterofthefish.mobregionlimiter;

public class MobScheduler implements Runnable {

    private final MobRegionLimiter plugin;
    private final Config config;
    private final MobManager mobManager;

    public MobScheduler(final MobRegionLimiter plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigSettings();
        this.mobManager = plugin.getMobManager();
    }

    @Override
    public void run() {
        for(final String world : config.getEnabledWorlds()) {
            final WorldRegions worldRegions = mobManager.getWorldRegions(world);
            if(worldRegions == null) {
                continue;
            }
            worldRegions.checkAllRegionMobs();
        }
    }
}