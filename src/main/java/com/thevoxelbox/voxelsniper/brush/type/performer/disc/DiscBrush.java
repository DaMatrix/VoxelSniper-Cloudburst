package com.thevoxelbox.voxelsniper.brush.type.performer.disc;

import com.thevoxelbox.voxelsniper.brush.type.performer.AbstractPerformerBrush;
import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import com.thevoxelbox.voxelsniper.sniper.toolkit.ToolkitProperties;
import org.cloudburstmc.server.utils.TextFormat;
public class DiscBrush extends AbstractPerformerBrush {

	private double trueCircle;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		for (String s : parameters) {
			String parameter = s.toLowerCase();
			if (parameter.equalsIgnoreCase("info")) {
				messenger.sendMessage(TextFormat.GOLD + "Disc Brush Parameters:");
				messenger.sendMessage(TextFormat.AQUA + "/b d true|false" + " -- toggles useing the true circle algorithm instead of the skinnier version with classic sniper nubs. (false is default)");
				return;
			} else if (parameter.startsWith("true")) {
				this.trueCircle = 0.5;
				messenger.sendMessage(TextFormat.AQUA + "True circle mode ON.");
			} else if (parameter.startsWith("false")) {
				this.trueCircle = 0;
				messenger.sendMessage(TextFormat.AQUA + "True circle mode OFF.");
			} else {
				messenger.sendMessage(TextFormat.RED + "Invalid brush parameters! use the info parameter to display parameter info.");
			}
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		disc(snipe, targetBlock);
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block lastBlock = getLastBlock();
		disc(snipe, lastBlock);
	}

	private void disc(Snipe snipe, Block targetBlock) {
		ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
		int brushSize = toolkitProperties.getBrushSize();
		double radiusSquared = (brushSize + this.trueCircle) * (brushSize + this.trueCircle);
		Vector centerPoint = targetBlock.getLocation()
			.toVector();
		Vector currentPoint = new Vector().copy(centerPoint);
		for (int x = -brushSize; x <= brushSize; x++) {
			currentPoint.setX(centerPoint.getX() + x);
			for (int z = -brushSize; z <= brushSize; z++) {
				currentPoint.setZ(centerPoint.getZ() + z);
				if (centerPoint.distanceSquared(currentPoint) <= radiusSquared) {
					this.performer.perform(clampY(currentPoint.getBlockX(), currentPoint.getBlockY(), currentPoint.getBlockZ()));
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
			.send();
	}
}
