package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.cloudburstmc.server.utils.TextFormat;
public class RingBrush extends AbstractPerformerBrush {

	private double trueCircle;
	private double innerSize;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String parameter : parameters) {
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(TextFormat.GOLD + "Ring Brush Parameters:");
				messenger.sendMessage(TextFormat.AQUA + "/b ri true -- will use a true circle algorithm instead of the skinnier version with classic sniper nubs. /b ri false will switch back. (false is default)");
				messenger.sendMessage(TextFormat.AQUA + "/b ri ir2.5 -- will set the inner radius to 2.5 units");
				return;
			} else if (parameter.startsWith("true")) {
				this.trueCircle = 0.5;
				messenger.sendMessage(TextFormat.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = 0;
				messenger.sendMessage(TextFormat.AQUA + "True circle mode OFF.");
			} else if (parameter.startsWith("ir")) {
				try {
					this.innerSize = Double.parseDouble(parameter.replace("ir", ""));
					messenger.sendMessage(TextFormat.AQUA + "The inner radius has been set to " + TextFormat.RED + this.innerSize);
				} catch (NumberFormatException exception) {
					messenger.sendMessage(TextFormat.RED + "The parameters included are invalid.");
				}
			} else {
				messenger.sendMessage(TextFormat.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		ring(snipe, targetBlock);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		ring(snipe, lastBlock);
	}

	private void ring(Snipe snipe, Block targetBlock) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double outerSquared = Math.pow(brushSize + this.trueCircle, 2);
		double innerSquared = Math.pow(this.innerSize, 2);
		for (int x = brushSize; x >= 0; x--) {
			double xSquared = Math.pow(x, 2);
			for (int z = brushSize; z >= 0; z--) {
				double ySquared = Math.pow(z, 2);
				if (xSquared + ySquared <= outerSquared && xSquared + ySquared >= innerSquared) {
					this.performer.perform(targetBlock.getRelative(x, 0, z));
					this.performer.perform(targetBlock.getRelative(x, 0, -z));
					this.performer.perform(targetBlock.getRelative(-x, 0, z));
					this.performer.perform(targetBlock.getRelative(-x, 0, -z));
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
			.brushSizeMessage()
			.message(TextFormat.AQUA + "The inner radius is " + TextFormat.RED + this.innerSize)
			.send();
	}
}
