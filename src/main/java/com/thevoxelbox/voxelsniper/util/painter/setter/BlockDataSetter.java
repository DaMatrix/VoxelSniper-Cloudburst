package com.thevoxelbox.voxelsniper.util.painter.setter;

import com.thevoxelbox.voxelsniper.util.math.vector.VectorVS;
import com.thevoxelbox.voxelsniper.util.painter.BlockSetter;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.level.Level;

public class BlockDataSetter implements BlockSetter {

	private Level world;
	private BlockState blockData;
	private boolean applyPhysics;

	public static BlockDataSetterBuilder builder() {
		return new BlockDataSetterBuilder();
	}

	public BlockDataSetter(Level world, BlockState blockData, boolean applyPhysics) {
		this.world = world;
		this.blockData = blockData;
		this.applyPhysics = applyPhysics;
	}

	@Override
	public void setBlockAt(VectorVS position) {
		int x = position.getX();
		int y = position.getY();
		int z = position.getZ();
		this.world.setBlockAt(x, y, z, this.blockData);
	}

	public Level getWorld() {
		return this.world;
	}

	public BlockState getBlockData() {
		return this.blockData;
	}

	public boolean isApplyPhysics() {
		return this.applyPhysics;
	}
}
