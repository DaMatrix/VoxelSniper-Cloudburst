package com.thevoxelbox.voxelsniper.command.executor;

import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import org.cloudburstmc.server.command.CommandSender;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.Location;
import org.cloudburstmc.server.player.Player;

public class VoxelChunkExecutor implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		Player player = (Player) sender;
		Level world = player.getLevel();
		Location location = player.getLocation();
		int x = location.getFloorX();
		int z = location.getFloorZ();
		//TODO: what does this do?
		// world.refreshChunk(x, z);
	}
}
