package com.thevoxelbox.voxelsniper.command;

import org.cloudburstmc.server.command.CommandSender;

public interface CommandExecutor {

	void executeCommand(CommandSender sender, String[] arguments);
}
