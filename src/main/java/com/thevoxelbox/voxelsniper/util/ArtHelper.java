package com.thevoxelbox.voxelsniper.util;

import com.nukkitx.math.vector.Vector3f;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.entity.Entity;
import org.cloudburstmc.server.entity.EntityType;
import org.cloudburstmc.server.entity.EntityTypes;
import org.cloudburstmc.server.entity.misc.Painting;
import org.cloudburstmc.server.level.Location;
import org.cloudburstmc.server.level.chunk.Chunk;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.TextFormat;
import org.jetbrains.annotations.Nullable;

public final class ArtHelper {

	private ArtHelper() {
		throw new UnsupportedOperationException("Cannot create instance of utility class");
	}

	/**
	 * The paint method used to scroll or set a painting to a specific type.
	 *
	 * @param player The player executing the method
	 * @param art Chosen art to set the painting to
	 */
	public static void paint(Player player, Painting.Motive art) {
		Painting bestMatch = matchPainting(player);
		if (bestMatch == null) {
			return;
		}
		if (art == null) {
			player.sendMessage(TextFormat.RED + "Your input was invalid somewhere.");
			return;
		}
		bestMatch.setMotive(art);
		player.sendMessage(TextFormat.GREEN + "Painting set to: " + art);
	}

	public static void paintAuto(Player player, boolean back) {
		Painting bestMatch = matchPainting(player);
		if (bestMatch == null) {
			return;
		}
		Painting.Motive bestMatchArt = bestMatch.getMotive();
		int ordinal = bestMatchArt.ordinal() + (back ? -1 : 1);
		if (ordinal < 0 || ordinal >= Painting.Motive.values().length) {
			player.sendMessage(TextFormat.RED + "This is the final painting, try scrolling to the other direction.");
			return;
		}
		Painting.Motive ordinalArt = Painting.Motive.values()[ordinal];
		bestMatch.setMotive(ordinalArt);
		player.sendMessage(TextFormat.GREEN + "Painting set to: " + ordinalArt);
	}

	@Nullable
	private static Painting matchPainting(Player player) {
		Painting bestMatch = null;
		BlockState targetBlock = player.getTargetBlock(4, null);
		//TODO: this should be the block location
		// Location location = targetBlock.getLocation();
		Vector3f location = player.getPosition();
		Chunk paintingChunk = player.getLevel().getChunk(location);
		double bestDistanceMatch = 50.0;
		for (Entity entity : paintingChunk.getEntities()) {
			if (entity.getType() == EntityTypes.PAINTING) {
				double distance = location.distanceSquared(entity.getPosition());
				if (distance <= 4 && distance < bestDistanceMatch) {
					bestDistanceMatch = distance;
					bestMatch = (Painting) entity;
				}
			}
		}
		return bestMatch;
	}
}
