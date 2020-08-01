package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.utils.TextFormat;
/**
 * Regenerates the target chunk.
 */
public class RegenerateChunkBrush extends AbstractBrush {

	@Override
	public void handleArrowAction(Snipe snipe) {
		generateChunk(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		generateChunk(snipe);
	}

	@SuppressWarnings("deprecation")
	private void generateChunk(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		Chunk chunk = targetBlock.getChunk();
		Undo undo = new Undo();
		Level world = getLevel();
		for (int z = CHUNK_SIZE; z >= 0; z--) {
			for (int x = CHUNK_SIZE; x >= 0; x--) {
				for (int y = 256; y >= 0; y--) {
					undo.put(chunk.getBlock(x, y, z));
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendMessage("Generate that chunk! " + chunk.getX() + " " + chunk.getZ());
		world.regenerateChunk(chunk.getX(), chunk.getZ());
		world.refreshChunk(chunk.getX(), chunk.getZ());
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(TextFormat.LIGHT_PURPLE + "Tread lightly.");
		messenger.sendMessage(TextFormat.LIGHT_PURPLE + "This brush will melt your spleen and sell your kidneys.");
	}
}
