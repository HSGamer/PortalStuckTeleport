package me.hsgamer.portalstuckteleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class PortalStuckTeleport extends JavaPlugin implements Listener {
    private Location location;
    private long delayTicks = 0;

    @Override
    public void onEnable() {
        if (loadConfig()) {
            getServer().getPluginManager().registerEvents(this, this);
        }
    }

    private boolean loadConfig() {
        saveDefaultConfig();

        try {
            location = Location.deserialize(getConfig().getConfigurationSection("location").getValues(false));
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Cannot load location. The plugin will not work", e);
            return false;
        }
        delayTicks = getConfig().getInt("delay", 0) * 20L;

        return true;
    }

    private void schedule(Player player) {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (player.getLocation().getBlock().getType() == Material.NETHER_PORTAL) {
                player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        }, delayTicks);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((Plugin) this);
    }

    @EventHandler
    public void onEnter(EntityPortalEnterEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (event.getLocation().getBlock().getType() == Material.NETHER_PORTAL) {
            schedule(player);
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getBlock().getType() == Material.NETHER_PORTAL) {
            schedule(player);
        }
    }
}
