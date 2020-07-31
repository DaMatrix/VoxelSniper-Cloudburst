package com.thevoxelbox.voxelsniper.util.painter.setter;


import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.Location;
import org.cloudburstmc.server.utils.Identifier;

public class BlockDataSetterBuilder {

	private Level world;
	private BlockState blockData;
	private boolean applyPhysics;

	public BlockDataSetterBuilder world(Block block) {
		Level world = block.getLevel();
		return world(world);
	}

	public BlockDataSetterBuilder world(Location location) {
		return world(location.getLevel());
	}

	public BlockDataSetterBuilder world(Level world) {
		this.world = world;
		return this;
	}

	public BlockDataSetterBuilder blockData(Identifier material) {
		return blockData(BlockState.get(material));
	}

	public BlockDataSetterBuilder blockData(BlockState blockData) {
		this.blockData = blockData;
		return this;
	}

	public BlockDataSetterBuilder applyPhysics() {
		this.applyPhysics = true;
		return this;
	}

	public BlockDataSetter build() {
		if (this.world == null) {
			throw new RuntimeException("World must be specified");
		}
		if (this.blockData == null) {
			throw new RuntimeException("Block data must be specified");
		}
		return new BlockDataSetter(this.world, this.blockData, this.applyPhysics);
	}
}
