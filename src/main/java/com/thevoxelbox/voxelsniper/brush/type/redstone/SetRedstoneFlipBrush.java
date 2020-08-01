package com.thevoxelbox.voxelsniper.brush.type.redstone;

import java.util.stream.Stream;
import com.thevoxelbox.voxelsniper.brush.type.AbstractBrush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockState;
import org.cloudburstmc.server.block.BlockTraits;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.utils.TextFormat;
import org.jetbrains.annotations.Nullable;

public class SetRedstoneFlipBrush extends AbstractBrush {

	@Nullable
	private Block block;
	private Undo undo;
	private boolean northSouth = true;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(TextFormat.GOLD + "Set Repeater Flip Parameters:");
				messenger.sendMessage(TextFormat.AQUA + "/b setrf <direction> -- valid direction inputs are(n,s,e,world), Set the direction that you wish to flip your repeaters, defaults to north/south.");
				return;
			}
			if (Stream.of("n", "s", "ns")
				.anyMatch(parameter::startsWith)) {
				this.northSouth = true;
				messenger.sendMessage(TextFormat.AQUA + "Flip direction set to north/south");
			} else if (Stream.of("e", "world", "ew")
				.anyMatch(parameter::startsWith)) {
				this.northSouth = false;
				messenger.sendMessage(TextFormat.AQUA + "Flip direction set to east/west.");
			} else {
				messenger.sendMessage(TextFormat.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		if (set(targetBlock)) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.GRAY + "Point one");
		} else {
			Sniper sniper = snipe.getSniper();
			sniper.storeUndo(this.undo);
		}
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		if (set(lastBlock)) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.GRAY + "Point one");
		} else {
			Sniper sniper = snipe.getSniper();
			sniper.storeUndo(this.undo);
		}
	}

	private boolean set(Block block) {
		if (this.block == null) {
			this.block = block;
			return true;
		} else {
			this.undo = new Undo();
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
			for (int y = lowY; y <= highY; y++) {
				for (int x = lowX; x <= highX; x++) {
					for (int z = lowZ; z <= highZ; z++) {
						this.perform(this.clampY(x, y, z));
					}
				}
			}
			this.block = null;
			return false;
		}
	}

	private void perform(Block block) {
		if (block.getState().getType() == BlockTypes.POWERED_REPEATER || block.getState().getType() == BlockTypes.UNPOWERED_REPEATER) {
			BlockState blockData = block.getState();
			int delay = blockData.getTrait(BlockTraits.REPEATER_DELAY);
			if (this.northSouth) {
				if ((delay % 4) == 1) {
					this.undo.put(block);
					blockData = blockData.withTrait(BlockTraits.REPEATER_DELAY, delay + 2);
				} else if ((delay % 4) == 3) {
					this.undo.put(block);
					blockData = blockData.withTrait(BlockTraits.REPEATER_DELAY, delay - 2);
				}
			} else {
				if ((delay % 4) == 2) {
					this.undo.put(block);
					blockData = blockData.withTrait(BlockTraits.REPEATER_DELAY, delay - 2);
				} else if ((delay % 4) == 0) {
					this.undo.put(block);
					blockData = blockData.withTrait(BlockTraits.REPEATER_DELAY, delay + 2);
				}
			}
			block.set(blockData);
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		this.block = null;
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}
