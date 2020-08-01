package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.level.chunk.Chunk;
import org.cloudburstmc.server.utils.TextFormat;
public class FlatOceanBrush extends AbstractBrush {

	private static final int DEFAULT_WATER_LEVEL = 29;
	private static final int DEFAULT_FLOOR_LEVEL = 8;

	private int waterLevel = DEFAULT_WATER_LEVEL;
	private int floorLevel = DEFAULT_FLOOR_LEVEL;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(TextFormat.GREEN + "yo[number] to set the Level to which the water will rise.");
				messenger.sendMessage(TextFormat.GREEN + "yl[number] to set the Level to which the ocean floor will rise.");
			}
			if (parameter.startsWith("yo")) {
				int newWaterLevel = Integer.parseInt(parameter.replace("yo", ""));
				if (newWaterLevel < this.floorLevel) {
					newWaterLevel = this.floorLevel + 1;
				}
				this.waterLevel = newWaterLevel;
				messenger.sendMessage(TextFormat.GREEN + "Water Level set to " + this.waterLevel);
			} else if (parameter.startsWith("yl")) {
				int newFloorLevel = Integer.parseInt(parameter.replace("yl", ""));
				if (newFloorLevel > this.waterLevel) {
					newFloorLevel = this.waterLevel - 1;
					if (newFloorLevel == 0) {
						newFloorLevel = 1;
						this.waterLevel = 2;
					}
				}
				this.floorLevel = newFloorLevel;
				messenger.sendMessage(TextFormat.GREEN + "Ocean floor Level set to " + this.floorLevel);
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		flatOceanAtTarget();
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		flatOceanAtTarget();
		flatOceanAtTarget(CHUNK_SIZE, 0);
		flatOceanAtTarget(CHUNK_SIZE, CHUNK_SIZE);
		flatOceanAtTarget(0, CHUNK_SIZE);
		flatOceanAtTarget(-CHUNK_SIZE, CHUNK_SIZE);
		flatOceanAtTarget(-CHUNK_SIZE, 0);
		flatOceanAtTarget(-CHUNK_SIZE, -CHUNK_SIZE);
		flatOceanAtTarget(0, -CHUNK_SIZE);
		flatOceanAtTarget(CHUNK_SIZE, -CHUNK_SIZE);
	}

	private void flatOceanAtTarget(int additionalX, int additionalZ) {
		Level world = getLevel();
		Block targetBlock = getTargetBlock();
		int blockX = targetBlock.getX();
		int blockZ = targetBlock.getZ();
		Block block = clampY(blockX + additionalX, 1, blockZ + additionalZ);
		Chunk chunk = world.getChunk(block.getPosition());
		flatOcean(chunk);
	}

	private void flatOceanAtTarget() {
		Level world = getLevel();
		Block targetBlock = getTargetBlock();
		Chunk chunk = world.getChunk(targetBlock.getPosition());
		flatOcean(chunk);
	}

	private void flatOcean(Chunk chunk) {
		int baseX = chunk.getX() << 4;
		int baseZ = chunk.getZ() << 4;
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int z = 0; z < CHUNK_SIZE; z++) {
				Level world = chunk.getLevel();
				for (int y = 0; y < 256; y++) {
					Block block = world.getBlock(baseX + x, y, baseZ + z);
					if (y <= this.floorLevel) {
						block.set(BlockState.get(BlockTypes.DIRT));
					} else if (y <= this.waterLevel) {
						block.set(BlockState.get(BlockTypes.WATER), true, false);
					} else {
						block.set(BlockState.AIR, true, false);
					}
				}
			}
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(TextFormat.RED + "THIS BRUSH DOES NOT UNDO");
		messenger.sendMessage(TextFormat.GREEN + "Water level set to " + this.waterLevel);
		messenger.sendMessage(TextFormat.GREEN + "Ocean floor level set to " + this.floorLevel);
	}
}
