package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.cloudburstmc.server.command.CommandSender;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.Location;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.TextFormat;
public class GotoExecutor implements CommandExecutor {

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		Player player = (Player) sender;
		Level world = player.getLevel();
		Integer x = NumericParser.parseInteger(arguments[0]);
		Integer z = NumericParser.parseInteger(arguments[1]);
		if (x == null || z == null) {
			sender.sendMessage(TextFormat.RED + "Invalid syntax.");
			return;
		}
		player.teleport(Location.from(x, world.getHighestBlock(x, z), z, world));
		sender.sendMessage(TextFormat.GREEN + "Woosh!");
	}
}
