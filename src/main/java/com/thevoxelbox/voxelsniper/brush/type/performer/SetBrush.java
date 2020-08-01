package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.utils.TextFormat;
import org.jetbrains.annotations.Nullable;

public class SetBrush extends AbstractPerformerBrush {

	private static final int SELECTION_SIZE_MAX = 5000000;

	@Nullable
	private Block block;

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		if (set(targetBlock, snipe)) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.GRAY + "Point one");
		} else {
			Sniper sniper = snipe.getSniper();
			sniper.storeUndo(this.performer.getUndo());
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		if (set(lastBlock, snipe)) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.GRAY + "Point one");
		} else {
			Sniper sniper = snipe.getSniper();
			sniper.storeUndo(this.performer.getUndo());
		}
	}

	private boolean set(Block block, Snipe snipe) {
		if (this.block == null) {
			this.block = block;
			return true;
		}
		SnipeMessenger messenger = snipe.createMessenger();
		Level thisBlockLevel = this.block.getLevel();
		String name = thisBlockLevel.getName();
		Level parameterBlockLevel = block.getLevel();
		String parameterBlockLevelName = parameterBlockLevel.getName();
		if (!name.equals(parameterBlockLevelName)) {
			messenger.sendMessage(TextFormat.RED + "You selected points in different worlds!");
			this.block = null;
			return true;
		}
		int x1 = this.block.getX();
		int x2 = block.getX();
		int y1 = this.block.getY();
		int y2 = block.getY();
		int z1 = this.block.getZ();
		int z2 = block.getZ();
		int lowX = Math.min(x1, x2);
		int lowY = Math.min(y1, y2);
		int lowZ = Math.min(z1, z2);
		int highX = Math.max(x1, x2);
		int highY = Math.max(y1, y2);
		int highZ = Math.max(z1, z2);
		if (Math.abs(highX - lowX) * Math.abs(highZ - lowZ) * Math.abs(highY - lowY) > SELECTION_SIZE_MAX) {
			messenger.sendMessage(TextFormat.RED + "Selection size above hardcoded limit, please use a smaller selection.");
		} else {
			for (int y = lowY; y <= highY; y++) {
				for (int x = lowX; x <= highX; x++) {
					for (int z = lowZ; z <= highZ; z++) {
						this.performer.perform(clampY(x, y, z));
					}
				}
			}
		}
		this.block = null;
		return false;
	}

	@Override
	public void sendInfo(Snipe snipe) {
		this.block = null;
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}
