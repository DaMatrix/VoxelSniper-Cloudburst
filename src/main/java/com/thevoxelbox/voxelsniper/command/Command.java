package com.thevoxelbox.voxelsniper.command;

import java.util.List;
import com.thevoxelbox.voxelsniper.command.property.CommandProperties;
import org.cloudburstmc.server.utils.TextFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Command extends org.bukkit.command.Command {

	private CommandProperties properties;
	private CommandExecutor executor;
	private TabCompleter tabCompleter;

	public Command(CommandProperties properties, CommandExecutor executor) {
		super(properties.getName(), properties.getDescriptionOrDefault(), properties.getUsage(), properties.getAliases());
		setupPermission(properties);
		this.properties = properties;
		this.executor = executor;
		if (executor instanceof TabCompleter) {
			this.tabCompleter = (TabCompleter) executor;
		}
	}

	private void setupPermission(CommandProperties properties) {
		String permission = properties.getPermission();
		setPermission(permission);
		setPermissionMessage(TextFormat.RED + "Insufficient permissions.");
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

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) {
		if (this.tabCompleter == null) {
			return super.tabComplete(sender, alias, args, location);
		}
		return this.tabCompleter.complete(sender, args);
	}
}
