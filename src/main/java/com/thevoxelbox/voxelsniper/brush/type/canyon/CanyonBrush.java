package com.thevoxelbox.voxelsniper.brush.type.canyon;

import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.utils.TextFormat;
public class CanyonBrush extends AbstractBrush {

	private static final int SHIFT_LEVEL_MIN = 10;
	private static final int SHIFT_LEVEL_MAX = 60;
	private int yLevel = 10;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		String firstParameter = parameters[0];
		if (firstParameter.equalsIgnoreCase("info")) {
			messenger.sendMessage(TextFormat.GREEN + "y[number] to set the Level to which the land will be shifted down");
		}
		if (!firstParameter.isEmpty() && firstParameter.charAt(0) == 'y') {
			int y = Integer.parseInt(firstParameter.replace("y", ""));
			if (y < SHIFT_LEVEL_MIN) {
				y = SHIFT_LEVEL_MIN;
			} else if (y > SHIFT_LEVEL_MAX) {
				y = SHIFT_LEVEL_MAX;
			}
			this.yLevel = y;
			messenger.sendMessage(TextFormat.GREEN + "Shift Level set to " + this.yLevel);
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Undo undo = new Undo();
		canyon(getTargetBlock().getChunk(), undo);
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Undo undo = new Undo();
		Chunk targetChunk = getTargetBlock().getChunk();
		for (int x = targetChunk.getX() - 1; x <= targetChunk.getX() + 1; x++) {
			for (int z = targetChunk.getX() - 1; z <= targetChunk.getX() + 1; z++) {
				canyon(getWorld().getChunkAt(x, z), undo);
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	protected void canyon(Chunk chunk, Undo undo) {
		for (int x = 0; x < CHUNK_SIZE; x++) {
			for (int z = 0; z < CHUNK_SIZE; z++) {
				int currentYLevel = this.yLevel;
				for (int y = 63; y < this.getWorld()
					.getMaxHeight(); y++) {
					Block block = chunk.getBlock(x, y, z);
					Block currentYLevelBlock = chunk.getBlock(x, currentYLevel, z);
					undo.put(block);
					undo.put(currentYLevelBlock);
					currentYLevelBlock.setType(block.getType(), false);
					block.setType(Material.AIR);
					currentYLevel++;
				}
				Block block = chunk.getBlock(x, 0, z);
				undo.put(block);
				block.setType(Material.BEDROCK);
				for (int y = 1; y < SHIFT_LEVEL_MIN; y++) {
					Block currentBlock = chunk.getBlock(x, y, z);
					undo.put(currentBlock);
					currentBlock.setType(Material.STONE);
				}
			}
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.message(TextFormat.GREEN + "Shift Level set to " + this.yLevel)
			.send();
	}

	public int getYLevel() {
		return this.yLevel;
	}

	public void setYLevel(int yLevel) {
		this.yLevel = yLevel;
	}
}
