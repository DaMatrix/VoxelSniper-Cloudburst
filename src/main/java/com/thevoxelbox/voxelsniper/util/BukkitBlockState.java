package com.thevoxelbox.voxelsniper.util;

import com.nukkitx.math.vector.Vector3i;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.level.Level;

/**
 * @author DaPorkchop_
 */
public class BukkitBlockState {
	public final BlockState state;
	public final Vector3i position;
	public final Level level;

	public BukkitBlockState(Block block)	 {
		this.state = block.getState();
		this.position = block.getPosition();
		this.level = block.getLevel();
	}

	public Block getBlock()	{
		return this.level.getBlock(this.position);
	}
}
