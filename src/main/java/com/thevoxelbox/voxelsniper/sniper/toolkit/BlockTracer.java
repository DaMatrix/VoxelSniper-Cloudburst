package com.thevoxelbox.voxelsniper.sniper.toolkit;

import java.util.Iterator;

import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.math.vector.Vector3i;
import com.thevoxelbox.voxelsniper.util.material.Materials;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.Location;
import org.cloudburstmc.server.math.BlockRayTrace;
import org.cloudburstmc.server.player.Player;

public class BlockTracer {

	private Block targetBlock;
	private Block lastBlock;

	BlockTracer(Player player, int distance) {
		Vector3f eyeLocation = player.getPosition().add(0f, player.getEyeHeight(), 0f);
		//System.out.println("Starting trace at " + eyeLocation);
		Block block = player.getLevel().getBlock(eyeLocation);
		this.targetBlock = block;
		this.lastBlock = block;
		Iterator<Vector3i> iterator = BlockRayTrace.of(eyeLocation, player.getDirectionVector(), distance).iterator();
		this.iterate(player.getLevel(), iterator);
	}

	private void iterate(Level level, Iterator<? extends Vector3i> iterator) {
		while (iterator.hasNext()) {
			//System.out.printf("last: %s@%s, next: %s@%s\n", this.lastBlock.getState(), this.lastBlock.getPosition(), this.targetBlock.getState(), this.targetBlock.getPosition());
			Block block = level.getBlock(iterator.next());
			this.lastBlock = this.targetBlock;
			this.targetBlock = block;
			if (!Materials.isEmpty(block.getState().getType())) {
				//System.out.printf("last: %s@%s, next: %s@%s\n", this.lastBlock.getState(), this.lastBlock.getPosition(), this.targetBlock.getState(), this.targetBlock.getPosition());
				//System.out.println("done");
				return;
			}
		}
	}

	public Block getTargetBlock() {
		return this.targetBlock;
	}

	public Block getLastBlock() {
		return this.lastBlock;
	}
}
