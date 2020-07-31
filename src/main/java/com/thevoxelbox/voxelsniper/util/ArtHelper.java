package com.thevoxelbox.voxelsniper.util;

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
	public static void paint(Player player, Art art) {
		Painting bestMatch = matchPainting(player);
		if (bestMatch == null) {
			return;
		}
		if (art == null) {
			player.sendMessage(TextFormat.RED + "Your input was invalid somewhere.");
			return;
		}
		bestMatch.setArt(art);
		player.sendMessage(TextFormat.GREEN + "Painting set to: " + art);
	}

	public static void paintAuto(Player player, boolean back) {
		Painting bestMatch = matchPainting(player);
		if (bestMatch == null) {
			return;
		}
		Art bestMatchArt = bestMatch.getArt();
		int ordinal = bestMatchArt.ordinal() + (back ? -1 : 1);
		if (ordinal < 0 || ordinal >= Art.values().length) {
			player.sendMessage(TextFormat.RED + "This is the final painting, try scrolling to the other direction.");
			return;
		}
		Art ordinalArt = Art.values()[ordinal];
		bestMatch.setArt(ordinalArt);
		player.sendMessage(TextFormat.GREEN + "Painting set to: " + ordinalArt);
	}

	@Nullable
	private static Painting matchPainting(Player player) {
		Painting bestMatch = null;
		Block targetBlock = player.getTargetBlock(null, 4);
		Location location = targetBlock.getLocation();
		Chunk paintingChunk = location.getChunk();
		double bestDistanceMatch = 50.0;
		for (Entity entity : paintingChunk.getEntities()) {
			if (entity.getType() == EntityType.PAINTING) {
				double distance = location.distanceSquared(entity.getLocation());
				if (distance <= 4 && distance < bestDistanceMatch) {
					bestDistanceMatch = distance;
					bestMatch = (Painting) entity;
				}
			}
		}
		return bestMatch;
	}
}
