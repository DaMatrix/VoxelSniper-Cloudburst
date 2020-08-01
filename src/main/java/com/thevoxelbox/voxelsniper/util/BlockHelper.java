package com.thevoxelbox.voxelsniper.util;

import com.nukkitx.math.vector.Vector3i;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.math.Direction;

/**
 * @author DaPorkchop_
 */
public class BlockHelper {
	public static Direction getSide(Block src, Block dst) {
		return getSide(src.getPosition(), dst.getPosition());
	}

	public static Direction getSide(Vector3i src, Vector3i dst) {
		for (Direction direction : Direction.values()) {
			if (src.getX() + direction.getXOffset() == dst.getX()
				&& src.getY() + direction.getYOffset() == dst.getY()
				&& src.getZ() + direction.getZOffset() == dst.getZ()) {
				return direction;
			}
		}
		return null;
	}
}
