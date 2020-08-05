package com.thevoxelbox.voxelsniper.listener;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.event.EventHandler;
import org.cloudburstmc.server.event.player.PlayerInteractEvent;
import org.cloudburstmc.server.math.Direction;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.Identifier;

public class PlayerInteractListener implements Listener<PlayerInteractEvent> {

	private VoxelSniperPlugin plugin;

	public PlayerInteractListener(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	@Override
	public void listen(PlayerInteractEvent event) {
		System.out.printf("%s: action: %s, block: %s@%s, touchVector: %s, direction: %s, item: %s\n",
			event.getPlayer().getName(), event.getAction(), event.getBlock().getState(), event.getBlock().getPosition(), event.getTouchVector(), event.getFace(), event.getItem());
		Player player = event.getPlayer();
		if (!player.hasPermission("voxelsniper.sniper")) {
			return;
		}
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Sniper sniper = sniperRegistry.getSniper(player);
		if (sniper == null) {
			return;
		}
		PlayerInteractEvent.Action action = event.getAction();
		Identifier usedItem = event.getItem().getId();
		Block clickedBlock = event.getBlock();
		Direction clickedBlockFace = event.getFace();
		if (sniper.isEnabled() && sniper.snipe(player, action, usedItem, clickedBlock, clickedBlockFace)) {
			//System.out.println("snipe successful");
			event.setCancelled(true);
		}
	}
}
