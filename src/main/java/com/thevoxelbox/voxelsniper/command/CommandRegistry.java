package com.thevoxelbox.voxelsniper.command;

import com.thevoxelbox.voxelsniper.command.property.CommandProperties;
import org.cloudburstmc.server.Server;
import org.cloudburstmc.server.plugin.Plugin;

public class CommandRegistry {

	private Plugin plugin;

	public CommandRegistry(Plugin plugin) {
		this.plugin = plugin;
	}

	public void register(CommandProperties properties, CommandExecutor executor) {
		Command command = new Command(properties, executor);
		register(command);
	}

	public void register(Command command) {
		Server server = this.plugin.getServer();
		org.cloudburstmc.server.registry.CommandRegistry commandMap = getCommandMap(server);
		commandMap.register(this.plugin, command);
	}

	private org.cloudburstmc.server.registry.CommandRegistry getCommandMap(Server server) {
		return server.getCommandRegistry();
	}
}
