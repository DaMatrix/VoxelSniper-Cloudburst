package com.thevoxelbox.voxelsniper.util.material;

import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.utils.Identifier;

public final class Materials {

	private Materials() {
		throw new UnsupportedOperationException("Cannot create instance of this class");
	}

	public static boolean isEmpty(Identifier material) {
		return material == BlockTypes.AIR;
	}
}
