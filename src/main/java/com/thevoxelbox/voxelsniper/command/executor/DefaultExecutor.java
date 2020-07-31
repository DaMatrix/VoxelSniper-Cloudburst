package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import org.cloudburstmc.server.utils.TextFormat;
public class DefaultExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public DefaultExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Player player = (Player) sender;
		Sniper sniper = sniperRegistry.getSniper(player);
		if (sniper == null) {
			sender.sendMessage(TextFormat.RED + "Sniper not found.");
			return;
		}
		Toolkit toolkit = sniper.getCurrentToolkit();
		if (toolkit == null) {
			sender.sendMessage(TextFormat.RED + "Current toolkit not found.");
			return;
		}
		toolkit.reset();
		sender.sendMessage(TextFormat.AQUA + "Brush settings reset to their default values.");
	}
}
