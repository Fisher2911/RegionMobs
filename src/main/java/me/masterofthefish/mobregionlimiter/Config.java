package me.masterofthefish.mobregionlimiter;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class Config {

    private final MobRegionLimiter plugin;
    private final WorldGuard worldGuard;

    private final List<String> enabledWorlds = new ArrayList<>();
    private TeleportSetting teleportSetting;
    private int timerInterval;

    public Config(final MobRegionLimiter plugin) {
        this.plugin = plugin;
        this.worldGuard = plugin.getWorldGuard();
    }


    @SuppressWarnings("ConstantConditions")
    public void load() {
        plugin.getLogger().info("Loading region mobs config...");
        final MobManager mobManager = plugin.getMobManager();
        plugin.saveDefaultConfig();
        final FileConfiguration config = plugin.getConfig();
        final ConfigurationSection section = config.getConfigurationSection("worlds");
        if(section == null) {
            plugin.getLogger().severe("Error loading config file, no section found: \"worlds\"");
            return;
        }
        for(final String worldName: section.getKeys(false)) {
            final ConfigurationSection regionSection = section.getConfigurationSection(worldName + ".regions");
            final World world = Bukkit.getWorld(worldName);
            if(regionSection == null || world == null) {
                plugin.getLogger().severe("Error loading regions from world: " + worldName + " in config");
                continue;
            }
            plugin.getLogger().info("Successfully loaded world: " + worldName);

            final RegionManager regionManager = worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
            if(regionManager == null) {
                plugin.getLogger().severe("Region manager of world " + worldName + " not found");
                break;
            }
            plugin.getLogger().info("Successfully loaded RegionManager of world: " + worldName);

            enabledWorlds.add(worldName);

            final Map<String, RegionMobs> regionMobsMap = new HashMap<>();
            for(final String region : regionSection.getKeys(false)) {
                final List<String> mobs = regionSection.getStringList(region + ".mobs");
                final ProtectedRegion protectedRegion = regionManager.getRegion(region);
                plugin.getLogger().info("Successfuly Loaded region - " + region + " " + protectedRegion);
                final RegionMobs regionMobs = new RegionMobs(plugin, worldName, protectedRegion, new HashSet<>(mobs));
                regionMobsMap.put(region, regionMobs);
            }
            if(regionSection.getKeys(false).isEmpty()) {
                plugin.getLogger().severe("No regions found in world: " + worldName);
            }
            final WorldRegions worldRegions = new WorldRegions(worldName, regionMobsMap);
            mobManager.setWorldRegions(worldName, worldRegions);
        }
        if(section.getKeys(false).isEmpty()) {
            plugin.getLogger().severe("No Worlds Found");
        }

        final String settingsKey = "settings.";

        final String teleportSettingType = config.getString(settingsKey + "teleport-setting");
        try {
            this.teleportSetting = TeleportSetting.valueOf(teleportSettingType.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException | NullPointerException exception) {
            plugin.getLogger().severe("Error loading teleport setting type: " + teleportSettingType + " from config, valid " +
                    "formats are: EDGE, CENTER, DESPAWN");
        }
        timerInterval = Math.max(10, config.getInt(settingsKey + "timer-interval"));
    }

    public TeleportSetting getTeleportSetting() {
        return teleportSetting;
    }

    public List<String> getEnabledWorlds() {
        return Collections.unmodifiableList(enabledWorlds);
    }

    public int getTimerInterval() {
        return timerInterval;
    }

    public enum TeleportSetting {

        EDGE,

        CENTER,

        DESPAWN

    }
}