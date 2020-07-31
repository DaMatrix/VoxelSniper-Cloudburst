package com.thevoxelbox.voxelsniper.command;

import java.util.List;
import com.thevoxelbox.voxelsniper.command.property.CommandProperties;
import org.cloudburstmc.server.command.CommandSender;
import org.cloudburstmc.server.command.data.CommandData;
import org.cloudburstmc.server.utils.TextFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Command extends org.cloudburstmc.server.command.Command {

	private CommandProperties properties;
	private CommandExecutor executor;

	public Command(CommandProperties properties, CommandExecutor executor) {
		super(CommandData.builder(properties.getName())
			.setPermissions(properties.getPermission())
			.setPermissionMessage(TextFormat.RED + "Insufficient permissions.")
			.setDescription(properties.getDescription())
			.setUsageMessage(properties.getUsage())
			.setAliases(properties.getAliases().toArray(new String[properties.getAliases().size()]))
			.build());
		this.properties = properties;
		this.executor = executor;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		Class<? extends CommandSender> senderType = this.properties.getSenderTypeOrDefault();
		if (!senderType.isInstance(sender)) {
			sender.sendMessage(TextFormat.RED + "Only " + senderType.getSimpleName() + " can execute this command.");
			return true;
		}
		String permission = this.properties.getPermission();
		if (permission != null && !permission.isEmpty() && !sender.hasPermission(permission)) {
			sender.sendMessage(TextFormat.RED + "Insufficient permissions.");
			return true;
		}
		this.executor.executeCommand(sender, args);
		return true;
	}
}
