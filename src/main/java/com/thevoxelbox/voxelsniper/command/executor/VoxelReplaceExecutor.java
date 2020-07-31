package com.thevoxelbox.voxelsniper.command.executor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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
import org.cloudburstmc.server.utils.TextFormat;
public class VoxelReplaceExecutor implements CommandExecutor {

	/*private static final List<NamespacedKey> BLOCK_KEYS = Arrays.stream(Material.values())
		.filter(Material::isBlock)
		.map(Material::getKey)
		.collect(Collectors.toList());*/

	private VoxelSniperPlugin plugin;

	public VoxelReplaceExecutor(VoxelSniperPlugin plugin) {
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
			if (targetBlock != null) {
				Identifier type = targetBlock.getState().getType();
				toolkitProperties.setReplaceBlockType(type);
				messenger.sendReplaceBlockTypeMessage(type);
			}
			return;
		}
		Identifier material = Identifier.fromString(arguments[0]);
		if (material != null) {
			if (BlockState.get(material) != null) {
				toolkitProperties.setReplaceBlockType(material);
				messenger.sendReplaceBlockTypeMessage(material);
			} else {
				sender.sendMessage(TextFormat.RED + "You have entered an invalid Item ID.");
			}
		}
	}
}
