package com.thevoxelbox.voxelsniper.util;

import com.nukkitx.math.vector.Vector3d;
import com.nukkitx.math.vector.Vector3f;
import com.thevoxelbox.voxelsniper.util.math.vector.VectorVS;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.level.Location;

public final class Vectors {

	private Vectors() {
		throw new UnsupportedOperationException("Cannot create an instance of this class");
	}

	public static VectorVS of(Block block) {
		return new VectorVS(block.getX(), block.getY(), block.getZ());
	}

	public static VectorVS of(Location location) {
		return new VectorVS(location.getFloorX(), location.getFloorY(), location.getFloorZ());
	}

	public static VectorVS of(Vector3d vector) {
		return new VectorVS(vector.getFloorX(), vector.getFloorY(), vector.getFloorZ());
	}

	public static VectorVS of(Vector3f vector) {
		return new VectorVS(vector.getFloorX(), vector.getFloorY(), vector.getFloorZ());
	}

	public static Vector3f toBukkit(VectorVS vector)	{
		return Vector3f.from(vector.getX(), vector.getY(), vector.getZ());
	}
}
