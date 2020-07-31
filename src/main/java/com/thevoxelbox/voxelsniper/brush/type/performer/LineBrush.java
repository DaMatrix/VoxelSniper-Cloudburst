package com.thevoxelbox.voxelsniper.brush.type.performer;

import com.thevoxelbox.voxelsniper.sniper.Sniper;
import com.thevoxelbox.voxelsniper.sniper.snipe.Snipe;
import com.thevoxelbox.voxelsniper.sniper.snipe.message.SnipeMessenger;
import org.cloudburstmc.server.utils.TextFormat;
public class LineBrush extends AbstractPerformerBrush {

	private static final Vector HALF_BLOCK_OFFSET = new Vector(0.5, 0.5, 0.5);
	private Vector originCoordinates;
	private Vector targetCoordinates = new Vector();
	private World targetWorld;

	@Override
	public void handleCommand(String[] parameters, Snipe snipe) {
		if (parameters[0].equalsIgnoreCase("info")) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.GOLD + "Line Brush instructions: Right click first point with the arrow. Right click with powder to draw a line to set the second point.");
		}
	}

	@Override
	public void handleArrowAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		Location targetBlockLocation = targetBlock.getLocation();
		this.originCoordinates = targetBlockLocation.toVector();
		this.targetWorld = targetBlock.getWorld();
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendMessage(TextFormat.DARK_PURPLE + "First point selected.");
	}

	@Override
	public void handleGunpowderAction(Snipe snipe) {
		Block targetBlock = getTargetBlock();
		World world = getWorld();
		if (this.originCoordinates == null || !world.equals(this.targetWorld)) {
			SnipeMessenger messenger = snipe.createMessenger();
			messenger.sendMessage(TextFormat.RED + "Warning: You did not select a first coordinate with the arrow");
		} else {
			Location targetBlockLocation = targetBlock.getLocation();
			this.targetCoordinates = targetBlockLocation.toVector();
			linePowder(snipe);
		}
	}

	private void linePowder(Snipe snipe) {
		Vector originClone = new Vector().
			copy(this.originCoordinates)
			.add(HALF_BLOCK_OFFSET);
		Vector targetClone = new Vector().
			copy(this.targetCoordinates)
			.add(HALF_BLOCK_OFFSET);
		Vector direction = new Vector().
			copy(targetClone)
			.subtract(originClone);
		double length = this.targetCoordinates.distance(this.originCoordinates);
		if (length == 0) {
			this.performer.perform(this.targetCoordinates.toLocation(this.targetWorld)
				.getBlock());
		} else {
			BlockIterator blockIterator = new BlockIterator(this.targetWorld, originClone, direction, 0, NumberConversions.round(length));
			while (blockIterator.hasNext()) {
				Block currentBlock = blockIterator.next();
				this.performer.perform(currentBlock);
			}
		}
		Sniper sniper = snipe.getSniper();
		sniper.storeUndo(this.performer.getUndo());
	}

	@Override
	public void sendInfo(Snipe snipe) {
		SnipeMessenger messenger = snipe.createMessenger();
		messenger.sendBrushNameMessage();
	}
}
