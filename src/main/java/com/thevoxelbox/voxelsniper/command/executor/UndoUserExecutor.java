package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import org.cloudburstmc.server.utils.TextFormat;
public class UndoUserExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public UndoUserExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Player player = Bukkit.getPlayer(arguments[0]);
		if (player == null) {
			sender.sendMessage(TextFormat.GREEN + "Player not found.");
			return;
		}
		Sniper sniper = sniperRegistry.getSniper(player);
		if (sniper == null) {
			return;
		}
		sniper.undo(sender, 1);
	}
}
