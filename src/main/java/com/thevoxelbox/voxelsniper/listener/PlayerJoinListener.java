package com.thevoxelbox.voxelsniper.listener;

import java.util.UUID;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.cloudburstmc.server.event.EventHandler;
import org.cloudburstmc.server.event.player.PlayerJoinEvent;
import org.cloudburstmc.server.player.Player;

public class PlayerJoinListener implements Listener<PlayerJoinEvent> {

	private VoxelSniperPlugin plugin;

	public PlayerJoinListener(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	@Override
	public void listen(PlayerJoinEvent event) {
		VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
		Player player = event.getPlayer();
		UUID uuid = player.getServerId();
		Sniper sniper = getSniperFromRegistry(uuid);
		if (config.isMessageOnLoginEnabled() && player.hasPermission("voxelsniper.sniper")) {
			sniper.sendInfo(player);
		}
	}

	private Sniper getSniperFromRegistry(UUID uuid) {
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Sniper sniper = sniperRegistry.getSniper(uuid);
		if (sniper == null) {
			return registerNewSniper(uuid, sniperRegistry);
		}
		return sniper;
	}

	private Sniper registerNewSniper(UUID uuid, SniperRegistry sniperRegistry) {
		VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
		int undoCacheSize = config.getUndoCacheSize();
		Sniper newSniper = new Sniper(uuid, undoCacheSize);
		sniperRegistry.register(newSniper);
		return newSniper;
	}
}
