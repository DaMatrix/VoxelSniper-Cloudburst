package com.thevoxelbox.voxelsniper.command.executor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.config.VoxelSniperConfig;
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
import org.cloudburstmc.server.registry.BlockRegistry;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;
public class VoxelExecutor implements CommandExecutor {

	/*private static final List<Identifier> BLOCK_KEYS = Arrays.stream(Block.Material.values())
		.filter(Material::isBlock)
		.map(Material::getKey)
		.collect(Collectors.toList());*/

	private VoxelSniperPlugin plugin;

	public VoxelExecutor(VoxelSniperPlugin plugin) {
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
		VoxelSniperConfig config = this.plugin.getVoxelSniperConfig();
		List<Identifier> liteSniperRestrictedMaterials = config.getLitesniperRestrictedMaterials();
		if (arguments.length == 0) {
			BlockTracer blockTracer = toolkitProperties.createBlockTracer(player);
			Block targetBlock = blockTracer.getTargetBlock();
			if (targetBlock != null) {
				Identifier targetBlockType = targetBlock.getState().getType();
				if (!sender.hasPermission("voxelsniper.ignorelimitations") && liteSniperRestrictedMaterials.contains(targetBlockType)) {
					sender.sendMessage("You are not allowed to use " + targetBlockType.getName() + ".");
					return;
				}
				toolkitProperties.setBlockType(targetBlockType);
				messenger.sendBlockTypeMessage(targetBlockType);
			}
			return;
		}
		Identifier material = Identifier.fromString(arguments[0]);
		if (material != null && BlockState.get(material) != null) {
			if (!sender.hasPermission("voxelsniper.ignorelimitations") && liteSniperRestrictedMaterials.contains(material)) {
				sender.sendMessage("You are not allowed to use " + material.getName() + ".");
				return;
			}
			toolkitProperties.setBlockType(material);
			messenger.sendBlockTypeMessage(material);
		} else {
			sender.sendMessage(TextFormat.RED + "You have entered an invalid Item ID.");
		}
	}
}
