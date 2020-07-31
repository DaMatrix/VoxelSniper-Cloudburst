package com.thevoxelbox.voxelsniper.command.executor;

import java.util.List;
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
import org.cloudburstmc.server.utils.Identifier;

public class VoxelListExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public VoxelListExecutor(VoxelSniperPlugin plugin) {
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
		Messenger messenger = new Messenger(sender);
		if (arguments.length == 0) {
			BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
			Block targetBlock = blockTracer.getTargetBlock();
			if (targetBlock == null) {
				return;
			}
			BlockState blockData = targetBlock.getState();
			toolkitProperties.addToVoxelList(blockData);
			List<BlockState> voxelList = toolkitProperties.getVoxelList();
			messenger.sendVoxelListMessage(voxelList);
			return;
		} else {
			if (arguments[0].equalsIgnoreCase("clear")) {
				toolkitProperties.clearVoxelList();
				List<BlockState> voxelList = toolkitProperties.getVoxelList();
				messenger.sendVoxelListMessage(voxelList);
				return;
			}
		}
		boolean remove = false;
		for (String string : arguments) {
			String materialString;
			if (!string.isEmpty() && string.charAt(0) == '-') {
				remove = true;
				materialString = string.replaceAll("-", "");
			} else {
				materialString = string;
			}
			Identifier material = Identifier.fromString(materialString);
			if (material != null && BlockState.get(material) != null) {
				BlockState blockData = BlockState.get(material);
				if (remove) {
					toolkitProperties.removeFromVoxelList(blockData);
				} else {
					toolkitProperties.addToVoxelList(blockData);
				}
				List<BlockState> voxelList = toolkitProperties.getVoxelList();
				messenger.sendVoxelListMessage(voxelList);
			}
		}
	}
}
