package com.thevoxelbox.voxelsniper.brush.type;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.Undo;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import com.thevoxelbox.voxelsniper.util.Vectors;
import com.thevoxelbox.voxelsniper.util.math.MathHelper;
import com.thevoxelbox.voxelsniper.util.math.vector.VectorVS;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.block.BlockTypes;
import org.cloudburstmc.server.utils.Identifier;
import org.cloudburstmc.server.utils.TextFormat;
public class DrainBrush extends AbstractBrush {

	private double trueCircle;
	private boolean disc;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(TextFormat.GOLD + "Drain Brush Parameters:");
				messenger.sendMessage(TextFormat.AQUA + "/b drain true -- will use a true sphere algorithm instead of the skinnier version with classic sniper nubs. /b drain false will switch back. (false is default)");
				messenger.sendMessage(TextFormat.AQUA + "/b drain d -- toggles disc drain mode, as opposed to a ball drain mode");
				return;
			} else if (parameter.startsWith("true")) {
				this.trueCircle = 0.5;
				messenger.sendMessage(TextFormat.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = 0;
				messenger.sendMessage(TextFormat.AQUA + "True circle mode OFF.");
			} else if (parameter.equalsIgnoreCase("d")) {
				if (this.disc) {
					this.disc = false;
					messenger.sendMessage(TextFormat.AQUA + "Disc drain mode OFF");
				} else {
					this.disc = true;
					messenger.sendMessage(TextFormat.AQUA + "Disc drain mode ON");
				}
			} else {
				messenger.sendMessage(TextFormat.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		drain(snipe);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		drain(snipe);
	}

	private void drain(Snipe snipe) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double brushSizeSquared = Math.pow(brushSize + this.trueCircle, 2);
		Undo undo = new Undo();
		Block targetBlock = getTargetBlock();
		int targetBlockX = targetBlock.getX();
		int targetBlockY = targetBlock.getY();
		int targetBlockZ = targetBlock.getZ();
		VectorVS position = Vectors.of(targetBlock);
		if (this.disc) {
			for (int x = brushSize; x >= 0; x--) {
				double xSquared = MathHelper.square(x);
				for (int y = brushSize; y >= 0; y--) {
					double ySquared = MathHelper.square(y);
					if (xSquared + ySquared <= brushSizeSquared) {
						Identifier typePlusPlus = getBlockType(position.plus(x, 0, y));
						if (typePlusPlus == BlockTypes.WATER || typePlusPlus == BlockTypes.LAVA) {
							undo.put(clampY(position.plus(x, 0, y)));
							setBlockType(position.plus(x, 0, y), BlockTypes.AIR);
						}
						Identifier typePlusMinus = getBlockType(targetBlockX + x, targetBlockY, targetBlockZ - y);
						if (typePlusMinus == BlockTypes.WATER || typePlusMinus == BlockTypes.LAVA) {
							undo.put(clampY(targetBlockX + x, targetBlockY, targetBlockZ - y));
							setBlockType(targetBlockX + x, targetBlockY, targetBlockZ - y, BlockTypes.AIR);
						}
						Identifier typeMinusPlus = getBlockType(targetBlockX - x, targetBlockY, targetBlockZ + y);
						if (typeMinusPlus == BlockTypes.WATER || typeMinusPlus == BlockTypes.LAVA) {
							undo.put(clampY(targetBlockX - x, targetBlockY, targetBlockZ + y));
							setBlockType(targetBlockX - x, targetBlockY, targetBlockZ + y, BlockTypes.AIR);
						}
						Identifier typeMinusMinus = getBlockType(targetBlockX - x, targetBlockY, targetBlockZ - y);
						if (typeMinusMinus == BlockTypes.WATER || typeMinusMinus == BlockTypes.LAVA) {
							undo.put(clampY(targetBlockX - x, targetBlockY, targetBlockZ - y));
							setBlockType(targetBlockX - x, targetBlockY, targetBlockZ - y, BlockTypes.AIR);
						}
					}
				}
			}
		} else {
			for (int y = (brushSize + 1) * 2; y >= 0; y--) {
				double ySquared = MathHelper.square(y - brushSize);
				for (int x = (brushSize + 1) * 2; x >= 0; x--) {
					double xSquared = MathHelper.square(x - brushSize);
					for (int z = (brushSize + 1) * 2; z >= 0; z--) {
						if ((xSquared + MathHelper.square(z - brushSize) + ySquared) <= brushSizeSquared) {
							Identifier type = getBlockType(targetBlockX + x - brushSize, targetBlockY + z - brushSize, targetBlockZ + y - brushSize);
							if (type == BlockTypes.WATER || type == BlockTypes.LAVA) {
								undo.put(clampY(targetBlockX + x, targetBlockY + z, targetBlockZ + y));
								setBlockType(targetBlockX + x - brushSize, targetBlockY + z - brushSize, targetBlockZ + y - brushSize, BlockTypes.AIR);
							}
						}
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(undo);
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
		messenger.sendBrushSizeMessage();
		messenger.sendMessage(TextFormat.AQUA + (Double.compare(this.trueCircle, 0.5) == 0 ? "True circle mode ON" : "True circle mode OFF"));
		messenger.sendMessage(TextFormat.AQUA + (this.disc ? "Disc drain mode ON" : "Disc drain mode OFF"));
	}
}
