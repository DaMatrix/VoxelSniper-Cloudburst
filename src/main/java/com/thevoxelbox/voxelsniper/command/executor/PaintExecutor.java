package com.thevoxelbox.voxelsniper.command.executor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.command.TabCompleter;
import com.thevoxelbox.voxelsniper.util.ArtHelper;
import org.cloudburstmc.server.utils.TextFormat;
public class PaintExecutor implements CommandExecutor, TabCompleter {

	private static final List<String> ART_NAMES = Arrays.stream(Art.values())
		.map(Art::name)
		.map(String::toLowerCase)
		.collect(Collectors.toList());

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		Player player = (Player) sender;
		if (arguments.length == 1) {
			if (arguments[0].equalsIgnoreCase("back")) {
				ArtHelper.paintAuto(player, true);
			} else {
				Art art = Art.getByName(arguments[0]);
				if (art == null) {
					sender.sendMessage(TextFormat.RED + "Invalid art name.");
					return;
				}
				ArtHelper.paint(player, art);
			}
		} else {
			ArtHelper.paintAuto(player, false);
		}
	}

	@Override
	public List<String> complete(CommandSender sender, String[] arguments) {
		if (arguments.length == 1) {
			String argument = arguments[0];
			String argumentLowered = argument.toLowerCase();
			return ART_NAMES.stream()
				.filter(artName -> artName.startsWith(argumentLowered))
				.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
}
