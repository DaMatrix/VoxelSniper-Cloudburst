package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.BlockHelper;
import com.thevoxelbox.voxelsniper.util.text.NumericParser;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.level.Level;
import org.cloudburstmc.server.math.Direction;
import org.cloudburstmc.server.player.Player;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;
public class ScannerBrush extends AbstractBrush {

	private static final int DEPTH_MIN = 1;
	private static final int DEPTH_DEFAULT = 24;
	private static final int DEPTH_MAX = 64;

	private int depth = DEPTH_DEFAULT;
	private Identifier checkFor = BlockTypes.AIR;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(TextFormat.GOLD + "Scanner brush Parameters:");
				messenger.sendMessage(TextFormat.AQUA + "/b sc d# -- will set the search depth to #. Clamps to 1 - 64.");
				return;
			}
			if (!parameter.isEmpty() && parameter.charAt(0) == 'd') {
				Integer depth = NumericParser.parseInteger(parameter.substring(1));
				if (depth == null) {
					messenger.sendMessage(TextFormat.RED + "Depth is not a number.");
					return;
				}
				this.depth = depth < DEPTH_MIN ? DEPTH_MIN : Math.min(depth, DEPTH_MAX);
				messenger.sendMessage(TextFormat.AQUA + "Scanner depth set to " + this.depth);
			} else {
				messenger.sendMessage(TextFormat.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.checkFor = toolkitProperties.getBlockType();
		Block targetBlock = getTargetBlock();
		Block lastBlock = getLastBlock();
		Direction face = BlockHelper.getSide(targetBlock, lastBlock);
		if (face == null) {
			return;
		}
		scan(snipe, face);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		this.checkFor = toolkitProperties.getBlockType();
		Block targetBlock = getTargetBlock();
		Block lastBlock = getLastBlock();
		Direction face = BlockHelper.getSide(targetBlock, lastBlock);
		if (face == null) {
			return;
		}
		scan(snipe, face);
	}

	private void scan(Snipe snipe, Direction blockFace) {
		SnipeMessenger messenger = snipe.createMessenger();
		Block targetBlock = getTargetBlock();
		if (blockFace == Direction.NORTH) {// Scan south
			for (int i = 1; i < this.depth + 1; i++) {
				if (this.clampY(targetBlock.getX() + i, targetBlock.getY(), targetBlock.getZ())
					.getState().getType() == this.checkFor) {
					messenger.sendMessage(TextFormat.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
					return;
				}
			}
			messenger.sendMessage(TextFormat.GRAY + "Nope.");
		} else if (blockFace == Direction.SOUTH) {// Scan north
			for (int i = 1; i < this.depth + 1; i++) {
				if (this.clampY(targetBlock.getX() - i, targetBlock.getY(), targetBlock.getZ())
						.getState().getType() == this.checkFor) {
					messenger.sendMessage(TextFormat.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
					return;
				}
			}
			messenger.sendMessage(TextFormat.GRAY + "Nope.");
		} else if (blockFace == Direction.EAST) {// Scan west
			for (int i = 1; i < this.depth + 1; i++) {
				if (this.clampY(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ() + i)
						.getState().getType() == this.checkFor) {
					messenger.sendMessage(TextFormat.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
					return;
				}
			}
			messenger.sendMessage(TextFormat.GRAY + "Nope.");
		} else if (blockFace == Direction.WEST) {// Scan east
			for (int i = 1; i < this.depth + 1; i++) {
				if (this.clampY(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ() - i)
						.getState().getType() == this.checkFor) {
					messenger.sendMessage(TextFormat.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
					return;
				}
			}
			messenger.sendMessage(TextFormat.GRAY + "Nope.");
		} else if (blockFace == Direction.UP) {// Scan down
			for (int i = 1; i < this.depth + 1; i++) {
				if ((targetBlock.getY() - i) <= 0) {
					break;
				}
				if (this.clampY(targetBlock.getX(), targetBlock.getY() - i, targetBlock.getZ())
						.getState().getType() == this.checkFor) {
					messenger.sendMessage(TextFormat.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
					return;
				}
			}
			messenger.sendMessage(TextFormat.GRAY + "Nope.");
		} else if (blockFace == Direction.DOWN) {// Scan up
			for (int i = 1; i < this.depth + 1; i++) {
				Sniper sniper = snipe.getSniper();
				Player player = sniper.getPlayer();
				Level world = player.getLevel();
				if ((targetBlock.getY() + i) >= 256) {
					break;
				}
				if (this.clampY(targetBlock.getX(), targetBlock.getY() + i, targetBlock.getZ())
						.getState().getType() == this.checkFor) {
					messenger.sendMessage(TextFormat.GREEN + String.valueOf(this.checkFor) + " found after " + i + " blocks.");
					return;
				}
			}
			messenger.sendMessage(TextFormat.GRAY + "Nope.");
		}
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendMessage(TextFormat.GREEN + "Scanner depth set to " + this.depth);
		messenger.sendMessage(TextFormat.GREEN + "Scanner scans for " + this.checkFor + " (change with /v #)");
	}
}
