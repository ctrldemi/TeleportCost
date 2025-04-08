package com.demi.teleportcost;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * TeleportCost - A Minecraft plugin that adds experience costs to teleport commands
 * with visual and auditory feedback for both success and failure cases.
 */
public class TeleportCost extends JavaPlugin implements Listener {

    // Plugin constants
    private static final String BYPASS_META = "teleportcost_bypass"; // Metadata key for command bypass
    private final int PARTICLE_COUNT = 30;          // Number of particles to spawn
    private final double PARTICLE_OFFSET = 0.5;     // Particle spread radius
    private final float SUCCESS_PITCH = 1.0f;       // Pitch for success sounds
    private final float FAIL_PITCH = 0.8f;          // Pitch for failure sounds

    @Override
    public void onEnable() {
        // Save default configuration if not present
        saveDefaultConfig();
        // Register this class as an event listener
        getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * Handles player command execution events to intercept teleport commands
     * @param event The PlayerCommandPreprocessEvent containing command data
     */
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();
        String rawCommand = event.getMessage();

        // Check if the command is a teleport command
        if (command.startsWith("/tp ") || command.startsWith("/minecraft:tp ")) {
            // Bypass check to prevent infinite command loops
            if (player.hasMetadata(BYPASS_META)) {
                player.removeMetadata(BYPASS_META, this);
                return;
            }

            // Retrieve configuration values
            int cost = getConfig().getInt("teleport-cost", 5);
            String costType = getConfig().getString("cost-type", "levels").toLowerCase();
            boolean useLevels = costType.equals("levels");
            
            // Store original location for pre-teleport effects
            Location originalLocation = player.getLocation();
            World originalWorld = originalLocation.getWorld();

            // Validate cost-type configuration
            if (!useLevels && !costType.equals("exp")) {
                getLogger().warning("Invalid cost-type in config! Using levels as default.");
                useLevels = true;
            }

            // Check if player can afford the teleport
            boolean canAfford = useLevels ? 
                (player.getLevel() >= cost) : 
                (player.getTotalExperience() >= cost);

            if (!canAfford) {
                // Failed teleport effects (visible to all nearby players)
                originalWorld.spawnParticle(Particle.ANGRY_VILLAGER, originalLocation, PARTICLE_COUNT, 
                    PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0.1);
                originalWorld.playSound(originalLocation, Sound.ENTITY_SHEEP_AMBIENT, 
                    SoundCategory.PLAYERS, 1.0f, FAIL_PITCH);

                // Send error message to player
                String unit = useLevels ? "levels" : "experience points";
                player.sendMessage(ChatColor.RED + "You need at least " + cost + " " + unit + " to teleport!");
                event.setCancelled(true);
                return;
            }

            // Deduct experience cost from player
            if (useLevels) {
                player.giveExpLevels(-cost);
            } else {
                player.giveExp(-cost);
            }

            // Pre-teleport effects at original location (smoke cloud)
            originalWorld.spawnParticle(Particle.CLOUD, originalLocation, PARTICLE_COUNT, 
                PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0.1);
            originalWorld.playSound(originalLocation, Sound.ENTITY_ITEM_PICKUP, 
                SoundCategory.PLAYERS, 0.8f, SUCCESS_PITCH);

            // Send success message to player
            String unitSymbol = useLevels ? "levels" : "exp";
            player.sendMessage(ChatColor.GREEN + "Teleported! " + ChatColor.RED + "(-" + cost + " " + unitSymbol + ")");

            // Cancel original command and prepare for re-dispatch
            event.setCancelled(true);
            player.setMetadata(BYPASS_META, new FixedMetadataValue(this, true));

            // Clean and prepare command for re-execution
            String sanitizedCmd = rawCommand.substring(1)
                .replaceFirst("(?i)minecraft:", "")
                .trim();

            // Schedule command execution after teleport processing
            getServer().getScheduler().runTask(this, () -> {
                // Execute the sanitized teleport command
                getServer().dispatchCommand(player, sanitizedCmd);
                
                // Post-teleport effects at new location
                Location newLocation = player.getLocation();
                World newWorld = newLocation.getWorld();
                
                // Destination effects (happy villager particles)
                newWorld.spawnParticle(Particle.HAPPY_VILLAGER, newLocation, PARTICLE_COUNT, 
                    PARTICLE_OFFSET, PARTICLE_OFFSET, PARTICLE_OFFSET, 0.1);
                newWorld.playSound(newLocation, Sound.ENTITY_ITEM_PICKUP, 
                    SoundCategory.PLAYERS, 0.8f, SUCCESS_PITCH);
                
                // Clean up bypass metadata
                player.removeMetadata(BYPASS_META, this);
            });
        }
    }
}
