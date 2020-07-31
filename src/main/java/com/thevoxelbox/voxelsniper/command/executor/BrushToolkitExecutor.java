package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.VoxelSniperPlugin;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.SniperRegistry;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolAction;
import com.thevoxelbox.voxelsniper.sniper.toolkit.Toolkit;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.cloudburstmc.server.utils.TextFormat;
public class BrushToolkitExecutor implements CommandExecutor {

	private VoxelSniperPlugin plugin;

	public BrushToolkitExecutor(VoxelSniperPlugin plugin) {
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
		int length = arguments.length;
		String firstArgument = arguments[0];
		if (length == 3 && firstArgument.equalsIgnoreCase("assign")) {
			ToolAction action = ToolAction.getToolAction(arguments[1]);
			if (action == null) {
				sender.sendMessage("/btool assign <arrow|gunpowder> <toolkit name>");
				return;
			}
			PlayerInventory inventory = player.getInventory();
			ItemStack itemInHand = inventory.getItemInMainHand();
			Material itemType = itemInHand.getType();
			if (Materials.isEmpty(itemType)) {
				sender.sendMessage("/btool assign <arrow|gunpowder> <toolkit name>");
				return;
			}
			String toolkitName = arguments[2];
			Toolkit toolkit = sniper.getToolkit(toolkitName);
			if (toolkit == null) {
				toolkit = new Toolkit(toolkitName);
			}
			toolkit.addToolAction(itemType, action);
			sniper.addToolkit(toolkit);
			sender.sendMessage(itemType.name() + " has been assigned to '" + toolkitName + "' as action " + action.name() + ".");
			return;
		}
		if (length == 2 && firstArgument.equalsIgnoreCase("remove")) {
			Toolkit toolkit = sniper.getToolkit(arguments[1]);
			if (toolkit == null) {
				sender.sendMessage(TextFormat.RED + "Toolkit " + arguments[1] + " not found.");
				return;
			}
			sniper.removeToolkit(toolkit);
			return;
		}
		if (length == 1 && firstArgument.equalsIgnoreCase("remove")) {
			PlayerInventory inventory = player.getInventory();
			ItemStack itemInHand = inventory.getItemInMainHand();
			Material material = itemInHand.getType();
			if (Materials.isEmpty(material)) {
				sender.sendMessage("Can't unassign empty hands.");
				return;
			}
			Toolkit toolkit = sniper.getCurrentToolkit();
			if (toolkit == null) {
				sender.sendMessage("Can't unassign default tool.");
				return;
			}
			toolkit.removeToolAction(material);
			return;
		}
		sender.sendMessage("/btool assign <arrow|gunpowder> <toolkit name>");
		sender.sendMessage("/btool remove <toolkit name>");
		sender.sendMessage("/btool remove");
	}
}
