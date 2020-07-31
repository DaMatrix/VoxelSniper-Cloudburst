package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.cloudburstmc.server.utils.TextFormat;
public class GotoExecutor implements CommandExecutor {

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		Player player = (Player) sender;
		World world = player.getWorld();
		Integer x = NumericParser.parseInteger(arguments[0]);
		Integer z = NumericParser.parseInteger(arguments[1]);
		if (x == null || z == null) {
			sender.sendMessage(TextFormat.RED + "Invalid syntax.");
			return;
		}
		player.teleport(new Location(world, x, world.getHighestBlockYAt(x, z), z));
		sender.sendMessage(TextFormat.GREEN + "Woosh!");
	}
}
