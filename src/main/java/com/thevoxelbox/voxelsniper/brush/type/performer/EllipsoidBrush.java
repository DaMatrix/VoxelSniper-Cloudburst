package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.block.Block;
import org.cloudburstmc.server.utils.TextFormat;
public class EllipsoidBrush extends AbstractPerformerBrush {

	private double xRad;
	private double yRad;
	private double zRad;
	private boolean istrue;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		this.istrue = false;
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			try {
				if (parameter.equalsIgnoreCase("info")) {
					messenger.sendMessage(TextFormat.GOLD + "Ellipse brush parameters");
					messenger.sendMessage(TextFormat.AQUA + "x[n]: Set X radius to n");
					messenger.sendMessage(TextFormat.AQUA + "y[n]: Set Y radius to n");
					messenger.sendMessage(TextFormat.AQUA + "z[n]: Set Z radius to n");
					return;
				} else if (!parameter.isEmpty() && parameter.charAt(0) == 'x') {
					this.xRad = Integer.parseInt(parameter.replace("x", ""));
					messenger.sendMessage(TextFormat.AQUA + "X radius set to: " + this.xRad);
				} else if (!parameter.isEmpty() && parameter.charAt(0) == 'y') {
					this.yRad = Integer.parseInt(parameter.replace("y", ""));
					messenger.sendMessage(TextFormat.AQUA + "Y radius set to: " + this.yRad);
				} else if (!parameter.isEmpty() && parameter.charAt(0) == 'z') {
					this.zRad = Integer.parseInt(parameter.replace("z", ""));
					messenger.sendMessage(TextFormat.AQUA + "Z radius set to: " + this.zRad);
				} else if (parameter.equalsIgnoreCase("true")) {
					this.istrue = true;
				} else {
					messenger.sendMessage(TextFormat.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
				}
			} catch (NumberFormatException exception) {
				messenger.sendMessage(TextFormat.RED + "Incorrect parameter \"" + parameter + "\"; use the \"info\" parameter.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		execute(snipe, targetBlock);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		execute(snipe, lastBlock);
	}

	private void execute(Snipe snipe, Block targetBlock) {
		this.performer.perform(targetBlock);
		double trueOffset = this.istrue ? 0.5 : 0;
		int blockPositionX = targetBlock.getX();
		int blockPositionY = targetBlock.getY();
		int blockPositionZ = targetBlock.getZ();
		for (double x = 0; x <= this.xRad; x++) {
			double xSquared = (x / (this.xRad + trueOffset)) * (x / (this.xRad + trueOffset));
			for (double z = 0; z <= this.zRad; z++) {
				double zSquared = (z / (this.zRad + trueOffset)) * (z / (this.zRad + trueOffset));
				for (double y = 0; y <= this.yRad; y++) {
					double ySquared = (y / (this.yRad + trueOffset)) * (y / (this.yRad + trueOffset));
					if (xSquared + ySquared + zSquared <= 1) {
						this.performer.perform(clampY((int) (blockPositionX + x), (int) (blockPositionY + y), (int) (blockPositionZ + z)));
						this.performer.perform(clampY((int) (blockPositionX + x), (int) (blockPositionY + y), (int) (blockPositionZ - z)));
						this.performer.perform(clampY((int) (blockPositionX + x), (int) (blockPositionY - y), (int) (blockPositionZ + z)));
						this.performer.perform(clampY((int) (blockPositionX + x), (int) (blockPositionY - y), (int) (blockPositionZ - z)));
						this.performer.perform(clampY((int) (blockPositionX - x), (int) (blockPositionY + y), (int) (blockPositionZ + z)));
						this.performer.perform(clampY((int) (blockPositionX - x), (int) (blockPositionY + y), (int) (blockPositionZ - z)));
						this.performer.perform(clampY((int) (blockPositionX - x), (int) (blockPositionY - y), (int) (blockPositionZ + z)));
						this.performer.perform(clampY((int) (blockPositionX - x), (int) (blockPositionY - y), (int) (blockPositionZ - z)));
					}
				}
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	@Override
	public void sendInfo(Snipe snipe) {
		snipe.createMessageSender()
			.brushNameMessage()
			.message(TextFormat.AQUA + "X-size set to: " + TextFormat.DARK_AQUA + this.xRad)
			.message(TextFormat.AQUA + "Y-size set to: " + TextFormat.DARK_AQUA + this.yRad)
			.message(TextFormat.AQUA + "Z-size set to: " + TextFormat.DARK_AQUA + this.zRad)
			.send();
	}
}
