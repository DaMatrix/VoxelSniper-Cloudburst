package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.BlockTracer;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.message.Messenger;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.command.CommandSender;
import org.cloudburstmc.server.player.Player;

public class VoxelInkExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public VoxelInkExecutor(VoxelSniperPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		SniperRegistry sniperRegistry = this.plugin.getSniperRegistry();
		Player player = (Player) sender;
		Sniper sniper = sniperRegistry.getSniper(player);
		if (sniper == null) {
			return;
		}
		Toolkit toolkit = sniper.getCurrentToolkit();
		if (toolkit == null) {
			return;
		}
		ToolkitProperties toolkitProperties = toolkit.getProperties();
		if (toolkitProperties == null) {
			return;
		}
		BlockState blockData;
		if (arguments.length == 0) {
			BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
			Block targetBlock = blockTracer.getTargetBlock();
			blockData = targetBlock.getState();
		} else {
			try {
				//TODO: parse block state
				// blockData = Bukkit.createBlockData(arguments[0]);
				throw new IllegalArgumentException();
			} catch (IllegalArgumentException exception) {
				sender.sendMessage("Couldn't parse input.");
				return;
			}
		}
		toolkitProperties.setBlockData(blockData);
		Messenger messenger = new Messenger(sender);
		messenger.sendBlockDataMessage(blockData);
	}
}
