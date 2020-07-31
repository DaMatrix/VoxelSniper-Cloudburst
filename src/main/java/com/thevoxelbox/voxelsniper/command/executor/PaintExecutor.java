package com.thevoxelbox.voxelsniper.command.executor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.thevoxelbox.voxelsniper.command.CommandExecutor;
import com.thevoxelbox.voxelsniper.util.ArtHelper;
import org.cloudburstmc.server.command.CommandSender;
import org.cloudburstmc.server.entity.misc.Painting;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.TextFormat;
public class PaintExecutor implements CommandExecutor {

	private static final List<String> ART_NAMES = Arrays.stream(Painting.Motive.values())
		.map(Painting.Motive::name)
		.map(String::toLowerCase)
		.collect(Collectors.toList());

	@Override
	public void executeCommand(CommandSender sender, String[] arguments) {
		Player player = (Player) sender;
		if (arguments.length == 1) {
			if (arguments[0].equalsIgnoreCase("back")) {
				ArtHelper.paintAuto(player, true);
			} else {
				try {
					Painting.Motive art = Painting.Motive.valueOf(arguments[0].toUpperCase());
					ArtHelper.paint(player, art);
				} catch (IllegalArgumentException e)	{
					sender.sendMessage(TextFormat.RED + "Invalid art name.");
					return;
				}
			}
		} else {
			ArtHelper.paintAuto(player, false);
		}
	}
}
